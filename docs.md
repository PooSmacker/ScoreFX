# ScoreFX Documentation

**Version:** 1.1.0-SNAPSHOT  
**Target:** Paper 1.21.8  
**Last Updated:** October 5, 2025

---

## Overview

ScoreFX is a high-performance, developer-first Scoreboard API plugin for Paper servers. It provides a clean, intuitive API for creating per-player scoreboards with support for dynamic updates, animations, RGB colors, and PlaceholderAPI integration.

**Version 1.1.0** introduces full support for Adventure Components, enabling modern text features like RGB colors, gradients, and rich formatting while maintaining complete backward compatibility with the legacy String-based API.

### Key Features

- **Adventure Component Support (NEW in 1.1.0)**: Full RGB colors, gradients, and modern text features
- **Backward Compatible**: Legacy String-based methods continue to work with '&' codes and hex colors
- **Per-Player Scoreboards**: Each player can have a unique, independent scoreboard
- **Flicker-Free Rendering**: Updates are seamless using the Team-based prefix/suffix method
- **Dynamic Line Updates**: Lines can be set, updated, and removed without visual artifacts
- **Animation Support**: Built-in support for animated titles and lines (Component and String-based)
- **Custom Update Intervals**: Optimize performance by controlling how often lines update
- **PlaceholderAPI Integration**: Soft dependency with automatic placeholder resolution (String-based only)
- **Clean API**: Simple, fluent API exposed via Bukkit Services Manager
- **Resource Management**: Automatic cleanup on player quit and server shutdown

---

## API Module (scorefx-api)

### Package Structure

```
com.dripps.scorefx.api
├── ScoreFX.java              (Main API entrypoint)
├── BoardManager.java         (Manages scoreboard lifecycle)
├── Board.java                (Represents a per-player scoreboard)
└── animation/
    ├── Animation.java        (Animation interface)
    └── AnimationFactory.java (Factory for creating animations)
```

### Core Interfaces

#### ScoreFX
The main entrypoint for the ScoreFX API. Obtain via Bukkit Services Manager.

**Methods:**
- `BoardManager getBoardManager()` - Get the board manager
- `AnimationFactory getAnimationFactory()` - Get the animation factory

#### BoardManager
Manages the lifecycle of all player scoreboards.

**Methods:**
- `Board createBoard(Player player)` - Create a new board for a player (replaces existing)
- `Optional<Board> getBoard(Player player)` - Retrieve a player's active board
- `void removeBoard(Player player)` - Destroy and remove a player's board

#### Board
Represents a single, per-player scoreboard supporting 15 visible rows.

**Modern API Methods (v1.1.0+, Component-based):**
- `void setTitle(Component title)` - Set a static title with full Adventure Component support
- `void setLine(int row, Component text)` - Set a line with Component (default update interval)
- `void setLine(int row, Component text, int updateIntervalTicks)` - Set a line with Component and custom update interval
- `void setAnimatedTitle(Animation titleAnimation)` - Set an animated title (Animation produces Components in v1.1.0)
- `void setAnimatedLine(int row, Animation animation)` - Set an animated line (Animation produces Components in v1.1.0)

**Legacy API Methods (v1.0, deprecated but fully supported):**
- `@Deprecated void setTitle(String title)` - Set a static title with legacy String formatting
- `@Deprecated void setLine(int row, String text)` - Set a line with default update interval (every tick)
- `@Deprecated void setLine(int row, String text, int updateIntervalTicks)` - Set a line with custom update interval

**Common Methods:**
- `void removeLine(int row)` - Clear a line
- `Player getPlayer()` - Get the board owner

**Row Numbering:**
- Rows are numbered 1-15
- Row 1 appears at the BOTTOM
- Row 15 appears at the TOP

**Important Notes:**
- Component methods are the recommended approach for v1.1.0+
- String methods still work perfectly and support PlaceholderAPI
- PlaceholderAPI placeholders only work with String-based methods, not Component methods

#### Animation
Represents a sequence of text frames.

**Methods:**
- `Component nextFrame()` - Get the next frame in the sequence (returns Component in v1.1.0, was String in v1.0)
- `int getIntervalTicks()` - Get the interval between frames

**Note:** In v1.1.0, animations are Component-based internally. Legacy String animations are converted to Components automatically.

#### AnimationFactory
Factory for creating animations.

**Modern API Methods (v1.1.0+):**
- `Animation fromComponents(List<Component> frames, int intervalTicks)` - Create a cycling animation from Components

**Legacy API Methods (deprecated but fully supported):**
- `@Deprecated Animation fromFrames(List<String> frames, int intervalTicks)` - Create a cycling animation from Strings

**Note:** The `fromFrames` method is deprecated in v1.1.0 but remains fully functional. It converts Strings to Components internally using LegacySupport.

---

## Usage Examples

### Basic Setup

```java
// Get the API from Services Manager
ScoreFX api = Bukkit.getServicesManager().load(ScoreFX.class);
BoardManager boardManager = api.getBoardManager();

// Create a board for a player
Board board = boardManager.createBoard(player);
```

### Modern API: Using Adventure Components (v1.1.0+)

**Recommended for new projects.** Components support full RGB colors, gradients, and modern formatting.

```java
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

// Set a title with RGB color
board.setTitle(Component.text("My Server", TextColor.color(0xFF5733)));

// Set a title with named colors and bold
board.setTitle(
    Component.text("My Server", NamedTextColor.GOLD)
        .decorate(TextDecoration.BOLD)
);

// Set lines with Component builders
board.setLine(15, Component.text("------------------", NamedTextColor.GRAY));
board.setLine(14, 
    Component.text("Player: ", NamedTextColor.YELLOW)
        .append(Component.text("Steve", TextColor.color(0x55FF55)))
);
board.setLine(13,
    Component.text("Rank: ", NamedTextColor.YELLOW)
        .append(Component.text("VIP", NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
);

// Create Component-based animations
AnimationFactory factory = api.getAnimationFactory();
Animation titleAnim = factory.fromComponents(
    List.of(
        Component.text("Frame 1", NamedTextColor.GOLD),
        Component.text("Frame 2", TextColor.color(0xFF5733)),
        Component.text("Frame 3", NamedTextColor.RED)
    ),
    20  // 20 ticks = 1 second per frame
);
board.setAnimatedTitle(titleAnim);
```

**Important Limitation:** PlaceholderAPI placeholders are not currently supported in Component-based content. If you need placeholders, use the legacy String-based methods below.

### Legacy API: Using Strings (v1.0, fully supported in v1.1.0)

**All String methods remain fully functional and backward compatible!** They support `&` and `§` color codes, hex colors (`&#RRGGBB`), and PlaceholderAPI placeholders.

```java
// Set a title
board.setTitle("&6&lMy Server");

// Set lines (row 1 = bottom, row 15 = top)
board.setLine(15, "&7------------------");
board.setLine(14, "&ePlayer: &f%player_name%");
board.setLine(13, "&eRank: &f%vault_rank%");
board.setLine(12, "");
board.setLine(11, "&eBalance: &a$%vault_eco_balance%");
board.setLine(10, "&7------------------");
```

### Performance Optimization with Custom Update Intervals

```java
// This line contains an expensive placeholder - only update every 5 seconds (100 ticks)
board.setLine(11, "&eBalance: &a$%vault_eco_balance%", 100);

// This line has cheap placeholders - update every tick
board.setLine(14, "&ePlayer: &f%player_name%");
```

### Creating Animations

**Modern API (v1.1.0+ with Components):**

```java
AnimationFactory factory = api.getAnimationFactory();

// Create a title animation with RGB colors
Animation titleAnim = factory.fromComponents(
    List.of(
        Component.text("ScoreFX", TextColor.color(0xFF0000)),  // Red
        Component.text("ScoreFX", TextColor.color(0xFF7F00)),  // Orange
        Component.text("ScoreFX", TextColor.color(0xFFFF00)),  // Yellow
        Component.text("ScoreFX", TextColor.color(0x00FF00)),  // Green
        Component.text("ScoreFX", TextColor.color(0x0000FF))   // Blue
    ),
    10  // Change frame every 10 ticks (0.5 seconds)
);
board.setAnimatedTitle(titleAnim);

// Create a line animation with complex formatting
Animation statusAnim = factory.fromComponents(
    List.of(
        Component.text("● ", NamedTextColor.GREEN).append(Component.text("Online", NamedTextColor.GRAY)),
        Component.text("● ", NamedTextColor.YELLOW).append(Component.text("Online", NamedTextColor.GRAY)),
        Component.text("● ", NamedTextColor.RED).append(Component.text("Online", NamedTextColor.GRAY))
    ),
    20  // Change frame every 20 ticks (1 second)
);
board.setAnimatedLine(13, statusAnim);
```

**Legacy API (v1.0, deprecated but fully supported):**

```java
AnimationFactory factory = api.getAnimationFactory();

// Create a title animation
Animation titleAnim = factory.fromFrames(
    List.of(
        "&6&lM&e&ly Server",
        "&e&lM&6&ly Server",
        "&6&lMy &e&lServer",
        "&e&lMy &6&lServer"
    ),
    10  // Change frame every 10 ticks (0.5 seconds)
);
board.setAnimatedTitle(titleAnim);

// Create a line animation
Animation lineAnim = factory.fromFrames(
    List.of(
        "&a● &7Online",
        "&e● &7Online",
        "&c● &7Online"
    ),
    20  // Change frame every 20 ticks (1 second)
);
board.setAnimatedLine(13, lineAnim);
```

**Animations with Placeholders (String-based only):**

```java
// Placeholders work in String animations and are resolved per-frame
Animation greeting = factory.fromFrames(
    List.of(
        "&aWelcome &f%player_name%!",
        "&eWelcome &f%player_name%!",
        "&cWelcome &f%player_name%!"
    ),
    15  // Change frame every 15 ticks (0.75 seconds)
);
board.setAnimatedLine(14, greeting);
```

### Removing Content

```java
// Remove a specific line
board.removeLine(12);

// Remove the entire board
boardManager.removeBoard(player);
```

---

## Thread Safety

**CRITICAL:** All methods that modify board state must be called from the main server thread. Calling these methods asynchronously will throw an `IllegalStateException`.

Safe methods (can be called from any thread):
- `BoardManager.getBoard(Player)`
- `Board.getPlayer()`

Unsafe methods (main thread only):
- `BoardManager.createBoard(Player)`
- `BoardManager.removeBoard(Player)`
- `Board.setTitle(String)` / `Board.setTitle(Component)`
- `Board.setAnimatedTitle(Animation)`
- `Board.setLine(...)` (all overloads)
- `Board.setAnimatedLine(...)`
- `Board.removeLine(int)`

---

## PlaceholderAPI Integration

ScoreFX automatically detects and uses PlaceholderAPI if it's installed on the server. No additional configuration is required.

**String-based methods** support:
- Minecraft color codes: `§` and `&` formats
- Hex colors: `&#RRGGBB` format
- PlaceholderAPI placeholders: `%placeholder_name%`

**Component-based methods** support:
- Full Adventure Component API features (RGB colors, gradients, formatting)
- **Note:** PlaceholderAPI placeholders are NOT supported in Component methods

**Why the limitation?** PlaceholderAPI is a String-based API that doesn't understand Adventure Components. To use both placeholders and modern formatting, you need to manually resolve placeholders first, then build Components.

**Workaround for Components + Placeholders:**

```java
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

// Option 1: Resolve placeholders, then build Component
String balance = PlaceholderAPI.setPlaceholders(player, "%vault_eco_balance%");
Component line = Component.text()
    .append(Component.text("Balance: ", NamedTextColor.YELLOW))
    .append(Component.text("$" + balance, NamedTextColor.GREEN))
    .build();
board.setLine(11, line);

// Option 2: Use MiniMessage (if you have adventure-text-minimessage)
// String text = PlaceholderAPI.setPlaceholders(player, "<yellow>Balance: <green>$%vault_eco_balance%");
// Component line = MiniMessage.miniMessage().deserialize(text);
// board.setLine(11, line);
```

Placeholders in String methods are resolved just before rendering, ensuring up-to-date values.

---

## Best Practices

1. **Choose the Right API:**
   - Use **Components** for static content with RGB colors and modern formatting
   - Use **Strings** for dynamic content with PlaceholderAPI placeholders
   - Use **both** in the same board (hybrid approach)

2. **Use Custom Update Intervals**: For expensive placeholders (database queries, complex calculations), use `setLine(row, text, intervalTicks)` to reduce server load

3. **Clean Up Resources**: Always remove boards when they're no longer needed (ScoreFX handles this automatically on player quit)

4. **Avoid Rapid Updates**: Don't update the same line from multiple sources simultaneously

5. **Test Performance**: Monitor server TPS when using many animated boards

6. **Use Thread-Safe Access**: Always check if you're on the main thread before modifying boards

7. **Migration from v1.0**: Your existing String-based code will continue to work. Deprecation warnings are informational only - methods will not be removed.

---

## Implementation Status

### ✅ Completed Tasks

- **Task 1: API Module Creation** - All interfaces created with comprehensive Javadocs and @NotNull annotations
- **Task 2: Core Module Skeleton** - Main plugin class, plugin.yml, and build configuration completed
- **Task 3: Implement TeamBoardImpl** - Core board implementation with flicker-free rendering and LineSplitter utility
- **Task 4: Implement Heartbeat Scheduler** - High-performance task scheduler with PriorityQueue and lifecycle management
- **Task 5: Implement BoardManagerImpl** - Board lifecycle manager with Heartbeat integration
- **Task 6: Wire Everything Together** - Full plugin integration with API registration and event listeners
- **Task 7: Implement Animations** - Full animation support for titles and lines with Heartbeat integration
- **Task 8: Implement PlaceholderAPI Hook** - Complete PlaceholderAPI integration with automatic placeholder detection and replacement
- **Task 9: Adventure Component API Integration (v1.1.0)** - Full Adventure Component support with RGB colors and modern formatting
- **Task 10: LegacySupport & ComponentLineSplitter (v1.1.0)** - Utility classes for String→Component conversion and Component splitting
- **Task 11: Adventure-First Refactoring (v1.1.0)** - Refactored all internal systems to use Components as the primary data type

### 🔄 In Progress

None

### ⏳ Pending Tasks

None - All planned features implemented!

---

## Core Module (scorefx-core)

### Utility Classes (v1.1.0)

**LegacySupport** - Utility for converting legacy String-based text to Adventure Components.

**Purpose:**
- Centralized conversion logic for backward compatibility
- Transforms '&' color codes and #RRGGBB hex colors to Components
- Enables "Adventure-First" internal architecture while supporting legacy API

**Features:**
- Static `toComponent(String text)` method for conversion
- Uses `LegacyComponentSerializer` with hex color support
- Thread-safe (static, immutable serializer)
- Supports all legacy formatting: colors, decorations, hex codes, reset

**Example:**
```java
Component c1 = LegacySupport.toComponent("&6&lGold Bold");
Component c2 = LegacySupport.toComponent("&#FF5733Custom RGB");
```

**ComponentLineSplitter** - Utility for splitting Components into prefix/suffix for team-based rendering.

**Purpose:**
- Splits Components at 16-character boundary for scoreboard entry names
- Preserves all styling across the split boundary
- Handles complex Component trees with nested formatting

**Architecture:**
1. Flattens component tree into styled text segments
2. Determines split point based on 16-character entry limit
3. Builds prefix and suffix components from segments
4. Carries styling from last prefix character to suffix for continuity

**Features:**
- Returns `SplitResult` record with prefix and suffix Components
- Respects Minecraft limits: 128 chars prefix, 128 chars suffix, 16 chars entry
- Thread-safe (static methods, no state)
- Intelligent style preservation for seamless visual rendering

**Example:**
```java
Component line = Component.text("Long text here", NamedTextColor.GOLD);
SplitResult result = ComponentLineSplitter.split(line);
Component prefix = result.prefix();  // First 16 chars
Component suffix = result.suffix();  // Remaining chars with gold color preserved
```

**Why Two Splitters?**
- `LineSplitter` (v1.0): Handles legacy Strings with § codes
- `ComponentLineSplitter` (v1.1.0): Handles Adventure Components with complex styling
- Both are needed because the internal architecture is Adventure-First, but legacy API still uses Strings

### Main Plugin Class

**ScoreFXPlugin** - The main plugin entry point that extends `JavaPlugin`.

**Initialization Order (onEnable):**
1. Create `PAPIHook` for PlaceholderAPI integration
2. Create `Heartbeat` scheduler with PAPIHook reference
3. Create `AnimationFactoryImpl`
4. Create `BoardManagerImpl` with Heartbeat reference
5. Create `ScoreFXImpl` API implementation
6. Register API with `ServicesManager` (ServicePriority.Normal)
7. Register `PlayerQuitListener` for automatic cleanup
8. Start Heartbeat scheduler

**Shutdown Order (onDisable):**
1. Remove all active boards via `boardManager.removeAllBoards()`
2. Stop Heartbeat scheduler (cancels all tasks)
3. Unregister all services

**Features:**
- Comprehensive error handling with try-catch blocks
- Detailed logging at each initialization step
- Graceful shutdown with cleanup verification
- Automatic plugin disable if initialization fails
- Public getters for Heartbeat and BoardManager (testing/internal use)

### API Implementation

**ScoreFXImpl** - Implementation of the main `ScoreFX` API interface.

**Structure:**
- Holds references to `BoardManager` and `AnimationFactory`
- Implements `getBoardManager()` and `getAnimationFactory()`
- Validates constructor parameters (null checks)
- Registered with Bukkit ServicesManager for public access

**Usage by Other Plugins:**
```java
ScoreFX api = Bukkit.getServicesManager().load(ScoreFX.class);
BoardManager manager = api.getBoardManager();
AnimationFactory factory = api.getAnimationFactory();
```

### Event Listeners

**PlayerQuitListener** - Automatic scoreboard cleanup on player disconnect.

**Features:**
- Listens to `PlayerQuitEvent` with `EventPriority.MONITOR`
- Calls `boardManager.removeBoard(player)` automatically
- Exception handling with logging (prevents listener failures)
- Fine-grained logging for debugging
- Ensures no memory leaks from disconnected players

**Why MONITOR Priority:**
- Runs after all other plugins have handled the event
- Minimizes conflicts with other plugin scoreboards
- Ensures cleanup happens even if other handlers fail

### Animation System

**AnimationFactoryImpl** - Implementation of the `AnimationFactory` interface.

**Features:**
- Creates `SimpleAnimation` instances from Component or String frame lists
- **v1.1.0:** Primary method is `fromComponents(List<Component>, int)`
- **v1.1.0:** Deprecated method `fromFrames(List<String>, int)` converts Strings to Components via LegacySupport
- Validates all parameters (null checks, empty checks, tick validation)
- Thread-safe and stateless (safe to share across threads)

**SimpleAnimation** - Internal implementation of the `Animation` interface.

**Features:**
- **v1.1.0:** Stores `List<Component>` internally (was `List<String>` in v1.0)
- **v1.1.0:** `nextFrame()` returns `Component` (was `String` in v1.0)
- Maintains internal frame index with wrap-around
- Thread-safe via synchronized methods
- Defensive copy of frames list (immutable after construction)
- Modulo arithmetic for efficient cycling: `(index + 1) % frameCount`

**Methods:**
- `nextFrame()`: Returns current Component frame and advances index (synchronized)
- `getIntervalTicks()`: Returns configured interval
- `getCurrentFrameIndex()`: For testing/debugging (synchronized)
- `getFrameCount()`: Returns total frame count

### Board Implementation

**TeamBoardImpl** - Implementation of the `Board` interface using team-based rendering.

**Architecture:**
- Creates a dedicated Bukkit `Scoreboard` for each player
- Uses a single `Objective` displayed in the sidebar slot
- Pre-creates 16 `Team` objects (one per line, rows 0-15)
- Each team has a unique entry string generated with invisible color codes
- Updates are flicker-free by modifying team prefix/suffix instead of recreating scores
- Holds reference to Heartbeat for animation task scheduling
- **v1.1.0:** Adventure-First architecture - all internal processing uses Components

**Constructor:**
- Accepts `Player` and `Heartbeat` parameters
- Initializes scoreboard, objective, teams, and animation tracking maps
- `TITLE_ROW = -1` constant for title animation tracking

**Key Methods (v1.1.0 - Component-based, primary):**
- `setTitle(Component)`: Cancels title animation, updates objective display name with Component
- `setAnimatedTitle(Animation)`: Schedules animation task, shows first frame immediately
- `setLine(int, Component)`: Cancels line animation, splits Component, updates team prefix/suffix
- `setLine(int, Component, int)`: Sets line with custom update interval
- `setAnimatedLine(int, Animation)`: Schedules animation task, shows first frame immediately
- `removeLine(int)`: Cancels animation, resets score, clears team prefix/suffix
- `destroy()`: Clears animations, unregisters teams/objective, resets scoreboard

**Legacy Methods (v1.0 - String-based, wrappers):**
- `@Deprecated setTitle(String)`: Converts String to Component via LegacySupport, delegates to setTitle(Component)
- `@Deprecated setLine(int, String)`: Converts String to Component via LegacySupport, delegates to setLine(int, Component)
- `@Deprecated setLine(int, String, int)`: Converts String to Component via LegacySupport, delegates to setLine(int, Component, int)

**Text Splitting (v1.1.0):**
- Uses **ComponentLineSplitter** for Component→prefix/suffix splitting (primary path)
- Preserves all Component styling across split boundary
- Team API calls use Component methods: `team.prefix(Component)` and `team.suffix(Component)`

**Animation Support:**
- `Map<Integer, Animation> activeAnimations`: Tracks animations by row (or TITLE_ROW)
- `Animation titleAnimation`: Stores current title animation
- `getAnimation(int row)`: Public method for Heartbeat to retrieve animations
- `updateTitleDirect(Component)`: Internal method to update title without canceling animation
- `updateLineDirect(int, Component)`: Internal method to update line without canceling animation
- `cancelTitleAnimation()`: Removes title animation from tracking
- `cancelLineAnimation(int)`: Removes line animation from tracking
- `generateAnimationId(int)`: Creates unique animation ID for task tracking

**Animation Flow:**
1. User calls `setAnimatedTitle()` or `setAnimatedLine()`
2. Previous animation cancelled if exists
3. Animation stored in `activeAnimations` map
4. `UpdateTask` created with `TITLE_ANIMATION` or `LINE_ANIMATION` type
5. Task scheduled with Heartbeat at current tick + interval
6. First frame displayed immediately via direct update (Component-based in v1.1.0)
7. Animation re-registered (since direct update clears it)
8. Heartbeat executes task when due, calls `getAnimation()` and `nextFrame()`
9. **Animation returns Component frame (v1.1.0 change)**
10. **For String-based animations: PlaceholderAPI replaces placeholders in the frame before display**
11. Task automatically rescheduled due to `isRecurring() = true`
12. Process repeats until animation cancelled or board destroyed

**Placeholder Support:**
- `setTitle(String)`: Automatically detects placeholders with `PAPIHook.hasPlaceholders()`
- `setLine(int, String, int)`: Automatically detects placeholders
- If placeholders detected, schedules recurring `TITLE_UPDATE` or `LINE_UPDATE` task
- Uses specified `updateIntervalTicks` or defaults to 1 tick for titles
- Placeholders replaced via PAPIHook before each update
- Works with or without PlaceholderAPI installed (graceful fallback)

**Private Helper Methods:**
- `scheduleRecurringTitleUpdate(String, int)`: Schedules TITLE_UPDATE task
- `scheduleRecurringLineUpdate(int, String, int)`: Schedules LINE_UPDATE task

**Thread Safety:**
- All state-modifying methods check `Bukkit.isPrimaryThread()`
- Throws `IllegalStateException` with descriptive message if called from wrong thread

### PlaceholderAPI Integration

**PAPIHook** - Safe PlaceholderAPI integration with automatic detection.

**Features:**
- Automatically detects PlaceholderAPI availability at plugin startup
- Graceful fallback when PlaceholderAPI not installed
- Thread-safe placeholder replacement
- Static utility method for placeholder detection

**Methods:**
- `PAPIHook(Logger)`: Constructor that detects PAPI availability
- `setPlaceholders(Player, String)`: Replaces placeholders or returns original text
- `isAvailable()`: Returns true if PlaceholderAPI is functional
- `hasPlaceholders(String)`: Static method to check if text contains % patterns

**Detection Logic:**
1. Checks if "PlaceholderAPI" plugin is loaded
2. Attempts to load `me.clip.placeholderapi.PlaceholderAPI` class
3. Logs result (INFO level for success/not found, WARNING for errors)
4. Safe fallback if detection fails

**Replacement Logic:**
- If PAPI available: Calls `PlaceholderAPI.setPlaceholders(player, text)`
- If PAPI unavailable: Returns text unchanged
- Exception handling: Returns original text on error, logs warning

**Placeholder Detection Heuristic:**
- Looks for first `%` character
- Checks for second `%` after the first
- Returns true if both found (pattern: `%...%`)
- Simple and fast, doesn't validate placeholder validity

**Usage in Heartbeat:**
- `executeTask()` calls `papiHook.setPlaceholders()` for all task types
- Applied to LINE_UPDATE, LINE_ANIMATION, TITLE_UPDATE, TITLE_ANIMATION
- Placeholders replaced just before board update
- Player online check before replacement

**Usage in TeamBoardImpl:**
- `setTitle()` detects placeholders, schedules recurring updates
- `setLine()` detects placeholders, schedules recurring updates with custom interval
- Animation frames have placeholders replaced during execution

### Manager System

**BoardManagerImpl** - Implementation of the `BoardManager` interface.

**Architecture:**
- Uses `ConcurrentHashMap<UUID, Board>` for thread-safe board storage
- Coordinates with Heartbeat for board registration and task management
- Enforces main-thread access for all modification operations
- Provides cleanup utilities for plugin shutdown

**Key Methods:**
- `createBoard(Player)`: Creates new board, registers with Heartbeat, stores in map
  - Automatically removes existing board if present
  - Returns the newly created `Board` instance
- `getBoard(Player)`: Returns `Optional<Board>` from map lookup (thread-safe read)
- `removeBoard(Player)`: Unregisters from Heartbeat, destroys board, removes from map
  - Cancels all scheduled tasks via Heartbeat
  - Calls `TeamBoardImpl.destroy()` for cleanup
  - Resets player's scoreboard to server default
- `removeAllBoards()`: Cleans up all boards (called during plugin shutdown)
  - Iterates through all active boards
  - Handles both online and offline players gracefully
  - Logs summary of cleanup operation

**Additional Features:**
- `getActiveBoardCount()`: Returns number of active boards (monitoring/debugging)
- `hasBoard(Player)`: Convenience method to check if player has a board
- `getHeartbeat()`: Provides access to Heartbeat instance (testing/debugging)
- Fine-grained logging with player names and UUIDs

**Lifecycle Integration:**
- Constructor receives Heartbeat and Logger instances
- Registers new boards with Heartbeat on creation
- Unregisters boards from Heartbeat on removal
- Ensures proper cleanup during plugin shutdown

### Utilities

**LineSplitter** - Legacy text splitter for String-based prefix/suffix division (v1.0).

**Features:**
- Splits String text at optimal point to fit within prefix/suffix limits (128 chars each)
- Preserves color codes across the prefix/suffix boundary
- Extracts and carries over formatting codes (color, bold, italic, etc.)
- Handles both § and & color code formats
- Returns `SplitResult` record with prefix and suffix

**Color Code Handling:**
- Color codes (0-9, a-f) reset formatting
- Format codes (k-o) stack on top of colors
- Reset code (r) clears all formatting
- Last active color and format codes are prepended to suffix

**Note:** In v1.1.0, this is still used internally but ComponentLineSplitter is the primary splitter for the Adventure-First architecture.

**ComponentLineSplitter** - Modern Component splitter for Adventure-based prefix/suffix division (v1.1.0).

**Features:**
- Splits Component trees at 16-character boundary for scoreboard entry names
- Preserves all Component styling (colors, decorations, hover/click events) across split
- Handles complex nested Component structures
- Returns `SplitResult` record with Component prefix and Component suffix
- Thread-safe (static methods, no mutable state)

**Algorithm:**
1. Flatten Component tree into list of `TextPart` objects (text + style)
2. Find split point at 16-character boundary (Minecraft team entry limit)
3. Build prefix Component from parts before split
4. Build suffix Component from remaining parts
5. Carry last prefix style to first suffix character for visual continuity

**Constants:**
- `MAX_ENTRY_LENGTH = 16` (Minecraft scoreboard entry name limit)
- `MAX_PREFIX_LENGTH = 128` (team prefix character limit)
- `MAX_SUFFIX_LENGTH = 128` (team suffix character limit)

**Example:**
```java
Component line = Component.text("This is a very long line with RGB colors", TextColor.color(0xFF5733));
SplitResult result = ComponentLineSplitter.split(line);
Component prefix = result.prefix();  // "This is a very l" (16 chars) with RGB color
Component suffix = result.suffix();  // "ong line with RGB colors" with RGB color preserved
```

### Scheduler System

**Heartbeat** - Central task scheduler for all scoreboard updates.

**Architecture:**
- Single `BukkitRunnable` that executes every server tick
- Uses `PriorityQueue<UpdateTask>` (min-heap) for chronological task processing
- Tracks tasks by board UUID for efficient cancellation
- Maintains map of active boards for task execution
- Thread-safe task scheduling with concurrent data structures
- **Holds reference to PAPIHook for placeholder replacement**

**Performance Benefits:**
- Only one scheduler task runs per tick (minimal overhead)
- Tasks processed in chronological order (O(log n) insertion/removal)
- Scales to thousands of boards without performance degradation
- Supports per-line custom update intervals

**Constructor:**
- Accepts `Plugin` and `PAPIHook` parameters
- Validates both not null

**Key Methods:**
- `start()`: Starts the BukkitRunnable timer task (every 1 tick)
- `stop()`: Cancels the task and clears all pending work
- `tick()`: Main execution loop - processes all due tasks from queue
- `scheduleTask(UpdateTask)`: Adds task to priority queue
- `registerBoard(UUID, TeamBoardImpl)`: Registers board for task execution
- `cancelTasksForBoard(UUID)`: Removes all tasks for a specific board
- `executeTask(UpdateTask)`: Performs the actual update operation
  - **Checks if player is online before execution**
  - **v1.1.0:** Uses instanceof pattern matching to detect String vs other types
  - **v1.1.0:** Calls `LegacySupport.toComponent()` for String-based updates
  - `LINE_UPDATE`: If textObject is String, replaces placeholders, converts to Component, calls `board.updateLineDirect(row, component)`
  - `LINE_ANIMATION`: Gets animation, calls `nextFrame()` (returns Component), updates line directly
  - `TITLE_UPDATE`: If textObject is String, replaces placeholders, converts to Component, calls `board.updateTitleDirect(component)`
  - `TITLE_ANIMATION`: Gets animation, calls `nextFrame()` (returns Component), updates title directly

**Lifecycle:**
- Started in plugin `onEnable()`
- Stopped in plugin `onDisable()`
- Automatically reschedules recurring tasks after execution

**Animation Execution:**
- When animation task executes, Heartbeat retrieves animation from board
- Calls `animation.nextFrame()` to get next Component frame (v1.1.0: returns Component, not String)
- **For legacy String animations:** Frames were converted to Components during AnimationFactory creation
- **PlaceholderAPI:** Not supported in Component frames (Components don't contain placeholder strings)
- Uses direct update methods to avoid canceling the animation
- Task automatically rescheduled for next update interval

**Placeholder Execution (String-based updates only):**
- When placeholder task executes, text retrieved from UpdateTask.textObject
- **v1.1.0:** Uses instanceof to check if textObject is a String
- **If String:** Replaces placeholders via PAPIHook, converts to Component via LegacySupport
- **If not String:** Logs warning (unexpected for UPDATE tasks)
- Updates board with processed Component
- Task automatically rescheduled for next update interval

**UpdateTask** - Record representing a scheduled update operation.

**Fields:**
- `TaskType type`: LINE_UPDATE, LINE_ANIMATION, TITLE_UPDATE, or TITLE_ANIMATION
- `UUID boardId`: Player UUID who owns the board
- `long executionTick`: Server tick when task should execute
- `int row`: Row number (1-15) for line updates, -1 for title
- `Object textObject`: **v1.1.0 change:** Was `String text` in v1.0, now `Object` to allow flexibility
  - For LINE_UPDATE/TITLE_UPDATE: Contains String (for placeholder resolution)
  - For animations: Empty string or null (animation retrieved from board)
- `String animationId`: Optional animation identifier
- `int intervalTicks`: Repeat interval (0 for one-time tasks)

**Features:**
- Implements `Comparable<UpdateTask>` for PriorityQueue ordering
- `reschedule(long)`: Creates new task for next execution (recurring tasks)
- `isRecurring()`: Checks if task should repeat
- Immutable record with validation in compact constructor
- **v1.1.0:** textObject defaults to empty string if null

### Plugin Configuration

**plugin.yml:**
- Name: ScoreFX
- Version: 1.1.0-SNAPSHOT (substituted from Maven)
- API Version: 1.21
- Load Priority: STARTUP (ensures early availability)
- Soft Dependency: PlaceholderAPI

**Build Configuration:**
- **v1.1.0:** Adventure libraries (adventure-api, adventure-text-serializer-legacy) shaded into plugin
- Adventure relocated to `com.dripps.scorefx.libs.adventure` to prevent conflicts
- Maven Shade Plugin configured to relocate JetBrains annotations to `com.dripps.scorefx.libs.annotations`
- Filters configured to exclude signature files and prevent manifest conflicts
- Resource filtering enabled for version substitution in plugin.yml
- Dependency-reduced POM generated after shading

---

*This documentation will be updated as development progresses.*
