package com.dripps.scorefx.listener;

import com.dripps.scorefx.api.BoardManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

/**
 * Listens for player quit events to automatically clean up scoreboards.
 * <p>
 * When a player disconnects from the server, their scoreboard should be removed
 * to prevent memory leaks and ensure proper resource cleanup. This listener handles
 * that automatically.
 * </p>
 * <p>
 * The listener uses {@link EventPriority#MONITOR} to ensure it runs after all other
 * plugins have had a chance to handle the quit event, minimizing conflicts.
 * </p>
 */
public final class PlayerQuitListener implements Listener {
    
    private final BoardManager boardManager;
    private final Logger logger;
    
    /**
     * Creates a new PlayerQuitListener.
     *
     * @param boardManager the board manager to use for cleanup, must not be null
     * @param logger the logger for diagnostic messages, must not be null
     */
    public PlayerQuitListener(@NotNull BoardManager boardManager, @NotNull Logger logger) {
        if (boardManager == null) {
            throw new IllegalArgumentException("BoardManager cannot be null");
        }
        if (logger == null) {
            throw new IllegalArgumentException("Logger cannot be null");
        }
        
        this.boardManager = boardManager;
        this.logger = logger;
    }
    
    /**
     * Handles player quit events by removing their scoreboard.
     * <p>
     * This method is called automatically by Bukkit when a player disconnects.
     * It removes the player's board if one exists, ensuring all resources are
     * properly cleaned up.
     * </p>
     *
     * @param event the player quit event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        try {
            boardManager.removeBoard(event.getPlayer());
            logger.fine("Cleaned up scoreboard for " + event.getPlayer().getName() + " on quit");
        } catch (Exception e) {
            logger.warning("Error cleaning up scoreboard for " + event.getPlayer().getName() + ": " + e.getMessage());
        }
    }
}
