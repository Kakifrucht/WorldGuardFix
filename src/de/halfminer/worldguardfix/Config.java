package de.halfminer.worldguardfix;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Config {

    private final WorldGuardFix fix = WorldGuardFix.getInstance();

    private final Map<ConfigNode, Boolean> settings = new HashMap<>();
    private final Set<World> disabledWorlds = new HashSet<>();

    public Config() {
        load();
    }

    public boolean checkEnabled(ConfigNode node, Location loc) {
        return settings.get(node) && !disabledWorlds.contains(loc.getWorld());
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
            settings.put(ConfigNode.FISHING_HOOK, config.getBoolean("enableFishingHookCheck", true));
            settings.put(ConfigNode.FROSTWALKER, config.getBoolean("enableFrostwalkerCheck", true));
            settings.put(ConfigNode.CHORUS_FRUIT, config.getBoolean("enableChorusFruitCheck", true));
            settings.put(ConfigNode.BOAT_PLACE, config.getBoolean("enableBoatCheck", true));
            settings.put(ConfigNode.END_CRYSTAL_PLACE, config.getBoolean("enableEndCrystalCheck", true));
            settings.put(ConfigNode.LILYPAD_BREAK, config.getBoolean("enableLilypadCheck", true));

            disabledWorlds.clear();
            for (String worldName : config.getStringList("disableAllInWorld")) {
                for (World world : Bukkit.getServer().getWorlds())
                    if (world.getName().equalsIgnoreCase(worldName)) {
                        disabledWorlds.add(world);
                        break;
                    }
            }
            return true;
        } else {
            settings.put(ConfigNode.FISHING_HOOK, true);
            settings.put(ConfigNode.FROSTWALKER, true);
            settings.put(ConfigNode.CHORUS_FRUIT, true);
            settings.put(ConfigNode.BOAT_PLACE, true);
            settings.put(ConfigNode.END_CRYSTAL_PLACE, true);
            settings.put(ConfigNode.LILYPAD_BREAK, true);
            return false;
        }
    }

    private boolean useConfigFile() {
        return new File(fix.getDataFolder(), "config.yml").exists();
    }

    enum ConfigNode {
        FISHING_HOOK,
        FROSTWALKER,
        CHORUS_FRUIT,
        BOAT_PLACE,
        END_CRYSTAL_PLACE,
        LILYPAD_BREAK
    }
}
