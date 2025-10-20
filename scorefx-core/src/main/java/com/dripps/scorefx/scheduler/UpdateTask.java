package com.dripps.scorefx.scheduler;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents a scheduled update task for the Heartbeat scheduler.
 * Encapsulates when and what to update on a player's scoreboard. Ordered by
 * scheduled execution tick (earlier runs first).
 *
 * As of 1.1.0, textObject can be a String (legacy placeholder updates) or other types
 * (typically unused for animations) to support an Adventure-first design.
 *
 * @param type the type of update to perform
 * @param boardId the UUID of the player who owns the board
 * @param executionTick the server tick at which this task should execute
 * @param row the row number for line updates (1-15), or -1 for title updates
 * @param textObject the text object to process - String for placeholder updates, or empty string for animations
 * @param animationId optional animation identifier for animated content, or null
 * @param intervalTicks the interval in ticks before this task should repeat (0 for one-time tasks)
 * @since 1.0 (textObject changed from String text in 1.1.0)
 */
public record UpdateTask(
    @NotNull TaskType type,
    @NotNull UUID boardId,
    long executionTick,
    int row,
    @NotNull Object textObject,
    String animationId,
    int intervalTicks
) implements Comparable<UpdateTask> {
    
    /** Compact constructor for validation. */
    public UpdateTask {
        if (type == null) {
            throw new IllegalArgumentException("Task type cannot be null");
        }
        if (boardId == null) {
            throw new IllegalArgumentException("Board ID cannot be null");
        }
        if (textObject == null) {
            textObject = "";
        }
        if (intervalTicks < 0) {
            throw new IllegalArgumentException("Interval ticks cannot be negative");
        }
    }
    
    /** Compare by execution tick for priority ordering. */
    @Override
    public int compareTo(@NotNull UpdateTask other) {
        return Long.compare(this.executionTick, other.executionTick);
    }
    
    /** Reschedule this recurring task for currentTick + intervalTicks. */
    @NotNull
    public UpdateTask reschedule(long currentTick) {
        if (intervalTicks == 0) {
            throw new IllegalStateException("Cannot reschedule a one-time task");
        }
        
        return new UpdateTask(
            type,
            boardId,
            currentTick + intervalTicks,
            row,
            textObject,
            animationId,
            intervalTicks
        );
    }
    
    /** Returns true if intervalTicks > 0 (recurring). */
    public boolean isRecurring() {
        return intervalTicks > 0;
    }
    
    /** The type of update operation this task performs. */
    public enum TaskType {
        /** Update a static line with text (may contain placeholders). */
        LINE_UPDATE,
        
        /** Advance an animated line to its next frame. */
        LINE_ANIMATION,
        
        /** Update the title with static text (may contain placeholders). */
        TITLE_UPDATE,
        
        /** Advance the title animation to its next frame. */
        TITLE_ANIMATION
    }
}
