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
 * The Heartbeat is the central scheduler for all scoreboard updates in ScoreFX.
 * <p>
 * Instead of running separate tasks for each board and each line, the Heartbeat maintains
 * a single {@link BukkitRunnable} that executes every server tick. It uses a {@link PriorityQueue}
 * to efficiently process only the tasks that are due for execution on the current tick.
 * </p>
 * <p>
 * This architecture provides several critical benefits:
 * <ul>
 *   <li><strong>Performance:</strong> Only one task runs per tick, minimizing scheduler overhead</li>
 *   <li><strong>Efficiency:</strong> Tasks are processed in chronological order using a min-heap</li>
 *   <li><strong>Scalability:</strong> Handles thousands of boards without performance degradation</li>
 *   <li><strong>Flexibility:</strong> Supports different update intervals per line/title</li>
 * </ul>
 * </p>
 * <p>
 * <strong>Thread Safety:</strong> The Heartbeat must be started and stopped from the main thread,
 * but the internal task queue uses concurrent data structures for safety.
 * </p>
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
    
    /**
     * Starts the Heartbeat scheduler.
     * <p>
     * This method creates and schedules the main {@link BukkitRunnable} that will execute
     * every server tick. The Heartbeat must be started before any tasks can be scheduled.
     * </p>
     * <p>
     * This method must be called from the main thread.
     * </p>
     *
     * @throws IllegalStateException if the Heartbeat is already running
     */
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
    
    /**
     * Stops the Heartbeat scheduler.
     * <p>
     * This method cancels the main task and clears all pending tasks from the queue.
     * After stopping, the Heartbeat can be restarted with {@link #start()}.
     * </p>
     * <p>
     * This method must be called from the main thread.
     * </p>
     */
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
    
    /**
     * The main tick execution method.
     * <p>
     * This method is called every server tick by the BukkitRunnable. It processes all tasks
     * from the priority queue whose execution tick is less than or equal to the current tick.
     * </p>
     * <p>
     * Tasks are processed in chronological order (earliest first) thanks to the min-heap
     * property of the PriorityQueue. Recurring tasks are automatically rescheduled after execution.
     * </p>
     * <p>
     * As of v2.0.1, this method flushes all pending updates to boards at the end of the tick
     * for improved batching performance.
     * </p>
     */
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
    
    /**
     * Executes a single update task.
     * <p>
     * This method retrieves the board from the active boards map and performs the
     * appropriate update operation based on the task type. As of version 1.1.0, this
     * method is Adventure-First: all text is converted to Components before being
     * passed to the board.
     * </p>
     * <p>
     * For String-based tasks (textObject is String), PlaceholderAPI replacement is
     * applied before conversion to Components. For Component-based tasks and animations,
     * PlaceholderAPI is not supported.
     * </p>
     *
     * @param task the task to execute
     * @since 1.0 (updated in 1.1.0 for Adventure-First architecture)
     */
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
    
    /**
     * Schedules a new task for execution.
     * <p>
     * The task will be added to the priority queue and will execute when its
     * scheduled execution tick is reached. Tasks are processed in chronological order.
     * </p>
     * <p>
     * This method is thread-safe and can be called from any thread.
     * </p>
     *
     * @param task the task to schedule, must not be null
     */
    public void scheduleTask(@NotNull UpdateTask task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        
        taskQueue.offer(task);
        
        // Track this task for the board
        boardTasks.computeIfAbsent(task.boardId(), k -> ConcurrentHashMap.newKeySet())
                  .add(task);
    }
    
    /**
     * Cancels all tasks associated with a specific board.
     * <p>
     * This method should be called when a board is destroyed to prevent memory leaks
     * and unnecessary task execution. All pending tasks for the board will be removed
     * from the queue.
     * </p>
     * <p>
     * This method is thread-safe and can be called from any thread.
     * </p>
     *
     * @param boardId the UUID of the board whose tasks should be cancelled
     */
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
    
    /**
     * Registers a board with the Heartbeat.
     * <p>
     * This method adds the board to the active boards map so that scheduled tasks
     * can find and update it. Boards must be registered before tasks can be scheduled for them.
     * </p>
     *
     * @param boardId the UUID of the player who owns the board
     * @param board the board instance
     */
    public void registerBoard(@NotNull UUID boardId, @NotNull TeamBoardImpl board) {
        if (boardId == null) {
            throw new IllegalArgumentException("Board ID cannot be null");
        }
        if (board == null) {
            throw new IllegalArgumentException("Board cannot be null");
        }
        
        activeBoardsMap.put(boardId, board);
    }
    
    /**
     * Unregisters a board from the Heartbeat.
     * <p>
     * This is equivalent to calling {@link #cancelTasksForBoard(UUID)} and should be
     * called when a board is destroyed.
     * </p>
     *
     * @param boardId the UUID of the board to unregister
     */
    public void unregisterBoard(@NotNull UUID boardId) {
        cancelTasksForBoard(boardId);
    }
    
    /**
     * Gets the current server tick as tracked by the Heartbeat.
     *
     * @return the current tick number
     */
    public long getCurrentTick() {
        return currentTick;
    }
    
    /**
     * Checks if the Heartbeat is currently running.
     *
     * @return true if running, false otherwise
     */
    public boolean isRunning() {
        return running;
    }
    
    /**
     * Gets the number of tasks currently in the queue.
     * <p>
     * This is primarily useful for debugging and monitoring.
     * </p>
     *
     * @return the number of pending tasks
     */
    public int getQueueSize() {
        return taskQueue.size();
    }
    
    /**
     * Gets the number of active boards registered with the Heartbeat.
     *
     * @return the number of active boards
     */
    public int getActiveBoardCount() {
        return activeBoardsMap.size();
    }
}
