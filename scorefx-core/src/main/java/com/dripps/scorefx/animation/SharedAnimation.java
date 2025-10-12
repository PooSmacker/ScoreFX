package com.dripps.scorefx.animation;

import com.dripps.scorefx.api.animation.Animation;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A shared animation that multiple boards can reference.
 * <p>
 * Unlike {@link SimpleAnimation} where each board has its own frame counter,
 * SharedAnimation uses a single shared frame counter. When multiple boards
 * reference the same SharedAnimation instance, they all advance frames in sync
 * and the frame calculation happens only once per tick instead of once per board.
 * </p>
 * <p>
 * This class is automatically used by {@link AnimationFactoryImpl} when creating
 * animations, providing transparent performance improvements.
 * </p>
 *
 * @since 2.0.1
 */
public final class SharedAnimation implements Animation {
    
    private final List<Component> frames;
    private final int intervalTicks;
    
    // Shared frame counter - all boards using this animation see the same frame
    private final AtomicInteger currentFrameIndex;
    
    // Track how many boards are using this animation (for cleanup)
    private final AtomicInteger referenceCount;
    
    /**
     * Creates a new SharedAnimation from Component frames.
     *
     * @param frames the list of Component frames to cycle through
     * @param intervalTicks the interval in ticks between frame updates
     */
    public SharedAnimation(@NotNull List<Component> frames, int intervalTicks) {
        this.frames = List.copyOf(frames);
        this.intervalTicks = intervalTicks;
        this.currentFrameIndex = new AtomicInteger(0);
        this.referenceCount = new AtomicInteger(0);
    }
    
    @NotNull
    @Override
    public Component nextFrame() {
        // Advance to next frame (wraps around to 0 when reaching the end)
        int nextIndex = currentFrameIndex.getAndUpdate(i -> (i + 1) % frames.size());
        return frames.get(nextIndex);
    }
    
    @Override
    public int getIntervalTicks() {
        return intervalTicks;
    }
    
    /**
     * Increments the reference count for this animation.
     * Called when a board starts using this animation.
     */
    public void addReference() {
        referenceCount.incrementAndGet();
    }
    
    /**
     * Decrements the reference count for this animation.
     * Called when a board stops using this animation.
     *
     * @return true if there are no more references (animation can be cleaned up)
     */
    public boolean removeReference() {
        return referenceCount.decrementAndGet() <= 0;
    }
    
    /**
     * Checks if this animation has any active references.
     *
     * @return true if at least one board is using this animation
     */
    public boolean hasSubscribers() {
        return referenceCount.get() > 0;
    }
    
    /**
     * Gets the number of boards using this animation.
     *
     * @return the reference count
     */
    public int getSubscriberCount() {
        return referenceCount.get();
    }
}
