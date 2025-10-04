package com.dripps.scorefx.animation;

import com.dripps.scorefx.api.animation.Animation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A simple implementation of {@link Animation} that cycles through a list of frames.
 * <p>
 * This animation maintains an internal frame index that increments with each call to
 * {@link #nextFrame()}, wrapping around to the beginning when the end is reached.
 * </p>
 * <p>
 * This class is thread-safe through synchronization of the frame index.
 * </p>
 */
final class SimpleAnimation implements Animation {
    
    private final List<String> frames;
    private final int intervalTicks;
    private int currentFrameIndex;
    
    /**
     * Creates a new SimpleAnimation.
     *
     * @param frames the list of frames to cycle through, must not be null or empty
     * @param intervalTicks the interval in ticks between frame updates, must be positive
     */
    SimpleAnimation(@NotNull List<String> frames, int intervalTicks) {
        // Make a defensive copy to prevent external modification
        this.frames = List.copyOf(frames);
        this.intervalTicks = intervalTicks;
        this.currentFrameIndex = 0;
    }
    
    @NotNull
    @Override
    public synchronized String nextFrame() {
        String frame = frames.get(currentFrameIndex);
        
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
