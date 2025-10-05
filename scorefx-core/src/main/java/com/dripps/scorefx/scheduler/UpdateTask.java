package com.dripps.scorefx.scheduler;

import com.dripps.scorefx.board.TeamBoardImpl;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents a scheduled update task for the Heartbeat scheduler.
 * <p>
 * Each task encapsulates information about when and what to update on a player's scoreboard.
 * Tasks are ordered by their scheduled execution tick to enable efficient priority queue processing.
 * </p>
 * <p>
 * As of version 1.1.0, the {@code textObject} field can hold either a {@link String} (for legacy
 * placeholder-based updates) or any other object type (typically unused for animations). This enables
 * the Adventure-First architecture while maintaining backward compatibility with PlaceholderAPI.
 * </p>
 * <p>
 * This record implements {@link Comparable} to allow natural ordering in the priority queue,
 * with tasks scheduled for earlier ticks having higher priority.
 * </p>
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
    
    /**
     * Compact constructor for validation.
     */
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
    
    /**
     * Compares this task to another based on execution tick.
     * <p>
     * Tasks with earlier execution ticks are considered "less than" tasks with later
     * execution ticks, ensuring they are processed first in a min-heap priority queue.
     * </p>
     *
     * @param other the other task to compare to
     * @return negative if this task should execute before other, positive if after, 0 if same time
     */
    @Override
    public int compareTo(@NotNull UpdateTask other) {
        return Long.compare(this.executionTick, other.executionTick);
    }
    
    /**
     * Creates a new task scheduled for a future tick based on this task's interval.
     * <p>
     * This is used for recurring tasks (animations, placeholder updates) to reschedule
     * themselves after execution.
     * </p>
     *
     * @param currentTick the current server tick
     * @return a new UpdateTask scheduled for execution at currentTick + intervalTicks
     * @throws IllegalStateException if this task is not recurring (intervalTicks = 0)
     */
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
    
    /**
     * Checks if this task is recurring (should be rescheduled after execution).
     *
     * @return true if intervalTicks > 0, false otherwise
     */
    public boolean isRecurring() {
        return intervalTicks > 0;
    }
    
    /**
     * The type of update operation this task performs.
     */
    public enum TaskType {
        /**
         * Update a static line with text (may contain placeholders).
         */
        LINE_UPDATE,
        
        /**
         * Advance an animated line to its next frame.
         */
        LINE_ANIMATION,
        
        /**
         * Update the title with static text (may contain placeholders).
         */
        TITLE_UPDATE,
        
        /**
         * Advance the title animation to its next frame.
         */
        TITLE_ANIMATION
    }
}
