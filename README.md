# WorldGuardFix
Bukkit plugin that adds some missing protections for Minecraft/WorldGuard 1.9.
Download link can be found on [SpigotMC](https://www.spigotmc.org/resources/worldguard-fix.22712/).

This plugin is dependant on [WorldGuard](https://github.com/sk89q/WorldGuard). It is drop and go and does not require any configuration.

## Current features

- Hooks into WorldGuard
- Fixes potion blacklist, blocked potions can no longer be used
  - Will also check lingering potions and tipped arrows (removes effect of arrows)
- Disables lingering potions in no splash regions
  - Disables areacloud effect in no splash regions
  - Disables negative areacloud effects in no pvp regions
- Disables fishing hook in no PvP regions
- Disable frost walker in no build regions
- Disable chorus fruit tp in no enderpearl regions
- Disable placement of boats in no build regions
- Protect lilypads from being destroyed with boats in regions (will remove boat)
- Disable placement of endercrystals in no build regions