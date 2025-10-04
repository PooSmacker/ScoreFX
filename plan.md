ScoreFX — AI Implementation Blueprint
1. Project Mission & Core Philosophy
   Mission: You are to construct a high-performance, developer-first Scoreboard API plugin for Paper 1.21.8 named ScoreFX. This project will be built using a multi-module Maven setup. The final product must be robust, easy for other developers to integrate, and highly performant.
   Core Philosophy (You MUST adhere to these principles):
   API-First Design: The scorefx-api module is a sacred, immutable contract. It will contain zero implementation logic. The scorefx-core is the engine that brings it to life. This separation is absolute.
   Performance is Non-Negotiable: Every architectural decision must prioritize performance. This means minimal object creation in hot paths, intelligent scheduling, and efficient packet manipulation via the Bukkit API.
   Developer Experience (DX) is Paramount: The public API must be intuitive, thoroughly documented via Javadocs, and safe. It must guide developers toward correct usage and aggressively prevent misuse (e.g., async access). (Please just put all documentation in docs.md and KEEP IT UPDATED AS YOU WORK!)
   Stability and Resilience: The code must be resilient to edge cases (players leaving, server reloads) and must not create memory leaks. All resources (listeners, tasks) must be meticulously cleaned up.
2. Project Structure & Maven Configuration
   You will work within the pre-configured multi-module Maven project:
   scorefx-parent: The aggregator POM. You will not write code here.
   scorefx-api: The API module. Only interfaces, models (records/POJOs), and custom Bukkit events go here.
   scorefx-core: The implementation module. This is where all logic, plugin lifecycle, and internal classes reside.
3. Feature Set & Requirements
   Per-Player Scoreboards: Each player can have a unique scoreboard.
   Dynamic Line Updates: Lines can be set, updated, and removed efficiently without flicker.
   Animated Lines & Titles: Support for text animations (e.g., scrolling, blinking, fading) with configurable update intervals.
   Per-Line Update Intervals: Allow developers to specify that a line with costly placeholders should only update every N ticks, reducing server load.
   PlaceholderAPI Integration: If PlaceholderAPI is present, it must be used to resolve placeholders in all text. This must be a soft dependency.
   Flicker-Free Rendering: Updates must be seamless. This will be achieved via the Scoreboard Team Prefix/Suffix method.
   Clean API: A simple, fluent API exposed via the Bukkit Services Manager.
   Resource Management: All tasks and listeners must be properly registered on enable and unregistered on disable to prevent memory leaks and server hangs.
4. API Surface Definition (scorefx-api Module)
   You are to create the following interfaces and classes within the com.dripps.scorefx.api package structure. Every public class, interface, and method MUST have comprehensive Javadocs explaining its purpose, parameters, and return value. Use @NotNull annotations from a shaded library (e.g., JetBrains annotations) for all non-nullable parameters and return types.
<details>
<summary>Click to expand API definitions</summary>
code
Java
package com.dripps.scorefx.api;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull; // Example annotation
import java.util.Optional;

/**
* The main entrypoint for the ScoreFX API.
* Obtain this service from the Bukkit Services Manager.
  */
  public interface ScoreFX {
  @NotNull BoardManager getBoardManager();
  @NotNull AnimationFactory getAnimationFactory();
  }
  code
  Java
  package com.dripps.scorefx.api;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;

/**
* Manages the lifecycle of all scoreboards.
  */
  public interface BoardManager {
  /**
    * Creates a new scoreboard for a player, replacing any existing one.
    * @param player The player to create the board for.
    * @return The newly created Board instance.
      */
      @NotNull Board createBoard(@NotNull Player player);

  /**
    * Retrieves the active scoreboard for a player.
    * @param player The player whose board to retrieve.
    * @return An Optional containing the board, or empty if none exists.
      */
      @NotNull Optional<Board> getBoard(@NotNull Player player);

  /**
    * Destroys and removes a player's scoreboard.
    * @param player The player whose board to remove.
      */
      void removeBoard(@NotNull Player player);
      }
      code
      Java
      package com.dripps.scorefx.api;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
* Represents a single, per-player scoreboard.
* All methods that modify state are strictly main-thread only.
  */
  public interface Board {
  /**
    * Sets the title of the scoreboard. Supports color codes.
    * @param title The text to display.
      */
      void setTitle(@NotNull String title);

  /**
    * Sets the title of the scoreboard to an animation.
    * @param titleAnimation The animation to play in the title.
      */
      void setAnimatedTitle(@NotNull Animation titleAnimation);

  /**
    * Sets the text for a specific row. This line will update every tick if it contains placeholders.
    * @param row The row number (1-15).
    * @param text The text to display. Supports color codes and placeholders.
      */
      void setLine(int row, @NotNull String text);

  /**
    * Sets the text for a specific row with a custom update interval.
    * Use this for lines with performance-intensive placeholders.
    * @param row The row number (1-15).
    * @param text The text to display. Supports color codes and placeholders.
    * @param updateIntervalTicks The interval in server ticks between placeholder updates.
      */
      void setLine(int row, @NotNull String text, int updateIntervalTicks);

  /**
    * Sets a row to display an animation.
    * @param row The row number (1-15).
    * @param animation The animation to play on this line.
      */
      void setAnimatedLine(int row, @NotNull Animation animation);

  /**
    * Clears a line from the scoreboard.
    * @param row The row number to clear.
      */
      void removeLine(int row);

  /**
    * Gets the player who owns this board.
    * @return The owning player.
      */
      @NotNull Player getPlayer();
      }
      code
      Java
      package com.dripps.scorefx.api.animation;

/**
* Represents a sequence of text frames.
  */
  public interface Animation {
  /**
    * Gets the next frame of the animation.
    * @return The next string in the sequence.
      */
      @NotNull String nextFrame();

  /**
    * Gets the interval in server ticks between frames.
    * @return The tick interval.
      */
      int getIntervalTicks();
      }
      code
      Java
      package com.dripps.scorefx.api.animation;

import org.jetbrains.annotations.NotNull;
import java.util.List;

/**
* A factory for creating common animation patterns.
  */
  public interface AnimationFactory {
  /**
    * Creates a simple animation that cycles through a list of frames.
    * @param frames The list of strings to cycle through.
    * @param intervalTicks The delay in ticks between each frame.
    * @return A new Animation instance.
      */
      @NotNull Animation fromFrames(@NotNull List<String> frames, int intervalTicks);
      }
</details>
5. Implementation Architecture (scorefx-core Module)
You will implement the logic as follows. Adherence to this architecture is mandatory.
Rendering Engine: Team-Based Approach ONLY
The core implementation class, TeamBoardImpl, will implement the Board interface.
Upon creation, it will generate a new Scoreboard object, a single Objective, and 16 Team objects, one for each potential line.
Flicker-Free Updates: To change a line's text, you MUST NOT remove the old score. You MUST get the Team corresponding to that line and update its prefix and suffix.
Line Splitting: You must create a LineSplitter utility class. This class will take a single string and intelligently divide it between the team's prefix and suffix to accommodate modern text lengths. This is a critical component.
Advanced Scheduler: The "Heartbeat"
You will create a Heartbeat class that runs a single BukkitRunnable every single tick.
This class will manage a PriorityQueue of scheduled update tasks. A task could be "update line 5 on player X's board" or "advance title animation for player Y".
Each tick, the Heartbeat will execute only the tasks whose scheduled execution time has arrived. This is far more efficient than iterating over every board and every line every tick.
The setLine and setAnimatedLine methods will simply schedule tasks with the Heartbeat.
Plugin Lifecycle & Service Registration
The main class ScoreFXPlugin will extend JavaPlugin.
In onEnable(), you will initialize all managers (BoardManagerImpl, etc.) and the Heartbeat. You will register the API implementation with Bukkit.getServicesManager().register(...). You will also register a PlayerQuitEvent listener to automatically call boardManager.removeBoard(player).
In onDisable(), you will cancel the Heartbeat task, unregister the service, and clear all active boards to ensure a clean shutdown.
6. Coding Standards & Mandates (MUST BE FOLLOWED)
Language Level: Use modern Java 21 features where appropriate (e.g., records for simple data carriers, var for local variables, switch expressions).
Immutability and Finality:
All class fields that are not reassigned MUST be declared final.
Favor immutable objects and collections (List.of(), Map.of()) where possible.
API Thread Safety: Every public method in your core implementation that modifies state (e.g., TeamBoardImpl.setLine) MUST begin with the following check:
code
Java
if (!Bukkit.isPrimaryThread()) {
    throw new IllegalStateException("ScoreFX API must be accessed from the main server thread.");
}
Documentation:
Every public API class, interface, and method in the scorefx-api module MUST have comprehensive Javadocs.
Complex or non-obvious code blocks in scorefx-core MUST have explanatory comments.
Naming Conventions:
Interfaces are named plainly (e.g., Board).
Implementations are named with an Impl suffix (e.g., BoardManagerImpl).
Use standard Java camelCase conventions.
Error Handling & Logging:
DO NOT use System.out.println(). Use the plugin's Logger provided by JavaPlugin.getLogger().
Use Optional correctly in the API to represent the potential absence of a value. Avoid returning null from public API methods.
Catch exceptions gracefully. Do not let them bubble up to the console unless they are critical and unrecoverable.
Dependency Shading: You will use the maven-shade-plugin. Add the org.jetbrains:annotations dependency and configure the plugin to relocate it to com.dripps.scorefx.libs.annotations. This is a test of your ability to follow complex build instructions.
7. Step-by-Step Execution Plan
Execute the following tasks in this precise order.
Task 1: API Module Creation
In the scorefx-api module, create all interfaces and classes defined in Section 4.
Ensure all Javadocs and annotations are present.
Task 2: Core Module Skeleton
Create the ScoreFXPlugin main class in scorefx-core.
Implement onEnable and onDisable with logging messages.
Create the plugin.yml file in src/main/resources.
Task 3: Implement the TeamBoardImpl
Create the TeamBoardImpl class implementing Board.
Implement the constructor to create the Scoreboard, Objective, and 16 Team objects.
Implement the LineSplitter utility.
Implement the setLine method (without animation/scheduling) to use the LineSplitter and update team prefixes/suffixes.
Task 4: Implement the Heartbeat Scheduler
Create the Heartbeat class with its PriorityQueue and BukkitRunnable.
Define a simple task record/class for the queue to hold.
Task 5: Implement BoardManagerImpl
Create BoardManagerImpl. It will hold a Map<UUID, Board>.
Implement createBoard, getBoard, and removeBoard.
The createBoard method will instantiate TeamBoardImpl and register it with the Heartbeat for future updates.
Task 6: Wire Everything Together
In ScoreFXPlugin, instantiate your managers and the Heartbeat.
Register your API implementation with the ServicesManager.
Implement the PlayerQuitEvent listener to clean up boards.
Task 7: Implement Animations
Implement the AnimationFactory.
Modify TeamBoardImpl's setAnimatedLine and setAnimatedTitle methods to create recurring tasks and schedule them with the Heartbeat.
Implement the per-line update interval logic by scheduling tasks appropriately in the Heartbeat.
Task 8: Implement PlaceholderAPI Hook
Create a PAPIHook class that safely checks for PlaceholderAPI's existence.
Integrate the hook into the Heartbeat's update logic, so PlaceholderAPI.setPlaceholders() is called just before a line's text is sent to the board.
Task 9: Final Review and Cleanup
Review all code against the Coding Standards in Section 6.
Ensure all resources are properly managed.
Verify the maven-shade-plugin is configured correctly.
Build the project and confirm the final JARs are created correctly.