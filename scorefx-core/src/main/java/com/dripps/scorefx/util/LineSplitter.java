package com.dripps.scorefx.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for splitting scoreboard line text between team prefix and suffix.
 * <p>
 * Modern Minecraft allows up to 16384 characters in team prefixes and suffixes,
 * but we must intelligently split the text to ensure color codes are preserved
 * across the boundary and the total length doesn't exceed limits.
 * </p>
 * <p>
 * This class handles:
 * <ul>
 *   <li>Splitting text at the optimal point</li>
 *   <li>Preserving color codes across prefix/suffix boundaries</li>
 *   <li>Handling legacy color codes (§ and &amp;)</li>
 * </ul>
 * </p>
 */
public final class LineSplitter {
    
    // Modern Minecraft has much larger limits, but we'll be conservative
    private static final int MAX_PREFIX_LENGTH = 128;
    private static final int MAX_SUFFIX_LENGTH = 128;
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = 
        LegacyComponentSerializer.legacyAmpersand();
    
    /**
     * Private constructor to prevent instantiation.
     */
    private LineSplitter() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /**
     * Splits the given text into a prefix and suffix for a team.
     * <p>
     * The text will be split intelligently to fit within the prefix/suffix limits
     * while preserving color codes and formatting across the boundary.
     * </p>
     *
     * @param text the text to split, must not be null
     * @return a SplitResult containing the prefix and suffix
     */
    @NotNull
    public static SplitResult split(@NotNull String text) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        
        // Convert & color codes to § for consistency
        String normalized = text.replace('&', '§');
        
        // If the text fits entirely in the prefix, use it there
        if (normalized.length() <= MAX_PREFIX_LENGTH) {
            return new SplitResult(normalized, "");
        }
        
        // Split the text - we need to be careful about color codes
        String prefix = normalized.substring(0, Math.min(MAX_PREFIX_LENGTH, normalized.length()));
        String suffix = normalized.length() > MAX_PREFIX_LENGTH 
            ? normalized.substring(MAX_PREFIX_LENGTH) 
            : "";
        
        // Extract the last color/format codes from the prefix to prepend to suffix
        String carryOverFormatting = extractLastFormatting(prefix);
        
        // Ensure suffix doesn't exceed its limit
        if (suffix.length() > MAX_SUFFIX_LENGTH) {
            suffix = suffix.substring(0, MAX_SUFFIX_LENGTH);
        }
        
        // Prepend the carried-over formatting to the suffix
        if (!carryOverFormatting.isEmpty() && !suffix.isEmpty()) {
            suffix = carryOverFormatting + suffix;
            // Re-check suffix length after adding formatting
            if (suffix.length() > MAX_SUFFIX_LENGTH) {
                suffix = suffix.substring(0, MAX_SUFFIX_LENGTH);
            }
        }
        
        return new SplitResult(prefix, suffix);
    }
    
    /**
     * Extracts the last color and formatting codes from the given text.
     * <p>
     * This ensures that formatting (color, bold, italic, etc.) carries over
     * from the prefix to the suffix seamlessly.
     * </p>
     *
     * @param text the text to extract formatting from
     * @return the last active formatting codes
     */
    @NotNull
    private static String extractLastFormatting(@NotNull String text) {
        StringBuilder lastColor = new StringBuilder();
        StringBuilder lastFormat = new StringBuilder();
        
        for (int i = 0; i < text.length() - 1; i++) {
            if (text.charAt(i) == '§') {
                char code = text.charAt(i + 1);
                
                // Color codes (0-9, a-f) reset formatting
                if ((code >= '0' && code <= '9') || (code >= 'a' && code <= 'f')) {
                    lastColor.setLength(0);
                    lastColor.append('§').append(code);
                    lastFormat.setLength(0); // Color codes reset formatting
                }
                // Format codes (k-o, r)
                else if (code >= 'k' && code <= 'o') {
                    lastFormat.append('§').append(code);
                }
                // Reset code (r)
                else if (code == 'r') {
                    lastColor.setLength(0);
                    lastFormat.setLength(0);
                }
            }
        }
        
        return lastColor.toString() + lastFormat.toString();
    }
    
    /**
     * Represents the result of splitting text into prefix and suffix.
     *
     * @param prefix the text to use as the team prefix
     * @param suffix the text to use as the team suffix
     */
    public record SplitResult(@NotNull String prefix, @NotNull String suffix) {
        
        /**
         * Creates a new SplitResult.
         *
         * @param prefix the prefix text, must not be null
         * @param suffix the suffix text, must not be null
         */
        public SplitResult {
            if (prefix == null) {
                prefix = "";
            }
            if (suffix == null) {
                suffix = "";
            }
        }
    }
}
