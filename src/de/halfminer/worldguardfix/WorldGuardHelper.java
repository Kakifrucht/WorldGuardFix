package de.halfminer.worldguardfix;

import com.sk89q.worldguard.bukkit.*;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionData;

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
        return isAllowed(attacker, DefaultFlag.PVP) && isAllowed(victim, DefaultFlag.PVP);
    }

    public boolean isAllowed(Player player, StateFlag flag) {
        return isAllowedQuery((BukkitPlayer) wg.wrapPlayer(player), player.getLocation(), flag, true);
    }

    public boolean isAllowed(Location loc, StateFlag flag) {
        return isAllowedQuery(null, loc, flag, true);
    }

    public boolean isAllowed(Player player, Location loc, StateFlag flag) {
        return isAllowedQuery(player != null ? (BukkitPlayer) wg.wrapPlayer(player) : null, loc, flag, true);
    }

    public boolean isAllowed(Player player, Location loc, StateFlag flag, boolean valueIfNull) {
        return isAllowedQuery(player != null ? (BukkitPlayer) wg.wrapPlayer(player) : null, loc, flag, valueIfNull);
    }

    private boolean isAllowedQuery(BukkitPlayer ass, Location loc, StateFlag flag, boolean valueIfNull) {

        if (ass != null
                && !flag.equals(DefaultFlag.PVP)
                && ass.hasPermission("worldguard.region.bypass." + loc.getWorld().getName()))
            return true;

        StateFlag.State state = wg.getRegionContainer().createQuery().queryState(loc, ass, flag);

        boolean isAllowed = valueIfNull;
        if (state != null) isAllowed = state.equals(StateFlag.State.ALLOW);
        return isAllowed;
    }

    public boolean isBlacklistedPotion(PotionData meta, World world) {
        return wg.getGlobalStateManager().get(world).blockPotions.contains(meta.getType().getEffectType());
    }
}
