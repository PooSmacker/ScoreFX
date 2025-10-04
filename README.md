# ScoreFX

[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/projects/jdk/21/)
[![Paper 1.21](https://img.shields.io/badge/Paper-1.21.8-blue.svg)](https://papermc.io/)
[![Version](https://img.shields.io/badge/Version-1.0--SNAPSHOT-green.svg)]()

**ScoreFX** is a high-performance, developer-first Scoreboard API plugin for Paper 1.21.8. Designed for developers who need a robust, efficient, and feature-rich scoreboard system.

## Features

### 🚀 High Performance
- **Single-tick Scheduler**: Only one BukkitRunnable runs every tick, managing all boards efficiently
- **Priority Queue Architecture**: Tasks processed in chronological order using a min-heap (O(log n) operations)
- **Flicker-Free Updates**: Team-based rendering ensures seamless visual updates
- **Scalable**: Handles thousands of boards without performance degradation

### 🎨 Rich Feature Set
- **Per-Player Scoreboards**: Each player has an independent, customizable scoreboard
- **Animations**: Built-in support for animated titles and lines
- **PlaceholderAPI Integration**: Automatic placeholder detection and replacement (soft dependency)
- **Custom Update Intervals**: Optimize performance by controlling how often lines update
- **Dynamic Line Management**: Add, update, and remove lines on the fly

### 👨‍💻 Developer-First Design
- **Clean API**: Simple, intuitive methods with comprehensive Javadoc
- **Thread Safety**: Enforced main-thread access with clear error messages
- **Automatic Cleanup**: Resources cleaned up on player quit and server shutdown
- **Service Manager Integration**: Easy access via Bukkit Services Manager
- **No Dependencies**: Works standalone, PlaceholderAPI is optional

## Installation

1. Download the latest `scorefx-core-1.0-SNAPSHOT.jar` from the releases
2. Place the JAR in your server's `plugins` folder
3. (Optional) Install [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) for placeholder support
4. Restart your server

## Quick Start

### For Plugin Developers

#### 1. Add ScoreFX to your project

**Maven:**
```xml
<dependency>
    <groupId>com.dripps.scorefx</groupId>
    <artifactId>scorefx-api</artifactId>
    <version>1.0-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

**Gradle:**
```gradle
dependencies {
    compileOnly 'com.dripps.scorefx:scorefx-api:1.0-SNAPSHOT'
}
```

#### 2. Add ScoreFX as a dependency in your plugin.yml

```yaml
depend:
  - ScoreFX
```

#### 3. Get the API and create a board

```java
import com.dripps.scorefx.api.ScoreFX;
import com.dripps.scorefx.api.Board;
import com.dripps.scorefx.api.BoardManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

// Get the API from Services Manager
ScoreFX api = Bukkit.getServicesManager().load(ScoreFX.class);
BoardManager boardManager = api.getBoardManager();

// Create a board for a player
Board board = boardManager.createBoard(player);

// Set the title
board.setTitle("&6&lMy Server");

// Add lines (row 1 = bottom, row 15 = top)
board.setLine(15, "&7------------------");
board.setLine(14, "&ePlayer: &f%player_name%");
board.setLine(13, "&eRank: &f%vault_rank%");
board.setLine(12, "");
board.setLine(11, "&eBalance: &a$%vault_eco_balance%");
board.setLine(10, "&7------------------");
```

## API Documentation

### Creating Boards

```java
// Create a new board (replaces existing if present)
Board board = boardManager.createBoard(player);

// Get an existing board
Optional<Board> optBoard = boardManager.getBoard(player);

// Remove a board
boardManager.removeBoard(player);
```

### Setting Static Content

```java
// Set a static title
board.setTitle("&6&lMy Server");

// Set a static line (updates every tick if it contains placeholders)
board.setLine(5, "&eOnline: &f%server_online%");

// Set a static line with custom update interval (every 5 seconds = 100 ticks)
board.setLine(5, "&eBalance: &a$%vault_eco_balance%", 100);
```

### Creating Animations

```java
AnimationFactory factory = api.getAnimationFactory();

// Create a title animation (changes every 10 ticks = 0.5 seconds)
Animation titleAnim = factory.fromFrames(
    List.of(
        "&6&lM&e&ly Server",
        "&e&lM&6&ly Server",
        "&6&lMy &e&lServer",
        "&e&lMy &6&lServer"
    ),
    10
);
board.setAnimatedTitle(titleAnim);

// Create a line animation
Animation lineAnim = factory.fromFrames(
    List.of(
        "&a● &7Online",
        "&e● &7Online",
        "&c● &7Online"
    ),
    20  // Changes every 20 ticks = 1 second
);
board.setAnimatedLine(13, lineAnim);
```

### Managing Lines

```java
// Remove a line
board.removeLine(5);

// Get the player who owns the board
Player owner = board.getPlayer();
```

## Advanced Features

### Performance Optimization

For lines with expensive placeholders (database queries, complex calculations), use custom update intervals:

```java
// Update balance every 5 seconds instead of every tick
board.setLine(11, "&eBalance: &a$%vault_eco_balance%", 100);

// Update statistics every 10 seconds
board.setLine(10, "&eKills: &f%stats_kills%", 200);
```

### Mixing Animations and Placeholders

Animation frames can contain placeholders:

```java
Animation anim = factory.fromFrames(
    List.of(
        "&aWelcome &f%player_name%!",
        "&eWelcome &f%player_name%!",
        "&cWelcome &f%player_name%!"
    ),
    15
);
board.setAnimatedLine(14, anim);
// PlaceholderAPI will replace %player_name% in each frame
```

### Thread Safety

**Important:** All methods that modify board state must be called from the main thread:

```java
// ✅ CORRECT - Called from main thread
board.setTitle("Title");

// ❌ WRONG - Will throw IllegalStateException
Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
    board.setTitle("Title"); // Error!
});

// ✅ CORRECT - Schedule sync task
Bukkit.getScheduler().runTask(plugin, () -> {
    board.setTitle("Title");
});
```

Safe methods (can be called from any thread):
- `boardManager.getBoard(player)`
- `board.getPlayer()`

## Architecture

### Multi-Module Structure

```
scorefx-parent
├── scorefx-api          (Pure API interfaces)
└── scorefx-core         (Implementation)
```

### Key Components

- **Heartbeat Scheduler**: Single BukkitRunnable managing all updates
- **TeamBoardImpl**: Flicker-free rendering using Bukkit teams
- **BoardManager**: Lifecycle management for all boards
- **PAPIHook**: Safe PlaceholderAPI integration with fallback
- **UpdateTask**: Priority queue task system

## Building from Source

### Prerequisites
- Java 21 JDK
- Maven 3.6+
- Git

### Build Steps

```bash
# Clone the repository
git clone https://github.com/dripps/scorefx.git
cd scorefx

# Build with Maven
mvn clean package

# Output JARs will be in:
# - scorefx-api/target/scorefx-api-1.0-SNAPSHOT.jar (API only)
# - scorefx-core/target/scorefx-core-1.0-SNAPSHOT.jar (Plugin with shaded dependencies)
```

## Configuration

ScoreFX requires no configuration files - it's pure API. Configuration is done programmatically through the API.

## Support

- **Documentation**: See [docs.md](docs.md) for comprehensive documentation
- **Issues**: Report bugs on the GitHub issue tracker
- **API Questions**: Check the Javadocs (included in API JAR)

## Technical Details

### Performance Metrics

- **Tick Processing**: O(k log n) where k = tasks due this tick, n = total tasks
- **Task Insertion**: O(log n) priority queue insertion
- **Memory Usage**: O(t) where t = total scheduled tasks
- **Board Creation**: O(1) constant time

### Dependencies

**Runtime:**
- Paper API 1.21.8 (provided by server)
- PlaceholderAPI 2.11.6 (optional, soft dependency)

**Shaded (included in plugin):**
- JetBrains Annotations 24.1.0 (relocated to prevent conflicts)
- scorefx-api 1.0-SNAPSHOT

## License

This project is provided as-is for educational and development purposes.

## Credits

- **Author**: Dripps
- **AI Assistant**: GitHub Copilot
- **Platform**: Paper MC
- **Inspiration**: Modern scoreboard plugins and best practices

---

**Made with ❤️ for the Minecraft plugin development community**
