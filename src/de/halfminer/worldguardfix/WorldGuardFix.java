package de.halfminer.worldguardfix;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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

    private Config config;
    private WorldGuardHelper wgh;

    @Override
    public void onEnable() {

        instance = this;
        config = new Config();
        wgh = new WorldGuardHelper();
        getServer().getPluginManager().registerEvents(new Listeners(), this);
        getLogger().info("WorldGuardFix enabled");
    }

    @Override
    public void onDisable() {

        getLogger().info("WorldGuardFix disabled");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("worldguardfix")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission");
            return true;
        }

        if (args.length > 0) {

            if (args[0].equalsIgnoreCase("generate")) {
                if (config.generate()) sender.sendMessage(ChatColor.GREEN + "Configuration generated successfully");
                else sender.sendMessage(ChatColor.RED + "You already have a config file");
            } else if (args[0].equalsIgnoreCase("reload")) {
                if (config.load()) sender.sendMessage(ChatColor.GREEN + "WorldGuardFix Configuration reloaded");
                else sender.sendMessage(ChatColor.RED + "No configuration file found, generate it with"
                        + ChatColor.ITALIC + " /worldguardfix generate");
            }

        } else sender.sendMessage("WorldGuardFix version " + getDescription().getVersion()
                + "\n" + "WorldGuard version " + wgh.getWorldGuard().getDescription().getVersion()
                + "\n" + ChatColor.AQUA + ChatColor.ITALIC + "\n" + "https://www.spigotmc.org/resources/worldguard-fix.22712/"
                + "\n \n" + ChatColor.RESET + ChatColor.BOLD + "Commands:\n" + ChatColor.RESET
                + "Generate default config file: " + ChatColor.ITALIC + " /worldguardfix generate"
                + ChatColor.RESET + "\n" + "Reload config: " + ChatColor.ITALIC + "/worldguardfix reload");
        return false;
    }

    public Config getCustomConfig() {
        return config;
    }

    public WorldGuardHelper getWgHelper() {
        return wgh;
    }
}