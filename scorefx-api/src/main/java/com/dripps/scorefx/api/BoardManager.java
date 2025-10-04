package com.dripps.scorefx.api;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Manages the lifecycle of all player scoreboards.
 * <p>
 * The BoardManager is responsible for creating, retrieving, and removing scoreboards
 * for individual players. Each player can have at most one active scoreboard at a time.
 * Creating a new board for a player who already has one will replace the existing board.
 * </p>
 * <p>
 * All methods in this interface must be called from the main server thread.
 * </p>
 *
 * @since 1.0
 */
public interface BoardManager {
    
    /**
     * Creates a new scoreboard for the specified player.
     * <p>
     * If the player already has an active scoreboard, it will be destroyed and replaced
     * with a new one. The newly created board will be empty (no title or lines set).
     * </p>
     * <p>
     * This method must be called from the main server thread.
     * </p>
     *
     * @param player the player to create the scoreboard for, must not be null
     * @return the newly created Board instance, never null
     * @throws IllegalStateException if called from a non-main thread
     */
    @NotNull
    Board createBoard(@NotNull Player player);
    
    /**
     * Retrieves the active scoreboard for the specified player.
     * <p>
     * If the player does not have an active scoreboard, an empty Optional is returned.
     * </p>
     *
     * @param player the player whose scoreboard to retrieve, must not be null
     * @return an Optional containing the player's Board if one exists, or empty otherwise
     */
    @NotNull
    Optional<Board> getBoard(@NotNull Player player);
    
    /**
     * Destroys and removes the scoreboard for the specified player.
     * <p>
     * This method cleans up all resources associated with the player's scoreboard,
     * including scheduled tasks and team registrations. If the player does not have
     * an active scoreboard, this method does nothing.
     * </p>
     * <p>
     * The player's scoreboard will be reset to the server's default scoreboard.
     * This method must be called from the main server thread.
     * </p>
     *
     * @param player the player whose scoreboard to remove, must not be null
     * @throws IllegalStateException if called from a non-main thread
     */
    void removeBoard(@NotNull Player player);
}
