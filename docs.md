# ScoreFX Documentation

**Version:** 1.0-SNAPSHOT  
**Target:** Paper 1.21.8  
**Last Updated:** October 4, 2025

---

## Overview

ScoreFX is a high-performance, developer-first Scoreboard API plugin for Paper servers. It provides a clean, intuitive API for creating per-player scoreboards with support for dynamic updates, animations, and PlaceholderAPI integration.

### Key Features

- **Per-Player Scoreboards**: Each player can have a unique, independent scoreboard
- **Flicker-Free Rendering**: Updates are seamless using the Team-based prefix/suffix method
- **Dynamic Line Updates**: Lines can be set, updated, and removed without visual artifacts
- **Animation Support**: Built-in support for animated titles and lines
- **Custom Update Intervals**: Optimize performance by controlling how often lines update
- **PlaceholderAPI Integration**: Soft dependency with automatic placeholder resolution
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

**Methods:**
- `void setTitle(String title)` - Set a static title
- `void setAnimatedTitle(Animation titleAnimation)` - Set an animated title
- `void setLine(int row, String text)` - Set a line with default update interval (every tick)
- `void setLine(int row, String text, int updateIntervalTicks)` - Set a line with custom update interval
- `void setAnimatedLine(int row, Animation animation)` - Set an animated line
- `void removeLine(int row)` - Clear a line
- `Player getPlayer()` - Get the board owner

**Row Numbering:**
- Rows are numbered 1-15
- Row 1 appears at the BOTTOM
- Row 15 appears at the TOP

#### Animation
Represents a sequence of text frames.

**Methods:**
- `String nextFrame()` - Get the next frame in the sequence
- `int getIntervalTicks()` - Get the interval between frames

#### AnimationFactory
Factory for creating animations.

**Methods:**
- `Animation fromFrames(List<String> frames, int intervalTicks)` - Create a cycling animation

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

### Setting Static Content

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
- `Board.setTitle(String)`
- `Board.setAnimatedTitle(Animation)`
- `Board.setLine(...)`
- `Board.setAnimatedLine(...)`
- `Board.removeLine(int)`

---

## PlaceholderAPI Integration

ScoreFX automatically detects and uses PlaceholderAPI if it's installed on the server. No additional configuration is required.

All text strings (titles, lines, animation frames) support:
- Minecraft color codes: `§` and `&` formats
- PlaceholderAPI placeholders: `%placeholder_name%`

Placeholders are resolved just before rendering, ensuring up-to-date values.

---

## Best Practices

1. **Use Custom Update Intervals**: For expensive placeholders (database queries, complex calculations), use `setLine(row, text, intervalTicks)` to reduce server load
2. **Clean Up Resources**: Always remove boards when they're no longer needed (ScoreFX handles this automatically on player quit)
3. **Avoid Rapid Updates**: Don't update the same line from multiple sources simultaneously
4. **Test Performance**: Monitor server TPS when using many animated boards
5. **Use Thread-Safe Access**: Always check if you're on the main thread before modifying boards

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

### 🔄 In Progress

None

### ⏳ Pending Tasks

- Task 9: Final Review and Cleanup

---

## Core Module (scorefx-core)

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
- Creates `SimpleAnimation` instances from frame lists
- Validates all parameters (null checks, empty checks, tick validation)
- Thread-safe and stateless (safe to share across threads)

**SimpleAnimation** - Internal implementation of the `Animation` interface.

**Features:**
- Maintains internal frame index with wrap-around
- Thread-safe via synchronized methods
- Defensive copy of frames list (immutable after construction)
- Modulo arithmetic for efficient cycling: `(index + 1) % frameCount`

**Methods:**
- `nextFrame()`: Returns current frame and advances index (synchronized)
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

**Constructor:**
- Accepts `Player` and `Heartbeat` parameters
- Initializes scoreboard, objective, teams, and animation tracking maps
- `TITLE_ROW = -1` constant for title animation tracking

**Key Methods:**
- `setTitle(String)`: Cancels title animation, updates objective display name
- `setAnimatedTitle(Animation)`: Schedules animation task, shows first frame immediately
- `setLine(int, String)`: Cancels line animation, splits text, updates team prefix/suffix
- `setLine(int, String, int)`: Sets line with custom update interval
- `setAnimatedLine(int, Animation)`: Schedules animation task, shows first frame immediately
- `removeLine(int)`: Cancels animation, resets score, clears team prefix/suffix
- `destroy()`: Clears animations, unregisters teams/objective, resets scoreboard

**Animation Support:**
- `Map<Integer, Animation> activeAnimations`: Tracks animations by row (or TITLE_ROW)
- `Animation titleAnimation`: Stores current title animation
- `getAnimation(int row)`: Public method for Heartbeat to retrieve animations
- `updateTitleDirect(String)`: Internal method to update title without canceling animation
- `updateLineDirect(int, String)`: Internal method to update line without canceling animation
- `cancelTitleAnimation()`: Removes title animation from tracking
- `cancelLineAnimation(int)`: Removes line animation from tracking
- `generateAnimationId(int)`: Creates unique animation ID for task tracking

**Animation Flow:**
1. User calls `setAnimatedTitle()` or `setAnimatedLine()`
2. Previous animation cancelled if exists
3. Animation stored in `activeAnimations` map
4. `UpdateTask` created with `TITLE_ANIMATION` or `LINE_ANIMATION` type
5. Task scheduled with Heartbeat at current tick + interval
6. First frame displayed immediately via direct update
7. Animation re-registered (since direct update clears it)
8. Heartbeat executes task when due, calls `getAnimation()` and `nextFrame()`
9. **PlaceholderAPI replaces placeholders in the frame before display**
10. Task automatically rescheduled due to `isRecurring() = true`
11. Process repeats until animation cancelled or board destroyed

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

**LineSplitter** - Intelligent text splitter for team prefix/suffix division.

**Features:**
- Splits text at optimal point to fit within prefix/suffix limits (128 chars each)
- Preserves color codes across the prefix/suffix boundary
- Extracts and carries over formatting codes (color, bold, italic, etc.)
- Handles both § and & color code formats
- Returns `SplitResult` record with prefix and suffix

**Color Code Handling:**
- Color codes (0-9, a-f) reset formatting
- Format codes (k-o) stack on top of colors
- Reset code (r) clears all formatting
- Last active color and format codes are prepended to suffix

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
  - **Calls `papiHook.setPlaceholders()` for all task types**
  - `LINE_UPDATE`: Replaces placeholders, calls `board.updateLineDirect(row, text)`
  - `LINE_ANIMATION`: Gets animation, calls `nextFrame()`, replaces placeholders, updates line
  - `TITLE_UPDATE`: Replaces placeholders, calls `board.updateTitleDirect(text)`
  - `TITLE_ANIMATION`: Gets animation, calls `nextFrame()`, replaces placeholders, updates title

**Lifecycle:**
- Started in plugin `onEnable()`
- Stopped in plugin `onDisable()`
- Automatically reschedules recurring tasks after execution

**Animation Execution:**
- When animation task executes, Heartbeat retrieves animation from board
- Calls `animation.nextFrame()` to get next frame
- **Replaces placeholders in the frame via PAPIHook**
- Uses direct update methods to avoid canceling the animation
- Task automatically rescheduled due to `intervalTicks > 0`

**Placeholder Execution:**
- When placeholder task executes, text retrieved from UpdateTask
- **Replaces placeholders via PAPIHook**
- Updates board with processed text
- Task automatically rescheduled for next update interval

**UpdateTask** - Record representing a scheduled update operation.

**Fields:**
- `TaskType type`: LINE_UPDATE, LINE_ANIMATION, TITLE_UPDATE, or TITLE_ANIMATION
- `UUID boardId`: Player UUID who owns the board
- `long executionTick`: Server tick when task should execute
- `int row`: Row number (1-15) for line updates, -1 for title
- `String text`: Text to display (may contain placeholders)
- `String animationId`: Optional animation identifier
- `int intervalTicks`: Repeat interval (0 for one-time tasks)

**Features:**
- Implements `Comparable<UpdateTask>` for PriorityQueue ordering
- `reschedule(long)`: Creates new task for next execution (recurring tasks)
- `isRecurring()`: Checks if task should repeat
- Immutable record with validation in compact constructor

### Plugin Configuration

**plugin.yml:**
- Name: ScoreFX
- API Version: 1.21
- Load Priority: STARTUP (ensures early availability)
- Soft Dependency: PlaceholderAPI

**Build Configuration:**
- Maven Shade Plugin configured to relocate JetBrains annotations to `com.dripps.scorefx.libs.annotations`
- Filters configured to exclude signature files
- Resource filtering enabled for version substitution

---

*This documentation will be updated as development progresses.*
