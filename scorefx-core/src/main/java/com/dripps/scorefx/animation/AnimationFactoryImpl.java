package com.dripps.scorefx.animation;

import com.dripps.scorefx.api.animation.Animation;
import com.dripps.scorefx.api.animation.AnimationFactory;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Implementation of the {@link AnimationFactory} interface.
 * <p>
 * This factory provides methods for creating {@link Animation} instances from
 * common patterns like frame lists.
 * </p>
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
        
        return new SimpleAnimation(frames, intervalTicks);
    }
}
