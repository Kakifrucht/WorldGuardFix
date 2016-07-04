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

        if (configUsed = useConfigFile()) {

            fix.reloadConfig();
            FileConfiguration config = fix.getConfig();
            config.options().copyDefaults(true);
            fix.saveConfig();

            for (Node node : Node.values()) settings.put(node, config.getBoolean(node.getSetting(), true));

            disabledWorlds.clear();
            for (String worldName : config.getStringList("disableAllInWorld")) {
                World world = Bukkit.getWorld(worldName);
                if (world != null) disabledWorlds.add(world);
            }
            worldsDisabled = disabledWorlds.size() > 0;

            return true;
        } else {

            settings.clear();
            return false;
        }
    }

    private boolean useConfigFile() {
        return new File(fix.getDataFolder(), "config.yml").exists();
    }

    enum Node {

        FISHING_HOOK        ("enableFishingHookCheck"),
        FROSTWALKER         ("enableFrostwalkerCheck"),
        CHORUS_FRUIT        ("enableChorusFruitCheck"),
        BOAT_PLACE          ("enableBoatCheck"),
        END_CRYSTAL_PLACE   ("enableEndCrystalCheck"),
        LILYPAD_BREAK       ("enableLilypadCheck");

        private String setting;

        Node(String configSetting) {
            setting = configSetting;
        }

        String getSetting() {
            return setting;
        }
    }
}
