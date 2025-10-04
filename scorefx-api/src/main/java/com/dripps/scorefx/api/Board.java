package com.dripps.scorefx.api;

import com.dripps.scorefx.api.animation.Animation;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a single, per-player scoreboard.
 * <p>
 * A Board provides methods to set the title, add/update lines, and display animations.
 * Scoreboards support up to 15 visible lines (rows 1-15), with row 1 appearing at the
 * bottom and row 15 appearing at the top.
 * </p>
 * <p>
 * All text supports Minecraft color codes (§ and &amp;) and, if PlaceholderAPI is installed,
 * placeholder resolution. Lines can be configured to update at custom intervals to optimize
 * performance when using expensive placeholders.
 * </p>
 * <p>
 * <strong>Thread Safety:</strong> All methods that modify the board's state are strictly
 * main-thread only and will throw an {@link IllegalStateException} if called from any other thread.
 * </p>
 *
 * @since 1.0
 */
public interface Board {
    
    /**
     * Sets the title of the scoreboard to static text.
     * <p>
     * The title supports Minecraft color codes (using § or &amp;) and PlaceholderAPI placeholders
     * if available. If the title contains placeholders, it will be updated every tick.
     * </p>
     * <p>
     * Calling this method replaces any previously set static or animated title.
     * </p>
     *
     * @param title the text to display as the title, must not be null
     * @throws IllegalStateException if called from a non-main thread
     */
    void setTitle(@NotNull String title);
    
    /**
     * Sets the title of the scoreboard to an animation.
     * <p>
     * The animation will cycle through its frames at the interval specified by the
     * animation itself. Each frame supports color codes and PlaceholderAPI placeholders.
     * </p>
     * <p>
     * Calling this method replaces any previously set static or animated title.
     * </p>
     *
     * @param titleAnimation the animation to play in the title, must not be null
     * @throws IllegalStateException if called from a non-main thread
     */
    void setAnimatedTitle(@NotNull Animation titleAnimation);
    
    /**
     * Sets the text for a specific row on the scoreboard.
     * <p>
     * The text supports Minecraft color codes (using § or &amp;) and PlaceholderAPI placeholders
     * if available. If the text contains placeholders, it will be updated every tick by default.
     * </p>
     * <p>
     * Rows are numbered 1-15, with row 1 appearing at the bottom of the scoreboard and row 15
     * at the top. Calling this method replaces any previously set static or animated content
     * on this row.
     * </p>
     *
     * @param row the row number (1-15), must be within valid range
     * @param text the text to display on this row, must not be null
     * @throws IllegalArgumentException if row is not between 1 and 15
     * @throws IllegalStateException if called from a non-main thread
     */
    void setLine(int row, @NotNull String text);
    
    /**
     * Sets the text for a specific row with a custom update interval.
     * <p>
     * This method is useful for lines that contain performance-intensive placeholders
     * or data that doesn't need to update every tick. The text will only be re-evaluated
     * and updated every {@code updateIntervalTicks} server ticks.
     * </p>
     * <p>
     * The text supports Minecraft color codes (using § or &amp;) and PlaceholderAPI placeholders
     * if available.
     * </p>
     * <p>
     * Rows are numbered 1-15, with row 1 appearing at the bottom of the scoreboard and row 15
     * at the top. Calling this method replaces any previously set static or animated content
     * on this row.
     * </p>
     *
     * @param row the row number (1-15), must be within valid range
     * @param text the text to display on this row, must not be null
     * @param updateIntervalTicks the interval in server ticks between updates, must be positive
     * @throws IllegalArgumentException if row is not between 1 and 15, or if updateIntervalTicks is less than 1
     * @throws IllegalStateException if called from a non-main thread
     */
    void setLine(int row, @NotNull String text, int updateIntervalTicks);
    
    /**
     * Sets a row to display an animation.
     * <p>
     * The animation will cycle through its frames at the interval specified by the
     * animation itself. Each frame supports color codes and PlaceholderAPI placeholders.
     * </p>
     * <p>
     * Rows are numbered 1-15, with row 1 appearing at the bottom of the scoreboard and row 15
     * at the top. Calling this method replaces any previously set static or animated content
     * on this row.
     * </p>
     *
     * @param row the row number (1-15), must be within valid range
     * @param animation the animation to play on this row, must not be null
     * @throws IllegalArgumentException if row is not between 1 and 15
     * @throws IllegalStateException if called from a non-main thread
     */
    void setAnimatedLine(int row, @NotNull Animation animation);
    
    /**
     * Clears a line from the scoreboard.
     * <p>
     * This removes all content (static text or animation) from the specified row.
     * If the row is already empty, this method does nothing.
     * </p>
     *
     * @param row the row number (1-15) to clear
     * @throws IllegalArgumentException if row is not between 1 and 15
     * @throws IllegalStateException if called from a non-main thread
     */
    void removeLine(int row);
    
    /**
     * Gets the player who owns this scoreboard.
     *
     * @return the owning player, never null
     */
    @NotNull
    Player getPlayer();
}
