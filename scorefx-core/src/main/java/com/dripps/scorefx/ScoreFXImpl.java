package com.dripps.scorefx;

import com.dripps.scorefx.api.BoardManager;
import com.dripps.scorefx.api.ScoreFX;
import com.dripps.scorefx.api.animation.AnimationFactory;
import org.jetbrains.annotations.NotNull;

/**
 * Implementation of the main {@link ScoreFX} API interface.
 * <p>
 * This class serves as the primary entrypoint for the ScoreFX API, providing access
 * to the board manager and animation factory. It is registered with the Bukkit
 * Services Manager and can be obtained via:
 * <pre>{@code
 * ScoreFX api = Bukkit.getServicesManager().load(ScoreFX.class);
 * }</pre>
 * </p>
 */
public final class ScoreFXImpl implements ScoreFX {
    
    private final BoardManager boardManager;
    private final AnimationFactory animationFactory;
    
    /**
     * Creates a new ScoreFXImpl instance.
     *
     * @param boardManager the board manager implementation, must not be null
     * @param animationFactory the animation factory implementation, must not be null
     */
    public ScoreFXImpl(@NotNull BoardManager boardManager, @NotNull AnimationFactory animationFactory) {
        if (boardManager == null) {
            throw new IllegalArgumentException("BoardManager cannot be null");
        }
        if (animationFactory == null) {
            throw new IllegalArgumentException("AnimationFactory cannot be null");
        }
        
        this.boardManager = boardManager;
        this.animationFactory = animationFactory;
    }
    
    @NotNull
    @Override
    public BoardManager getBoardManager() {
        return boardManager;
    }
    
    @NotNull
    @Override
    public AnimationFactory getAnimationFactory() {
        return animationFactory;
    }
}
