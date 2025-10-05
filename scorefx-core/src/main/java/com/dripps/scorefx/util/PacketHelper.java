package com.dripps.scorefx.util;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * PacketHelper - Elite NMS packet manipulation utility for ScoreFX 2.0.
 * <p>
 * This class isolates all NMS reflection code in one centralized location, using
 * MethodHandles for maximum performance. It provides direct packet-based score
 * manipulation, eliminating all external dependencies (ProtocolLib).
 * </p>
 * <p>
 * <strong>Architecture:</strong> Inspired by FastBoard's reflection approach, this
 * utility caches all necessary MethodHandles during static initialization, ensuring
 * zero reflection overhead during runtime operations.
 * </p>
 * <p>
 * <strong>Version Compatibility:</strong> Automatically detects server version and
 * adapts to NMS package structure changes across Minecraft versions.
 * </p>
 *
 * @since 2.0.0
 */
public final class PacketHelper {

    private static final Logger LOGGER = Logger.getLogger("ScoreFX");
    
    // Version detection
    private static final String SERVER_VERSION;
    private static final String NMS_PACKAGE;
    private static final String CRAFTBUKKIT_PACKAGE;
    
    // Cached MethodHandles - initialized once, used forever
    private static final MethodHandle CRAFT_PLAYER_GET_HANDLE;
    private static final MethodHandle PLAYER_CONNECTION_FIELD;
    private static final MethodHandle SEND_PACKET_METHOD;
    private static final MethodHandle SET_SCORE_PACKET_CONSTRUCTOR;
    private static final MethodHandle RESET_SCORE_PACKET_CONSTRUCTOR;
    private static final MethodHandle PAPER_ADVENTURE_AS_VANILLA;
    private static final MethodHandle FIXED_FORMAT_CONSTRUCTOR;
    private static final Object BLANK_NUMBER_FORMAT;
    
    // Initialization status
    private static final boolean INITIALIZED;
    private static final Throwable INIT_ERROR;
    
    /**
     * Static initialization block - the heart of PacketHelper.
     * <p>
     * This block executes once when the class is loaded, caching all necessary
     * MethodHandles for NMS operations. Any failure here is captured and logged,
     * allowing graceful degradation.
     * </p>
     */
    static {
        boolean success = false;
        Throwable error = null;
        
        // Temporary variables for handle initialization
        String serverVersion = null;
        String nmsPackage = null;
        String craftBukkitPackage = null;
        MethodHandle craftPlayerGetHandle = null;
        MethodHandle playerConnectionField = null;
        MethodHandle sendPacketMethod = null;
        MethodHandle setScorePacketConstructor = null;
        MethodHandle resetScorePacketConstructor = null;
        MethodHandle paperAdventureAsVanilla = null;
        MethodHandle fixedFormatConstructor = null;
        Object blankNumberFormat = null;
        
        try {
            LOGGER.info("[PacketHelper] Initializing NMS reflection layer for ScoreFX 2.0...");
            
            // Step 1: Detect server version
            String serverPackage = Bukkit.getServer().getClass().getPackage().getName();
            serverVersion = serverPackage.substring(serverPackage.lastIndexOf('.') + 1);
            LOGGER.info("[PacketHelper] Detected server version: " + serverVersion);
            
            // Step 2: Determine NMS package structure
            // Modern Paper (1.20.5+): No version in package, uses "craftbukkit"
            // Modern Paper (1.17-1.20.4): Uses versioned packages like v1_20_R3
            // Legacy: net.minecraft.server.v1_16_R3 (with version)
            
            boolean isModernPaper = !serverVersion.startsWith("v1_");
            
            if (isModernPaper) {
                // Modern Paper 1.20.5+ without versioned packages
                nmsPackage = "net.minecraft";
                craftBukkitPackage = "org.bukkit.craftbukkit";
                LOGGER.info("[PacketHelper] Using modern Paper (1.20.5+) package structure");
            } else {
                // Legacy or transitional versions with version in package
                nmsPackage = "net.minecraft";
                craftBukkitPackage = "org.bukkit.craftbukkit." + serverVersion;
                LOGGER.info("[PacketHelper] Using versioned package structure: " + serverVersion);
            }
            
            // Step 3: Initialize MethodHandles.Lookup with full privileges
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            
            // Step 4: Cache CraftPlayer.getHandle()
            Class<?> craftPlayerClass = Class.forName(craftBukkitPackage + ".entity.CraftPlayer");
            craftPlayerGetHandle = lookup.findVirtual(
                craftPlayerClass,
                "getHandle",
                MethodType.methodType(Class.forName(nmsPackage + ".server.level.EntityPlayer"))
            );
            LOGGER.fine("[PacketHelper] Cached CraftPlayer.getHandle()");
            
            // Step 5: Cache EntityPlayer.connection field (PlayerConnection)
            Class<?> entityPlayerClass = Class.forName(nmsPackage + ".server.level.EntityPlayer");
            Class<?> playerConnectionClass = Class.forName(nmsPackage + ".server.network.PlayerConnection");
            playerConnectionField = lookup.findGetter(
                entityPlayerClass,
                "connection",  // Modern name: 'connection', Legacy: 'playerConnection'
                playerConnectionClass
            );
            LOGGER.fine("[PacketHelper] Cached EntityPlayer.connection field");
            
            // Step 6: Cache PlayerConnection.sendPacket() / send()
            Class<?> packetClass = Class.forName(nmsPackage + ".network.protocol.Packet");
            try {
                // Try modern method name: send()
                sendPacketMethod = lookup.findVirtual(
                    playerConnectionClass,
                    "send",
                    MethodType.methodType(void.class, packetClass)
                );
                LOGGER.fine("[PacketHelper] Cached PlayerConnection.send() [modern]");
            } catch (NoSuchMethodException e) {
                // Fallback to legacy: sendPacket()
                sendPacketMethod = lookup.findVirtual(
                    playerConnectionClass,
                    "sendPacket",
                    MethodType.methodType(void.class, packetClass)
                );
                LOGGER.fine("[PacketHelper] Cached PlayerConnection.sendPacket() [legacy]");
            }
            
            // Step 7: Cache ClientboundSetScorePacket constructor
            // In Minecraft 1.21+, ClientboundSetScorePacket is a record with specific constructor
            Class<?> setScorePacketClass;
            try {
                setScorePacketClass = Class.forName(nmsPackage + ".network.protocol.game.ClientboundSetScorePacket");
                LOGGER.info("[PacketHelper] Found ClientboundSetScorePacket class");
            } catch (ClassNotFoundException e) {
                setScorePacketClass = Class.forName(nmsPackage + ".PacketPlayOutScoreboardScore");
                LOGGER.info("[PacketHelper] Found PacketPlayOutScoreboardScore [legacy]");
            }
            
            // Get required NMS classes
            Class<?> componentClass = Class.forName(nmsPackage + ".network.chat.Component");
            Class<?> numberFormatClass = Class.forName(nmsPackage + ".network.chat.numbers.NumberFormat");
            
            // In 1.21.8, ClientboundSetScorePacket is a record with constructor:
            // public ClientboundSetScorePacket(String holder, String objectiveName, int score, Optional<Component> display, Optional<NumberFormat> numberFormat)
            // The last two parameters are Optional!
            
            Class<?> optionalClass = Optional.class;
            
            try {
                // Try the Optional-based constructor (1.21.8+)
                setScorePacketConstructor = lookup.findConstructor(
                    setScorePacketClass,
                    MethodType.methodType(void.class, String.class, String.class, int.class, optionalClass, optionalClass)
                );
                LOGGER.info("[PacketHelper] Using ClientboundSetScorePacket constructor with Optional parameters");
            } catch (NoSuchMethodException e) {
                // Fallback: Try direct parameter constructor (older versions)
                try {
                    setScorePacketConstructor = lookup.findConstructor(
                        setScorePacketClass,
                        MethodType.methodType(void.class, String.class, String.class, int.class, componentClass, numberFormatClass)
                    );
                    LOGGER.info("[PacketHelper] Using ClientboundSetScorePacket constructor with direct parameters");
                } catch (NoSuchMethodException ex) {
                    // Debug: Log all available constructors
                    LOGGER.severe("[PacketHelper] Failed to find suitable constructor. Available constructors:");
                    for (var ctor : setScorePacketClass.getDeclaredConstructors()) {
                        StringBuilder params = new StringBuilder();
                        for (var param : ctor.getParameterTypes()) {
                            if (params.length() > 0) params.append(", ");
                            params.append(param.getSimpleName());
                        }
                        LOGGER.severe("[PacketHelper]   Constructor(" + params + ")");
                    }
                    throw new RuntimeException("No suitable ClientboundSetScorePacket constructor found");
                }
            }
            
            // Step 8: Cache ClientboundResetScorePacket constructor (optional, for removal)
            try {
                Class<?> resetScorePacketClass = Class.forName(nmsPackage + ".network.protocol.game.ClientboundResetScorePacket");
                resetScorePacketConstructor = lookup.findConstructor(
                    resetScorePacketClass,
                    MethodType.methodType(void.class, String.class, String.class)
                );
                LOGGER.fine("[PacketHelper] Cached ClientboundResetScorePacket constructor");
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                LOGGER.warning("[PacketHelper] ClientboundResetScorePacket not found - score removal may be limited");
                resetScorePacketConstructor = null;
            }
            
            // Step 9: Cache PaperAdventure.asVanilla(Component)
            Class<?> paperAdventureClass = Class.forName("io.papermc.paper.adventure.PaperAdventure");
            paperAdventureAsVanilla = lookup.findStatic(
                paperAdventureClass,
                "asVanilla",
                MethodType.methodType(componentClass, Component.class)
            );
            LOGGER.fine("[PacketHelper] Cached PaperAdventure.asVanilla()");
            
            // Step 10: Cache BlankFormat.INSTANCE singleton
            // BlankFormat is a singleton class with a static INSTANCE field
            Class<?> blankFormatClass = Class.forName(nmsPackage + ".network.chat.numbers.BlankFormat");
            blankNumberFormat = lookup.findStaticGetter(
                blankFormatClass,
                "INSTANCE",
                blankFormatClass
            ).invoke();
            LOGGER.fine("[PacketHelper] Cached BlankFormat.INSTANCE singleton");
            
            // Step 11: Cache FixedFormat constructor
            // FixedFormat is an inner class: NumberFormat$FixedFormat
            Class<?> fixedFormatClass = Class.forName(nmsPackage + ".network.chat.numbers.FixedFormat");
            fixedFormatConstructor = lookup.findConstructor(
                fixedFormatClass,
                MethodType.methodType(void.class, componentClass)
            );
            LOGGER.fine("[PacketHelper] Cached FixedFormat constructor");
            
            // Initialization successful!
            success = true;
            LOGGER.info("[PacketHelper] ✓ NMS reflection layer initialized successfully");
            
        } catch (Throwable t) {
            error = t;
            LOGGER.severe("[PacketHelper] ✗ Failed to initialize NMS reflection layer: " + t.getMessage());
            t.printStackTrace();
        }
        
        // Assign to final fields
        SERVER_VERSION = serverVersion;
        NMS_PACKAGE = nmsPackage;
        CRAFTBUKKIT_PACKAGE = craftBukkitPackage;
        CRAFT_PLAYER_GET_HANDLE = craftPlayerGetHandle;
        PLAYER_CONNECTION_FIELD = playerConnectionField;
        SEND_PACKET_METHOD = sendPacketMethod;
        SET_SCORE_PACKET_CONSTRUCTOR = setScorePacketConstructor;
        RESET_SCORE_PACKET_CONSTRUCTOR = resetScorePacketConstructor;
        PAPER_ADVENTURE_AS_VANILLA = paperAdventureAsVanilla;
        FIXED_FORMAT_CONSTRUCTOR = fixedFormatConstructor;
        BLANK_NUMBER_FORMAT = blankNumberFormat;
        INITIALIZED = success;
        INIT_ERROR = error;
    }
    
    /**
     * Private constructor - this is a utility class with only static methods.
     */
    private PacketHelper() {
        throw new UnsupportedOperationException("PacketHelper is a utility class and cannot be instantiated");
    }
    
    /**
     * Checks if the PacketHelper was successfully initialized.
     *
     * @return true if all reflection handles were cached successfully, false otherwise
     */
    public static boolean isInitialized() {
        return INITIALIZED;
    }
    
    /**
     * Gets the initialization error, if any.
     *
     * @return the throwable that caused initialization failure, or null if successful
     */
    @Nullable
    public static Throwable getInitError() {
        return INIT_ERROR;
    }
    
    /**
     * Sends a score packet to the client, setting or updating a score line.
     * <p>
     * This method directly sends a ClientboundSetScorePacket to the player's client,
     * bypassing all Bukkit Score objects. It supports custom number formats (styled
     * or blank) for complete visual control.
     * </p>
     *
     * @param player the player to send the packet to
     * @param objectiveName the scoreboard objective name
     * @param lineIdentifier the unique identifier for this score line (entry name)
     * @param score the integer score value (typically the row number)
     * @param customScore optional custom score component (null = BLANK/hidden)
     * @throws IllegalStateException if PacketHelper is not initialized
     */
    public static void sendScorePacket(
        @NotNull Player player,
        @NotNull String objectiveName,
        @NotNull String lineIdentifier,
        int score,
        @Nullable Component customScore
    ) {
        if (!INITIALIZED) {
            throw new IllegalStateException("PacketHelper is not initialized - cannot send score packets");
        }
        
        try {
            // Step 1: Determine the number format (BLANK or FixedFormat)
            Object numberFormat;
            if (customScore != null && !customScore.equals(Component.empty())) {
                // Custom score provided - create a FixedFormat with the Component
                Object nmsComponent = PAPER_ADVENTURE_AS_VANILLA.invoke(customScore);
                numberFormat = FIXED_FORMAT_CONSTRUCTOR.invoke(nmsComponent);
            } else {
                // No custom score - use BLANK format (hidden)
                numberFormat = BLANK_NUMBER_FORMAT;
            }
            
            // Step 2: Create the ClientboundSetScorePacket
            // Constructor signature in 1.21.8+: (String holder, String objectiveName, int score, Optional<Component> display, Optional<NumberFormat> numberFormat)
            // Note: First param is the HOLDER (entry/line identifier), second is OBJECTIVE NAME
            // The display and numberFormat are wrapped in Optional
            Object packet = SET_SCORE_PACKET_CONSTRUCTOR.invoke(
                lineIdentifier,              // holder (the score entry identifier)
                objectiveName,               // objectiveName (the objective this score belongs to)
                score,                       // score (the integer value)
                Optional.empty(),            // display (Optional<Component> - empty, we use team prefix/suffix)
                Optional.of(numberFormat)    // numberFormat (Optional<NumberFormat> - our BLANK or FixedFormat)
            );
            
            // Step 3: Get the player's connection
            Object craftPlayer = player;  // CraftPlayer
            Object entityPlayer = CRAFT_PLAYER_GET_HANDLE.invoke(craftPlayer);
            Object playerConnection = PLAYER_CONNECTION_FIELD.invoke(entityPlayer);
            
            // Step 4: Send the packet
            SEND_PACKET_METHOD.invoke(playerConnection, packet);
            
        } catch (Throwable t) {
            LOGGER.severe("[PacketHelper] Failed to send score packet: " + t.getMessage());
            t.printStackTrace();
        }
    }
    
    /**
     * Sends a reset score packet to the client, removing a score line.
     * <p>
     * This method sends a ClientboundResetScorePacket to remove a score from the
     * client's view. If the packet type is not available (older versions), this
     * method logs a warning and does nothing.
     * </p>
     *
     * @param player the player to send the packet to
     * @param objectiveName the scoreboard objective name
     * @param lineIdentifier the unique identifier for the score line to remove
     * @throws IllegalStateException if PacketHelper is not initialized
     */
    public static void sendRemoveScorePacket(
        @NotNull Player player,
        @NotNull String objectiveName,
        @NotNull String lineIdentifier
    ) {
        if (!INITIALIZED) {
            throw new IllegalStateException("PacketHelper is not initialized - cannot send remove score packets");
        }
        
        if (RESET_SCORE_PACKET_CONSTRUCTOR == null) {
            LOGGER.warning("[PacketHelper] ClientboundResetScorePacket is not available on this server version - cannot remove score");
            return;
        }
        
        try {
            // Step 1: Create the ClientboundResetScorePacket
            // Constructor: (String owner, String objectiveName)
            Object packet = RESET_SCORE_PACKET_CONSTRUCTOR.invoke(lineIdentifier, objectiveName);
            
            // Step 2: Get the player's connection
            Object craftPlayer = player;  // CraftPlayer
            Object entityPlayer = CRAFT_PLAYER_GET_HANDLE.invoke(craftPlayer);
            Object playerConnection = PLAYER_CONNECTION_FIELD.invoke(entityPlayer);
            
            // Step 3: Send the packet
            SEND_PACKET_METHOD.invoke(playerConnection, packet);
            
        } catch (Throwable t) {
            LOGGER.severe("[PacketHelper] Failed to send remove score packet: " + t.getMessage());
            t.printStackTrace();
        }
    }
    
    /**
     * Gets diagnostic information about the PacketHelper initialization.
     * <p>
     * Useful for debugging and verifying that the reflection layer is working correctly.
     * </p>
     *
     * @return a diagnostic string with version and initialization status
     */
    @NotNull
    public static String getDiagnostics() {
        StringBuilder sb = new StringBuilder();
        sb.append("PacketHelper Diagnostics:\n");
        sb.append("  Server Version: ").append(SERVER_VERSION).append("\n");
        sb.append("  NMS Package: ").append(NMS_PACKAGE).append("\n");
        sb.append("  CraftBukkit Package: ").append(CRAFTBUKKIT_PACKAGE).append("\n");
        sb.append("  Initialized: ").append(INITIALIZED).append("\n");
        
        if (!INITIALIZED && INIT_ERROR != null) {
            sb.append("  Error: ").append(INIT_ERROR.getMessage()).append("\n");
        }
        
        sb.append("  Handles Cached:\n");
        sb.append("    CraftPlayer.getHandle(): ").append(CRAFT_PLAYER_GET_HANDLE != null).append("\n");
        sb.append("    EntityPlayer.connection: ").append(PLAYER_CONNECTION_FIELD != null).append("\n");
        sb.append("    PlayerConnection.send(): ").append(SEND_PACKET_METHOD != null).append("\n");
        sb.append("    ClientboundSetScorePacket: ").append(SET_SCORE_PACKET_CONSTRUCTOR != null).append("\n");
        sb.append("    ClientboundResetScorePacket: ").append(RESET_SCORE_PACKET_CONSTRUCTOR != null).append("\n");
        sb.append("    PaperAdventure.asVanilla(): ").append(PAPER_ADVENTURE_AS_VANILLA != null).append("\n");
        sb.append("    FixedFormat constructor: ").append(FIXED_FORMAT_CONSTRUCTOR != null).append("\n");
        sb.append("    NumberFormat.BLANK: ").append(BLANK_NUMBER_FORMAT != null).append("\n");
        
        return sb.toString();
    }
}
