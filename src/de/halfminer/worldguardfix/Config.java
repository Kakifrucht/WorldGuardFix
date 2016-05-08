package de.halfminer.worldguardfix;

import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class Config {

    private final WorldGuardFix fix = WorldGuardFix.getInstance();

    boolean enableFishingHookCheck;
    boolean enableFrostwalkerCheck;
    boolean enableChorusFruitCheck;
    boolean enableBoatCheck;
    boolean enableLilypadCheck;

    public Config() {
        load();
    }

    public boolean generate() {

        if (!useConfigFile()) {
            fix.saveDefaultConfig();
            return true;
        } else return false;
    }

    public boolean load() {

        if (useConfigFile()) {
            fix.reloadConfig();
            FileConfiguration config = fix.getConfig();
            config.options().copyDefaults(true);
            fix.saveConfig();
            enableFishingHookCheck = config.getBoolean("enableFishingHookCheck", true);
            enableFrostwalkerCheck = config.getBoolean("enableFrostwalkerCheck", true);
            enableChorusFruitCheck = config.getBoolean("enableChorusFruitCheck", true);
            enableBoatCheck = config.getBoolean("enableBoatCheck", true);
            enableLilypadCheck = config.getBoolean("enableLilypadCheck", true);
            return true;
        } else {
            enableFishingHookCheck = true;
            enableFrostwalkerCheck = true;
            enableChorusFruitCheck = true;
            enableBoatCheck = true;
            enableLilypadCheck = true;
            return false;
        }
    }

    private boolean useConfigFile() {
        return new File(fix.getDataFolder(), "config.yml").exists();
    }
}
