package dev.pcrykh.runtime;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

public class AchievementProgressListener implements Listener {
    private final AchievementProgressService progressService;

    public AchievementProgressListener(AchievementProgressService progressService) {
        this.progressService = progressService;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        progressService.onBlockBreak(event.getPlayer(), event.getBlock().getType());
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        ItemStack result = event.getRecipe().getResult();
        if (result == null || result.getType() == Material.AIR) {
            return;
        }
        progressService.onItemCraft(player, result.getType());
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Entity killer = event.getEntity().getKiller();
        if (!(killer instanceof Player player)) {
            return;
        }
        EntityType type = event.getEntityType();
        progressService.onEntityKill(player, type);
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }
        Entity caught = event.getCaught();
        if (!(caught instanceof org.bukkit.entity.Item itemEntity)) {
            return;
        }
        ItemStack item = itemEntity.getItemStack();
        if (item == null || item.getType() == Material.AIR) {
            return;
        }
        progressService.onFishCatch(player, item.getType());
    }
}
