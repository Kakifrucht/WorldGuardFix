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

    private final Map<Node, Boolean> settings = new HashMap<>();
    private final Set<World> disabledWorlds = new HashSet<>();

    private boolean configUsed;
    private boolean worldsDisabled;

    public Config() {
        load();
    }

    public boolean checkEnabled(Node node, Location loc) {
        return !configUsed || (settings.get(node) && !(worldsDisabled && disabledWorlds.contains(loc.getWorld())));
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
            configUsed = true;
            settings.put(Node.FISHING_HOOK, config.getBoolean("enableFishingHookCheck", true));
            settings.put(Node.FROSTWALKER, config.getBoolean("enableFrostwalkerCheck", true));
            settings.put(Node.CHORUS_FRUIT, config.getBoolean("enableChorusFruitCheck", true));
            settings.put(Node.BOAT_PLACE, config.getBoolean("enableBoatCheck", true));
            settings.put(Node.END_CRYSTAL_PLACE, config.getBoolean("enableEndCrystalCheck", true));
            settings.put(Node.LILYPAD_BREAK, config.getBoolean("enableLilypadCheck", true));

            disabledWorlds.clear();
            for (String worldName : config.getStringList("disableAllInWorld")) {
                World world = Bukkit.getWorld(worldName);
                if (world != null) disabledWorlds.add(world);
            }
            worldsDisabled = disabledWorlds.size() > 0;
            return true;
        } else {
            configUsed = false;
            settings.clear();
            return false;
        }
    }

    private boolean useConfigFile() {
        return new File(fix.getDataFolder(), "config.yml").exists();
    }

    enum Node {
        FISHING_HOOK,
        FROSTWALKER,
        CHORUS_FRUIT,
        BOAT_PLACE,
        END_CRYSTAL_PLACE,
        LILYPAD_BREAK
    }
}
