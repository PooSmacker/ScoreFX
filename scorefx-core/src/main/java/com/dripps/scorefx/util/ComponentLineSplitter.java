package com.dripps.scorefx.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for splitting Adventure Components into prefix and suffix parts
 * suitable for Minecraft team-based scoreboard rendering.
 * <p>
 * Due to the limitations of the Minecraft scoreboard system, each line is represented
 * by a team with a prefix (up to 128 characters) and a suffix (up to 128 characters).
 * The actual scoreboard entry name is limited to 16 characters and is used for ordering.
 * </p>
 * <p>
 * This splitter intelligently divides a Component into two parts while preserving
 * text styling (colors, decorations) across the split boundary. This ensures that
 * formatting applied to the end of the prefix continues seamlessly into the suffix.
 * </p>
 * <p>
 * <strong>Strategy:</strong>
 * <ol>
 *     <li>Flatten the component tree into a list of text segments with their styles</li>
 *     <li>Determine the split point based on the 16-character entry name limit</li>
 *     <li>Build prefix and suffix components from the flattened segments</li>
 *     <li>Apply the style from the last prefix character to the suffix for continuity</li>
 * </ol>
 * </p>
 * <p>
 * This class is thread-safe as all methods are static and do not maintain state.
 * </p>
 *
 * @since 1.1.0
 */
public final class ComponentLineSplitter {
    
    /**
     * Maximum length for the scoreboard entry name (used for line ordering).
     * This is a Minecraft limitation.
     */
    private static final int MAX_ENTRY_LENGTH = 16;
    
    /**
     * Maximum length for the team prefix.
     * This is a Minecraft limitation.
     */
    private static final int MAX_PREFIX_LENGTH = 128;
    
    /**
     * Maximum length for the team suffix.
     * This is a Minecraft limitation.
     */
    private static final int MAX_SUFFIX_LENGTH = 128;
    
    /**
     * Private constructor to prevent instantiation.
     * This is a utility class and should only be used via static methods.
     */
    private ComponentLineSplitter() {
        throw new UnsupportedOperationException("ComponentLineSplitter is a utility class and cannot be instantiated");
    }
    
    /**
     * Splits a Component into prefix and suffix parts for scoreboard rendering.
     * <p>
     * This method analyzes the input Component and divides it into two parts:
     * a prefix (up to 128 chars) and a suffix (up to 128 chars). The split point
     * is determined by the 16-character entry name limit.
     * </p>
     * <p>
     * The method preserves all text styling across the split boundary, ensuring
     * that colors, bold, italic, and other formatting continue seamlessly from
     * prefix to suffix.
     * </p>
     * <p>
     * If the component's plain text is 16 characters or less, the entire component
     * becomes the prefix and the suffix is empty. Otherwise, it's split at the
     * 16-character boundary.
     * </p>
     *
     * @param component the component to split, must not be null
     * @return a {@link SplitResult} containing the prefix and suffix components
     * @throws NullPointerException if component is null
     */
    @NotNull
    public static SplitResult split(@NotNull Component component) {
        // Get the plain text to determine where to split
        String plainText = PlainTextComponentSerializer.plainText().serialize(component);
        
        // If the text fits within the entry name limit, no split needed
        if (plainText.length() <= MAX_ENTRY_LENGTH) {
            return new SplitResult(component, Component.empty());
        }
        
        // Flatten the component tree into segments
        List<StyledSegment> segments = flattenComponent(component, Style.empty());
        
        // Determine the split point (16 characters of plain text)
        int splitIndex = findSplitIndex(segments, MAX_ENTRY_LENGTH);
        
        // Build prefix and suffix components
        Component prefix = buildComponentFromSegments(segments, 0, splitIndex);
        Component suffix = buildComponentFromSegments(segments, splitIndex, segments.size());
        
        // Apply style continuity: carry over the style from the last character of prefix
        if (splitIndex > 0 && splitIndex < segments.size()) {
            Style lastPrefixStyle = segments.get(splitIndex - 1).style();
            suffix = suffix.style(lastPrefixStyle.merge(suffix.style()));
        }
        
        return new SplitResult(prefix, suffix);
    }
    
    /**
     * Flattens a Component tree into a list of styled text segments.
     * <p>
     * This method recursively traverses the component tree, extracting each piece
     * of text along with its accumulated style (color, decorations, etc.). The result
     * is a flat list where each segment represents a contiguous piece of text with
     * a single style.
     * </p>
     *
     * @param component the component to flatten
     * @param inheritedStyle the style inherited from parent components
     * @return a list of styled segments
     */
    @NotNull
    private static List<StyledSegment> flattenComponent(@NotNull Component component, @NotNull Style inheritedStyle) {
        List<StyledSegment> segments = new ArrayList<>();
        
        // Merge the component's style with inherited style
        Style currentStyle = inheritedStyle.merge(component.style());
        
        // Extract text content if this is a TextComponent
        if (component instanceof TextComponent textComponent) {
            String content = textComponent.content();
            if (!content.isEmpty()) {
                segments.add(new StyledSegment(content, currentStyle));
            }
        }
        
        // Recursively process children
        for (Component child : component.children()) {
            segments.addAll(flattenComponent(child, currentStyle));
        }
        
        return segments;
    }
    
    /**
     * Finds the index in the segments list where we should split based on character count.
     * <p>
     * This method iterates through segments, counting characters until we reach or exceed
     * the maximum entry length. It returns the index of the first segment that starts
     * after the split point.
     * </p>
     *
     * @param segments the list of styled segments
     * @param maxChars the maximum number of characters for the prefix (16 for entry name)
     * @return the index where the split should occur
     */
    private static int findSplitIndex(@NotNull List<StyledSegment> segments, int maxChars) {
        int charCount = 0;
        
        for (int i = 0; i < segments.size(); i++) {
            StyledSegment segment = segments.get(i);
            int segmentLength = segment.text().length();
            
            // If adding this entire segment would exceed the limit, we need to split within it
            if (charCount + segmentLength > maxChars) {
                // If we haven't added any segments yet, we must split this segment
                if (charCount == 0) {
                    return 1; // Split after a partial segment (handled in buildComponentFromSegments)
                }
                return i;
            }
            
            charCount += segmentLength;
            
            // If we've exactly hit the limit, split after this segment
            if (charCount == maxChars) {
                return i + 1;
            }
        }
        
        return segments.size();
    }
    
    /**
     * Builds a Component from a range of styled segments.
     * <p>
     * This method reconstructs a Component from the flattened segment list, preserving
     * all styles. Each segment becomes a component part with its associated formatting.
     * </p>
     *
     * @param segments the list of all segments
     * @param startIndex the starting index (inclusive)
     * @param endIndex the ending index (exclusive)
     * @return a Component built from the specified segment range
     */
    @NotNull
    private static Component buildComponentFromSegments(@NotNull List<StyledSegment> segments, 
                                                        int startIndex, 
                                                        int endIndex) {
        if (startIndex >= endIndex || startIndex >= segments.size()) {
            return Component.empty();
        }
        
        // Special case: if we need to split within the first segment
        if (startIndex == 0 && endIndex == 1 && segments.size() > 1) {
            StyledSegment segment = segments.get(0);
            if (segment.text().length() > MAX_ENTRY_LENGTH) {
                String text = segment.text().substring(0, MAX_ENTRY_LENGTH);
                return Component.text(text, segment.style());
            }
        }
        
        Component result = Component.empty();
        
        for (int i = startIndex; i < endIndex && i < segments.size(); i++) {
            StyledSegment segment = segments.get(i);
            result = result.append(Component.text(segment.text(), segment.style()));
        }
        
        return result;
    }
    
    /**
     * Represents the result of splitting a Component into prefix and suffix.
     *
     * @param prefix the prefix component (displayed before the entry name)
     * @param suffix the suffix component (displayed after the entry name)
     * @since 1.1.0
     */
    public record SplitResult(@NotNull Component prefix, @NotNull Component suffix) {
    }
    
    /**
     * Internal record representing a text segment with its associated style.
     * <p>
     * Used during the flattening process to track individual pieces of text
     * and their formatting.
     * </p>
     *
     * @param text the text content
     * @param style the style applied to this text
     */
    private record StyledSegment(@NotNull String text, @NotNull Style style) {
    }
}
