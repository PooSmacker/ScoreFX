package com.dripps.scorefx.api.animation;

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
 * Obtain an instance of this factory through {@link com.dripps.scorefx.api.ScoreFX#getAnimationFactory()}.
 * </p>
 *
 * @since 1.0
 */
public interface AnimationFactory {
    
    /**
     * Creates a simple animation that cycles through a list of frames.
     * <p>
     * The animation will loop through the provided frames in order, displaying each
     * frame for the specified interval before advancing to the next. When the last
     * frame is reached, the animation loops back to the first frame.
     * </p>
     * <p>
     * Each frame may contain Minecraft color codes (§ or &amp;) and PlaceholderAPI
     * placeholders, which will be processed before display.
     * </p>
     * <p>
     * Example usage:
     * <pre>{@code
     * Animation animation = factory.fromFrames(
     *     List.of("&eFrame 1", "&6Frame 2", "&cFrame 3"),
     *     20  // Each frame displays for 1 second
     * );
     * }</pre>
     * </p>
     *
     * @param frames the list of strings to cycle through, must not be null or empty
     * @param intervalTicks the delay in server ticks between each frame, must be positive
     * @return a new Animation instance that cycles through the provided frames
     * @throws IllegalArgumentException if frames is null, empty, or if intervalTicks is less than 1
     */
    @NotNull
    Animation fromFrames(@NotNull List<String> frames, int intervalTicks);
}
