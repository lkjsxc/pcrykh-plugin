package dev.pcrykh.gui;

import dev.pcrykh.domain.AchievementDefinition;
import dev.pcrykh.runtime.AchievementCatalog;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class AchievementMenuService {
    private static final int MENU_SIZE = 54;
    private static final int PAGE_SIZE = 45;

    private final AchievementCatalog catalog;

    public AchievementMenuService(AchievementCatalog catalog) {
        this.catalog = catalog;
    }

    public void openMenu(Player player, int page) {
        List<AchievementDefinition> achievements = catalog.achievements();
        int totalPages = Math.max(1, (int) Math.ceil(achievements.size() / (double) PAGE_SIZE));
        int safePage = Math.min(Math.max(page, 0), totalPages - 1);

        AchievementMenuHolder holder = new AchievementMenuHolder(safePage, totalPages);
        Inventory inventory = Bukkit.createInventory(holder, MENU_SIZE, Component.text("Pcrykh Achievements"));

        if (achievements.isEmpty()) {
            inventory.setItem(22, namedItem(Material.BARRIER, "No achievements loaded."));
            player.openInventory(inventory);
            return;
        }

        int startIndex = safePage * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, achievements.size());
        for (int i = startIndex; i < endIndex; i++) {
            AchievementDefinition achievement = achievements.get(i);
            inventory.setItem(i - startIndex, renderAchievementItem(achievement));
        }

        if (safePage > 0) {
            inventory.setItem(45, namedItem(Material.ARROW, "Previous"));
        }
        if (safePage < totalPages - 1) {
            inventory.setItem(53, namedItem(Material.ARROW, "Next"));
        }
        inventory.setItem(49, namedItem(Material.BARRIER, "Close"));

        player.openInventory(inventory);
    }

    public void openMenu(Player player) {
        openMenu(player, 0);
    }

    public boolean isMenu(Inventory inventory) {
        return inventory != null && inventory.getHolder() instanceof AchievementMenuHolder;
    }

    private ItemStack renderAchievementItem(AchievementDefinition achievement) {
        Material material = Material.matchMaterial(achievement.icon());
        if (material == null || !material.isItem()) {
            material = Material.PAPER;
        }
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(achievement.title()));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(achievement.description()));
        lore.add(Component.text("id: " + achievement.id()));
        lore.add(Component.text("category: " + achievement.categoryId()));
        meta.lore(lore);

        item.setItemMeta(meta);
        return item;
    }

    private ItemStack namedItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(name));
        item.setItemMeta(meta);
        return item;
    }
}
