package com.dripps.scorefx.animation;

import com.dripps.scorefx.api.animation.Animation;
import com.dripps.scorefx.api.animation.AnimationFactory;
import com.dripps.scorefx.util.LegacySupport;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of the {@link AnimationFactory} interface.
 * <p>
 * This factory provides methods for creating {@link Animation} instances from
 * common patterns like frame lists. As of version 1.1.0, this factory is
 * Adventure-First, producing Component-based animations.
 * </p>
 * <p>
 * Legacy String-based animations are automatically converted to Components
 * via {@link LegacySupport} for backward compatibility.
 * </p>
 * <p>
 * <strong>Automatic Animation Sharing (v2.0.1):</strong> This factory automatically
 * detects identical animations (same frames + interval) and shares them across
 * multiple boards. This significantly improves performance by reducing frame
 * calculations from O(N) to O(1) when many boards use the same animation.
 * </p>
 *
 * @since 1.0
 */
public final class AnimationFactoryImpl implements AnimationFactory {
    
    // Animation cache for automatic sharing (v2.0.1)
    private final Map<AnimationKey, SharedAnimation> animationCache;
    
    /**
     * Creates a new AnimationFactoryImpl.
     */
    public AnimationFactoryImpl() {
        this.animationCache = new ConcurrentHashMap<>();
    }
    
    @NotNull
    @Override
    public Animation fromComponents(@NotNull List<Component> frames, int intervalTicks) {
        if (frames == null) {
            throw new IllegalArgumentException("Frames list cannot be null");
        }
        if (frames.isEmpty()) {
            throw new IllegalArgumentException("Frames list cannot be empty");
        }
        if (intervalTicks < 1) {
            throw new IllegalArgumentException("Interval ticks must be at least 1, got: " + intervalTicks);
        }
        
        // v2.0.1: Automatic sharing - create cache key and return shared animation
        AnimationKey key = new AnimationKey(frames, intervalTicks);
        return animationCache.computeIfAbsent(key, k -> new SharedAnimation(frames, intervalTicks));
    }
    
    @NotNull
    @Override
    public Animation fromFrames(@NotNull List<String> frames, int intervalTicks) {
        if (frames == null) {
            throw new IllegalArgumentException("Frames list cannot be null");
        }
        if (frames.isEmpty()) {
            throw new IllegalArgumentException("Frames list cannot be empty");
        }
        if (intervalTicks < 1) {
            throw new IllegalArgumentException("Interval ticks must be at least 1, got: " + intervalTicks);
        }
        
        // Convert legacy String frames to Components using LegacySupport
        List<Component> componentFrames = frames.stream()
                .map(LegacySupport::toComponent)
                .toList();
        
        // Delegate to the Component-based method (Adventure-First)
        return fromComponents(componentFrames, intervalTicks);
    }
    
    /**
     * Removes unused animations from the cache.
     * <p>
     * This method should be called when an animation is no longer in use
     * (e.g., when a board is destroyed or an animation is cancelled).
     * It checks if the animation has any active subscribers, and if not,
     * removes it from the cache to prevent memory leaks.
     * </p>
     * <p>
     * This method is thread-safe and can be called from any thread.
     * </p>
     *
     * @param animation the animation to potentially remove from cache
     * @since 2.0.1
     */
    public void cleanupAnimation(@NotNull Animation animation) {
        if (animation instanceof SharedAnimation sharedAnimation) {
            // Only remove from cache if no subscribers remain
            if (!sharedAnimation.hasSubscribers()) {
                animationCache.values().remove(sharedAnimation);
            }
        }
    }
    
    /**
     * Gets the number of cached animations.
     * <p>
     * This is useful for monitoring and debugging.
     * </p>
     *
     * @return the number of cached animations
     * @since 2.0.1
     */
    public int getCacheSize() {
        return animationCache.size();
    }
}
