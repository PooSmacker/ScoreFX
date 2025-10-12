package com.dripps.scorefx.animation;

import com.dripps.scorefx.api.animation.Animation;
import com.dripps.scorefx.api.animation.AnimationFactory;
import com.dripps.scorefx.util.LegacySupport;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
 *
 * @since 1.0
 */
public final class AnimationFactoryImpl implements AnimationFactory {
    
    /**
     * Creates a new AnimationFactoryImpl.
     */
    public AnimationFactoryImpl() {
        // No initialization required
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
        if (intervalTicks < 10) {
            throw new IllegalArgumentException("Interval ticks must be at least 10, got: " + intervalTicks);
        }
        
        return new SimpleAnimation(frames, intervalTicks);
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
        if (intervalTicks < 10) {
            throw new IllegalArgumentException("Interval ticks must be at least 10, got: " + intervalTicks);
        }
        
        // Convert legacy String frames to Components using LegacySupport
        List<Component> componentFrames = frames.stream()
                .map(LegacySupport::toComponent)
                .toList();
        
        // Delegate to the Component-based method (Adventure-First)
        return fromComponents(componentFrames, intervalTicks);
    }
}
