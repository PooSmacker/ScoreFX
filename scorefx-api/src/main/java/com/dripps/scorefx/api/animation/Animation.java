package com.dripps.scorefx.api.animation;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a sequence of text frames that can be displayed in succession.
 * <p>
 * An Animation defines a series of Component frames that cycle in order,
 * creating an animated effect on the scoreboard. Each frame is displayed for
 * a specified interval before advancing to the next frame.
 * </p>
 * <p>
 * <strong>Modern API (v1.1.0+):</strong> Animations now produce Adventure Components,
 * enabling full RGB colors, gradients, and other modern text features in animated content.
 * </p>
 * <p>
 * Animations can be applied to both scoreboard titles and individual lines.
 * For legacy String-based animations (created with {@link AnimationFactory#fromFrames}),
 * each frame supports Minecraft color codes and PlaceholderAPI placeholders.
 * For modern Component-based animations, PlaceholderAPI is not currently supported.
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
     * Gets the next frame of the animation as an Adventure Component.
     * <p>
     * This method advances the internal frame counter and returns the next
     * Component in the sequence. When the end of the sequence is reached, the
     * animation loops back to the first frame.
     * </p>
     * <p>
     * The returned Component may contain full RGB colors, gradients, and other
     * Adventure text features. For legacy String-based animations, the string
     * will be automatically converted to a Component with support for &amp; color
     * codes and #RRGGBB hex colors.
     * </p>
     *
     * @return the next Component in the animation sequence, never null
     * @since 1.1.0 (return type changed from String to Component)
     */
    @NotNull
    Component nextFrame();
    
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
