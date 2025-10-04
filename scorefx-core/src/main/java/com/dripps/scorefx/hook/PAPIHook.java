package com.dripps.scorefx.hook;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

/**
 * Hook for PlaceholderAPI integration.
 * <p>
 * This class provides safe access to PlaceholderAPI functionality, automatically
 * detecting whether PlaceholderAPI is installed and providing graceful fallback
 * when it's not available.
 * </p>
 * <p>
 * The integration is a soft dependency, meaning ScoreFX works perfectly fine
 * without PlaceholderAPI, but provides enhanced functionality when it's present.
 * </p>
 */
public final class PAPIHook {
    
    private final boolean available;
    private final Logger logger;
    
    /**
     * Creates a new PAPIHook and detects PlaceholderAPI availability.
     *
     * @param logger the logger for diagnostic messages, must not be null
     */
    public PAPIHook(@NotNull Logger logger) {
        if (logger == null) {
            throw new IllegalArgumentException("Logger cannot be null");
        }
        
        this.logger = logger;
        this.available = detectPlaceholderAPI();
        
        if (available) {
            logger.info("PlaceholderAPI detected - placeholder support enabled");
        } else {
            logger.info("PlaceholderAPI not found - placeholders will not be replaced");
        }
    }
    
    /**
     * Detects whether PlaceholderAPI is installed and enabled.
     *
     * @return true if PlaceholderAPI is available, false otherwise
     */
    private boolean detectPlaceholderAPI() {
        try {
            // Check if the plugin is loaded
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
                return false;
            }
            
            // Try to load the main PlaceholderAPI class to ensure it's functional
            Class.forName("me.clip.placeholderapi.PlaceholderAPI");
            
            return true;
        } catch (ClassNotFoundException e) {
            logger.warning("PlaceholderAPI plugin detected but main class not found: " + e.getMessage());
            return false;
        } catch (Exception e) {
            logger.warning("Error detecting PlaceholderAPI: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Replaces placeholders in the given text for the specified player.
     * <p>
     * If PlaceholderAPI is available, this method delegates to
     * {@code PlaceholderAPI.setPlaceholders(player, text)}. If PlaceholderAPI
     * is not available, the text is returned unchanged.
     * </p>
     * <p>
     * This method is safe to call from any thread, though PlaceholderAPI itself
     * may have thread safety requirements depending on the specific placeholders used.
     * </p>
     *
     * @param player the player for whom to replace placeholders, must not be null
     * @param text the text containing placeholders, must not be null
     * @return the text with placeholders replaced, or the original text if PAPI is unavailable
     */
    @NotNull
    public String setPlaceholders(@NotNull Player player, @NotNull String text) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        
        // If PlaceholderAPI is not available, return text unchanged
        if (!available) {
            return text;
        }
        
        try {
            // Use PlaceholderAPI to replace placeholders
            return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, text);
        } catch (Exception e) {
            logger.warning("Error replacing placeholders: " + e.getMessage());
            return text; // Return original text on error
        }
    }
    
    /**
     * Checks if PlaceholderAPI is available and functional.
     *
     * @return true if PlaceholderAPI is available, false otherwise
     */
    public boolean isAvailable() {
        return available;
    }
    
    /**
     * Checks if the given text contains placeholders.
     * <p>
     * This is a simple heuristic check that looks for the pattern %...%.
     * It doesn't validate that the placeholders are actually valid PlaceholderAPI
     * placeholders, just that the text might contain them.
     * </p>
     *
     * @param text the text to check, must not be null
     * @return true if the text appears to contain placeholders, false otherwise
     */
    public static boolean hasPlaceholders(@NotNull String text) {
        if (text == null) {
            return false;
        }
        
        // Simple check: does the text contain % signs that could indicate placeholders?
        int firstPercent = text.indexOf('%');
        if (firstPercent == -1) {
            return false;
        }
        
        // Check if there's a second % after the first one
        int secondPercent = text.indexOf('%', firstPercent + 1);
        return secondPercent != -1;
    }
}
