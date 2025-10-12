package com.dripps.scorefx.board;

import com.dripps.scorefx.api.Board;
import com.dripps.scorefx.api.animation.Animation;
import com.dripps.scorefx.hook.PAPIHook;
import com.dripps.scorefx.scheduler.Heartbeat;
import com.dripps.scorefx.scheduler.UpdateTask;
import com.dripps.scorefx.util.ComponentLineSplitter;
import com.dripps.scorefx.util.LegacySupport;
import com.dripps.scorefx.util.LineSplitter;
import com.dripps.scorefx.util.PacketHelper;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of the {@link Board} interface using the Team-based rendering approach.
 * <p>
 * This implementation creates a dedicated Bukkit {@link Scoreboard} for each player with:
 * <ul>
 *   <li>A single {@link Objective} displayed on the sidebar</li>
 *   <li>16 {@link Team} objects, one for each potential scoreboard line (rows 1-15 + 1 extra)</li>
 *   <li>Flicker-free updates via team prefix/suffix modification</li>
 * </ul>
 * </p>
 * <p>
 * <strong>Adventure-First Architecture (v1.1.0+):</strong> Internally, all text is processed
 * as Adventure Components. Legacy String-based methods are converted to Components via
 * {@link LegacySupport} at the API boundary.
 * </p>
 * <p>
 * <strong>Thread Safety:</strong> All methods that modify state must be called from the main thread.
 * </p>
 *
 * @since 1.0
 */
public final class TeamBoardImpl implements Board {

    private static final int MAX_LINES = 15;
    public static final String OBJECTIVE_NAME = "scorefx_board";
    private static final String TEAM_PREFIX = "sfx_line_";
    private static final int TITLE_ROW = -1; // Special row number for title
    
    private final Player player;
    private final Scoreboard scoreboard;
    private final Objective objective;
    private final Map<Integer, Team> teams;
    private final Map<Integer, String> entries; // Stores the entry string for each line
    private final Heartbeat heartbeat;
    
    // Animation tracking
    private final Map<Integer, Animation> activeAnimations; // row -> animation (or TITLE_ROW for title)
    private Animation titleAnimation;
    
    // Custom score tracking (v1.2.0)
    private final Map<Integer, Component> customScores; // row -> custom score component
    
    /**
     * Creates a new TeamBoardImpl for the specified player.
     * <p>
     * This constructor initializes the Bukkit scoreboard, creates the objective,
     * and sets up all 16 teams for potential use.
     * </p>
     *
     * @param player the player who owns this board, must not be null
     * @param heartbeat the Heartbeat scheduler for task scheduling, must not be null
     */
    public TeamBoardImpl(@NotNull Player player, @NotNull Heartbeat heartbeat) {
        this.player = player;
        this.heartbeat = heartbeat;
        this.teams = new HashMap<>();
        this.entries = new HashMap<>();
        this.activeAnimations = new HashMap<>();
        this.titleAnimation = null;
        this.customScores = new ConcurrentHashMap<>();
        
        // Create a new scoreboard for this player
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        
        // Create the objective for the sidebar
        this.objective = scoreboard.registerNewObjective(
            OBJECTIVE_NAME,
            "dummy"
        );
        this.objective.displayName(Component.empty());

        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        
        // Pre-create all 16 teams (for lines 0-15, where 0 is unused but reserved)
        for (int i = 0; i <= MAX_LINES; i++) {
            Team team = scoreboard.registerNewTeam(TEAM_PREFIX + i);
            teams.put(i, team);
            
            // Create a unique entry for this line using color codes
            // We use invisible characters to make each entry unique
            String entry = generateEntry(i);
            entries.put(i, entry);
            team.addEntry(entry);
        }
        
        // Set the player's scoreboard
        player.setScoreboard(scoreboard);
    }
    
    /**
     * Generates a unique entry string for a scoreboard line.
     * <p>
     * Each line needs a unique entry (the actual score holder). We use
     * invisible/formatting codes to create unique strings that won't be visible.
     * </p>
     *
     * @param lineNumber the line number (0-15)
     * @return a unique entry string
     */
    @NotNull
    private String generateEntry(int lineNumber) {
        // Use § codes to create invisible unique entries
        // §r resets formatting, and we can chain invisible characters
        return "§" + Integer.toHexString(lineNumber) + "§r";
    }
    
    @Override
    public void setTitle(@NotNull Component title) {
        checkMainThread();
        
        if (title == null) {
            throw new IllegalArgumentException("Title cannot be null");
        }
        
        // Cancel any existing title animation
        cancelTitleAnimation();
        
        // Set the title directly using the Component
        objective.displayName(title);
        
        // Note: PlaceholderAPI is not supported for Component-based content
        // If placeholders are needed, use the legacy String-based method
    }
    
    @Override
    public void setTitle(@NotNull String title) {
        checkMainThread();
        
        if (title == null) {
            throw new IllegalArgumentException("Title cannot be null");
        }
        
        // Cancel any existing title animation
        cancelTitleAnimation();
        
        // Convert legacy String to Component (Adventure-First)
        Component titleComponent = LegacySupport.toComponent(title);
        
        // Set the title using the Component method
        objective.displayName(titleComponent);
        
        // If the title contains placeholders, schedule periodic updates
        if (PAPIHook.hasPlaceholders(title)) {
            scheduleRecurringTitleUpdate(title, 20); // Update every second
        }
    }
    
    @Override
    public void setAnimatedTitle(@NotNull Animation titleAnimation) {
        checkMainThread();
        
        if (titleAnimation == null) {
            throw new IllegalArgumentException("Title animation cannot be null");
        }
        
        // Cancel any existing title animation
        cancelTitleAnimation();
        
        // Store the animation
        this.titleAnimation = titleAnimation;
        this.activeAnimations.put(TITLE_ROW, titleAnimation);
        
        // Schedule the animation task with the Heartbeat
        UpdateTask task = new UpdateTask(
            UpdateTask.TaskType.TITLE_ANIMATION,
            player.getUniqueId(),
            heartbeat.getCurrentTick() + titleAnimation.getIntervalTicks(),
            TITLE_ROW,
            "", // Text not used for animations (animation provides frames)
            generateAnimationId(TITLE_ROW),
            titleAnimation.getIntervalTicks()
        );
        
        heartbeat.scheduleTask(task);
        
        // Immediately show the first frame (now returns Component)
        Component firstFrame = titleAnimation.nextFrame();
        updateTitleDirect(firstFrame);
        
        // Re-register the animation since updateTitleDirect doesn't cancel it
        // (but we need to ensure it's tracked)
        this.titleAnimation = titleAnimation;
        this.activeAnimations.put(TITLE_ROW, titleAnimation);
    }
    
    @Override
    public void setLine(int row, @NotNull Component text) {
        setLine(row, text, 20); // Update every second
    }
    
    @Override
    public void setLine(int row, @NotNull Component text, int updateIntervalTicks) {
        checkMainThread();
        validateRow(row);
        
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        
        if (updateIntervalTicks < 1) {
            throw new IllegalArgumentException("Update interval must be at least 1 tick");
        }
        
        // Cancel any existing animation for this row
        cancelLineAnimation(row);
        
        // Get the team for this row
        Team team = teams.get(row);
        if (team == null) {
            throw new IllegalStateException("Team for row " + row + " not found");
        }
        
        // Split the Component into prefix and suffix using ComponentLineSplitter
        ComponentLineSplitter.SplitResult split = ComponentLineSplitter.split(text);
        
        // Update the team's prefix and suffix (flicker-free!) using Component API
        team.prefix(split.prefix());
        team.suffix(split.suffix());
        
        // Send the score packet directly using PacketHelper (v2.0)
        String entry = entries.get(row);
        Component customScore = customScores.get(row); // null = hidden score (default)
        PacketHelper.sendScorePacket(player, OBJECTIVE_NAME, entry, row, customScore);
        
        // Note: PlaceholderAPI is not supported for Component-based content
        // If placeholders are needed, use the legacy String-based method
    }
    
    @Override
    public void setLine(int row, @NotNull String text) {
        setLine(row, text, 20); // Update every second
    }
    
    @Override
    public void setLine(int row, @NotNull String text, int updateIntervalTicks) {
        checkMainThread();
        validateRow(row);
        
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        
        if (updateIntervalTicks < 1) {
            throw new IllegalArgumentException("Update interval must be at least 1 tick");
        }
        
        // Cancel any existing animation for this row
        cancelLineAnimation(row);
        
        // Convert legacy String to Component (Adventure-First)
        Component textComponent = LegacySupport.toComponent(text);
        
        // Get the team for this row
        Team team = teams.get(row);
        if (team == null) {
            throw new IllegalStateException("Team for row " + row + " not found");
        }
        
        // Split the Component into prefix and suffix
        ComponentLineSplitter.SplitResult split = ComponentLineSplitter.split(textComponent);
        
        // Update the team's prefix and suffix (flicker-free!)
        team.prefix(split.prefix());
        team.suffix(split.suffix());
        
        // Send the score packet directly using PacketHelper (v2.0)
        String entry = entries.get(row);
        Component customScore = customScores.get(row); // null = hidden score (default)
        PacketHelper.sendScorePacket(player, OBJECTIVE_NAME, entry, row, customScore);
        
        // If the text contains placeholders, schedule periodic updates
        if (PAPIHook.hasPlaceholders(text)) {
            scheduleRecurringLineUpdate(row, text, updateIntervalTicks);
        }
    }
    
    @Override
    public void setAnimatedLine(int row, @NotNull Animation animation) {
        checkMainThread();
        validateRow(row);
        
        if (animation == null) {
            throw new IllegalArgumentException("Animation cannot be null");
        }
        
        // Cancel any existing animation for this row
        cancelLineAnimation(row);
        
        // Store the animation
        activeAnimations.put(row, animation);
        
        // Schedule the animation task with the Heartbeat
        UpdateTask task = new UpdateTask(
            UpdateTask.TaskType.LINE_ANIMATION,
            player.getUniqueId(),
            heartbeat.getCurrentTick() + animation.getIntervalTicks(),
            row,
            "", // Text not used for animations (animation provides frames)
            generateAnimationId(row),
            animation.getIntervalTicks()
        );
        
        heartbeat.scheduleTask(task);
        
        // Immediately show the first frame (now returns Component)
        Component firstFrame = animation.nextFrame();
        updateLineDirect(row, firstFrame);
        
        // Re-register the animation since updateLineDirect doesn't cancel it
        activeAnimations.put(row, animation);
    }
    
    @Override
    public void removeLine(int row) {
        checkMainThread();
        validateRow(row);
        
        // Cancel any animation for this row
        cancelLineAnimation(row);
        
        // Remove custom score tracking
        customScores.remove(row);
        
        // Send remove packet using PacketHelper (v2.0)
        String entry = entries.get(row);
        PacketHelper.sendRemoveScorePacket(player, OBJECTIVE_NAME, entry);
        
        // Clear the team's prefix and suffix
        Team team = teams.get(row);
        if (team != null) {
            team.prefix(net.kyori.adventure.text.Component.empty());
            team.suffix(net.kyori.adventure.text.Component.empty());
        }
    }
    
    @NotNull
    @Override
    public Player getPlayer() {
        return player;
    }
    
    /**
     * Cleans up this board and all its resources.
     * <p>
     * This method unregisters all teams, the objective, and resets the player's
     * scoreboard to the server default. It also cancels all scheduled tasks and animations.
     * </p>
     */
    public void destroy() {
        checkMainThread();
        
        // Cancel all animations (this will be handled by Heartbeat.unregisterBoard)
        // but we clear our local references
        activeAnimations.clear();
        titleAnimation = null;
        
        // Unregister all teams
        for (Team team : teams.values()) {
            try {
                team.unregister();
            } catch (IllegalStateException ignored) {
                // Team already unregistered
            }
        }
        teams.clear();
        
        // Unregister the objective
        try {
            objective.unregister();
        } catch (IllegalStateException ignored) {
            // Objective already unregistered
        }
        
        // Reset player's scoreboard to server default
        if (player.isOnline()) {
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
        
        entries.clear();
    }
    
    /**
     * Gets the active animation for a specific row.
     * <p>
     * This is used by the Heartbeat to advance animation frames.
     * </p>
     *
     * @param row the row number, or TITLE_ROW for the title
     * @return the animation, or null if no animation is active for this row
     */
    @Nullable
    public Animation getAnimation(int row) {
        return activeAnimations.get(row);
    }
    
    /**
     * Updates the title to display the given Component directly (bypassing setTitle to avoid canceling animations).
     * <p>
     * This is an internal method used by the Heartbeat to update animated titles and scheduled title updates.
     * </p>
     *
     * @param component the Component to display as the title
     * @since 1.1.0 (changed from String to Component)
     */
    public void updateTitleDirect(@NotNull Component component) {
        objective.displayName(component);
    }
    
    /**
     * Updates a line to display the given Component directly (bypassing setLine to avoid canceling animations).
     * <p>
     * This is an internal method used by the Heartbeat to update animated lines and scheduled line updates.
     * </p>
     *
     * @param row the row number
     * @param component the Component to display
     * @since 1.1.0 (changed from String to Component)
     */
    public void updateLineDirect(int row, @NotNull Component component) {
        Team team = teams.get(row);
        if (team == null) {
            return;
        }
        
        // Split the Component into prefix and suffix using ComponentLineSplitter
        ComponentLineSplitter.SplitResult split = ComponentLineSplitter.split(component);
        
        // Update the team's prefix and suffix using Component API
        team.prefix(split.prefix());
        team.suffix(split.suffix());
        
        // Send the score packet directly using PacketHelper (v2.0)
        String entry = entries.get(row);
        Component customScore = customScores.get(row); // null = hidden score (default)
        PacketHelper.sendScorePacket(player, OBJECTIVE_NAME, entry, row, customScore);
    }
    
    /**
     * Cancels the title animation if one is active.
     */
    private void cancelTitleAnimation() {
        if (titleAnimation != null) {
            activeAnimations.remove(TITLE_ROW);
            titleAnimation = null;
            // Task cancellation is handled by the next animation task or board removal
        }
    }
    
    /**
     * Cancels any animation for the specified row.
     *
     * @param row the row number
     */
    private void cancelLineAnimation(int row) {
        activeAnimations.remove(row);
        // Task cancellation is handled by the next animation task or board removal
    }
    
    /**
     * Generates a unique animation ID for tracking.
     *
     * @param row the row number (or TITLE_ROW for title)
     * @return a unique animation ID string
     */
    @NotNull
    private String generateAnimationId(int row) {
        return "anim_" + player.getUniqueId() + "_" + row + "_" + System.currentTimeMillis();
    }
    
    /**
     * Schedules a recurring title update task for placeholder replacement.
     *
     * @param text the text containing placeholders
     * @param intervalTicks the interval in ticks between updates
     */
    private void scheduleRecurringTitleUpdate(@NotNull String text, int intervalTicks) {
        UpdateTask task = new UpdateTask(
            UpdateTask.TaskType.TITLE_UPDATE,
            player.getUniqueId(),
            heartbeat.getCurrentTick() + intervalTicks,
            TITLE_ROW,
            text,
            null, // No animation ID for placeholder updates
            intervalTicks
        );
        
        heartbeat.scheduleTask(task);
    }
    
    /**
     * Schedules a recurring line update task for placeholder replacement.
     *
     * @param row the row number
     * @param text the text containing placeholders
     * @param intervalTicks the interval in ticks between updates
     */
    private void scheduleRecurringLineUpdate(int row, @NotNull String text, int intervalTicks) {
        UpdateTask task = new UpdateTask(
            UpdateTask.TaskType.LINE_UPDATE,
            player.getUniqueId(),
            heartbeat.getCurrentTick() + intervalTicks,
            row,
            text,
            null, // No animation ID for placeholder updates
            intervalTicks
        );
        
        heartbeat.scheduleTask(task);
    }
    
    /**
     * Validates that the given row number is within the valid range (1-15).
     *
     * @param row the row number to validate
     * @throws IllegalArgumentException if the row is not between 1 and 15
     */
    private void validateRow(int row) {
        if (row < 1 || row > MAX_LINES) {
            throw new IllegalArgumentException(
                "Row must be between 1 and " + MAX_LINES + ", got: " + row
            );
        }
    }
    
    /**
     * Ensures that the current thread is the main server thread.
     * <p>
     * This is a critical safety check to prevent concurrent modification issues
     * and comply with the Bukkit API's threading requirements.
     * </p>
     *
     * @throws IllegalStateException if called from a non-main thread
     */
    private void checkMainThread() {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException(
                "ScoreFX API must be accessed from the main server thread. " +
                "Current thread: " + Thread.currentThread().getName()
            );
        }
    }
    
    // ==================== Custom Score API (v1.2.0 / v2.0.0) ====================
    
    @Override
    public void setLineScore(int row, @Nullable Component score) {
        checkMainThread();
        validateRow(row);
        
        if (score == null || score.equals(Component.empty())) {
            // Remove custom score (revert to hidden)
            customScores.remove(row);
        } else {
            // Set custom score
            customScores.put(row, score);
        }
        
        // Immediately send updated packet with new score format (v2.0)
        String entry = entries.get(row);
        int displayScore = row; // The numeric score value (row number for positioning)
        PacketHelper.sendScorePacket(player, OBJECTIVE_NAME, entry, displayScore, score);
    }
    
    @Override
    public void setLine(int row, @NotNull Component text, @Nullable Component score) {
        // Delegate to existing setLine for text
        setLine(row, text);
        
        // Set the custom score
        setLineScore(row, score);
    }
    
    @Deprecated(since = "1.2.0", forRemoval = false)
    @Override
    public void setLine(int row, @NotNull String text, @Nullable String score) {
        // Convert both parameters to Components
        Component textComponent = LegacySupport.toComponent(text);
        Component scoreComponent = (score != null) ? LegacySupport.toComponent(score) : null;
        
        // Delegate to Component-based method
        setLine(row, textComponent, scoreComponent);
    }
    
    /**
     * Retrieves the custom score component for a specific row.
     * <p>
     * This method is used internally to determine what custom score format to
     * display in the score slot for each line. Returns empty Optional if no
     * custom score is set (defaults to hidden/BLANK format).
     * </p>
     *
     * @param row the row number (1-15)
     * @return an Optional containing the custom score Component, or empty if no custom score is set
     * @since 1.2.0
     */
    @NotNull
    public Optional<Component> getCustomScore(int row) {
        return Optional.ofNullable(customScores.get(row));
    }
}
