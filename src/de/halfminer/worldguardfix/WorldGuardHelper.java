package de.halfminer.worldguardfix;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
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

    public boolean isPotionSplashAllowed(Location loc) {

        StateFlag.State s = wg.getRegionManager(loc.getWorld()).getApplicableRegions(loc)
                .queryValue(null, DefaultFlag.POTION_SPLASH);

        boolean isSplashAllowed = true;
        if (s != null) isSplashAllowed = s.toString().equals("ALLOW");
        return isSplashAllowed;
    }

    public boolean isPvPAllowed(Player attacker, Player victim) {
        return isPvPAllowedQuery(attacker) && isPvPAllowedQuery(victim);
    }

    private boolean isPvPAllowedQuery(Player p) {

        StateFlag.State s = wg
                            .getRegionManager(p.getWorld())
                            .getApplicableRegions(p.getLocation())
                            .queryValue(null, DefaultFlag.PVP);

        boolean isPvPAllowed = true;
        if (s != null) isPvPAllowed = s.toString().equals("ALLOW");
        return isPvPAllowed;
    }
}