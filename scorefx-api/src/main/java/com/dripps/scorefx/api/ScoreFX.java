package com.dripps.scorefx.api;

import com.dripps.scorefx.api.animation.AnimationFactory;
import org.jetbrains.annotations.NotNull;

/**
 * The main entrypoint for the ScoreFX API.
 * <p>
 * This service should be obtained from the Bukkit Services Manager using:
 * <pre>{@code
 * ScoreFX api = Bukkit.getServicesManager().load(ScoreFX.class);
 * }</pre>
 * </p>
 * <p>
 * ScoreFX provides a high-performance, per-player scoreboard system with support
 * for dynamic updates, animations, and PlaceholderAPI integration.
 * </p>
 *
 * @since 1.0
 */
public interface ScoreFX {
    
    /**
     * Gets the board manager responsible for creating and managing player scoreboards.
     * <p>
     * The BoardManager handles the lifecycle of all scoreboards and provides methods
     * to create, retrieve, and destroy player-specific scoreboards.
     * </p>
     *
     * @return the board manager instance, never null
     */
    @NotNull
    BoardManager getBoardManager();
    
    /**
     * Gets the animation factory for creating text animations.
     * <p>
     * The AnimationFactory provides convenient methods for creating common animation
     * patterns that can be applied to scoreboard titles and lines.
     * </p>
     *
     * @return the animation factory instance, never null
     */
    @NotNull
    AnimationFactory getAnimationFactory();
}
