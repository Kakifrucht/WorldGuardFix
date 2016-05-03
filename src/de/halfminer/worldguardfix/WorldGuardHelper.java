package de.halfminer.worldguardfix;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WorldGuardHelper {

    private final WorldGuardPlugin wg;

    public WorldGuardHelper() {
        wg = WGBukkit.getPlugin();
    }

    public WorldGuardPlugin getWorldGuard() {
        return wg;
    }

    public boolean hasRegion(Location loc) {
        return wg.getRegionManager(loc.getWorld()).getApplicableRegions(loc).size() > 0;
    }

    public boolean isPvPAllowed(Player attacker, Player victim) {

        return isAllowed(attacker.getLocation(), DefaultFlag.PVP) && isAllowed(victim.getLocation(), DefaultFlag.PVP);
    }

    public boolean isAllowed(Location loc, Flag<StateFlag.State> flag) {

        StateFlag.State s = wg
                .getRegionManager(loc.getWorld())
                .getApplicableRegions(loc)
                .queryValue(null, flag);

        boolean isAllowed = true;
        if (s != null) isAllowed = s.toString().equals("ALLOW");
        return isAllowed;
    }
}