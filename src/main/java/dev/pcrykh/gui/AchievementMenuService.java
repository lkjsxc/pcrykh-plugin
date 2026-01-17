package dev.pcrykh.gui;

import dev.pcrykh.domain.AchievementDefinition;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class AchievementMenuService {
    private static final int INVENTORY_SIZE = 54;
    private static final int PAGE_SIZE = 45;
    private static final String TITLE = "Pcrykh Achievements";

    private final List<AchievementDefinition> achievements;

    public AchievementMenuService(List<AchievementDefinition> achievements) {
        List<AchievementDefinition> sorted = new ArrayList<>(achievements);
        sorted.sort(Comparator.comparing(AchievementDefinition::id));
        this.achievements = List.copyOf(sorted);
    }

    public void open(Player player, int requestedPage) {
        int totalPages = Math.max(1, (int) Math.ceil(achievements.size() / (double) PAGE_SIZE));
        int page = Math.max(0, Math.min(requestedPage, totalPages - 1));
        AchievementMenuHolder holder = new AchievementMenuHolder(this, page);
        Inventory inventory = Bukkit.createInventory(holder, INVENTORY_SIZE, TITLE);
        holder.attachInventory(inventory);

        if (achievements.isEmpty()) {
            inventory.setItem(22, namedItem(Material.BARRIER, "No achievements loaded."));
            player.openInventory(inventory);
            return;
        }

        int start = page * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, achievements.size());
        for (int index = start; index < end; index++) {
            AchievementDefinition achievement = achievements.get(index);
            ItemStack item = renderAchievement(achievement);
            inventory.setItem(index - start, item);
        }

        if (page > 0) {
            inventory.setItem(45, namedItem(Material.ARROW, "Previous"));
        }
        if (page < totalPages - 1) {
            inventory.setItem(53, namedItem(Material.ARROW, "Next"));
        }
        inventory.setItem(49, namedItem(Material.BARRIER, "Close"));

        player.openInventory(inventory);
    }

    private ItemStack renderAchievement(AchievementDefinition achievement) {
        Material material = Material.matchMaterial(achievement.icon());
        if (material == null || material.isAir()) {
            material = Material.PAPER;
        }
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(achievement.title());
            List<String> lore = List.of(
                achievement.description(),
                "id: " + achievement.id(),
                "category: " + achievement.categoryId()
            );
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack namedItem(Material material, String name) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }
}
