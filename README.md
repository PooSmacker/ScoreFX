# ScoreFX

[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/projects/jdk/21/)
[![Paper 1.21](https://img.shields.io/badge/Paper-1.21.8-blue.svg)](https://papermc.io/)
[![Version](https://img.shields.io/badge/Version-1.0--SNAPSHOT-green.svg)]()

ScoreFX is a high-performance, developer-focused Scoreboard API plugin for Paper 1.21.8. It provides a clean, efficient API for per‑player scoreboards with dynamic updates, animations, and optional PlaceholderAPI support.

- Paper: 1.21.8
- Java: 21
- Module layout: `scorefx-api` (API), `scorefx-core` (plugin)
- Documentation: see [docs.md](docs.md)

---

## Why ScoreFX

- Single-tick scheduler: one task drives all boards
- Priority queue scheduling: O(log n) insertion and due-time processing
- Flicker-free rendering: team prefix/suffix updates (no score churn)
- Per-player boards: each player gets an independent board
- Animations: titles and lines with precise intervals
- PlaceholderAPI: auto-detected, soft dependency
- Thread-safety rules: clear main-thread enforcement and safe reads

---

## Installation

1. Download `scorefx-core-1.0-SNAPSHOT.jar` from Releases.
2. Drop it into your server’s `plugins/` folder.
3. (Optional) Install [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/).
4. Restart the server.

No configuration files are required; everything is controlled through the API.

---

## Add to Your Plugin

### Build dependencies

Maven
```xml
<dependency>
  <groupId>com.dripps.scorefx</groupId>
  <artifactId>scorefx-api</artifactId>
  <version>1.0-SNAPSHOT</version>
  <scope>provided</scope>
</dependency>
```

Gradle
```gradle
dependencies {
    compileOnly 'com.dripps.scorefx:scorefx-api:1.0-SNAPSHOT'
}
```

plugin.yml
```yaml
name: YourPlugin
version: 1.0.0
main: your.package.YourPlugin
api-version: '1.21'

depend:
  - ScoreFX
```

---

## Getting the API

Use Bukkit’s Services Manager. Fail fast if the service is missing.

```java
import com.dripps.scorefx.api.ScoreFX;
import com.dripps.scorefx.api.BoardManager;
import com.dripps.scorefx.api.animation.AnimationFactory;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class YourPlugin extends JavaPlugin {

    private BoardManager boards;
    private AnimationFactory animations;

    @Override
    public void onEnable() {
        ScoreFX api = Bukkit.getServicesManager().load(ScoreFX.class);
        if (api == null) {
            getLogger().severe("ScoreFX service not found. Is the plugin installed and enabled?");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.boards = api.getBoardManager();
        this.animations = api.getAnimationFactory();
    }

    public BoardManager boards() { return boards; }
    public AnimationFactory animations() { return animations; }
}
```

---

## Basic Usage

Create a board for a player and set content. Rows are 1–15 (1 = bottom, 15 = top).

```java
import com.dripps.scorefx.api.Board;
import org.bukkit.entity.Player;

public void showBoard(Player player) {
    Board board = boards().createBoard(player);

    board.setTitle("&6&lMy Server");
    board.setLine(15, "&7------------------");
    board.setLine(14, "&ePlayer: &f%player_name%");
    board.setLine(13, "&eRank: &f%vault_rank%");
    board.setLine(12, "");
    board.setLine(11, "&eBalance: &a$%vault_eco_balance%");
    board.setLine(10, "&7------------------");
}
```

Remove a line or the entire board:

```java
board.removeLine(12);                // Clear a line
boards().removeBoard(player);        // Destroy player’s board
```

---

## Lifecycle Example

Create a board on join and let ScoreFX handle cleanup on quit. ScoreFX automatically removes boards on player quit and on plugin disable, but you can also manage it explicitly if needed.

```java
import com.dripps.scorefx.api.Board;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;

public final class JoinListener implements Listener {
    private final YourPlugin plugin;

    public JoinListener(YourPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        Board board = plugin.boards().createBoard(p);
        board.setTitle("&e&lWelcome");
        board.setLine(15, "&7------------------");
        board.setLine(14, "&f%player_name%");
        board.setLine(13, "&7Enjoy your stay!");
        board.setLine(12, "&7------------------");

        // Example: placeholder line with a cheaper interval (every tick)
        board.setLine(11, "&eOnline: &f%server_online%");
    }
}
```

Register your listener in `onEnable()`:
```java
getServer().getPluginManager().registerEvents(new JoinListener(this), this);
```

---

## Performance: Custom Update Intervals

Use custom intervals for expensive placeholders to reduce load.

```java
// Expensive placeholder (e.g., economy) → update every 5 seconds (100 ticks)
board.setLine(11, "&eBalance: &a$%vault_eco_balance%", 100);

// Cheaper placeholders can remain at the default (every tick if placeholders are present)
board.setLine(14, "&ePlayer: &f%player_name%");
```

Note:
- If a string contains placeholders, ScoreFX will auto-schedule updates.
- Without placeholders, the text is static until you change it.
- When you pass an interval explicitly, ScoreFX uses it for that line’s updates.

---

## Animations

Titles and lines support frame-based animations. You control the interval per animation.

```java
import com.dripps.scorefx.api.animation.Animation;
import com.dripps.scorefx.api.animation.AnimationFactory;

// Title animation: 0.5s per frame (10 ticks)
Animation title = animations().fromFrames(
    java.util.List.of(
        "&6&lM&e&ly Server",
        "&e&lM&6&ly Server",
        "&6&lMy &e&lServer",
        "&e&lMy &6&lServer"
    ),
    10
);
board.setAnimatedTitle(title);

// Line animation: 1s per frame (20 ticks)
Animation status = animations().fromFrames(
    java.util.List.of("&a● &7Online", "&e● &7Online", "&c● &7Online"),
    20
);
board.setAnimatedLine(13, status);
```

You can include placeholders inside frames. They are resolved per-frame just before rendering:

```java
Animation greeting = animations().fromFrames(
    java.util.List.of(
        "&aWelcome &f%player_name%!",
        "&eWelcome &f%player_name%!",
        "&cWelcome &f%player_name%!"
    ),
    15
);
board.setAnimatedLine(14, greeting);
```

---

## Thread Safety

All mutating operations must run on the main server thread. ScoreFX enforces this and throws `IllegalStateException` when violated.

- Safe from any thread:
    - `boards().getBoard(player)`
    - `board.getPlayer()`

- Main thread only:
    - `boards().createBoard(player)`
    - `boards().removeBoard(player)`
    - `board.setTitle(...)`, `board.setAnimatedTitle(...)`
    - `board.setLine(...)`, `board.setAnimatedLine(...)`
    - `board.removeLine(...)`

Scheduling example:

```java
// Wrong: async mutation
Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
    board.setTitle("&cThis will throw"); // IllegalStateException
});

// Correct: switch to main thread
Bukkit.getScheduler().runTask(plugin, () -> {
    board.setTitle("&aSafe update");
});
```

---

## PlaceholderAPI

- Works automatically if PlaceholderAPI is installed.
- All text inputs support `&` and `§` color codes and `%placeholders%`.
- Placeholders are resolved right before rendering (keeps data fresh).
- If PlaceholderAPI is not present, text is used as-is.

---

## Building from Source

```bash
git clone https://github.com/dripps/scorefx.git
cd scorefx
mvn clean package
```

Artifacts:
- `scorefx-api/target/scorefx-api-1.0-SNAPSHOT.jar`
- `scorefx-core/target/scorefx-core-1.0-SNAPSHOT.jar`

---

## FAQ

- Do I need PlaceholderAPI?
    - No. It’s optional. If installed, placeholders in your text will be resolved automatically.
- How many lines can I use?
    - 1–15. Row 1 is bottom, 15 is top.
- Do I have to manually clean up?
    - ScoreFX automatically cleans on player quit and on plugin disable. You can still remove boards yourself if you prefer explicit control.

---

## Support

- Documentation: see [docs.md](docs.md)
- Issues: use the GitHub issue tracker
- API Javadocs: included in the API JAR

---

## License

MIT License

## Credits

- Author: Dripps
- Platform: PaperMC
