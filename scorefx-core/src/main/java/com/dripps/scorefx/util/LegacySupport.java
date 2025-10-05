package com.dripps.scorefx.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for converting legacy String-based text to Adventure Components.
 * <p>
 * This class provides centralized conversion logic for transforming legacy Minecraft
 * text formatting (using '&amp;' color codes and #RRGGBB hex colors) into modern
 * Adventure Components. This enables backward compatibility while maintaining an
 * Adventure-First internal architecture.
 * </p>
 * <p>
 * The conversion supports:
 * <ul>
 *     <li>'&amp;' color codes (e.g., &amp;c for red, &amp;6 for gold)</li>
 *     <li>'&amp;' formatting codes (e.g., &amp;l for bold, &amp;o for italic)</li>
 *     <li>Hex colors using #RRGGBB format (e.g., &amp;#FF5733 for orange)</li>
 *     <li>The '&amp;r' reset code</li>
 * </ul>
 * </p>
 * <p>
 * This class is thread-safe as all methods are static and use immutable serializers.
 * </p>
 *
 * @since 1.1.0
 */
public final class LegacySupport {
    
    /**
     * The legacy component serializer configured to parse '&amp;' codes and hex colors.
     * This serializer is immutable and thread-safe.
     */
    private static final LegacyComponentSerializer SERIALIZER = 
            LegacyComponentSerializer.builder()
                    .hexColors()              // Enable #RRGGBB hex color support
                    .character('&')           // Use '&' as the formatting character
                    .build();
    
    /**
     * Private constructor to prevent instantiation.
     * This is a utility class and should only be used via static methods.
     */
    private LegacySupport() {
        throw new UnsupportedOperationException("LegacySupport is a utility class and cannot be instantiated");
    }
    
    /**
     * Converts a legacy String with formatting codes to an Adventure Component.
     * <p>
     * This method transforms legacy Minecraft text (with '&amp;' color codes and #RRGGBB hex colors)
     * into a modern Adventure Component. This is the primary method for converting all legacy
     * text input from the deprecated API methods.
     * </p>
     * <p>
     * Examples:
     * <pre>{@code
     * Component c1 = LegacySupport.toComponent("&6&lGold Bold");
     * Component c2 = LegacySupport.toComponent("&#FF5733Custom RGB Color");
     * Component c3 = LegacySupport.toComponent("&aNormal &r&cReset to Red");
     * }</pre>
     * </p>
     * <p>
     * <strong>Thread Safety:</strong> This method is thread-safe and can be called from any thread.
     * </p>
     *
     * @param text the legacy string to convert, must not be null
     * @return an Adventure Component representing the formatted text, never null
     * @throws NullPointerException if text is null
     */
    @NotNull
    public static Component toComponent(@NotNull String text) {
        return SERIALIZER.deserialize(text);
    }
}
