package com.dripps.scorefx.scheduler;

import com.dripps.scorefx.animation.SharedAnimation;
import com.dripps.scorefx.board.TeamBoardImpl;
import com.dripps.scorefx.hook.PAPIHook;
import com.dripps.scorefx.util.LegacySupport;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Central scheduler for all scoreboard updates in ScoreFX.
 * Runs a single BukkitRunnable every tick and processes due tasks from a
 * PriorityQueue (earliest first). Start/stop on the main thread; internal
 * structures are concurrent for safety.
 */
public final class Heartbeat {
    
    private final Plugin plugin;
    private final Logger logger;
    private final PAPIHook papiHook;
    private final PriorityQueue<UpdateTask> taskQueue;
    private final Map<UUID, Set<UpdateTask>> boardTasks; // Track tasks by board for cancellation
    private final Map<UUID, TeamBoardImpl> activeBoardsMap; // Reference to active boards
    
    private BukkitTask heartbeatTask;
    private long currentTick;
    private boolean running;
    
    /**
     * Creates a new Heartbeat scheduler.
     *
     * @param plugin the plugin instance, must not be null
     * @param papiHook the PlaceholderAPI hook, must not be null
     */
    public Heartbeat(@NotNull Plugin plugin, @NotNull PAPIHook papiHook) {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null");
        }
        if (papiHook == null) {
            throw new IllegalArgumentException("PAPIHook cannot be null");
        }
        
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.papiHook = papiHook;
        this.taskQueue = new PriorityQueue<>();
        this.boardTasks = new ConcurrentHashMap<>();
        this.activeBoardsMap = new ConcurrentHashMap<>();
        this.currentTick = 0;
        this.running = false;
    }
    
    /** Starts the Heartbeat scheduler. Main thread only. */
    public void start() {
        if (running) {
            throw new IllegalStateException("Heartbeat is already running");
        }
        
        logger.info("Starting Heartbeat scheduler...");
        
        heartbeatTask = new BukkitRunnable() {
            @Override
            public void run() {
                tick();
            }
        }.runTaskTimer(plugin, 0L, 1L); // Run every tick (1L = 1 tick)
        
        running = true;
        logger.info("Heartbeat scheduler started successfully");
    }
    
    /** Stops the Heartbeat scheduler and clears pending tasks. Main thread only. */
    public void stop() {
        if (!running) {
            logger.warning("Attempted to stop Heartbeat, but it's not running");
            return;
        }
        
        logger.info("Stopping Heartbeat scheduler...");
        
        if (heartbeatTask != null) {
            heartbeatTask.cancel();
            heartbeatTask = null;
        }
        
        // Clear all pending tasks
        taskQueue.clear();
        boardTasks.clear();
        
        running = false;
        currentTick = 0;
        
        logger.info("Heartbeat scheduler stopped successfully");
    }
    
    /** Main per-tick execution: processes due tasks and flushes board updates. */
    private void tick() {
        currentTick++;
        
        // Process all tasks that are due for execution this tick
        while (!taskQueue.isEmpty() && taskQueue.peek().executionTick() <= currentTick) {
            UpdateTask task = taskQueue.poll();
            
            // Execute the task
            executeTask(task);
            
            // If the task is recurring, reschedule it
            if (task.isRecurring()) {
                UpdateTask nextTask = task.reschedule(currentTick);
                scheduleTask(nextTask);
            } else {
                // Remove from board task tracking
                Set<UpdateTask> tasks = boardTasks.get(task.boardId());
                if (tasks != null) {
                    tasks.remove(task);
                }
            }
        }
        
        // v2.0.1: Flush all pending updates to boards (batching optimization)
        for (TeamBoardImpl board : activeBoardsMap.values()) {
            board.flushUpdates();
        }
    }
    
    /** Executes a single update task (Adventure-first; PAPI only for Strings). */
    private void executeTask(@NotNull UpdateTask task) {
        TeamBoardImpl board = activeBoardsMap.get(task.boardId());
        
        if (board == null) {
            // Board no longer exists - task is orphaned
            return;
        }
        
        // Get the player for placeholder replacement
        Player player = board.getPlayer();
        if (player == null || !player.isOnline()) {
            // Player is offline, skip this task
            return;
        }
        
        try {
            switch (task.type()) {
                case LINE_UPDATE -> {
                    // Process text object - only Strings support PlaceholderAPI
                    Component finalComponent;
                    if (task.textObject() instanceof String textString) {
                        // Replace placeholders in the String
                        String processedText = papiHook.setPlaceholders(player, textString);
                        // Convert to Component (Adventure-First)
                        finalComponent = LegacySupport.toComponent(processedText);
                    } else {
                        // Non-String objects are not supported for LINE_UPDATE
                        logger.warning("LINE_UPDATE task received non-String textObject for board " + task.boardId());
                        return;
                    }
                    
                    // Update the line with the Component
                    board.updateLineDirect(task.row(), finalComponent);
                }
                case LINE_ANIMATION -> {
                    // Advance the animation to the next frame (returns Component)
                    var animation = board.getAnimation(task.row());
                    if (animation != null) {
                        Component nextFrame = animation.nextFrame();
                        // Note: PlaceholderAPI is not supported for Component-based animations
                        board.updateLineDirect(task.row(), nextFrame);
                    }
                }
                case TITLE_UPDATE -> {
                    // Process text object - only Strings support PlaceholderAPI
                    Component finalComponent;
                    if (task.textObject() instanceof String textString) {
                        // Replace placeholders in the String
                        String processedText = papiHook.setPlaceholders(player, textString);
                        // Convert to Component (Adventure-First)
                        finalComponent = LegacySupport.toComponent(processedText);
                    } else {
                        // Non-String objects are not supported for TITLE_UPDATE
                        logger.warning("TITLE_UPDATE task received non-String textObject for board " + task.boardId());
                        return;
                    }
                    
                    // Update the title with the Component
                    board.updateTitleDirect(finalComponent);
                }
                case TITLE_ANIMATION -> {
                    // Advance the title animation to the next frame (returns Component)
                    var animation = board.getAnimation(-1); // -1 is TITLE_ROW
                    if (animation != null) {
                        Component nextFrame = animation.nextFrame();
                        // Note: PlaceholderAPI is not supported for Component-based animations
                        board.updateTitleDirect(nextFrame);
                    }
                }
            }
        } catch (Exception e) {
            logger.warning("Error executing task for board " + task.boardId() + ": " + e.getMessage());
        }
    }
    
    /** Schedules a new task for execution (thread-safe). */
    public void scheduleTask(@NotNull UpdateTask task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        
        taskQueue.offer(task);
        
        // Track this task for the board
        boardTasks.computeIfAbsent(task.boardId(), k -> ConcurrentHashMap.newKeySet())
                  .add(task);
    }
    
    /** Cancels all tasks associated with a specific board (thread-safe). */
    public void cancelTasksForBoard(@NotNull UUID boardId) {
        if (boardId == null) {
            throw new IllegalArgumentException("Board ID cannot be null");
        }
        
        Set<UpdateTask> tasks = boardTasks.remove(boardId);
        
        if (tasks != null) {
            // Remove all tasks for this board from the main queue
            taskQueue.removeAll(tasks);
            tasks.clear();
        }
        
        // Remove board from active boards map
        activeBoardsMap.remove(boardId);
    }
    
    /** Registers a board so scheduled tasks can find and update it. */
    public void registerBoard(@NotNull UUID boardId, @NotNull TeamBoardImpl board) {
        if (boardId == null) {
            throw new IllegalArgumentException("Board ID cannot be null");
        }
        if (board == null) {
            throw new IllegalArgumentException("Board cannot be null");
        }
        
        activeBoardsMap.put(boardId, board);
    }
    
    /** Unregisters a board (equivalent to cancelTasksForBoard). */
    public void unregisterBoard(@NotNull UUID boardId) {
        cancelTasksForBoard(boardId);
    }
    
    /** Returns the current server tick. */
    public long getCurrentTick() {
        return currentTick;
    }
    
    /** Returns true if the Heartbeat is currently running. */
    public boolean isRunning() {
        return running;
    }
    
    /** Returns the number of pending tasks. */
    public int getQueueSize() {
        return taskQueue.size();
    }
    
    /** Returns the number of active boards. */
    public int getActiveBoardCount() {
        return activeBoardsMap.size();
    }
}
