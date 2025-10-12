package com.dripps.scorefx.animation;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * A cache key for animations based on their frames and interval.
 * <p>
 * This class is used by {@link AnimationFactoryImpl} to automatically share
 * identical animations across multiple boards, reducing memory usage and
 * improving performance.
 * </p>
 * <p>
 * Two AnimationKey objects are considered equal if they have the same frames
 * (in the same order) and the same interval.
 * </p>
 *
 * @since 2.0.1
 */
final class AnimationKey {
    
    private final List<Component> frames;
    private final int intervalTicks;
    private final int hashCode;
    
    /**
     * Creates a new AnimationKey.
     *
     * @param frames the animation frames
     * @param intervalTicks the interval between frames in ticks
     */
    AnimationKey(@NotNull List<Component> frames, int intervalTicks) {
        // Store as immutable list to prevent external modification
        this.frames = List.copyOf(frames);
        this.intervalTicks = intervalTicks;
        // Pre-compute hash code for performance
        this.hashCode = Objects.hash(this.frames, intervalTicks);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AnimationKey other = (AnimationKey) obj;
        return intervalTicks == other.intervalTicks 
            && frames.equals(other.frames);
    }
    
    @Override
    public int hashCode() {
        return hashCode;
    }
    
    @Override
    public String toString() {
        return "AnimationKey{frames=" + frames.size() + ", interval=" + intervalTicks + "}";
    }
}
