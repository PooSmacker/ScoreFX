package com.dripps.scorefx.manager;

import com.dripps.scorefx.api.Board;
import com.dripps.scorefx.api.BoardManager;
import com.dripps.scorefx.board.TeamBoardImpl;
import com.dripps.scorefx.scheduler.Heartbeat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Implementation of the {@link BoardManager} interface.
 * <p>
 * This class manages the lifecycle of all player scoreboards, including creation,
 * retrieval, and destruction. It maintains a map of active boards and coordinates
 * with the {@link Heartbeat} scheduler for task registration and cleanup.
 * </p>
 * <p>
 * <strong>Thread Safety:</strong> This class uses a {@link ConcurrentHashMap} for
 * thread-safe board storage, but all modification methods enforce main-thread access
 * to comply with Bukkit API requirements.
 * </p>
 */
public final class BoardManagerImpl implements BoardManager {
    
    private final Heartbeat heartbeat;
    private final Logger logger;
    private final Map<UUID, Board> activeBoards;
    
    /**
     * Creates a new BoardManagerImpl.
     *
     * @param heartbeat the Heartbeat scheduler instance, must not be null
     * @param logger the logger instance for diagnostic messages, must not be null
     */
    public BoardManagerImpl(@NotNull Heartbeat heartbeat, @NotNull Logger logger) {
        if (heartbeat == null) {
            throw new IllegalArgumentException("Heartbeat cannot be null");
        }
        if (logger == null) {
            throw new IllegalArgumentException("Logger cannot be null");
        }
        
        this.heartbeat = heartbeat;
        this.logger = logger;
        this.activeBoards = new ConcurrentHashMap<>();
    }
    
    @NotNull
    @Override
    public Board createBoard(@NotNull Player player) {
        checkMainThread();
        
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        
        UUID playerId = player.getUniqueId();
        
        // Remove existing board if present
        if (activeBoards.containsKey(playerId)) {
            logger.fine("Removing existing board for player " + player.getName() + " before creating new one");
            removeBoard(player);
        }
        
        // Create new board
        TeamBoardImpl board = new TeamBoardImpl(player, heartbeat);
        
        // Register with Heartbeat for task scheduling
        heartbeat.registerBoard(playerId, board);
        
        // Store in active boards map
        activeBoards.put(playerId, board);
        
        logger.fine("Created new board for player " + player.getName() + " (UUID: " + playerId + ")");
        
        return board;
    }
    
    @NotNull
    @Override
    public Optional<Board> getBoard(@NotNull Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        
        Board board = activeBoards.get(player.getUniqueId());
        return Optional.ofNullable(board);
    }
    
    @Override
    public void removeBoard(@NotNull Player player) {
        checkMainThread();
        
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        
        UUID playerId = player.getUniqueId();
        Board board = activeBoards.remove(playerId);
        
        if (board == null) {
            // No board exists for this player - nothing to do
            return;
        }
        
        // Unregister from Heartbeat (cancels all scheduled tasks)
        heartbeat.unregisterBoard(playerId);
        
        // Destroy the board (cleans up Bukkit resources)
        if (board instanceof TeamBoardImpl teamBoard) {
            teamBoard.destroy();
        }
        
        logger.fine("Removed board for player " + player.getName() + " (UUID: " + playerId + ")");
    }
    
    /**
     * Removes all active boards and cleans up all resources.
     * <p>
     * This method should be called during plugin shutdown to ensure all boards
     * are properly destroyed and all tasks are cancelled. It iterates through
     * all active boards and calls {@link #removeBoard(Player)} for each.
     * </p>
     * <p>
     * This method must be called from the main thread.
     * </p>
     */
    public void removeAllBoards() {
        checkMainThread();
        
        logger.info("Removing all active boards...");
        
        int count = activeBoards.size();
        
        // Create a copy of the player UUIDs to avoid concurrent modification
        UUID[] playerIds = activeBoards.keySet().toArray(new UUID[0]);
        
        for (UUID playerId : playerIds) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                removeBoard(player);
            } else {
                // Player is offline, but we still need to clean up
                Board board = activeBoards.remove(playerId);
                heartbeat.unregisterBoard(playerId);
                
                if (board instanceof TeamBoardImpl teamBoard) {
                    try {
                        teamBoard.destroy();
                    } catch (Exception e) {
                        logger.warning("Error destroying board for offline player " + playerId + ": " + e.getMessage());
                    }
                }
            }
        }
        
        logger.info("Removed " + count + " active board(s)");
    }
    
    /**
     * Gets the number of currently active boards.
     * <p>
     * This is primarily useful for monitoring and debugging.
     * </p>
     *
     * @return the number of active boards
     */
    public int getActiveBoardCount() {
        return activeBoards.size();
    }
    
    /**
     * Checks if a player has an active board.
     * <p>
     * This is a convenience method that's equivalent to calling
     * {@code getBoard(player).isPresent()}.
     * </p>
     *
     * @param player the player to check
     * @return true if the player has an active board, false otherwise
     */
    public boolean hasBoard(@NotNull Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        
        return activeBoards.containsKey(player.getUniqueId());
    }
    
    /**
     * Gets a reference to the Heartbeat scheduler.
     * <p>
     * This is primarily used internally for testing and debugging purposes.
     * </p>
     *
     * @return the Heartbeat instance
     */
    @NotNull
    public Heartbeat getHeartbeat() {
        return heartbeat;
    }
    
    /**
     * Ensures that the current thread is the main server thread.
     * <p>
     * This is a critical safety check to prevent concurrent modification issues
     * and comply with the Bukkit API's threading requirements.
     * </p>
     *
     * @throws IllegalStateException if called from a non-main thread
     */
    private void checkMainThread() {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException(
                "ScoreFX API must be accessed from the main server thread. " +
                "Current thread: " + Thread.currentThread().getName()
            );
        }
    }
}
