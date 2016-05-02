package de.halfminer.worldguardfix;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * WorldGuard addon to implement 1.9 functionality
 * @author Fabian Prieto Wunderlich / Kakifrucht
 */
public class WorldGuardFix extends JavaPlugin {

    private static WorldGuardFix instance;

    public static WorldGuardFix getInstance() {
        return instance;
    }

    private WorldGuardHelper wgh;

    @Override
    public void onEnable() {

        instance = this;
        wgh = new WorldGuardHelper();
        getServer().getPluginManager().registerEvents(new Listeners(), this);
        getLogger().info("WorldGuardFix enabled");
    }

    @Override
    public void onDisable() {

        getLogger().info("WorldGuardFix disabled");
    }

    public WorldGuardHelper getWgHelper() {
        return wgh;
    }
}