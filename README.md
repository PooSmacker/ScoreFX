
# ScoreFX

A high-performance, dependency-free scoreboard API for Paper servers. Build clean per-player scoreboards with animations, custom score formatting, and modern text support.

[![MIT License](https://img.shields.io/badge/License-MIT-green.svg)](https://choosealicense.com/licenses/mit/)
[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/projects/jdk/21/)
[![Paper 1.21](https://img.shields.io/badge/Paper-1.21.8-blue.svg)](https://papermc.io/)
[![Version](https://img.shields.io/badge/Version-2.0.0--SNAPSHOT-green.svg)]()

## Features

- **Zero Dependencies** - No ProtocolLib or external libraries required
- **Direct NMS** - Pure packet manipulation with MethodHandles for maximum performance
- **Modern Text** - Native Adventure Component API with RGB colors and gradients
- **Custom Scores** - Hide numbers or show custom text on the right side
- **Smooth Animations** - Title and line animations with precise timing control
- **PlaceholderAPI** - Optional soft dependency for dynamic placeholders
- **Thread-Safe** - Enforced main-thread operations with safe concurrent reads


## Installation

Download the prebuilt jar from releases if you're a server owner and a plugin requires it.

-  Download `scorefx-core-2.0.0-SNAPSHOT.jar` from [Releases](https://github.com/PooSmacker/ScoreFX/releases)

-  Place it in your server's `plugins/` folder

- _(Optional)_ Install [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) for placeholder support

- Restart the server


### Add to Your Plugin (JitPack)

Maven (pom.xml)
```xml
<repository>
  <id>jitpack.io</id>
  <url>https://jitpack.io</url>
</repository>
```
```xml
<dependency>
  <groupId>com.github.PooSmacker.ScoreFX</groupId>
  <artifactId>scorefx-api</artifactId>
  <version>v2.0.0</version>
  <scope>provided</scope>
</dependency>
```

Gradle (settings.gradle)
```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

Gradle (build.gradle)
```gradle
dependencies {
    compileOnly 'com.github.PooSmacker.ScoreFX:scorefx-api:v2.0.0'
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

# ScoreFX Examples

A comprehensive guide showcasing ScoreFX 2.0's features with practical examples.


### Getting the API

```java
import com.dripps.scorefx.api.ScoreFX;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class MyPlugin extends JavaPlugin {
  private ScoreFX scorefx;

  @Override
  public void onEnable() {
    RegisteredServiceProvider<ScoreFX> provider =
            Bukkit.getServicesManager().getRegistration(ScoreFX.class);

    if (provider == null) {
      getLogger().severe("ScoreFX not found!");
      return;
    }

    this.scorefx = provider.getProvider();
  }
}
```

---

## Basic Examples

### Example 1: Simple Static Scoreboard

```java
import com.dripps.scorefx.api.Board;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public void createSimpleBoard(Player player) {
  Board board = scorefx.getBoardManager().createBoard(player);

  // Set the title
  board.setTitle(Component.text("MY SERVER", NamedTextColor.GOLD));

  // Add static lines (scores hidden by default)
  board.setLine(5, Component.text("Welcome!", NamedTextColor.GREEN));
  board.setLine(4, Component.empty()); // Spacer
  board.setLine(3, Component.text("Players Online:", NamedTextColor.GRAY));
  board.setLine(2, Component.text("Rank:", NamedTextColor.GRAY));
  board.setLine(1, Component.text("mc.example.com", NamedTextColor.AQUA));
}
```

### Example 2: Using MiniMessage

```java
import net.kyori.adventure.text.minimessage.MiniMessage;

public void createMiniMessageBoard(Player player) {
  MiniMessage mm = MiniMessage.miniMessage();
  Board board = scorefx.getBoardManager().createBoard(player);

  // Gradient title
  board.setTitle(mm.deserialize("<gradient:#FF0000:#00FF00>SERVER NAME</gradient>"));

  // Colored and formatted lines
  board.setLine(5, mm.deserialize("<rainbow>Rainbow Text!</rainbow>"));
  board.setLine(4, mm.deserialize("<bold><yellow>⚡</yellow> <white>Energy System</white></bold>"));
  board.setLine(3, mm.deserialize("<gradient:#00D2FF:#3A7BD5>Premium Server</gradient>"));
  board.setLine(2, mm.deserialize("<gray>IP: <aqua>play.server.net"));
}
```

### Example 3: Lines with Custom Scores

```java
public void createBoardWithScores(Player player) {
  Board board = scorefx.getBoardManager().createBoard(player);
  board.setTitle(Component.text("STATS", NamedTextColor.GOLD));

  // Line with custom score on the right
  board.setLine(
          5,
          Component.text("Coins:", NamedTextColor.YELLOW),
          Component.text("1,250", NamedTextColor.GOLD) // Score displays on right
  );

  board.setLine(
          4,
          Component.text("Level:", NamedTextColor.AQUA),
          Component.text("42", NamedTextColor.GREEN)
  );

  // Line without score (hidden)
  board.setLine(3, Component.text("Premium Member", NamedTextColor.LIGHT_PURPLE));
}
```

### Example 4: Dynamic Updates

```java
public void createDynamicBoard(Player player, JavaPlugin plugin) {
  Board board = scorefx.getBoardManager().createBoard(player);
  board.setTitle(Component.text("LIVE STATS", NamedTextColor.GOLD));

  // Update every second (20 ticks)
  Bukkit.getScheduler().runTaskTimer(plugin, () -> {
    scorefx.getBoardManager().getBoard(player).ifPresent(b -> {
      // Update ping
      b.setLine(
              5,
              Component.text("Ping:", NamedTextColor.WHITE),
              Component.text(player.getPing() + "ms", NamedTextColor.GREEN)
      );

      // Update health
      int health = (int) player.getHealth();
      b.setLine(
              4,
              Component.text("Health:", NamedTextColor.RED),
              Component.text(health + "/20", NamedTextColor.WHITE)
      );

      // Update online players
      int online = Bukkit.getOnlinePlayers().size();
      b.setLine(
              3,
              Component.text("Players:", NamedTextColor.GRAY),
              Component.text(String.valueOf(online), NamedTextColor.AQUA)
      );
    });
  }, 0L, 20L);
}
```

---

## Intermediate Examples

### Example 5: PlaceholderAPI Integration

```java
import me.clip.placeholderapi.PlaceholderAPI;

public void createPlaceholderBoard(Player player, JavaPlugin plugin) {
  MiniMessage mm = MiniMessage.miniMessage();
  Board board = scorefx.getBoardManager().createBoard(player);

  board.setTitle(mm.deserialize("<gradient:#FF6B6B:#4ECDC4>PLAYER INFO</gradient>"));

  Bukkit.getScheduler().runTaskTimer(plugin, () -> {
    scorefx.getBoardManager().getBoard(player).ifPresent(b -> {
      // Parse placeholders and convert to Component
      String killsTemplate = "%statistic_player_kills%";
      String kills = PlaceholderAPI.setPlaceholders(player, killsTemplate);

      b.setLine(
              5,
              mm.deserialize("<gold>⚔</gold> <white>Kills"),
              mm.deserialize("<red>" + kills)
      );

      String deathsTemplate = "%statistic_deaths%";
      String deaths = PlaceholderAPI.setPlaceholders(player, deathsTemplate);

      b.setLine(
              4,
              mm.deserialize("<dark_red>☠</dark_red> <white>Deaths"),
              mm.deserialize("<gray>" + deaths)
      );

      // Calculate K/D ratio
      double kd = kills.equals("0") ? 0.0 : Double.parseDouble(kills) / Math.max(1, Double.parseDouble(deaths));
      b.setLine(
              3,
              mm.deserialize("<yellow>📊</yellow> <white>K/D Ratio"),
              mm.deserialize("<gradient:#00FF88:#FFD93D>" + String.format("%.2f", kd) + "</gradient>")
      );
    });
  }, 0L, 40L); // Update every 2 seconds
}
```

### Example 6: Conditional Formatting

```java
public void createConditionalBoard(Player player, JavaPlugin plugin) {
  MiniMessage mm = MiniMessage.miniMessage();
  Board board = scorefx.getBoardManager().createBoard(player);

  board.setTitle(mm.deserialize("<bold><gradient:#667eea:#764ba2>STATUS</gradient></bold>"));

  Bukkit.getScheduler().runTaskTimer(plugin, () -> {
    scorefx.getBoardManager().getBoard(player).ifPresent(b -> {
      // Ping with color based on latency
      int ping = player.getPing();
      String pingColor, pingIcon;

      if (ping < 50) {
        pingColor = "#00FF88";
        pingIcon = "●";
      } else if (ping < 100) {
        pingColor = "#FFD93D";
        pingIcon = "●";
      } else if (ping < 200) {
        pingColor = "#FF9F40";
        pingIcon = "◐";
      } else {
        pingColor = "#FF6B6B";
        pingIcon = "○";
      }

      b.setLine(
              6,
              mm.deserialize("<" + pingColor + ">" + pingIcon + "</> <white>Connection"),
              mm.deserialize("<" + pingColor + ">" + ping + "ms")
      );

      // Health bar with color coding
      double health = player.getHealth();
      int healthPercent = (int) ((health / 20.0) * 100);
      String healthColor = healthPercent >= 75 ? "#00FF88" :
              healthPercent >= 50 ? "#FFD93D" :
                      healthPercent >= 25 ? "#FF9F40" : "#FF6B6B";

      b.setLine(
              5,
              mm.deserialize("<red>❤</red> <white>Health"),
              mm.deserialize("<" + healthColor + ">" + healthPercent + "%")
      );

      // Food level
      int food = player.getFoodLevel();
      String foodColor = food >= 15 ? "#FFD93D" : food >= 10 ? "#FF9F40" : "#FF6B6B";

      b.setLine(
              4,
              mm.deserialize("<gold>🍖</gold> <white>Hunger"),
              mm.deserialize("<" + foodColor + ">" + food + "/20")
      );
    });
  }, 0L, 10L);
}
```

### Example 7: Removing and Hiding Scoreboards

```java
public void manageBoardVisibility(Player player) {
  Board board = scorefx.getBoardManager().createBoard(player);

  // Show board initially
  board.setTitle(Component.text("WELCOME"));
  board.setLine(1, Component.text("Hello!"));

  // Hide board after 5 seconds
  Bukkit.getScheduler().runTaskLater(plugin, () -> {
    scorefx.getBoardManager().getBoard(player).ifPresent(Board::hide);
  }, 100L);

  // Show again after 10 seconds
  Bukkit.getScheduler().runTaskLater(plugin, () -> {
    scorefx.getBoardManager().getBoard(player).ifPresent(Board::show);
  }, 200L);

  // Completely remove board after 15 seconds
  Bukkit.getScheduler().runTaskLater(plugin, () -> {
    scorefx.getBoardManager().removeBoard(player);
  }, 300L);
}
```

### Example 8: Custom Formatting with Separators

```java
public void createFormattedBoard(Player player) {
  MiniMessage mm = MiniMessage.miniMessage();
  Board board = scorefx.getBoardManager().createBoard(player);

  board.setTitle(mm.deserialize("<gradient:#FF6B6B:#4ECDC4><bold>STATS</bold></gradient>"));

  board.setLine(15, Component.empty());
  board.setLine(14, mm.deserialize("<dark_gray><strikethrough>━━━━━━━━━━━━━━━━━━━━</strikethrough>"));
  board.setLine(13, mm.deserialize("<gradient:#667eea:#764ba2><bold>  PLAYER INFO</bold></gradient>"));
  board.setLine(12, mm.deserialize("<dark_gray><strikethrough>━━━━━━━━━━━━━━━━━━━━</strikethrough>"));
  board.setLine(11, Component.empty());

  board.setLine(10, mm.deserialize("<white>Name: <gradient:#00D2FF:#3A7BD5>" + player.getName() + "</gradient>"));
  board.setLine(9, mm.deserialize("<white>Rank: <gold><bold>VIP</bold>"));
  board.setLine(8, Component.empty());

  board.setLine(7, mm.deserialize("<dark_gray><strikethrough>━━━━━━━━━━━━━━━━━━━━</strikethrough>"));
  board.setLine(6, mm.deserialize("<gradient:#FF6B6B:#C44569><bold>  COMBAT</bold></gradient>"));
  board.setLine(5, mm.deserialize("<dark_gray><strikethrough>━━━━━━━━━━━━━━━━━━━━</strikethrough>"));
  board.setLine(4, Component.empty());

  board.setLine(3, mm.deserialize("<gold>⚔ <white>Kills:"), mm.deserialize("<red>42"));
  board.setLine(2, mm.deserialize("<dark_red>☠ <white>Deaths:"), mm.deserialize("<gray>13"));
  board.setLine(1, Component.empty());
}
```

---

## Animation Examples

### Example 9: Basic Text Animation

```java
import com.dripps.scorefx.api.animation.Animation;

public void createBasicAnimation(Player player) {
  Board board = scorefx.getBoardManager().createBoard(player);
  MiniMessage mm = MiniMessage.miniMessage();

  // Animated title that cycles colors
  Animation titleAnimation = scorefx.getAnimationFactory().fromComponents(
          List.of(
                  mm.deserialize("<red>MY SERVER"),
                  mm.deserialize("<gold>MY SERVER"),
                  mm.deserialize("<yellow>MY SERVER"),
                  mm.deserialize("<green>MY SERVER"),
                  mm.deserialize("<aqua>MY SERVER"),
                  mm.deserialize("<blue>MY SERVER"),
                  mm.deserialize("<light_purple>MY SERVER")
          ),
          10 // Change every 10 ticks (0.5 seconds)
  );
  board.setAnimatedTitle(titleAnimation);

  // Animated status line
  Animation statusAnimation = scorefx.getAnimationFactory().fromComponents(
          List.of(
                  mm.deserialize("<gray>● <white>Online"),
                  mm.deserialize("<green>● <white>Online"),
                  mm.deserialize("<dark_green>● <white>Online"),
                  mm.deserialize("<green>● <white>Online")
          ),
          5
  );
  board.setAnimatedLine(3, statusAnimation);
}
```

### Example 10: Loading Animation

```java
public void createLoadingAnimation(Player player) {
  MiniMessage mm = MiniMessage.miniMessage();
  Board board = scorefx.getBoardManager().createBoard(player);

  board.setTitle(mm.deserialize("<bold><aqua>LOADING</aqua></bold>"));

  // Animated loading bar
  Animation loadingBar = scorefx.getAnimationFactory().fromComponents(
          List.of(
                  mm.deserialize("<dark_gray>[<aqua>■<gray>□□□□□□□□□<dark_gray>]"),
                  mm.deserialize("<dark_gray>[<aqua>■■<gray>□□□□□□□□<dark_gray>]"),
                  mm.deserialize("<dark_gray>[<aqua>■■■<gray>□□□□□□□<dark_gray>]"),
                  mm.deserialize("<dark_gray>[<aqua>■■■■<gray>□□□□□□<dark_gray>]"),
                  mm.deserialize("<dark_gray>[<aqua>■■■■■<gray>□□□□□<dark_gray>]"),
                  mm.deserialize("<dark_gray>[<aqua>■■■■■■<gray>□□□□<dark_gray>]"),
                  mm.deserialize("<dark_gray>[<aqua>■■■■■■■<gray>□□□<dark_gray>]"),
                  mm.deserialize("<dark_gray>[<aqua>■■■■■■■■<gray>□□<dark_gray>]"),
                  mm.deserialize("<dark_gray>[<aqua>■■■■■■■■■<gray>□<dark_gray>]"),
                  mm.deserialize("<dark_gray>[<aqua>■■■■■■■■■■<dark_gray>]"),
                  mm.deserialize("<dark_gray>[<green>■■■■■■■■■■<dark_gray>] <green>Done!")
          ),
          8
  );
  board.setAnimatedLine(5, loadingBar);
}
```

### Example 11: Smooth Breathing Effect

```java
public void createBreathingAnimation(Player player) {
  MiniMessage mm = MiniMessage.miniMessage();
  Board board = scorefx.getBoardManager().createBoard(player);

  board.setTitle(mm.deserialize("<gradient:#667eea:#764ba2>SERVER</gradient>"));

  // Ultra-smooth breathing animation (fades in and out)
  Animation breathing = scorefx.getAnimationFactory().fromComponents(
          List.of(
                  mm.deserialize("<#333333>⬤ <#666666>Active"),
                  mm.deserialize("<#444444>⬤ <#777777>Active"),
                  mm.deserialize("<#555555>⬤ <#888888>Active"),
                  mm.deserialize("<#666666>⬤ <#999999>Active"),
                  mm.deserialize("<#777777>⬤ <#AAAAAA>Active"),
                  mm.deserialize("<#888888>⬤ <#BBBBBB>Active"),
                  mm.deserialize("<#999999>⬤ <#CCCCCC>Active"),
                  mm.deserialize("<#AAAAAA>⬤ <#DDDDDD>Active"),
                  mm.deserialize("<#BBBBBB>⬤ <#EEEEEE>Active"),
                  mm.deserialize("<#CCCCCC>⬤ <#FFFFFF>Active"),
                  mm.deserialize("<#DDDDDD>⬤ <#FFFFFF>Active"),
                  mm.deserialize("<#EEEEEE>⬤ <#FFFFFF>Active"),
                  mm.deserialize("<#FFFFFF>⬤ <#FFFFFF>Active"),
                  mm.deserialize("<#EEEEEE>⬤ <#FFFFFF>Active"),
                  mm.deserialize("<#DDDDDD>⬤ <#FFFFFF>Active"),
                  mm.deserialize("<#CCCCCC>⬤ <#FFFFFF>Active"),
                  mm.deserialize("<#BBBBBB>⬤ <#EEEEEE>Active"),
                  mm.deserialize("<#AAAAAA>⬤ <#DDDDDD>Active"),
                  mm.deserialize("<#999999>⬤ <#CCCCCC>Active"),
                  mm.deserialize("<#888888>⬤ <#BBBBBB>Active"),
                  mm.deserialize("<#777777>⬤ <#AAAAAA>Active"),
                  mm.deserialize("<#666666>⬤ <#999999>Active"),
                  mm.deserialize("<#555555>⬤ <#888888>Active"),
                  mm.deserialize("<#444444>⬤ <#777777>Active")
          ),
          2 // 2 ticks = buttery smooth
  );
  board.setAnimatedLine(5, breathing);
}
```

### Example 12: Scrolling Text

```java
public void createScrollingText(Player player) {
  MiniMessage mm = MiniMessage.miniMessage();
  Board board = scorefx.getBoardManager().createBoard(player);

  board.setTitle(mm.deserialize("<bold><gold>NEWS</gold></bold>"));

  // Scrolling announcement
  String message = "Welcome to our server! Visit our website for updates and events!";
  List<Component> frames = new ArrayList<>();

  int windowSize = 20; // Characters visible at once
  for (int i = 0; i <= message.length(); i++) {
    int end = Math.min(i + windowSize, message.length());
    String visible = message.substring(i, end);
    frames.add(mm.deserialize("<yellow>" + visible));
  }

  Animation scrolling = scorefx.getAnimationFactory().fromComponents(frames, 3);
  board.setAnimatedLine(5, scrolling);
}
```

### Example 13: Rainbow Gradient Animation

```java
public void createRainbowAnimation(Player player) {
  MiniMessage mm = MiniMessage.miniMessage();
  Board board = scorefx.getBoardManager().createBoard(player);

  // Cycling rainbow gradient title
  Animation rainbowTitle = scorefx.getAnimationFactory().fromComponents(
          List.of(
                  mm.deserialize("<gradient:#FF0000:#FF7F00><bold>RAINBOW SERVER</bold></gradient>"),
                  mm.deserialize("<gradient:#FF7F00:#FFFF00><bold>RAINBOW SERVER</bold></gradient>"),
                  mm.deserialize("<gradient:#FFFF00:#00FF00><bold>RAINBOW SERVER</bold></gradient>"),
                  mm.deserialize("<gradient:#00FF00:#00FFFF><bold>RAINBOW SERVER</bold></gradient>"),
                  mm.deserialize("<gradient:#00FFFF:#0000FF><bold>RAINBOW SERVER</bold></gradient>"),
                  mm.deserialize("<gradient:#0000FF:#8B00FF><bold>RAINBOW SERVER</bold></gradient>"),
                  mm.deserialize("<gradient:#8B00FF:#FF00FF><bold>RAINBOW SERVER</bold></gradient>"),
                  mm.deserialize("<gradient:#FF00FF:#FF0000><bold>RAINBOW SERVER</bold></gradient>")
          ),
          4
  );
  board.setAnimatedTitle(rainbowTitle);
}
```

---

## Advanced Examples

### Example 14: Multi-Zone Scoreboard

```java
public void createMultiZoneBoard(Player player, JavaPlugin plugin) {
  MiniMessage mm = MiniMessage.miniMessage();
  Board board = scorefx.getBoardManager().createBoard(player);

  // Animated gradient title
  Animation titleAnim = scorefx.getAnimationFactory().fromComponents(
          List.of(
                  mm.deserialize("<gradient:#FF6B6B:#4ECDC4><bold>GAME HUB</bold></gradient>"),
                  mm.deserialize("<gradient:#4ECDC4:#45B7D1><bold>GAME HUB</bold></gradient>"),
                  mm.deserialize("<gradient:#45B7D1:#96CEB4><bold>GAME HUB</bold></gradient>"),
                  mm.deserialize("<gradient:#96CEB4:#FFEAA7><bold>GAME HUB</bold></gradient>"),
                  mm.deserialize("<gradient:#FFEAA7:#FF6B6B><bold>GAME HUB</bold></gradient>")
          ),
          5
  );
  board.setAnimatedTitle(titleAnim);

  // ===== ZONE 1: Player Info =====
  board.setLine(15, Component.empty());
  board.setLine(14, mm.deserialize("<dark_gray>╔════════════════════╗"));
  board.setLine(13, mm.deserialize("<gradient:#667eea:#764ba2>  👤 PROFILE</gradient>"));
  board.setLine(12, mm.deserialize("<dark_gray>╚════════════════════╝"));
  board.setLine(11, Component.empty());

  // ===== ZONE 2: Stats =====
  board.setLine(10, mm.deserialize("<dark_gray>╔════════════════════╗"));
  board.setLine(9, mm.deserialize("<gradient:#FF6B6B:#C44569>  📊 STATS</gradient>"));
  board.setLine(8, mm.deserialize("<dark_gray>╚════════════════════╝"));
  board.setLine(7, Component.empty());

  // ===== ZONE 3: Server Info =====
  board.setLine(6, mm.deserialize("<dark_gray>╔════════════════════╗"));
  board.setLine(5, mm.deserialize("<gradient:#00D2FF:#3A7BD5>  🌐 SERVER</gradient>"));
  board.setLine(4, mm.deserialize("<dark_gray>╚════════════════════╝"));
  board.setLine(3, Component.empty());

  // Animated footer
  Animation footerAnim = scorefx.getAnimationFactory().fromComponents(
          List.of(
                  mm.deserialize("<gradient:#667eea:#764ba2>mc.example.com</gradient>"),
                  mm.deserialize("<gradient:#764ba2:#667eea>mc.example.com</gradient>")
          ),
          10
  );
  board.setAnimatedLine(1, footerAnim);

  // Dynamic content updates
  Bukkit.getScheduler().runTaskTimer(plugin, () -> {
    scorefx.getBoardManager().getBoard(player).ifPresent(b -> {
      // Zone 1 content
      b.setLine(10, mm.deserialize("<white>Rank:"),
              mm.deserialize("<gradient:#FFD93D:#FF9F40>VIP</gradient>"));

      // Zone 2 content
      int ping = player.getPing();
      String pingColor = ping < 50 ? "#00FF88" : ping < 100 ? "#FFD93D" : "#FF6B6B";
      b.setLine(7, mm.deserialize("<white>Ping:"),
              mm.deserialize("<" + pingColor + ">" + ping + "ms"));

      // Zone 3 content
      int online = Bukkit.getOnlinePlayers().size();
      b.setLine(3, mm.deserialize("<white>Online:"),
              mm.deserialize("<aqua>" + online));
    });
  }, 0L, 20L);
}
```

### Example 15: Game Stats Tracker

```java
public void createGameStatsBoard(Player player, JavaPlugin plugin) {
  MiniMessage mm = MiniMessage.miniMessage();
  Board board = scorefx.getBoardManager().createBoard(player);

  board.setTitle(mm.deserialize("<gradient:#FF6B6B:#4ECDC4><bold>BEDWARS</bold></gradient>"));

  // Game timer animation
  AtomicInteger gameTime = new AtomicInteger(0);

  Bukkit.getScheduler().runTaskTimer(plugin, () -> {
    scorefx.getBoardManager().getBoard(player).ifPresent(b -> {
      int seconds = gameTime.incrementAndGet();
      int minutes = seconds / 60;
      int secs = seconds % 60;

      b.setLine(15, Component.empty());
      b.setLine(14, mm.deserialize("<white>Time: <yellow>" +
              String.format("%02d:%02d", minutes, secs)));
      b.setLine(13, Component.empty());
      b.setLine(12, mm.deserialize("<dark_gray><strikethrough>────────────────────</strikethrough>"));
      b.setLine(11, Component.empty());

      // Team status with emojis
      b.setLine(10, mm.deserialize("<red>🛏 Red: <green>✓ Alive"));
      b.setLine(9, mm.deserialize("<blue>🛏 Blue: <red>✗ Eliminated"));
      b.setLine(8, mm.deserialize("<green>🛏 Green: <green>✓ Alive"));
      b.setLine(7, mm.deserialize("<yellow>🛏 Yellow: <green>✓ Alive"));
      b.setLine(6, Component.empty());
      b.setLine(5, mm.deserialize("<dark_gray><strikethrough>────────────────────</strikethrough>"));
      b.setLine(4, Component.empty());

      // Personal stats
      b.setLine(3, mm.deserialize("<gold>⚔</gold> <white>Kills:"),
              mm.deserialize("<red>12"));
      b.setLine(2, mm.deserialize("<aqua>🛏</aqua> <white>Beds:"),
              mm.deserialize("<gold>3"));
      b.setLine(1, Component.empty());
    });
  }, 0L, 20L);
}
```

### Example 16: Economy Display

```java
public void createEconomyBoard(Player player, JavaPlugin plugin) {
  MiniMessage mm = MiniMessage.miniMessage();
  Board board = scorefx.getBoardManager().createBoard(player);

  board.setTitle(mm.deserialize("<gradient:#FFD700:#FFA500><bold>💰 ECONOMY</bold></gradient>"));

  // Coin animation
  Animation coinAnim = scorefx.getAnimationFactory().fromComponents(
          List.of(
                  mm.deserialize("<yellow>◐ <white>Balance"),
                  mm.deserialize("<yellow>◓ <white>Balance"),
                  mm.deserialize("<yellow>◑ <white>Balance"),
                  mm.deserialize("<yellow>◒ <white>Balance")
          ),
          5
  );
  board.setAnimatedLine(10, coinAnim);

  Bukkit.getScheduler().runTaskTimer(plugin, () -> {
    scorefx.getBoardManager().getBoard(player).ifPresent(b -> {
      b.setLine(15, Component.empty());

      // Main balance with large number formatting
      b.setLine(10, mm.deserialize("<yellow>💰 <white>Balance:"),
              mm.deserialize("<gradient:#FFD700:#FFA500>$1,234,567</gradient>"));

      b.setLine(9, Component.empty());
      b.setLine(8, mm.deserialize("<dark_gray><strikethrough>────────────────────</strikethrough>"));
      b.setLine(7, Component.empty());

      // Currency breakdown
      b.setLine(6, mm.deserialize("<gold>Gold: <yellow>542"));
      b.setLine(5, mm.deserialize("<aqua>Diamonds: <blue>128"));
      b.setLine(4, mm.deserialize("<green>Emeralds: <dark_green>34"));

      b.setLine(3, Component.empty());
      b.setLine(2, mm.deserialize("<dark_gray><strikethrough>────────────────────</strikethrough>"));
      b.setLine(1, mm.deserialize("<gradient:#667eea:#764ba2>VIP Rank Active</gradient>"));
    });
  }, 0L, 40L);
}
```

---

## Complete Implementations

### Example 17: Full-Featured Server Hub

```java
public class HubScoreboardManager {
  private final JavaPlugin plugin;
  private final ScoreFX scorefx;
  private final MiniMessage mm;

  public HubScoreboardManager(JavaPlugin plugin, ScoreFX scorefx) {
    this.plugin = plugin;
    this.scorefx = scorefx;
    this.mm = MiniMessage.miniMessage();
  }

  public void setupHubBoard(Player player) {
    Board board = scorefx.getBoardManager().createBoard(player);

    // Cycling gradient title
    Animation titleAnim = scorefx.getAnimationFactory().fromComponents(
            List.of(
                    mm.deserialize("<gradient:#FF6B6B:#4ECDC4><bold>MEGA NETWORK</bold></gradient>"),
                    mm.deserialize("<gradient:#4ECDC4:#45B7D1><bold>MEGA NETWORK</bold></gradient>"),
                    mm.deserialize("<gradient:#45B7D1:#96CEB4><bold>MEGA NETWORK</bold></gradient>"),
                    mm.deserialize("<gradient:#96CEB4:#FFEAA7><bold>MEGA NETWORK</bold></gradient>"),
                    mm.deserialize("<gradient:#FFEAA7:#FF6B6B><bold>MEGA NETWORK</bold></gradient>")
            ),
            6
    );
    board.setAnimatedTitle(titleAnim);

    // Static structure
    board.setLine(15, Component.empty());
    board.setLine(14, mm.deserialize("<gradient:#667eea:#764ba2>━━━━━━━━━━━━━━━━━━━━</gradient>"));
    board.setLine(13, Component.empty());

    // Animated status indicator
    Animation statusAnim = scorefx.getAnimationFactory().fromComponents(
            List.of(
                    mm.deserialize("<#888888>⬤ <#BBBBBB>Status"),
                    mm.deserialize("<#AAAAAA>⬤ <#DDDDDD>Status"),
                    mm.deserialize("<#CCCCCC>⬤ <#FFFFFF>Status"),
                    mm.deserialize("<#EEEEEE>⬤ <#FFFFFF>Status"),
                    mm.deserialize("<#FFFFFF>⬤ <#FFFFFF>Status"),
                    mm.deserialize("<#EEEEEE>⬤ <#FFFFFF>Status"),
                    mm.deserialize("<#CCCCCC>⬤ <#FFFFFF>Status"),
                    mm.deserialize("<#AAAAAA>⬤ <#DDDDDD>Status")
            ),
            3
    );
    board.setAnimatedLine(12, statusAnim);

    board.setLine(11, Component.empty());
    board.setLine(10, mm.deserialize("<dark_gray>┃"));
    board.setLine(6, mm.deserialize("<dark_gray>┃"));
    board.setLine(5, Component.empty());
    board.setLine(4, mm.deserialize("<gradient:#667eea:#764ba2>━━━━━━━━━━━━━━━━━━━━</gradient>"));
    board.setLine(3, Component.empty());

    // Pulsing footer
    Animation footerAnim = scorefx.getAnimationFactory().fromComponents(
            List.of(
                    mm.deserialize("<#667eea>play.example.net"),
                    mm.deserialize("<#7080ec>play.example.net"),
                    mm.deserialize("<#7a86ee>play.example.net"),
                    mm.deserialize("<#7080ec>play.example.net")
            ),
            8
    );
    board.setAnimatedLine(1, footerAnim);

    // Dynamic updates
    startDynamicUpdates(player);
  }

  private void startDynamicUpdates(Player player) {
    Bukkit.getScheduler().runTaskTimer(plugin, () -> {
      scorefx.getBoardManager().getBoard(player).ifPresent(b -> {
        // Rank display
        String rank = "VIP"; // Get from permission system
        b.setLine(9, mm.deserialize("<gradient:#667eea:#764ba2>◆</gradient> <white>Rank:"),
                mm.deserialize("<gradient:#FFD700:#FFA500>" + rank + "</gradient>"));

        // Level
        int level = player.getLevel();
        b.setLine(8, mm.deserialize("<gradient:#667eea:#764ba2>◆</gradient> <white>Level:"),
                mm.deserialize("<gradient:#00FF88:#00D2FF>" + level + "</gradient>"));

        // Coins (mock data)
        b.setLine(7, mm.deserialize("<gradient:#667eea:#764ba2>◆</gradient> <white>Coins:"),
                mm.deserialize("<gold>12,450</gold>"));

        // Online players
        int online = Bukkit.getOnlinePlayers().size();
        b.setLine(2, mm.deserialize("<white>Players: <aqua>" + online));
      });
    }, 0L, 20L);
  }
}
```

### Example 18: PvP Arena Scoreboard

```java
public class PvPArenaBoard {
  private final JavaPlugin plugin;
  private final ScoreFX scorefx;
  private final MiniMessage mm;
  private final Map<UUID, Integer> killStreaks;

  public PvPArenaBoard(JavaPlugin plugin, ScoreFX scorefx) {
    this.plugin = plugin;
    this.scorefx = scorefx;
    this.mm = MiniMessage.miniMessage();
    this.killStreaks = new HashMap<>();
  }

  public void createArenaBoard(Player player) {
    Board board = scorefx.getBoardManager().createBoard(player);

    board.setTitle(mm.deserialize("<gradient:#FF0000:#8B0000><bold>⚔ PVP ARENA ⚔</bold></gradient>"));

    board.setLine(15, Component.empty());
    board.setLine(14, mm.deserialize("<red><strikethrough>━━━━━━━━━━━━━━━━━━━━</strikethrough>"));
    board.setLine(13, Component.empty());

    Bukkit.getScheduler().runTaskTimer(plugin, () -> {
      scorefx.getBoardManager().getBoard(player).ifPresent(b -> {
        int streak = killStreaks.getOrDefault(player.getUniqueId(), 0);

        // Kill streak with special formatting
        String streakColor, streakPrefix;
        if (streak >= 10) {
          streakColor = "#FF0000";
          streakPrefix = "🔥";
        } else if (streak >= 5) {
          streakColor = "#FF9F40";
          streakPrefix = "⚡";
        } else {
          streakColor = "#FFFFFF";
          streakPrefix = "●";
        }

        b.setLine(12, mm.deserialize("<white>Kill Streak:"),
                mm.deserialize("<" + streakColor + ">" + streakPrefix + " " + streak));

        b.setLine(11, Component.empty());

        // Combat stats
        b.setLine(10, mm.deserialize("<gold>⚔ <white>Kills:"),
                mm.deserialize("<red>24"));
        b.setLine(9, mm.deserialize("<dark_red>☠ <white>Deaths:"),
                mm.deserialize("<gray>8"));

        // K/D ratio
        double kd = 24.0 / Math.max(1, 8);
        String kdColor = kd >= 2.0 ? "#00FF88" : kd >= 1.0 ? "#FFD93D" : "#FF6B6B";
        b.setLine(8, mm.deserialize("<yellow>📊 <white>K/D:"),
                mm.deserialize("<" + kdColor + ">" + String.format("%.2f", kd)));

        b.setLine(7, Component.empty());

        // Health and status
        int health = (int) player.getHealth();
        String healthColor = health >= 15 ? "#00FF88" : health >= 10 ? "#FFD93D" : "#FF6B6B";
        b.setLine(6, mm.deserialize("<red>❤ <white>Health:"),
                mm.deserialize("<" + healthColor + ">" + health + "/20"));

        // Combat timer (if applicable)
        b.setLine(5, mm.deserialize("<dark_red>⏱ <white>Combat:"),
                mm.deserialize("<red>15s"));

        b.setLine(4, Component.empty());
        b.setLine(3, mm.deserialize("<red><strikethrough>━━━━━━━━━━━━━━━━━━━━</strikethrough>"));
        b.setLine(2, Component.empty());
        b.setLine(1, mm.deserialize("<gray>Top Killer: <gold>PlayerName"));
      });
    }, 0L, 10L); // Fast updates for combat
  }

  public void incrementKillStreak(Player player) {
    killStreaks.merge(player.getUniqueId(), 1, Integer::sum);
  }

  public void resetKillStreak(Player player) {
    killStreaks.remove(player.getUniqueId());
  }
}
```

---

## Tips & Best Practices

### Performance Optimization

```java
// ✅ GOOD: Single task updates multiple lines
Bukkit.getScheduler().runTaskTimer(plugin, () -> {
        scorefx.getBoardManager().getBoard(player).ifPresent(b -> {
        b.setLine(5, updateLine5());
        b.setLine(4, updateLine4());
        b.setLine(3, updateLine3());
        });
        }, 0L, 20L);

// ❌ BAD: Multiple tasks for each line
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
        scorefx.getBoardManager().getBoard(player).ifPresent(b -> b.setLine(5, updateLine5()));
        }, 0L, 20L);
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
        scorefx.getBoardManager().getBoard(player).ifPresent(b -> b.setLine(4, updateLine4()));
        }, 0L, 20L);
```

### Cleanup

```java
// Always clean up when plugin disables
@Override
public void onDisable() {
  // Remove all boards
  Bukkit.getOnlinePlayers().forEach(player ->
          scorefx.getBoardManager().removeBoard(player)
  );

  // Cancel all tasks
  Bukkit.getScheduler().cancelTasks(this);
}
```

### Safe Updates

```java
// Always use ifPresent() for safety
scorefx.getBoardManager().getBoard(player).ifPresent(board -> {
        // Board exists, safe to update
        board.setLine(5, Component.text("Updated!"));
        });
```

---

## Need More Help?

- Message me on discord at `dripps.`

**ScoreFX v2.0.0** - Dependency-Free Scoreboard API
