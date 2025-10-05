package com.dripps.scorefx;

import com.dripps.scorefx.animation.AnimationFactoryImpl;
import com.dripps.scorefx.api.ScoreFX;
import com.dripps.scorefx.hook.PAPIHook;
import com.dripps.scorefx.listener.PlayerQuitListener;
import com.dripps.scorefx.manager.BoardManagerImpl;
import com.dripps.scorefx.scheduler.Heartbeat;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin class for ScoreFX.
 * <p>
 * This class manages the plugin's lifecycle, including initialization of all managers,
 * registration of the API service, and cleanup of resources on shutdown.
 * </p>
 * <p>
 * <strong>Initialization Order (v2.0.0):</strong>
 * <ol>
 *   <li>Create PlaceholderAPI hook</li>
 *   <li>Create Heartbeat scheduler with PAPI hook</li>
 *   <li>Create AnimationFactory</li>
 *   <li>Create BoardManager with Heartbeat reference</li>
 *   <li>Create ScoreFX API implementation</li>
 *   <li>Register API with ServicesManager</li>
 *   <li>Register event listeners</li>
 *   <li>Start Heartbeat</li>
 * </ol>
 * </p>
 * <p>
 * <strong>Shutdown Order:</strong>
 * <ol>
 *   <li>Remove all active boards</li>
 *   <li>Stop Heartbeat scheduler</li>
 *   <li>Unregister API service</li>
 * </ol>
 * </p>
 *
 * @author ScoreFX
 * @version 2.0.0-SNAPSHOT
 */
public final class ScoreFXPlugin extends JavaPlugin {
    
    private PAPIHook papiHook;
    private Heartbeat heartbeat;
    private BoardManagerImpl boardManager;
    private AnimationFactoryImpl animationFactory;
    private ScoreFXImpl apiImpl;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("┌─────────────────────────────────────┐");
        getLogger().info("│         ScoreFX v2.0.0-SNAPSHOT    │");
        getLogger().info("│   Dependency-Free Scoreboard API    │");
        getLogger().info("└─────────────────────────────────────┘");
        
        getLogger().info("Initializing ScoreFX...");
        
        try {
            // 1. Create PlaceholderAPI hook
            getLogger().info("Initializing PlaceholderAPI hook...");
            papiHook = new PAPIHook(getLogger());
            
            // 2. Create Heartbeat scheduler
            getLogger().info("Creating Heartbeat scheduler...");
            heartbeat = new Heartbeat(this, papiHook);
            
            // 3. Create AnimationFactory
            getLogger().info("Creating Animation factory...");
            animationFactory = new AnimationFactoryImpl();
            
            // 4. Create BoardManager with Heartbeat reference
            getLogger().info("Creating Board manager...");
            boardManager = new BoardManagerImpl(heartbeat, getLogger());
            
            // 5. Create ScoreFX API implementation
            getLogger().info("Creating API implementation...");
            apiImpl = new ScoreFXImpl(boardManager, animationFactory);
            
            // 6. Register API with ServicesManager
            getLogger().info("Registering API with Services Manager...");
            ServicesManager servicesManager = Bukkit.getServicesManager();
            servicesManager.register(
                ScoreFX.class,
                apiImpl,
                this,
                ServicePriority.Normal
            );
            
            // 7. Register event listeners
            getLogger().info("Registering event listeners...");
            Bukkit.getPluginManager().registerEvents(
                new PlayerQuitListener(boardManager, getLogger()),
                this
            );
            
            // 8. Start Heartbeat
            getLogger().info("Starting Heartbeat scheduler...");
            heartbeat.start();
            
            getLogger().info("ScoreFX has been enabled successfully!");
            getLogger().info("API is now available via Bukkit Services Manager");
            
        } catch (Exception e) {
            getLogger().severe("Failed to initialize ScoreFX: " + e.getMessage());
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Shutting down ScoreFX...");
        
        try {
            // 1. Remove all active boards (must be done before stopping Heartbeat)
            if (boardManager != null) {
                getLogger().info("Removing all active boards...");
                boardManager.removeAllBoards();
            }
            
            // 2. Stop Heartbeat scheduler (cancels all tasks)
            if (heartbeat != null) {
                getLogger().info("Stopping Heartbeat scheduler...");
                heartbeat.stop();
            }
            
            // 3. Unregister API service
            getLogger().info("Unregistering API service...");
            ServicesManager servicesManager = Bukkit.getServicesManager();
            servicesManager.unregisterAll(this);
            
            getLogger().info("ScoreFX has been disabled successfully!");
            
        } catch (Exception e) {
            getLogger().severe("Error during shutdown: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Gets the Heartbeat scheduler instance.
     * <p>
     * This is primarily for internal use and testing.
     * </p>
     *
     * @return the Heartbeat instance, or null if not initialized
     */
    public Heartbeat getHeartbeat() {
        return heartbeat;
    }
    
    /**
     * Gets the BoardManager instance.
     * <p>
     * This is primarily for internal use and testing.
     * </p>
     *
     * @return the BoardManager instance, or null if not initialized
     */
    public BoardManagerImpl getBoardManager() {
        return boardManager;
    }
}
