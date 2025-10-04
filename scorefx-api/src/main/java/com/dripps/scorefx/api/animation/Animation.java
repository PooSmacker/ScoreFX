package com.dripps.scorefx.api.animation;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a sequence of text frames that can be displayed in succession.
 * <p>
 * An Animation defines a series of text strings (frames) that cycle in order,
 * creating an animated effect on the scoreboard. Each frame is displayed for
 * a specified interval before advancing to the next frame.
 * </p>
 * <p>
 * Animations can be applied to both scoreboard titles and individual lines.
 * Each frame supports Minecraft color codes and PlaceholderAPI placeholders.
 * </p>
 * <p>
 * Implementations of this interface should be stateful and track the current
 * frame index internally.
 * </p>
 *
 * @since 1.0
 */
public interface Animation {
    
    /**
     * Gets the next frame of the animation.
     * <p>
     * This method advances the internal frame counter and returns the next
     * string in the sequence. When the end of the sequence is reached, the
     * animation loops back to the first frame.
     * </p>
     * <p>
     * The returned string may contain Minecraft color codes (§ or &amp;) and
     * PlaceholderAPI placeholders, which will be processed before display.
     * </p>
     *
     * @return the next string in the animation sequence, never null
     */
    @NotNull
    String nextFrame();
    
    /**
     * Gets the interval in server ticks between frame updates.
     * <p>
     * This value determines how long each frame is displayed before advancing
     * to the next frame. A value of 20 would result in each frame being displayed
     * for 1 second (assuming the server is running at 20 TPS).
     * </p>
     *
     * @return the interval in server ticks, always positive
     */
    int getIntervalTicks();
}
