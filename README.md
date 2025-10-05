# ScoreFX# ScoreFX# ScoreFX



[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/projects/jdk/21/)

[![Paper 1.21](https://img.shields.io/badge/Paper-1.21+-blue.svg)](https://papermc.io/)

[![Version](https://img.shields.io/badge/Version-2.0.0-green.svg)]()[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/projects/jdk/21/)[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/projects/jdk/21/)

[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

[![Paper 1.21](https://img.shields.io/badge/Paper-1.21.8-blue.svg)](https://papermc.io/)[![Paper 1.21](https://img.shields.io/badge/Paper-1.21.8-blue.svg)](https://papermc.io/)

A high-performance, dependency-free scoreboard API for Paper servers. Build clean per-player scoreboards with animations, custom score formatting, and modern text support.

[![Version](https://img.shields.io/badge/Version-2.0.0--SNAPSHOT-green.svg)]()[![Version](https://img.shields.io/badge/Version-2.0.0--SNAPSHOT-green.svg)]()

## Features

[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

- **🚀 Zero Dependencies** - No ProtocolLib or external libraries required

- **⚡ Direct NMS** - Pure packet manipulation with MethodHandles for maximum performance

- **🎨 Modern Text** - Native Adventure Component API with RGB colors and gradients

- **💎 Custom Scores** - Hide numbers or show custom text on the right sideA high-performance, dependency-free scoreboard API for Paper servers. Clean per-player scoreboards with animations, custom score formatting, and modern text support.A high-performance, dependency-free scoreboard API for Paper servers. Clean per-player scoreboards with animations, custom score formatting, and modern text support.

- **🎬 Smooth Animations** - Title and line animations with precise timing control

- **🔌 PlaceholderAPI** - Optional soft dependency for dynamic placeholders

- **🛡️ Thread-Safe** - Enforced main-thread operations with safe concurrent reads

## Features## Features

## Installation



1. Download `scorefx-core-2.0.0-SNAPSHOT.jar` from [Releases](https://github.com/PooSmacker/ScoreFX/releases)

2. Place it in your server's `plugins/` folder- **🚀 Zero Dependencies** - No ProtocolLib or external libraries required- **🚀 Zero Dependencies** - No ProtocolLib or external libraries required

3. _(Optional)_ Install [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) for placeholder support

4. Restart the server- **⚡ Direct NMS** - Pure packet manipulation with MethodHandles for maximum performance- **⚡ Direct NMS** - Pure packet manipulation with MethodHandles for maximum performance



No configuration files needed - everything is controlled through the API.- **🎨 Modern Text** - Native Adventure Component API with RGB colors and gradients- **🎨 Modern Text** - Native Adventure Component API with RGB colors and gradients



---- **💎 Custom Scores** - Hide numbers or show custom text on the right side- **💎 Custom Scores** - Hide numbers or show custom text on the right side



## Add to Your Plugin- **🎬 Smooth Animations** - Title and line animations with precise timing- **🎬 Smooth Animations** - Title and line animations with precise timing



### Maven- **🔌 PlaceholderAPI** - Optional soft dependency for dynamic placeholders- **🔌 PlaceholderAPI** - Optional soft dependency for dynamic placeholders



Add the JitPack repository:- **✅ Backward Compatible** - Full support for legacy String-based API- **✅ Backward Compatible** - Full support for legacy String-based API



```xml

<repository>

  <id>jitpack.io</id>## Installation## Installation

  <url>https://jitpack.io</url>

</repository>

```

1. Download `scorefx-core-2.0.0-SNAPSHOT.jar` from Releases.1. Download `scorefx-core-2.0.0-SNAPSHOT.jar` from Releases.

Add the dependency:

2. Place it in your server's `plugins/` folder.

```xml

<dependency>3. _(Optional)_ Install [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) for placeholder support.2. Drop it into your server's `plugins/` folder.

  <groupId>com.github.PooSmacker.ScoreFX</groupId>

  <artifactId>scorefx-api</artifactId>4. Restart the server.

  <version>v2.0.0</version>

  <scope>provided</scope>ScoreFX is a high-performance, **dependency-free** Scoreboard API plugin for Paper 1.21.8. It provides a clean, efficient API for per‑player scoreboards with dynamic updates, animations, custom score formatting, and optional PlaceholderAPI support.3. (Optional) Install [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) for placeholder support.

</dependency>

```No configuration files needed - everything is controlled through the API.



### Gradle4. (Optional) Install [ProtocolLib 5.0+](https://www.spigotmc.org/resources/protocollib.1997/) for custom score formatting.



Add the JitPack repository in `settings.gradle`:---



```gradle**v2.0.0** is a major architectural upgrade that **eliminates all external dependencies** by using direct NMS packet manipulation via MethodHandles reflection. This provides **maximum performance, full control, and zero dependency conflicts** while maintaining 100% backward compatibility with the v1.x API.5. Restart the server.

dependencyResolutionManagement {

    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)## Add to Your Plugin

    repositories {

        mavenCentral()

        maven { url 'https://jitpack.io' }

    }### Maven (pom.xml)

}

```---No configuration files are required; everything is controlled through the API.ttps://openjdk.java.net/projects/jdk/21/)



Add the dependency in `build.gradle`:```xml



```gradle<repository>[![Paper 1.21](https://img.shields.io/badge/Paper-1.21.8-blue.svg---

dependencies {

    compileOnly 'com.github.PooSmacker.ScoreFX:scorefx-api:v2.0.0'  <id>jitpack.io</id>

}

```  <url>https://jitpack.io</url>## 🎯 Why ScoreFX 2.0



### plugin.yml</repository>



```yaml```## FAQ

name: YourPlugin

version: 1.0.0

main: your.package.YourPlugin

api-version: '1.21'```xml- **🚀 Dependency-Free**: Zero external libraries required (ProtocolLib removed)



depend:<dependency>

  - ScoreFX

```  <groupId>com.github.PooSmacker.ScoreFX</groupId>- **⚡ Direct NMS**: Pure packet manipulation with MethodHandles reflection- Do I need PlaceholderAPI?



---  <artifactId>scorefx-api</artifactId>



## Quick Start  <version>v2.0.0</version>- **🔥 Maximum Performance**: All reflection cached during static initialization - zero runtime overhead    - No. It's optional. If installed, placeholders in your text will be resolved automatically.



### Getting the API  <scope>provided</scope>



```java</dependency>- **💎 Custom Score Formatting**: Hide default numbers or show custom text/Components (no ProtocolLib needed!)- Do I need ProtocolLib?

import com.dripps.scorefx.api.ScoreFX;

import com.dripps.scorefx.api.BoardManager;```

import org.bukkit.Bukkit;

import org.bukkit.plugin.java.JavaPlugin;- **🌈 Modern Text Support**: Native Adventure Component API with RGB colors, gradients, etc.    - No. It's optional. If installed, you can customize score formatting (hide numbers or show custom text).



public class YourPlugin extends JavaPlugin {### Gradle (settings.gradle)

    private BoardManager boardManager;

    - **✅ Backward Compatible**: All v1.x code works identically- How many lines can I use?

    @Override

    public void onEnable() {```gradle

        ScoreFX api = Bukkit.getServicesManager().load(ScoreFX.class);

        if (api == null) {dependencyResolutionManagement {- **🔧 PlaceholderAPI Support**: Optional soft dependency for dynamic placeholders    - 1–15. Row 1 is bottom, 15 is top.

            getLogger().severe("ScoreFX not found!");

            getServer().getPluginManager().disablePlugin(this);    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

            return;

        }    repositories {- **🎬 Animations**: Smooth title and line animations with precise tick intervals- Do I have to manually clean up?

        

        this.boardManager = api.getBoardManager();        mavenCentral()

    }

}        maven { url 'https://jitpack.io' }- **🛡️ Thread-Safe**: Clear main-thread enforcement with safe reads    - ScoreFX automatically cleans on player quit and on plugin disable. You can still remove boards yourself if you prefer explicit control.

```

    }

### Basic Scoreboard

}

```java

import com.dripps.scorefx.api.Board;```

import net.kyori.adventure.text.Component;

import net.kyori.adventure.text.format.NamedTextColor;------apermc.io/)



public void createBoard(Player player) {### Gradle (build.gradle)

    Board board = boardManager.createBoard(player);

    [![Version](https://img.shields.io/badge/Version-1.2.0--SNAPSHOT-green.svg)]()

    board.setTitle(Component.text("MY SERVER", NamedTextColor.GOLD));

    ```gradle

    board.setLine(5, Component.text("Welcome!", NamedTextColor.GREEN));

    board.setLine(4, Component.empty());dependencies {## 📦 What's New in v2.0.0

    board.setLine(3, Component.text("Players: 47", NamedTextColor.GRAY));

    board.setLine(2, Component.text("Rank: VIP", NamedTextColor.YELLOW));    compileOnly 'com.github.PooSmacker.ScoreFX:scorefx-api:v2.0.0'

    board.setLine(1, Component.text("play.example.com", NamedTextColor.AQUA));

}}ScoreFX is a high-performance, developer-focused Scoreboard API plugin for Paper 1.21.8. It provides a clean, efficient API for per‑player scoreboards with dynamic updates, animations, custom score formatting, and optional PlaceholderAPI/ProtocolLib support.

```

```

### RGB Colors & Gradients

### 🎯 **Dependency-Free Architecture**

```java

import net.kyori.adventure.text.format.TextColor;### plugin.yml

import net.kyori.adventure.text.minimessage.MiniMessage;

- ✅ **ProtocolLib removed** - Direct NMS packet manipulation replaces all ProtocolLib usage## FAQ

MiniMessage mm = MiniMessage.miniMessage();

```yaml

// Gradient title

board.setTitle(mm.deserialize("<gradient:#FF0000:#00FF00>RAINBOW</gradient>"));name: YourPlugin- ✅ **PacketHelper utility** - Elite MethodHandles-based reflection layer for maximum performance



// Hex colorsversion: 1.0.0

Component title = Component.text("SERVER", TextColor.fromHexString("#FF6B35"));

board.setTitle(title);main: your.package.YourPlugin- ✅ **Zero reflection overhead** - All MethodHandles cached during static initialization- **Do I need PlaceholderAPI?**



// Complex formattingapi-version: '1.21'

board.setLine(5, mm.deserialize("<gradient:#667eea:#764ba2>⭐ VIP Player ⭐</gradient>"));

```- ✅ **Full control** - Complete ownership of packet construction and delivery    - No. It's optional. If installed, placeholders in String-based methods will be resolved automatically.



### Custom Scoresdepend:



By default, scores (numbers on the right) are hidden. You can show custom text:  - ScoreFX    - Note: Placeholders only work with String methods, not Component methods.



```java```

// Custom score text

board.setLine(### ⚡ **Performance Optimizations**

    5,

    Component.text("Coins:", NamedTextColor.YELLOW),---

    Component.text("1,250", NamedTextColor.GOLD)

);- ✅ **Direct packet sending** - No middleware, no interception, pure NMS- **Do I need ProtocolLib?**



// Hide score explicitly (default)## Quick Start

board.setLine(4, Component.text("Welcome!"), null);

- ✅ **MethodHandles caching** - Reflection happens once at startup, then zero overhead    - No. It's optional. If installed, you can customize score formatting (e.g., hide default numbers or show custom text).

// Unicode symbols as scores

board.setLine(### Getting the API

    3,

    Component.text("Health", NamedTextColor.RED),- ✅ **Eliminated dependency** - Faster startup, smaller JAR, no conflicts    - Without ProtocolLib, scores remain visible with default numeric formatting.

    Component.text("❤", NamedTextColor.RED)

);```java

```

import com.dripps.scorefx.api.ScoreFX;    

### Animations

import com.dripps.scorefx.api.BoardManager;

```java

import com.dripps.scorefx.api.animation.Animation;import org.bukkit.Bukkit;### 🔧 **Backward Compatibility**- **Should I use Components or Strings in v1.2.0?**



// Animated titleimport org.bukkit.plugin.java.JavaPlugin;

Animation titleAnim = api.getAnimationFactory().fromComponents(

    List.of(- ✅ **API unchanged** - All v1.x code works identically    - **Components** for static content with RGB colors, gradients, and modern formatting

        mm.deserialize("<red>MY SERVER"),

        mm.deserialize("<gold>MY SERVER"),public class YourPlugin extends JavaPlugin {

        mm.deserialize("<yellow>MY SERVER"),

        mm.deserialize("<green>MY SERVER")    private BoardManager boardManager;- ✅ **Custom scores still work** - `setLineScore()` and `setLine(text, score)` fully functional    - **Strings** for dynamic content with PlaceholderAPI placeholders

    ),

    10 // 10 ticks per frame (0.5 seconds)    

);

board.setAnimatedTitle(titleAnim);    @Override- ✅ **Default hidden scores** - Scores remain hidden by default (NumberFormat.BLANK)    - **Both** work perfectly - use what fits your needs!



// Smooth breathing effect    public void onEnable() {

Animation breathing = api.getAnimationFactory().fromComponents(

    List.of(        ScoreFX api = Bukkit.getServicesManager().load(ScoreFX.class);- ✅ **Migration path** - Simply update version and remove ProtocolLib    

        mm.deserialize("<#555555>⬤ <#888888>Online"),

        mm.deserialize("<#888888>⬤ <#BBBBBB>Online"),        if (api == null) {

        mm.deserialize("<#CCCCCC>⬤ <#FFFFFF>Online"),

        mm.deserialize("<#FFFFFF>⬤ <#FFFFFF>Online"),            getLogger().severe("ScoreFX not found!");- **Will my v1.0 or v1.1.0 code still work?**

        mm.deserialize("<#CCCCCC>⬤ <#FFFFFF>Online"),

        mm.deserialize("<#888888>⬤ <#BBBBBB>Online")            getServer().getPluginManager().disablePlugin(this);

    ),

    3 // 3 ticks per frame            return;---    - Yes! Full backward compatibility. String methods are deprecated but will never be removed.

);

board.setAnimatedLine(5, breathing);        }

```

            

### Dynamic Updates

        this.boardManager = api.getBoardManager();

```java

// Update every second    }## 🔄 Migration from v1.2.0 to v2.0.0- **How do I customize score formatting?**

Bukkit.getScheduler().runTaskTimer(plugin, () -> {

    boardManager.getBoard(player).ifPresent(board -> {}

        int ping = player.getPing();

        board.setLine(```    - Install ProtocolLib 5.0+

            5,

            Component.text("Ping:", NamedTextColor.WHITE),

            Component.text(ping + "ms", NamedTextColor.GREEN)

        );### Basic Scoreboard**Good news:** Your code doesn't need any changes! The API is 100% backward compatible.    - Use `board.setLineScore(row, Component)` to set custom score text

        

        int health = (int) player.getHealth();

        board.setLine(

            4,```java    - Use `board.setLineScore(row, null)` to hide scores (default if ProtocolLib is present)

            Component.text("Health:", NamedTextColor.RED),

            Component.text(health + "/20", NamedTextColor.WHITE)import com.dripps.scorefx.api.Board;

        );

    });import net.kyori.adventure.text.Component;### What You Need to Do:    - Without ProtocolLib, scores remain visible as default numbers

}, 0L, 20L);

```import net.kyori.adventure.text.format.NamedTextColor;



### PlaceholderAPI Support    



PlaceholderAPI works with the legacy String-based API:public void createBoard(Player player) {



```java    Board board = boardManager.createBoard(player);1. **Update dependency version:**- **How do I use RGB colors?**

// String methods support placeholders

board.setLine(5, "&eBalance: &a$%vault_eco_balance%");    

board.setLine(4, "&eKills: &c%statistic_player_kills%");

    board.setTitle(Component.text("MY SERVER", NamedTextColor.GOLD));   ```xml    - With Strings: `&#FF6B35Text`

// For Components, resolve placeholders manually

String balance = PlaceholderAPI.setPlaceholders(player, "%vault_eco_balance%");    

Component line = Component.text()

    .append(Component.text("Balance: ", NamedTextColor.YELLOW))    board.setLine(5, Component.text("Welcome!", NamedTextColor.GREEN));   <dependency>    - With Components: `TextColor.fromHexString("#FF6B35")`

    .append(Component.text("$" + balance, NamedTextColor.GREEN))

    .build();    board.setLine(4, Component.empty()); // Spacer

board.setLine(5, line);

```    board.setLine(3, Component.text("Players: 47", NamedTextColor.GRAY));     <groupId>com.github.PooSmacker.ScoreFX</groupId>    - With MiniMessage: `<#FF6B35>Text`



---    board.setLine(2, Component.text("Rank: VIP", NamedTextColor.YELLOW));



## Examples    board.setLine(1, Component.text("play.example.com", NamedTextColor.AQUA));     <artifactId>scorefx-api</artifactId>    



Check out [EXAMPLES.md](EXAMPLES.md) for 18 comprehensive examples:}

- Basic scoreboards

- Advanced animations  ```     <version>v2.0.0</version>- **How many lines can I use?**

- Multi-zone layouts

- Game stats tracking

- Economy displays

- PvP arena boards### RGB Colors & Gradients     <scope>provided</scope>    - 1–15. Row 1 is bottom, 15 is top.

- And more!



---

```java   </dependency>    

## API Reference

import net.kyori.adventure.text.format.TextColor;

### Board Management

import net.kyori.adventure.text.minimessage.MiniMessage;   ```- **Do I have to manually clean up?**

```java

Board createBoard(Player player)

Optional<Board> getBoard(Player player)

void removeBoard(Player player)// Using MiniMessage for gradients    - No. ScoreFX automatically cleans on player quit and on plugin disable. You can still remove boards yourself if you prefer explicit control.**v1.1.0** adds native support for Paper's Adventure Component API, enabling modern text features like RGB colors, gradients, hover/click events, and more—while maintaining full backward compatibility with the String-based API.

```

MiniMessage mm = MiniMessage.miniMessage();

### Board Methods

board.setTitle(mm.deserialize("<gradient:#FF0000:#00FF00>RAINBOW</gradient>"));2. **Remove ProtocolLib (optional):**

```java

// Title

void setTitle(Component title)

void setAnimatedTitle(Animation animation)// Using hex colors directly   - ProtocolLib is no longer needed or used by ScoreFX**v1.2.0** adds optional custom score formatting via ProtocolLib, allowing you to hide default score numbers or replace them with custom text/Components for a cleaner, more polished scoreboard appearance.



// Lines (1-15, bottom to top)Component title = Component.text("SERVER", TextColor.fromHexString("#FF6B35"));

void setLine(int row, Component text)

void setLine(int row, Component text, @Nullable Component score)board.setTitle(title);   - You can safely remove it from your server if no other plugins need it

void setAnimatedLine(int row, Animation animation)



// Scores

void setLineScore(int row, @Nullable Component score)// Complex formatting   - Custom score formatting still works without ProtocolLib!- Paper: 1.21.8



// Managementboard.setLine(5, mm.deserialize("<gradient:#667eea:#764ba2>⭐ VIP Player ⭐</gradient>"));

void removeLine(int row)

void show()```- Java: 21

void hide()

boolean isHidden()

Player getPlayer()

```### Custom Scores3. **That's it!** Your existing code will work exactly as before.- Module layout: `scorefx-api` (API), `scorefx-core` (plugin)



### Legacy String API



Still supported for backward compatibility:By default, scores (numbers on the right) are hidden. You can show custom text:- Optional dependencies: PlaceholderAPI (placeholders), ProtocolLib 5.0+ (custom scores)



```java

void setTitle(String text)

void setLine(int row, String text)```java### What Changed Under the Hood:- Documentation: see [docs.md](docs.md)

void setLine(int row, String text, @Nullable String score)

```// Show custom score text



Supports `&` color codes, `§` section signs, `&#RRGGBB` hex colors, and `%placeholders%`.board.setLine(



---    5,



## Performance    Component.text("Coins:", NamedTextColor.YELLOW),| v1.2.0 (Old) | v2.0.0 (New) |---



- **MethodHandles Reflection** - All reflection cached at startup, zero runtime overhead    Component.text("1,250", NamedTextColor.GOLD) // Custom score

- **Single Scheduler** - One task efficiently manages all boards

- **Direct NMS Packets** - No middleware or interception layers);|--------------|--------------|

- **Optimized Updates** - Only sends packets when content actually changes



---

// Explicitly hide score (default behavior)| `objective.getScore().setScore()` + ProtocolLib interception | Direct `ClientboundSetScorePacket` via NMS |## Why ScoreFX

## Thread Safety

board.setLine(4, Component.text("Welcome!"), null);

**Main thread only:**

- Creating/removing boards| External ProtocolLib dependency | Zero external dependencies |

- Setting titles, lines, scores

- Starting/stopping animations// Unicode symbols as scores



**Safe from any thread:**board.setLine(| Packet interception overhead | Direct packet construction |- **Custom Score Formatting (v1.2.0+)**: Hide default score numbers or display custom text/Components (requires ProtocolLib)

- Getting boards (`getBoard()`)

- Reading board state (`getPlayer()`, `isHidden()`)    3,



```java    Component.text("Health", NamedTextColor.RED),| Reflection on every call | MethodHandles cached once at startup |- **Modern Text Support (v1.1.0+)**: Native Adventure Component API with RGB colors, gradients etc.

// ✅ Correct

Bukkit.getScheduler().runTask(plugin, () -> {    Component.text("❤", NamedTextColor.RED)

    board.setTitle(Component.text("Updated"));

}););- **Backward Compatible**: Full String-based API support with automatic legacy color conversion



// ❌ Wrong - throws IllegalStateException```

Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

    board.setTitle(Component.text("Error!"));**Result:** Same functionality, better performance, zero dependencies.- Single-tick scheduler: one task drives all boards

});

```### Animations



---- Priority queue scheduling: O(log n) insertion and due-time processing



## Building from Source```java



```bashimport com.dripps.scorefx.api.animation.Animation;---- Flicker-free rendering: team prefix/suffix updates (no score churn)

git clone https://github.com/PooSmacker/ScoreFX.git

cd ScoreFX

mvn clean package

```// Animated title - cycles through colors- Per-player boards: each player gets an independent board



Artifacts will be in:Animation titleAnim = api.getAnimationFactory().fromComponents(

- `scorefx-api/target/scorefx-api-2.0.0-SNAPSHOT.jar`

- `scorefx-core/target/scorefx-core-2.0.0-SNAPSHOT.jar`    List.of(## 📥 Installation- Animations: titles and lines with precise intervals (Components or Strings)



---        mm.deserialize("<red>MY SERVER"),



## FAQ        mm.deserialize("<gold>MY SERVER"),- PlaceholderAPI: auto-detected, soft dependency



**Do I need PlaceholderAPI?**          mm.deserialize("<yellow>MY SERVER"),

No, it's optional. If installed, placeholders work automatically with String-based methods.

        mm.deserialize("<green>MY SERVER")1. Download `scorefx-core-2.0.0-SNAPSHOT.jar` from Releases.- Thread-safety rules: clear main-thread enforcement and safe reads

**Do I need ProtocolLib?**  

No! ScoreFX v2.0 uses direct NMS packets instead.    ),



**Should I use Components or Strings?**      10 // Change every 10 ticks (0.5 seconds)2. Drop it into your server's `plugins/` folder.

Use Components for modern features (RGB, gradients). Use Strings if you need PlaceholderAPI support.

);

**How many lines can I use?**  

1-15 lines. Row 1 is the bottom, row 15 is the top.board.setAnimatedTitle(titleAnim);3. (Optional) Install [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) for placeholder support.---



**Do I need to clean up boards manually?**  

No, ScoreFX automatically removes boards when players quit and when the plugin disables.

// Smooth breathing animation4. Restart the server.

**What Minecraft versions are supported?**  

Currently Paper 1.21.8. Support for other versions may be added in the future.Animation breathing = api.getAnimationFactory().fromComponents(



---    List.of(## Installation



## Contributing        mm.deserialize("<#555555>⬤ <#888888>Online"),



Contributions are welcome! Please feel free to submit pull requests or open issues for bugs and feature requests.        mm.deserialize("<#888888>⬤ <#BBBBBB>Online"),No configuration files are required; everything is controlled through the API.



---        mm.deserialize("<#CCCCCC>⬤ <#FFFFFF>Online"),



## License        mm.deserialize("<#FFFFFF>⬤ <#FFFFFF>Online"),1. Download `scorefx-core-1.0-SNAPSHOT.jar` from Releases.



This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.        mm.deserialize("<#CCCCCC>⬤ <#FFFFFF>Online"),



---        mm.deserialize("<#888888>⬤ <#BBBBBB>Online")---2. Drop it into your server’s `plugins/` folder.



## Links    ),



- **GitHub**: [https://github.com/PooSmacker/ScoreFX](https://github.com/PooSmacker/ScoreFX)    3 // 3 ticks per frame3. (Optional) Install [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/).

- **JitPack**: [https://jitpack.io/#PooSmacker/ScoreFX](https://jitpack.io/#PooSmacker/ScoreFX)

- **Examples**: [EXAMPLES.md](EXAMPLES.md));

- **PaperMC**: [https://papermc.io/](https://papermc.io/)

- **PlaceholderAPI**: [https://www.spigotmc.org/resources/placeholderapi.6245/](https://www.spigotmc.org/resources/placeholderapi.6245/)board.setAnimatedLine(5, breathing);## 🔨 Add to Your Plugin4. Restart the server.


```



### Dynamic Updates

### Maven (pom.xml)No configuration files are required; everything is controlled through the API.

```java

// Update scoreboard every second

Bukkit.getScheduler().runTaskTimer(plugin, () -> {

    boardManager.getBoard(player).ifPresent(board -> {```xml---

        // Update ping

        int ping = player.getPing();<repository>

        board.setLine(

            5,  <id>jitpack.io</id>### Add to Your Plugin (JitPack)

            Component.text("Ping:", NamedTextColor.WHITE),

            Component.text(ping + "ms", NamedTextColor.GREEN)  <url>https://jitpack.io</url>

        );

        </repository>Maven (pom.xml)

        // Update health

        int health = (int) player.getHealth();``````xml

        board.setLine(

            4,```xml<repository>

            Component.text("Health:", NamedTextColor.RED),

            Component.text(health + "/20", NamedTextColor.WHITE)<dependency>  <id>jitpack.io</id>

        );

    });  <groupId>com.github.PooSmacker.ScoreFX</groupId>  <url>https://jitpack.io</url>

}, 0L, 20L);

```  <artifactId>scorefx-api</artifactId></repository>



### PlaceholderAPI Support  <version>v2.0.0</version>```



PlaceholderAPI works with the legacy String-based API:  <scope>provided</scope>```xml



```java</dependency><dependency>

// Placeholders work with String methods

board.setLine(5, "&eBalance: &a$%vault_eco_balance%");```  <groupId>com.github.PooSmacker.ScoreFX</groupId>

board.setLine(4, "&eKills: &c%statistic_player_kills%");

  <artifactId>scorefx-api</artifactId>

// For Components, resolve placeholders manually

String balance = PlaceholderAPI.setPlaceholders(player, "%vault_eco_balance%");### Gradle (settings.gradle)  <version>v1.2.0</version>

Component line = Component.text()

    .append(Component.text("Balance: ", NamedTextColor.YELLOW))  <scope>provided</scope>

    .append(Component.text("$" + balance, NamedTextColor.GREEN))

    .build();```gradle</dependency>

board.setLine(5, line);

```dependencyResolutionManagement {```



---    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)



## Examples    repositories {Gradle (settings.gradle)



Check out [EXAMPLES.md](EXAMPLES.md) for 18 comprehensive examples including:        mavenCentral()```gradle

- Basic scoreboards

- Advanced animations        maven { url 'https://jitpack.io' }dependencyResolutionManagement {

- Multi-zone layouts

- Game stats tracking    }    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

- Economy displays

- PvP arenas}    repositories {

- And more!

```        mavenCentral()

---

        maven { url 'https://jitpack.io' }

## API Reference

### Gradle (build.gradle)    }

### Board Management

}

```java

// Create board for player```gradle```

Board createBoard(Player player)

dependencies {

// Get existing board

Optional<Board> getBoard(Player player)    compileOnly 'com.github.PooSmacker.ScoreFX:scorefx-api:v2.0.0'Gradle (build.gradle)



// Remove board}```gradle

void removeBoard(Player player)

``````dependencies {



### Board Methods    compileOnly 'com.github.PooSmacker.ScoreFX:scorefx-api:v1.2.0'



```java### plugin.yml}

// Title

void setTitle(Component title)```

void setAnimatedTitle(Animation animation)

```yaml

// Lines (1-15, bottom to top)

void setLine(int row, Component text)name: YourPluginplugin.yml

void setLine(int row, Component text, @Nullable Component score)

void setAnimatedLine(int row, Animation animation)version: 1.0.0```yaml



// Scoresmain: your.package.YourPluginname: YourPlugin

void setLineScore(int row, @Nullable Component score)

api-version: '1.21'version: 1.0.0

// Remove

void removeLine(int row)main: your.package.YourPlugin



// Visibilitydepend:api-version: '1.21'

void show()

void hide()  - ScoreFX

boolean isHidden()

```depend:

// Info

Player getPlayer()  - ScoreFX

```

---```

### Legacy String API



Still fully supported for backward compatibility:

## 🚀 Quick Start---

```java

void setTitle(String text)

void setLine(int row, String text)

void setLine(int row, String text, @Nullable String score)### Getting the API## Getting the API

```



Supports `&` color codes, `§` section signs, `&#RRGGBB` hex colors, and `%placeholders%`.

Use Bukkit's Services Manager to access ScoreFX:Use Bukkit’s Services Manager. Fail fast if the service is missing.

---



## Performance

```java```java

- **MethodHandles Reflection**: All reflection cached during static initialization - zero runtime overhead

- **Single Scheduler**: One task manages all boards efficientlyimport com.dripps.scorefx.api.ScoreFX;import com.dripps.scorefx.api.ScoreFX;

- **Direct Packets**: No middleware or interception layers

- **Thread-Safe**: Enforced main-thread operations with safe readsimport com.dripps.scorefx.api.BoardManager;import com.dripps.scorefx.api.BoardManager;



---import com.dripps.scorefx.api.animation.AnimationFactory;import com.dripps.scorefx.api.animation.AnimationFactory;



## Migration from v1.ximport org.bukkit.Bukkit;import org.bukkit.Bukkit;



Your code doesn't need changes! The API is 100% backward compatible.import org.bukkit.plugin.java.JavaPlugin;import org.bukkit.plugin.java.JavaPlugin;



1. Update dependency version to `v2.0.0`

2. Remove ProtocolLib (no longer needed)

3. Done!public final class YourPlugin extends JavaPlugin {public final class YourPlugin extends JavaPlugin {



Custom score formatting still works without ProtocolLib. All v1.x features work identically.



---    private BoardManager boards;    private BoardManager boards;



## Thread Safety    private AnimationFactory animations;    private AnimationFactory animations;



**Main thread only:**

- Creating/removing boards

- Setting titles, lines, scores    @Override    @Override

- Starting/stopping animations

    public void onEnable() {    public void onEnable() {

**Safe from any thread:**

- Getting boards        ScoreFX api = Bukkit.getServicesManager().load(ScoreFX.class);        ScoreFX api = Bukkit.getServicesManager().load(ScoreFX.class);

- Reading board state

        if (api == null) {        if (api == null) {

```java

// ✅ Correct            getLogger().severe("ScoreFX service not found. Is the plugin installed?");            getLogger().severe("ScoreFX service not found. Is the plugin installed and enabled?");

Bukkit.getScheduler().runTask(plugin, () -> {

    board.setTitle(Component.text("Updated"));            getServer().getPluginManager().disablePlugin(this);            getServer().getPluginManager().disablePlugin(this);

});

            return;            return;

// ❌ Wrong - throws IllegalStateException

Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {        }        }

    board.setTitle(Component.text("Error!"));

});

```

        this.boards = api.getBoardManager();        this.boards = api.getBoardManager();

---

        this.animations = api.getAnimationFactory();        this.animations = api.getAnimationFactory();

## Building from Source

    }    }

```bash

git clone https://github.com/PooSmacker/ScoreFX.git

cd ScoreFX

mvn clean package    public BoardManager boards() { return boards; }    public BoardManager boards() { return boards; }

```

    public AnimationFactory animations() { return animations; }    public AnimationFactory animations() { return animations; }

Artifacts:

- `scorefx-api/target/scorefx-api-2.0.0-SNAPSHOT.jar`}}

- `scorefx-core/target/scorefx-core-2.0.0-SNAPSHOT.jar`

``````

---



## FAQ

------

**Do I need PlaceholderAPI?**  

No, it's optional. If installed, placeholders work automatically with String-based methods.



**Do I need ProtocolLib?**  ## 🌈 Modern API: Using Components## Modern API: Using Components (v1.1.0+)

No! ScoreFX v2.0 uses direct NMS packets instead.



**Will my v1.x code work?**  

Yes! 100% backward compatible.**Native Adventure Component support** enables RGB colors, gradients, and modern formatting.**New in v1.1.0:** ScoreFX now natively supports Paper's Adventure Component API, enabling modern text features like RGB colors, gradients, hover/click events, and more.



**Should I use Components or Strings?**  

Components for modern text features (RGB, gradients). Strings for PlaceholderAPI support.

### Why Use Components?### Why Use Components?

**How many lines can I use?**  

1-15 lines. Row 1 is bottom, row 15 is top.



**Do I need to clean up boards?**  - **RGB Colors**: Full 16.7 million color palette- **RGB Colors**: Full 16.7 million color palette with `<#RRGGBB>` syntax

No, ScoreFX auto-cleans on player quit and plugin disable.

- **Gradients**: Smooth color transitions- **Gradients**: Smooth color transitions with `<gradient:...>` tags

---

- **Modern API**: Adventure is Paper's standard text API- **Hover/Click Events**: Interactive text (though only visual content renders on scoreboards)

## License

- **Future-Proof**: All internal processing uses Components- **Modern API**: Adventure is Paper's standard text API

MIT License - See [LICENSE](LICENSE) file for details.

- **Future-Proof**: All internal processing uses Components

---

### Basic Component Usage

## Links

### Basic Component Usage

- [GitHub Repository](https://github.com/PooSmacker/ScoreFX)

- [JitPack](https://jitpack.io/#PooSmacker/ScoreFX)```java

- [Examples](EXAMPLES.md)

- [PaperMC](https://papermc.io/)import com.dripps.scorefx.api.Board;```java


import net.kyori.adventure.text.Component;import com.dripps.scorefx.api.Board;

import net.kyori.adventure.text.format.NamedTextColor;import net.kyori.adventure.text.Component;

import net.kyori.adventure.text.format.TextColor;import net.kyori.adventure.text.format.NamedTextColor;

import net.kyori.adventure.text.format.TextDecoration;import net.kyori.adventure.text.format.TextColor;

import net.kyori.adventure.text.format.TextDecoration;

public void showModernBoard(Player player) {

    Board board = boards().createBoard(player);public void showModernBoard(Player player) {

    Board board = boards().createBoard(player);

    // RGB colors with hex values

    Component title = Component.text("My Server", TextColor.fromHexString("#FF6B35"))    // RGB colors with hex values

        .decorate(TextDecoration.BOLD);    Component title = Component.text("My Server", TextColor.fromHexString("#FF6B35"))

    board.setTitle(title);        .decorate(TextDecoration.BOLD);

    board.setTitle(title);

    // Named colors

    board.setLine(15, Component.text("------------------", NamedTextColor.GRAY));    // Named colors

        board.setLine(15, Component.text("------------------", NamedTextColor.GRAY));

    // Complex formatting    

    Component playerLine = Component.text()    // Complex formatting

        .append(Component.text("Player: ", NamedTextColor.YELLOW))    Component playerLine = Component.text()

        .append(Component.text(player.getName(), NamedTextColor.WHITE))        .append(Component.text("Player: ", NamedTextColor.YELLOW))

        .build();        .append(Component.text(player.getName(), NamedTextColor.WHITE))

    board.setLine(14, playerLine);        .build();

}    board.setLine(14, playerLine);

```

    // Gradient using MiniMessage (requires adventure-text-minimessage)

### Component Animations    // Component gradient = MiniMessage.miniMessage().deserialize("<gradient:red:blue>Beautiful Gradient</gradient>");

    // board.setLine(13, gradient);

```java}

// Create animated title with RGB colors```

Animation rgbTitle = animations().fromComponents(

    List.of(### Component Animations

        Component.text("ScoreFX", TextColor.fromHexString("#FF0000")),

        Component.text("ScoreFX", TextColor.fromHexString("#FF7F00")),```java

        Component.text("ScoreFX", TextColor.fromHexString("#FFFF00")),import net.kyori.adventure.text.Component;

        Component.text("ScoreFX", TextColor.fromHexString("#00FF00")),import net.kyori.adventure.text.format.TextColor;

        Component.text("ScoreFX", TextColor.fromHexString("#0000FF"))

    ),// Create animated title with RGB colors

    10  // 0.5s per frame (10 ticks)Animation rgbTitle = animations().fromComponents(

);    java.util.List.of(

board.setAnimatedTitle(rgbTitle);        Component.text("ScoreFX", TextColor.fromHexString("#FF0000")),

```        Component.text("ScoreFX", TextColor.fromHexString("#FF7F00")),

        Component.text("ScoreFX", TextColor.fromHexString("#FFFF00")),

---        Component.text("ScoreFX", TextColor.fromHexString("#00FF00")),

        Component.text("ScoreFX", TextColor.fromHexString("#0000FF"))

## 💎 Custom Score Formatting    ),

    10  // 0.5s per frame

**New in v2.0:** Custom scores work **without ProtocolLib** via direct NMS packet manipulation!);

board.setAnimatedTitle(rgbTitle);

### What Are Scores?```



Each scoreboard line has two parts:---

1. **Text (prefix/suffix)**: Main content set with `setLine()`

2. **Score (number)**: Numeric value on the right side## Custom Score Formatting (v1.2.0+)



### Default Behavior (v2.0)**New in v1.2.0:** ScoreFX now supports custom score formatting via ProtocolLib, allowing you to **hide default score numbers** or **replace them with custom text/Components** for a cleaner, more polished scoreboard appearance.



**All scores are hidden by default** for a clean, modern appearance.### What Are Scores?



```javaIn Minecraft scoreboards, each line has two parts:

Board board = boards().createBoard(player);1. **Text (prefix/suffix)**: The main content of the line (what you set with `setLine()`)

board.setLine(15, Component.text("Welcome!", NamedTextColor.GOLD));2. **Score (number)**: The numeric value displayed on the right side of each line

board.setLine(14, Component.text("Clean line", NamedTextColor.GRAY));

// ✅ Scores are HIDDEN by default (NumberFormat.BLANK)By default, Minecraft always shows scores as numbers (0, 1, 2, etc.). With ScoreFX v1.2.0 + ProtocolLib, you can customize or hide these numbers.

```

### Requirements

### Hiding Scores Explicitly

⚠️ **ProtocolLib 5.0+ is required for custom score formatting.** Without ProtocolLib:

```java- Scores remain visible as default numbers

// Explicitly hide score (this is the default behavior)- All score customization methods are silently ignored

board.setLineScore(15, null);- Your scoreboards will work normally with default numeric scores



// Or use convenience overloadInstall ProtocolLib: https://www.spigotmc.org/resources/protocollib.1997/

board.setLine(15, Component.text("Clean Line"), null);

```### Default Behavior (v1.2.0)



### Showing Custom Score Text**With ProtocolLib installed, all scores are hidden by default** for a clean, modern appearance. This matches player expectations and provides a polished look out of the box.



```java```java

// Display custom text instead of numbersBoard board = boards().createBoard(player);

Component customScore = Component.text("★", TextColor.fromHexString("#FFD700"));board.setLine(15, Component.text("Welcome!", NamedTextColor.GOLD));

board.setLineScore(14, customScore);board.setLine(14, Component.text("No scores visible", NamedTextColor.GRAY));

// ✅ Scores are HIDDEN by default (blank number format)

// Or use convenience overload```

board.setLine(14, 

    Component.text("VIP Rank", NamedTextColor.YELLOW),### Hiding Scores Explicitly

    Component.text("✓", NamedTextColor.GREEN)

);You can explicitly hide scores on any line by setting the score to `null`:

```

```java

### Practical Examples// Hide score on row 15 (this is the default behavior anyway)

board.setLineScore(15, null);

**Example 1: Clean Modern Scoreboard**

// Or use the convenience overload

```javaboard.setLine(15, Component.text("Clean Line"), null);

Board board = boards().createBoard(player);```



board.setTitle(Component.text("My Server", TextColor.fromHexString("#FF6B35")));### Showing Custom Score Text



// All lines with no scores (default behavior)To display custom text instead of numbers, provide a Component:

board.setLine(15, Component.text("------------------", NamedTextColor.GRAY));

board.setLine(14, Component.text("Player: " + player.getName(), NamedTextColor.YELLOW));```java

board.setLine(13, Component.text("Rank: VIP", NamedTextColor.GOLD));import net.kyori.adventure.text.Component;

board.setLine(12, Component.empty());import net.kyori.adventure.text.format.NamedTextColor;

board.setLine(11, Component.text("Online: 47", NamedTextColor.GREEN));import net.kyori.adventure.text.format.TextColor;

board.setLine(10, Component.text("------------------", NamedTextColor.GRAY));

// ✅ Clean scoreboard with no numeric clutter// Display custom score with RGB color

```Component customScore = Component.text("★", TextColor.fromHexString("#FFD700"));

board.setLineScore(14, customScore);

**Example 2: Custom Icons as Scores**

// Or use the convenience overload

```javaboard.setLine(14, 

// Use symbols/emojis as score indicators    Component.text("VIP Rank", NamedTextColor.YELLOW),

board.setLine(15,     Component.text("✓", NamedTextColor.GREEN)

    Component.text("Health", NamedTextColor.RED),);

    Component.text("❤", NamedTextColor.RED)```

);

board.setLine(14,### Practical Examples

    Component.text("Mana", NamedTextColor.BLUE),

    Component.text("✦", NamedTextColor.AQUA)**Example 1: Clean Modern Scoreboard (All Scores Hidden)**

);

board.setLine(13,```java

    Component.text("Level", NamedTextColor.YELLOW),Board board = boards().createBoard(player);

    Component.text("⭐", NamedTextColor.GOLD)

);// Title with gradient

```board.setTitle(Component.text("My Server", TextColor.fromHexString("#FF6B35")));



**Example 3: Mixed Approach**// All lines with no scores (default behavior)

board.setLine(15, Component.text("------------------", NamedTextColor.GRAY));

```javaboard.setLine(14, Component.text("Player: " + player.getName(), NamedTextColor.YELLOW));

// Show custom scores only where meaningfulboard.setLine(13, Component.text("Rank: VIP", NamedTextColor.GOLD));

board.setLine(15, Component.text("Players Online"), Component.text("47", NamedTextColor.GREEN));board.setLine(12, Component.empty());

board.setLine(14, Component.text("Your Kills"), Component.text("12", NamedTextColor.YELLOW));board.setLine(11, Component.text("Online: 47", NamedTextColor.GREEN));

board.setLine(13, Component.text("------------------"), null);  // Hiddenboard.setLine(10, Component.text("------------------", NamedTextColor.GRAY));

board.setLine(12, Component.text("Server Info"), null);          // Hidden

```// ✅ Result: Clean scoreboard with no numeric clutter

```

### API Reference

**Example 2: Custom Icons as Scores**

```java

// Set custom score for a specific row```java

void setLineScore(int row, @Nullable Component score)// Use symbols/emojis as score indicators

board.setLine(15, 

// Set both line text and custom score    Component.text("Health", NamedTextColor.RED),

void setLine(int row, @NotNull Component text, @Nullable Component score)    Component.text("❤", NamedTextColor.RED)

);

// Legacy String-based method (deprecated but functional)board.setLine(14,

@Deprecated    Component.text("Mana", NamedTextColor.BLUE),

void setLine(int row, @NotNull String text, @Nullable String score)    Component.text("✦", NamedTextColor.AQUA)

```);

board.setLine(13,

---    Component.text("Level", NamedTextColor.YELLOW),

    Component.text("⭐", NamedTextColor.GOLD)

## 📜 Legacy API: Using Strings);

```

**Still fully supported!** All String-based methods remain available and work exactly as before.

**Example 3: Mixed Approach (Some Visible, Some Hidden)**

```java

public void showLegacyBoard(Player player) {```java

    Board board = boards().createBoard(player);// Show custom scores only where meaningful

board.setLine(15, Component.text("Players Online"), Component.text("47", NamedTextColor.GREEN));

    board.setTitle("&6&lMy Server");board.setLine(14, Component.text("Your Kills"), Component.text("12", NamedTextColor.YELLOW));

    board.setLine(15, "&7------------------");board.setLine(13, Component.text("------------------"), null);  // No score

    board.setLine(14, "&ePlayer: &f%player_name%");board.setLine(12, Component.text("Server Info"), null);          // No score

    board.setLine(13, "&eRank: &f%vault_rank%");board.setLine(11, Component.text("discord.gg/example"), null);   // No score

    board.setLine(12, "");```

    board.setLine(11, "&eBalance: &a$%vault_eco_balance%");

    board.setLine(10, "&7------------------");**Example 4: Using Legacy String API with Scores**

}

```The deprecated String-based methods still support custom scores:



Supports:```java

- `&` color codes// Legacy API with custom score

- `§` section signsboard.setLine(15, "&eBalance", "&a$500");

- `&#RRGGBB` hex colors

- `%placeholders%` (requires PlaceholderAPI)// This converts to:

// - Text: Component from "§eBalance" 

---// - Score: Component from "§a$500"

```

## 🎬 Animations

### API Reference

Titles and lines support frame-based animations with custom intervals.

**Primary Method (Component-based):**

```java

// Title animation: 0.5s per frame (10 ticks)```java

Animation title = animations().fromFrames(void setLineScore(int row, @Nullable Component score)

    List.of(```

        "&6&lM&e&ly Server",- Sets the custom score for a specific row (1-15)

        "&e&lM&6&ly Server",- `null` hides the score (default behavior)

        "&6&lMy &e&lServer",- Non-null Component displays as custom score text

        "&e&lMy &6&lServer"- **@since 1.2.0**

    ),

    10**Convenience Overloads:**

);

board.setAnimatedTitle(title);```java

void setLine(int row, @NotNull Component text, @Nullable Component score)

// Line animation: 1s per frame (20 ticks)```

Animation status = animations().fromFrames(- Sets both line text and custom score in one call

    List.of("&a● &7Online", "&e● &7Online", "&c● &7Online"),- Equivalent to calling `setLine(row, text)` then `setLineScore(row, score)`

    20- **@since 1.2.0**

);

board.setAnimatedLine(13, status);```java

```@Deprecated

void setLine(int row, @NotNull String text, @Nullable String score)

Placeholders in animation frames are resolved per-frame before rendering.```

- Legacy String-based method with score support

---- Both parameters converted to Components via LegacySupport

- Supports `&` color codes and `&#RRGGBB` hex colors

## 🧵 Thread Safety- **@since 1.2.0** (deprecated but fully functional)



All mutating operations **must run on the main server thread**. ScoreFX enforces this and throws `IllegalStateException` when violated.### Important Notes



**Safe from any thread:**1. **ProtocolLib is required:** Without it, custom scores are silently ignored

- `boards().getBoard(player)`2. **Default is hidden:** Scores are hidden by default when ProtocolLib is present

- `board.getPlayer()`3. **Per-line control:** Each row can have different score formatting

4. **Component flexibility:** Scores support full Adventure Component features (colors, formatting, etc.)

**Main thread only:**5. **Thread safety:** All score methods must be called from the main thread

- `boards().createBoard(player)`6. **PlaceholderAPI limitation:** Placeholders don't work in Component-based scores (use String methods if needed)

- `boards().removeBoard(player)`

- `board.setTitle(...)`, `board.setAnimatedTitle(...)`### Troubleshooting

- `board.setLine(...)`, `board.setAnimatedLine(...)`

- `board.removeLine(...)`**Q: My custom scores aren't showing**  

A: Ensure ProtocolLib 5.0+ is installed and running. Check console for ProtocolLib detection messages.

**Example:**

```java**Q: Can I use PlaceholderAPI in custom scores?**  

// ❌ Wrong: async mutationA: Only with the deprecated String-based method: `setLine(row, "&eText", "&a%placeholder%")`

Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

    board.setTitle("&cThis will throw"); // IllegalStateException**Q: Can I animate custom scores?**  

});A: Not directly. You can manually update scores in a repeating task if needed.



// ✅ Correct: switch to main thread**Q: Will this break without ProtocolLib?**  

Bukkit.getScheduler().runTask(plugin, () -> {A: No. Without ProtocolLib, scoreboards work normally with default numeric scores visible.

    board.setTitle("&aSafe update");

});---

```

### Important: PlaceholderAPI Limitation

---

⚠️ **PlaceholderAPI only works with String-based methods.** If you need placeholder support, use the legacy String API:

## 🔌 PlaceholderAPI

```java

- Works automatically if PlaceholderAPI is installed// ✅ Placeholders work

- **String methods only:** Placeholders are resolved in String-based methods, not Component methodsboard.setLine(11, "&eBalance: &a$%vault_eco_balance%");

- Supports `&` and `§` color codes, `&#RRGGBB` hex colors, and `%placeholders%`

- Placeholders are resolved right before rendering (keeps data fresh)// ❌ Placeholders DO NOT work with Components

Component line = Component.text("Balance: $%vault_eco_balance%");

**Component + Placeholder Workaround:**board.setLine(11, line);  // Placeholder won't be replaced

```

```java

// 1. Resolve placeholders**Why?** PlaceholderAPI is a String-based API and doesn't understand Components. To support both RGB colors and placeholders, you would need to:

String resolved = PlaceholderAPI.setPlaceholders(player, "%vault_eco_balance%");1. Resolve placeholders with PlaceholderAPI (String → String)

2. Parse the result into a Component with MiniMessage or similar

// 2. Build Component with resolved value

Component line = Component.text()For static content with modern formatting, use Components. For dynamic placeholder-driven content, use Strings.

    .append(Component.text("Balance: ", NamedTextColor.YELLOW))

    .append(Component.text("$" + resolved, NamedTextColor.GREEN))---

    .build();

## Legacy API: Using Strings

board.setLine(11, line);

```## Legacy API: Using Strings



---**Still fully supported!** All String-based methods remain available and work exactly as before. These methods support `&` color codes, `§` codes, hex colors with `&#RRGGBB`, and PlaceholderAPI placeholders.



## ⚙️ Performance: Custom Update IntervalsCreate a board for a player and set content. Rows are 1–15 (1 = bottom, 15 = top).



Use custom intervals for expensive placeholders to reduce server load.```java

import com.dripps.scorefx.api.Board;

```javaimport org.bukkit.entity.Player;

// Expensive placeholder → update every 5 seconds (100 ticks)

board.setLine(11, "&eBalance: &a$%vault_eco_balance%", 100);public void showBoard(Player player) {

    Board board = boards().createBoard(player);

// Cheaper placeholders → default (every tick if placeholders present)

board.setLine(14, "&ePlayer: &f%player_name%");    board.setTitle("&6&lMy Server");

```    board.setLine(15, "&7------------------");

    board.setLine(14, "&ePlayer: &f%player_name%");

---    board.setLine(13, "&eRank: &f%vault_rank%");

    board.setLine(12, "");

## 🏗️ Building from Source    board.setLine(11, "&eBalance: &a$%vault_eco_balance%");

    board.setLine(10, "&7------------------");

```bash}

git clone https://github.com/PooSmacker/ScoreFX.git```

cd scorefx

mvn clean packageRemove a line or the entire board:

```

```java

**Artifacts:**board.removeLine(12);                // Clear a line

- `scorefx-api/target/scorefx-api-2.0.0-SNAPSHOT.jar`boards().removeBoard(player);        // Destroy player’s board

- `scorefx-core/target/scorefx-core-2.0.0-SNAPSHOT.jar````



------



## ❓ FAQ## Lifecycle Example



**Q: Do I need PlaceholderAPI?**  Create a board on join and let ScoreFX handle cleanup on quit. ScoreFX automatically removes boards on player quit and on plugin disable, but you can also manage it explicitly if needed.

A: No. It's optional. If installed, placeholders in String-based methods will be resolved automatically.

```java

**Q: Do I need ProtocolLib?**  import com.dripps.scorefx.api.Board;

A: **No! (v2.0 change)** ScoreFX no longer uses ProtocolLib. Custom score formatting works natively via direct NMS packets.import org.bukkit.event.EventHandler;

import org.bukkit.event.Listener;

**Q: Will my v1.x code work in v2.0?**  import org.bukkit.event.player.PlayerJoinEvent;

A: Yes! 100% backward compatible. Just update the version number.import org.bukkit.entity.Player;



**Q: Should I use Components or Strings?**  public final class JoinListener implements Listener {

A: **Components** for static content with RGB colors and modern formatting. **Strings** for dynamic content with PlaceholderAPI placeholders.    private final YourPlugin plugin;



**Q: How many lines can I use?**      public JoinListener(YourPlugin plugin) {

A: 1–15. Row 1 is bottom, 15 is top.        this.plugin = plugin;

    }

**Q: Do I have to manually clean up?**  

A: No. ScoreFX automatically cleans on player quit and plugin disable.    @EventHandler

    public void onJoin(PlayerJoinEvent e) {

**Q: What's the performance impact?**          Player p = e.getPlayer();

A: Minimal. Single-tick scheduler with priority queue (O(log n)). MethodHandles cached at startup = zero reflection overhead.

        Board board = plugin.boards().createBoard(p);

---        board.setTitle("&e&lWelcome");

        board.setLine(15, "&7------------------");

## 📚 Documentation        board.setLine(14, "&f%player_name%");

        board.setLine(13, "&7Enjoy your stay!");

- Full API documentation: [docs.md](docs.md)        board.setLine(12, "&7------------------");

- Javadocs: included in the API JAR

- Issues: [GitHub Issue Tracker](https://github.com/PooSmacker/ScoreFX/issues)        // Example: placeholder line with a cheaper interval (every tick)

        board.setLine(11, "&eOnline: &f%server_online%");

---    }

}

## 📄 License```



MIT LicenseRegister your listener in `onEnable()`:

```java

---getServer().getPluginManager().registerEvents(new JoinListener(this), this);

```

## 👏 Credits

---

- **Author:** Dripps

- **Platform:** PaperMC## Performance: Custom Update Intervals

- **Inspired by:** FastBoard's MethodHandles reflection architecture

Use custom intervals for expensive placeholders to reduce load.

---

```java

## 🔗 Links// Expensive placeholder (e.g., economy) → update every 5 seconds (100 ticks)

board.setLine(11, "&eBalance: &a$%vault_eco_balance%", 100);

- [GitHub Repository](https://github.com/PooSmacker/ScoreFX)

- [JitPack](https://jitpack.io/#PooSmacker/ScoreFX)// Cheaper placeholders can remain at the default (every tick if placeholders are present)

- [PaperMC](https://papermc.io/)board.setLine(14, "&ePlayer: &f%player_name%");

- [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)```


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
- **String methods only:** Placeholders are only resolved in String-based methods, not Component methods.
- All String inputs support `&` and `§` color codes, `&#RRGGBB` hex colors, and `%placeholders%`.
- Placeholders are resolved right before rendering (keeps data fresh).
- If PlaceholderAPI is not present, text is used as-is.

**Component + Placeholder Workaround:**

If you need both RGB colors and PlaceholderAPI, you must resolve placeholders yourself first. This is because PlaceholderAPI operates on raw strings, while Adventure Components are complex objects that cannot be easily searched for placeholders after they are created:

```java
// 1. Resolve placeholders
String resolved = PlaceholderAPI.setPlaceholders(player, "%vault_eco_balance%");

// 2. Build Component with resolved value
Component line = Component.text()
    .append(Component.text("Balance: ", NamedTextColor.YELLOW))
    .append(Component.text("$" + resolved, NamedTextColor.GREEN))
    .build();

board.setLine(11, line);
```

Alternatively, use MiniMessage to parse the entire string:
```java
String text = PlaceholderAPI.setPlaceholders(player, "Balance: <yellow>$%vault_eco_balance%");
Component line = MiniMessage.miniMessage().deserialize(text);
board.setLine(11, line);
```

---

## Building from Source

```bash
git clone https://github.com/dripps/scorefx.git
cd scorefx
mvn clean package
```

Artifacts:
- `scorefx-api/target/scorefx-api-1.2.0-SNAPSHOT.jar`
- `scorefx-core/target/scorefx-core-1.2.0-SNAPSHOT.jar`

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
