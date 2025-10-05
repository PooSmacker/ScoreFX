package com.dripps.scorefx.api.animation;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A factory for creating common animation patterns.
 * <p>
 * The AnimationFactory provides convenient methods for constructing {@link Animation}
 * instances without needing to implement the interface directly. This simplifies
 * the creation of animations for scoreboard titles and lines.
 * </p>
 * <p>
 * <strong>Modern API (v1.1.0+):</strong> Prefer using {@link #fromComponents(List, int)}
 * to create animations with full Adventure Component support, enabling RGB colors,
 * gradients, and other modern text features.
 * </p>
 * <p>
 * Obtain an instance of this factory through {@link com.dripps.scorefx.api.ScoreFX#getAnimationFactory()}.
 * </p>
 *
 * @since 1.0
 */
public interface AnimationFactory {
    
    /**
     * Creates an animation that cycles through a list of Adventure Components.
     * <p>
     * This is the modern, preferred method for creating animations. Components support
     * full RGB colors, text gradients, formatting, and other Adventure features.
     * </p>
     * <p>
     * The animation will loop through the provided Component frames in order, displaying each
     * frame for the specified interval before advancing to the next. When the last
     * frame is reached, the animation loops back to the first frame.
     * </p>
     * <p>
     * Example usage with RGB colors:
     * <pre>{@code
     * Animation animation = factory.fromComponents(
     *     List.of(
     *         Component.text("Frame 1", NamedTextColor.GOLD),
     *         Component.text("Frame 2", TextColor.color(0xFF5733)),
     *         Component.text("Frame 3", NamedTextColor.RED)
     *     ),
     *     20  // Each frame displays for 1 second
     * );
     * }</pre>
     * </p>
     * <p>
     * <strong>Note:</strong> PlaceholderAPI placeholders are not currently supported in
     * Component-based animations. Use {@link #fromFrames(List, int)} if you need placeholder support.
     * </p>
     *
     * @param frames the list of Components to cycle through, must not be null or empty
     * @param intervalTicks the delay in server ticks between each frame, must be positive
     * @return a new Animation instance that cycles through the provided Component frames
     * @throws IllegalArgumentException if frames is null, empty, or if intervalTicks is less than 1
     * @since 1.1.0
     */
    @NotNull
    Animation fromComponents(@NotNull List<Component> frames, int intervalTicks);
    
    /**
     * Creates a simple animation that cycles through a list of legacy String frames.
     * <p>
     * The animation will loop through the provided frames in order, displaying each
     * frame for the specified interval before advancing to the next. When the last
     * frame is reached, the animation loops back to the first frame.
     * </p>
     * <p>
     * Each frame may contain Minecraft color codes (§ or &amp;), hex colors using #RRGGBB format,
     * and PlaceholderAPI placeholders, which will be processed before display.
     * </p>
     * <p>
     * Example usage:
     * <pre>{@code
     * Animation animation = factory.fromFrames(
     *     List.of("&eFrame 1", "&#FF5733Frame 2", "&cFrame 3"),
     *     20  // Each frame displays for 1 second
     * );
     * }</pre>
     * </p>
     *
     * @param frames the list of strings to cycle through, must not be null or empty
     * @param intervalTicks the delay in server ticks between each frame, must be positive
     * @return a new Animation instance that cycles through the provided frames
     * @throws IllegalArgumentException if frames is null, empty, or if intervalTicks is less than 1
     * @deprecated As of 1.1.0, use {@link #fromComponents(List, int)} for full Adventure support and modern text features.
     *             This method remains fully functional for backward compatibility and is the only option for PlaceholderAPI support.
     */
    @Deprecated(since = "1.1.0", forRemoval = false)
    @NotNull
    Animation fromFrames(@NotNull List<String> frames, int intervalTicks);
}
