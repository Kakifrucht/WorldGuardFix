package de.halfminer.worldguardfix;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import net.minecraft.server.v1_9_R1.EntityFishingHook;
import net.minecraft.server.v1_9_R1.EntityPlayer;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.LingeringPotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.Iterator;

public class Listeners implements Listener {

    private final WorldGuardHelper helper = WorldGuardFix.getInstance().getWgHelper();
    private final WorldGuardPlugin wg = helper.getWorldGuard();

    @EventHandler
    public void disablePullEffect(ProjectileHitEvent e) {

        if (!(e.getEntity() instanceof FishHook)) {
            return;
        }

        Player shooter = (Player) e.getEntity().getShooter();

        EntityPlayer entityPlayer = ((CraftPlayer) shooter).getHandle();
        EntityFishingHook hook = entityPlayer.hookedFish;
        for (Entity entity : e.getEntity().getNearbyEntities(0.35D, 0.35D, 0.35D)) {

            if (entity instanceof Player && !entity.getName().equals(shooter.getName())) {

                if (helper.isPvPAllowed(shooter, (Player) entity)) continue;
                if (hook != null) {
                    hook.hooked = null;
                    hook.die();
                }
                entityPlayer.hookedFish = null;
                return;
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void disableFrostWalker(EntityBlockFormEvent e) {
        if (e.getEntity() instanceof Player
                && !wg.canBuild((Player) e.getEntity(), e.getBlock())) e.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void disableLingeringPotions(LingeringPotionSplashEvent e) {
        if (!helper.isPotionSplashAllowed(e.getEntity().getLocation())) e.setCancelled(true);
    }

    @EventHandler
    public void disableAreaEffect(AreaEffectCloudApplyEvent e) {

        Iterator<LivingEntity> it = e.getAffectedEntities().iterator();
        while (it.hasNext()) {
            LivingEntity current = it.next();
            if (current instanceof Player) {
                Player p = (Player) current;
                if (!helper.isPotionSplashAllowed(p.getLocation())) {
                    it.remove();
                }
            }
        }
    }
}