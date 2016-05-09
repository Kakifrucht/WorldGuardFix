package de.halfminer.worldguardfix;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.bukkit.cause.Cause;
import com.sk89q.worldguard.bukkit.event.inventory.UseItemEvent;
import com.sk89q.worldguard.bukkit.util.Materials;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import net.minecraft.server.v1_9_R1.EntityFishingHook;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.LingeringPotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

class Listeners implements Listener {

    private final Config config = WorldGuardFix.getInstance().getCustomConfig();
    private final WorldGuardHelper helper = WorldGuardFix.getInstance().getWgHelper();
    private final WorldGuardPlugin wg = helper.getWorldGuard();

    @EventHandler
    public void disableRodPullAndTippedArrowBlacklist(ProjectileHitEvent e) {

        if (e.getEntity() instanceof FishHook) {

            if (!config.enableFishingHookCheck) return;

            Player shooter = (Player) e.getEntity().getShooter();

            for (Entity entity : e.getEntity().getNearbyEntities(0.35d, 0.35d, 0.35d)) {

                if (entity instanceof Player
                        && !entity.equals(shooter)
                        && !helper.isPvPAllowed(shooter, (Player) entity)) {

                    EntityFishingHook hook = ((CraftPlayer) shooter).getHandle().hookedFish;
                    if (hook != null) {
                        hook.die();
                        shooter.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD
                                + "Hey!" + ChatColor.GRAY + " Sorry, but you can't PvP here.");
                    }
                    return;
                }
            }
        } else if (e.getEntity() instanceof TippedArrow
                && e.getEntity().getShooter() instanceof Player
                && !helper.getWorldGuard().hasPermission((Player) e.getEntity().getShooter(), "worldguard.override.potions")) {

            TippedArrow arrow = (TippedArrow) e.getEntity();
            if (helper.isBlacklistedPotion(arrow.getBasePotionData(), e.getEntity().getLocation().getWorld())) {
                ((Player) arrow.getShooter()).sendMessage(ChatColor.RED + "Sorry, tipped arrows with "
                        + arrow.getBasePotionData().getType().name() + " have no effect.");
                arrow.setBasePotionData(new PotionData(PotionType.AWKWARD));
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void disableFrostWalker(EntityBlockFormEvent e) {

        if (e.getEntity() instanceof Player
                && config.enableFrostwalkerCheck
                && !wg.canBuild((Player) e.getEntity(), e.getBlock())) e.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void disableLingeringPotions(LingeringPotionSplashEvent e) {

        Player shooter = null;
        if (e.getEntity().getShooter() instanceof Player) shooter = (Player) e.getEntity().getShooter();

        if (!helper.isAllowed(shooter, e.getEntity().getLocation(), DefaultFlag.POTION_SPLASH)
                || (shooter != null && !helper.isAllowed(shooter, DefaultFlag.POTION_SPLASH))) {

            e.setCancelled(true);
            if (shooter != null) shooter.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD
                    + "Hey!" + ChatColor.GRAY + " Sorry, but you can't use that here.");
        }

        // Call UseItemEvent
        LingeringPotion potion = e.getEntity();
        Bukkit.getServer().getPluginManager().callEvent(new UseItemEvent(e,
                Cause.create(shooter), potion.getLocation().getWorld(), potion.getItem()));
    }

    @EventHandler
    public void disableCloudEffect(AreaEffectCloudApplyEvent e) {

        boolean affectsPlayer = false;
        Iterator<LivingEntity> it = e.getAffectedEntities().iterator();
        while (it.hasNext()) {
            LivingEntity current = it.next();
            if (current instanceof Player) {
                if (!helper.isAllowed(current.getLocation(), DefaultFlag.POTION_SPLASH)) it.remove();
                else affectsPlayer = true;
            }
        }

        if (affectsPlayer) {

            // do entities throw lingering potions yet? check mob_damage flag either way
            boolean sourceIsPlayer = e.getEntity().getSource() instanceof Player;

            Set<PotionEffect> set = new HashSet<>(e.getEntity().getCustomEffects());
            set.add(new PotionEffect(e.getEntity().getBasePotionData().getType().getEffectType(), 1, 1));
            if (Materials.hasDamageEffect(set)) {

                Iterator<LivingEntity> it2 = e.getAffectedEntities().iterator();
                while (it2.hasNext()) {
                    LivingEntity current = it2.next();
                    if (current instanceof Player
                            && ((sourceIsPlayer && !helper.isAllowed((Player) current, DefaultFlag.PVP))
                            || (!sourceIsPlayer && !helper.isAllowed(current.getLocation(), DefaultFlag.MOB_DAMAGE))))
                        it2.remove();
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void disableBoatAndCrystalPlacement(PlayerInteractEvent e) {

        if (e.getItem() != null && !wg.canBuild(e.getPlayer(), e.getClickedBlock())) {

            boolean cancel = false;
            if (config.enableBoatCheck && e.getItem().getType().toString().startsWith("BOAT")
                    && !helper.isAllowed(e.getPlayer(),
                    e.getClickedBlock().getLocation(), DefaultFlag.PLACE_VEHICLE, false)) {

                cancel = true;
                e.getPlayer().sendMessage(ChatColor.RED.toString() + ChatColor.BOLD
                        + "Hey!" + ChatColor.GRAY + " Sorry, but you can't place vehicles here.");

            } else if (e.getItem().getType().equals(Material.END_CRYSTAL)) {

                cancel = true;
                e.getPlayer().sendMessage(ChatColor.RED.toString() + ChatColor.BOLD
                        + "Hey!" + ChatColor.GRAY + " Sorry, but you can't put that here.");
            }
            if (cancel) {
                e.setCancelled(true);
                e.getPlayer().updateInventory();
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void disableBoatLilypadBreak(EntityChangeBlockEvent e) {

        if (e.getBlock().getType().equals(Material.WATER_LILY)
                && config.enableLilypadCheck
                && helper.hasRegion(e.getBlock().getLocation())) {

            e.setCancelled(true);
            e.getEntity().remove();

            // force block update
            e.getBlock().setType(Material.AIR);
            e.getBlock().setType(Material.WATER_LILY);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void disableChorusFruitTp(PlayerTeleportEvent e) {

        if (e.getCause().equals(PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT)
                && config.enableChorusFruitCheck
                && (!helper.isAllowed(e.getPlayer(), DefaultFlag.ENDERPEARL)
                || !helper.isAllowed(e.getPlayer(), e.getTo(), DefaultFlag.ENDERPEARL))) {

            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED.toString() + ChatColor.BOLD
                    + "Hey!" + ChatColor.GRAY + " Sorry, but you can't use that here.");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void disableBlacklistedPotionUse(UseItemEvent e) {

        Material material = e.getItemStack().getType();
        if (material.equals(Material.POTION)
                || material.equals(Material.SPLASH_POTION)
                || material.equals(Material.LINGERING_POTION)) {

            Player p = e.getCause().getFirstPlayer();
            if (p != null && helper.getWorldGuard().hasPermission(p, "worldguard.override.potions")) return;

            PotionData potionType = ((PotionMeta) e.getItemStack().getItemMeta()).getBasePotionData();
            if (helper.isBlacklistedPotion(potionType, e.getWorld())) {

                e.setCancelled(true);
                if (e.getOriginalEvent() instanceof Cancellable)
                    ((Cancellable) e.getOriginalEvent()).setCancelled(true);

                if (p != null) {
                    p.sendMessage(ChatColor.RED + "Sorry, potions with "
                            + potionType.getType().name() + " can't be used.");
                    p.updateInventory();
                }
            }
        }
    }
}
