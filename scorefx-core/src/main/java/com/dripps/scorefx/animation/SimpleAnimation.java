package com.dripps.scorefx.animation;

import com.dripps.scorefx.api.animation.Animation;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A simple implementation of {@link Animation} that cycles through a list of Component frames.
 * <p>
 * This animation maintains an internal frame index that increments with each call to
 * {@link #nextFrame()}, wrapping around to the beginning when the end is reached.
 * </p>
 * <p>
 * As of version 1.1.0, this class is Component-based (Adventure-First). Legacy String-based
 * animations are automatically converted to Components during factory creation.
 * </p>
 * <p>
 * This class is thread-safe through synchronization of the frame index.
 * </p>
 *
 * @since 1.0
 */
final class SimpleAnimation implements Animation {
    
    private final List<Component> frames;
    private final int intervalTicks;
    private int currentFrameIndex;
    
    /**
     * Creates a new SimpleAnimation from Component frames.
     *
     * @param frames the list of Component frames to cycle through, must not be null or empty
     * @param intervalTicks the interval in ticks between frame updates, must be positive
     * @since 1.1.0 (changed from List&lt;String&gt; to List&lt;Component&gt;)
     */
    SimpleAnimation(@NotNull List<Component> frames, int intervalTicks) {
        // Make a defensive copy to prevent external modification
        this.frames = List.copyOf(frames);
        this.intervalTicks = intervalTicks;
        this.currentFrameIndex = 0;
    }
    
    @NotNull
    @Override
    public synchronized Component nextFrame() {
        Component frame = frames.get(currentFrameIndex);
        
        // Advance to next frame, wrapping around at the end
        currentFrameIndex = (currentFrameIndex + 1) % frames.size();
        
        return frame;
    }
    
    @Override
    public int getIntervalTicks() {
        return intervalTicks;
    }
    
    /**
     * Gets the current frame index.
     * <p>
     * This is primarily useful for testing and debugging.
     * </p>
     *
     * @return the current frame index (0-based)
     */
    synchronized int getCurrentFrameIndex() {
        return currentFrameIndex;
    }
    
    /**
     * Gets the total number of frames in this animation.
     *
     * @return the frame count
     */
    int getFrameCount() {
        return frames.size();
    }
}
