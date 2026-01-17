package dev.pcrykh.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public final class AchievementMenuListener implements Listener {
    private final AchievementMenuService menuService;

    public AchievementMenuListener(AchievementMenuService menuService) {
        this.menuService = menuService;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof AchievementMenuHolder holder)) {
            return;
        }
        int rawSlot = event.getRawSlot();
        if (rawSlot < 0 || rawSlot >= event.getInventory().getSize()) {
            return;
        }
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (rawSlot == 45) {
            menuService.open(player, holder.page() - 1);
            return;
        }
        if (rawSlot == 53) {
            menuService.open(player, holder.page() + 1);
            return;
        }
        if (rawSlot == 49) {
            player.closeInventory();
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof AchievementMenuHolder) {
            event.setCancelled(true);
        }
    }
}
