package com.dripps.scorefx.api;

import com.dripps.scorefx.api.animation.Animation;
import net.kyori.adventure.text.Component;
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
 * <strong>Modern API (v1.1.0+):</strong> ScoreFX now supports Adventure Components, enabling
 * full RGB colors, gradients, hover/click events, and other modern text features. The new
 * Component-based methods are the preferred way to interact with scoreboards.
 * </p>
 * <p>
 * <strong>Legacy Support:</strong> The original String-based methods continue to work and
 * support legacy formatting with '&amp;' color codes and hex colors via #RRGGBB format.
 * If PlaceholderAPI is installed, placeholder resolution works for String-based methods only.
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
     * Sets the title of the scoreboard using an Adventure Component.
     * <p>
     * This is the modern, preferred method for setting titles. Components support full RGB colors,
     * text gradients, formatting, and other Adventure features.
     * </p>
     * <p>
     * Example usage with RGB colors:
     * <pre>{@code
     * board.setTitle(Component.text("My Server", NamedTextColor.GOLD));
     * board.setTitle(Component.text("RGB Title", TextColor.color(0xFF5733)));
     * }</pre>
     * </p>
     * <p>
     * Calling this method replaces any previously set static or animated title.
     * </p>
     * <p>
     * <strong>Note:</strong> PlaceholderAPI placeholders are not currently supported in Components.
     * Use the legacy String-based method if you need placeholder support.
     * </p>
     *
     * @param title the component to display as the title, must not be null
     * @throws IllegalStateException if called from a non-main thread
     * @since 1.1.0
     */
    void setTitle(@NotNull Component title);
    
    /**
     * Sets the title of the scoreboard to static text using legacy formatting.
     * <p>
     * The title supports Minecraft color codes (using § or &amp;) and hex colors using #RRGGBB format.
     * If PlaceholderAPI is installed, placeholders will be resolved and updated every tick.
     * </p>
     * <p>
     * Example usage:
     * <pre>{@code
     * board.setTitle("&6&lMy Server");
     * board.setTitle("&#FF5733My RGB Title");
     * }</pre>
     * </p>
     * <p>
     * Calling this method replaces any previously set static or animated title.
     * </p>
     *
     * @param title the text to display as the title, must not be null
     * @throws IllegalStateException if called from a non-main thread
     * @deprecated As of 1.1.0, use {@link #setTitle(Component)} for full Adventure support and modern text features.
     *             This method remains fully functional for backward compatibility.
     */
    @Deprecated(since = "1.1.0", forRemoval = false)
    void setTitle(@NotNull String title);
    
    /**
     * Sets the title of the scoreboard to an animation.
     * <p>
     * The animation will cycle through its frames at the interval specified by the
     * animation itself. Each frame is a Component supporting full RGB colors and formatting.
     * </p>
     * <p>
     * Calling this method replaces any previously set static or animated title.
     * </p>
     * <p>
     * <strong>Note:</strong> PlaceholderAPI placeholders are not currently supported in Component-based
     * animations. For placeholder support in animations, use legacy String-based animations created
     * with {@link com.dripps.scorefx.api.animation.AnimationFactory#fromFrames(java.util.List, int)}.
     * </p>
     *
     * @param titleAnimation the animation to play in the title, must not be null
     * @throws IllegalStateException if called from a non-main thread
     * @deprecated As of 1.1.0, animations now produce Components. Use the same method but create your
     *             animation with {@link com.dripps.scorefx.api.animation.AnimationFactory#fromComponents(java.util.List, int)}.
     *             This deprecation is only to signal the API shift; the method signature remains unchanged.
     */
    @Deprecated(since = "1.1.0", forRemoval = false)
    void setAnimatedTitle(@NotNull Animation titleAnimation);
    
    /**
     * Sets the text for a specific row on the scoreboard using an Adventure Component.
     * <p>
     * This is the modern, preferred method for setting line content. Components support full RGB colors,
     * text gradients, formatting, and other Adventure features.
     * </p>
     * <p>
     * Example usage with RGB colors:
     * <pre>{@code
     * board.setLine(5, Component.text("Player: ", NamedTextColor.YELLOW)
     *     .append(Component.text("Steve", TextColor.color(0x55FF55))));
     * }</pre>
     * </p>
     * <p>
     * Rows are numbered 1-15, with row 1 appearing at the bottom of the scoreboard and row 15
     * at the top. Calling this method replaces any previously set static or animated content
     * on this row.
     * </p>
     * <p>
     * <strong>Note:</strong> PlaceholderAPI placeholders are not currently supported in Components.
     * Use the legacy String-based method if you need placeholder support.
     * </p>
     *
     * @param row the row number (1-15), must be within valid range
     * @param text the component to display on this row, must not be null
     * @throws IllegalArgumentException if row is not between 1 and 15
     * @throws IllegalStateException if called from a non-main thread
     * @since 1.1.0
     */
    void setLine(int row, @NotNull Component text);
    
    /**
     * Sets the text for a specific row on the scoreboard using legacy formatting.
     * <p>
     * The text supports Minecraft color codes (using § or &amp;), hex colors via #RRGGBB format,
     * and PlaceholderAPI placeholders if available. If the text contains placeholders, it will
     * be updated every tick by default.
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
     * @deprecated As of 1.1.0, use {@link #setLine(int, Component)} for full Adventure support and modern text features.
     *             This method remains fully functional for backward compatibility.
     */
    @Deprecated(since = "1.1.0", forRemoval = false)
    void setLine(int row, @NotNull String text);
    
    /**
     * Sets the text for a specific row on the scoreboard using an Adventure Component with a custom update interval.
     * <p>
     * This method allows you to control how frequently the visual representation is refreshed.
     * While Components don't currently support PlaceholderAPI, this interval could be useful
     * for future features or custom update logic.
     * </p>
     * <p>
     * Rows are numbered 1-15, with row 1 appearing at the bottom of the scoreboard and row 15
     * at the top. Calling this method replaces any previously set static or animated content
     * on this row.
     * </p>
     *
     * @param row the row number (1-15), must be within valid range
     * @param text the component to display on this row, must not be null
     * @param updateIntervalTicks the interval in server ticks between updates, must be positive
     * @throws IllegalArgumentException if row is not between 1 and 15, or if updateIntervalTicks is less than 1
     * @throws IllegalStateException if called from a non-main thread
     * @since 1.1.0
     */
    void setLine(int row, @NotNull Component text, int updateIntervalTicks);
    
    /**
     * Sets the text for a specific row with a custom update interval using legacy formatting.
     * <p>
     * This method is useful for lines that contain performance-intensive placeholders
     * or data that doesn't need to update every tick. The text will only be re-evaluated
     * and updated every {@code updateIntervalTicks} server ticks.
     * </p>
     * <p>
     * The text supports Minecraft color codes (using § or &amp;), hex colors via #RRGGBB format,
     * and PlaceholderAPI placeholders if available.
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
     * @deprecated As of 1.1.0, use {@link #setLine(int, Component, int)} for full Adventure support and modern text features.
     *             This method remains fully functional for backward compatibility.
     */
    @Deprecated(since = "1.1.0", forRemoval = false)
    void setLine(int row, @NotNull String text, int updateIntervalTicks);
    
    /**
     * Sets a row to display an animation.
     * <p>
     * The animation will cycle through its frames at the interval specified by the
     * animation itself. Each frame is a Component supporting full RGB colors and formatting.
     * </p>
     * <p>
     * Rows are numbered 1-15, with row 1 appearing at the bottom of the scoreboard and row 15
     * at the top. Calling this method replaces any previously set static or animated content
     * on this row.
     * </p>
     * <p>
     * <strong>Note:</strong> PlaceholderAPI placeholders are not currently supported in Component-based
     * animations. For placeholder support in animations, use legacy String-based animations created
     * with {@link com.dripps.scorefx.api.animation.AnimationFactory#fromFrames(java.util.List, int)}.
     * </p>
     *
     * @param row the row number (1-15), must be within valid range
     * @param animation the animation to play on this row, must not be null
     * @throws IllegalArgumentException if row is not between 1 and 15
     * @throws IllegalStateException if called from a non-main thread
     * @deprecated As of 1.1.0, animations now produce Components. Use the same method but create your
     *             animation with {@link com.dripps.scorefx.api.animation.AnimationFactory#fromComponents(java.util.List, int)}.
     *             This deprecation is only to signal the API shift; the method signature remains unchanged.
     */
    @Deprecated(since = "1.1.0", forRemoval = false)
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
