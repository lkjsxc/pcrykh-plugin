package dev.pcrykh.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AchievementMenuListener implements Listener {
    private final AchievementMenuService menuService;

    public AchievementMenuListener(AchievementMenuService menuService) {
        this.menuService = menuService;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getView().getTopInventory();
        if (!menuService.isMenu(inventory)) {
            return;
        }

        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) {
            return;
        }

        AchievementMenuHolder holder = (AchievementMenuHolder) inventory.getHolder();
        if (holder == null) {
            return;
        }

        int slot = event.getSlot();
        if (slot == 45 && clicked.getType() == Material.ARROW && holder.page() > 0) {
            menuService.openMenu(player, holder.page() - 1);
        } else if (slot == 53 && clicked.getType() == Material.ARROW && holder.page() < holder.totalPages() - 1) {
            menuService.openMenu(player, holder.page() + 1);
        } else if (slot == 49 && clicked.getType() == Material.BARRIER) {
            player.closeInventory();
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (menuService.isMenu(event.getView().getTopInventory())) {
            event.setCancelled(true);
        }
    }

}
