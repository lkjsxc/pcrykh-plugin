package dev.pcrykh.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class AchievementMenuHolder implements InventoryHolder {
    private final AchievementMenuService service;
    private final int page;
    private Inventory inventory;

    public AchievementMenuHolder(AchievementMenuService service, int page) {
        this.service = service;
        this.page = page;
    }

    public AchievementMenuService service() {
        return service;
    }

    public int page() {
        return page;
    }

    public void attachInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
