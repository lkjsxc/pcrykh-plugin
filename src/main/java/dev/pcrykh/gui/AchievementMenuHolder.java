package dev.pcrykh.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class AchievementMenuHolder implements InventoryHolder {
    private final int page;
    private final int totalPages;

    public AchievementMenuHolder(int page, int totalPages) {
        this.page = page;
        this.totalPages = totalPages;
    }

    public int page() {
        return page;
    }

    public int totalPages() {
        return totalPages;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
