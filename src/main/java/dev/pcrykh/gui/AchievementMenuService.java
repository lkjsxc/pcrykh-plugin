package dev.pcrykh.gui;

import dev.pcrykh.domain.AchievementDefinition;
import dev.pcrykh.runtime.AchievementCatalog;
import dev.pcrykh.runtime.AchievementProgressService;
import dev.pcrykh.runtime.RuntimeConfig;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class AchievementMenuService {
    private static final int MAIN_MENU_SIZE = 54;
    private static final int ACHIEVEMENTS_MENU_SIZE = 54;
    private static final int PROFILE_MENU_SIZE = 54;
    private static final int SETTINGS_MENU_SIZE = 54;
    private static final int PAGE_SIZE = 45;

    private final AchievementCatalog catalog;
    private final AchievementProgressService progressService;
    private final RuntimeConfig config;

    public AchievementMenuService(AchievementCatalog catalog, AchievementProgressService progressService, RuntimeConfig config) {
        this.catalog = catalog;
        this.progressService = progressService;
        this.config = config;
    }

    public void openMainMenu(Player player) {
        AchievementMenuHolder holder = new AchievementMenuHolder(MenuType.MAIN, 0, 1);
        Inventory inventory = Bukkit.createInventory(holder, MAIN_MENU_SIZE, Component.text("Pcrykh"));

        inventory.setItem(20, namedItem(Material.PLAYER_HEAD, "Profile", List.of("View your personal record")));
        inventory.setItem(22, namedItem(Material.BOOK, "Achievements", List.of("Browse the catalog")));
        inventory.setItem(24, namedItem(Material.REDSTONE, "Settings", List.of("Configure notifications")));

        fillBottomRow(inventory);

        player.openInventory(inventory);
    }

    public void openAchievementsMenu(Player player, int page) {
        List<AchievementDefinition> achievements = catalog.achievements();
        int totalPages = Math.max(1, (int) Math.ceil(achievements.size() / (double) PAGE_SIZE));
        int safePage = Math.min(Math.max(page, 0), totalPages - 1);

        AchievementMenuHolder holder = new AchievementMenuHolder(MenuType.ACHIEVEMENTS, safePage, totalPages);
        Inventory inventory = Bukkit.createInventory(holder, ACHIEVEMENTS_MENU_SIZE, Component.text("Achievements"));

        if (achievements.isEmpty()) {
            inventory.setItem(22, namedItem(Material.BARRIER, "No achievements loaded."));
            fillBottomRow(inventory);
            player.openInventory(inventory);
            return;
        }

        int startIndex = safePage * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, achievements.size());
        for (int i = startIndex; i < endIndex; i++) {
            AchievementDefinition achievement = achievements.get(i);
            inventory.setItem(i - startIndex, renderAchievementItem(player, achievement));
        }

        inventory.setItem(45, namedItem(Material.BARRIER, "Back"));
        if (safePage > 0) {
            inventory.setItem(47, namedItem(Material.ARROW, "Previous"));
        }
        if (safePage < totalPages - 1) {
            inventory.setItem(53, namedItem(Material.ARROW, "Next"));
        }
        inventory.setItem(49, namedItem(Material.PAPER, "Page", List.of(
                "page: " + (safePage + 1) + "/" + totalPages,
                "total: " + achievements.size()
        )));
        fillBottomRow(inventory);

        player.openInventory(inventory);
    }

    public void openProfileMenu(Player player) {
        AchievementMenuHolder holder = new AchievementMenuHolder(MenuType.PROFILE, 0, 1);
        Inventory inventory = Bukkit.createInventory(holder, PROFILE_MENU_SIZE, Component.text("Profile"));

        inventory.setItem(20, namedItem(Material.PLAYER_HEAD, "You", List.of(
                "name: " + player.getName(),
                "uuid: " + player.getUniqueId()
        )));

        int completed = progressService.getCompletedCount(player);
        int total = catalog.achievements().size();
        int apTotal = progressService.getTotalAp(player);
        int percent = total == 0 ? 0 : (int) Math.floor((completed * 100.0) / total);

        inventory.setItem(24, namedItem(Material.PAPER, "Progress", List.of(
                "completed: " + completed + "/" + total,
                "ap: " + apTotal,
                "completion: " + percent + "%"
        )));

        inventory.setItem(45, namedItem(Material.BARRIER, "Back"));
        fillBottomRow(inventory);
        player.openInventory(inventory);
    }

    public void openSettingsMenu(Player player) {
        AchievementMenuHolder holder = new AchievementMenuHolder(MenuType.SETTINGS, 0, 1);
        Inventory inventory = Bukkit.createInventory(holder, SETTINGS_MENU_SIZE, Component.text("Settings"));

        inventory.setItem(20, namedItem(Material.NAME_TAG, "Achievement Broadcasts", List.of(
                "global chat announcements",
                "state: " + onOff(config.chat().announceAchievements())
        )));
        inventory.setItem(22, namedItem(Material.BOOK, "Random Facts", List.of(
                "periodic global facts",
                "state: " + onOff(config.chat().factsEnabled())
        )));
        inventory.setItem(24, namedItem(Material.GLOWSTONE_DUST, "Progress Indicators", List.of(
            "action bar progress updates",
                "state: " + onOff(config.actionBar().progressEnabled())
        )));
        inventory.setItem(45, namedItem(Material.BARRIER, "Back"));

        fillBottomRow(inventory);

        player.openInventory(inventory);
    }

    public void openMenu(Player player) {
        openMainMenu(player);
    }

    public boolean isMenu(Inventory inventory) {
        return inventory != null && inventory.getHolder() instanceof AchievementMenuHolder;
    }

    private ItemStack renderAchievementItem(Player player, AchievementDefinition achievement) {
        Material material = Material.matchMaterial(achievement.icon());
        if (material == null || !material.isItem()) {
            material = Material.PAPER;
        }
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(achievement.title()));

        int current = progressService.getProgress(player, achievement);
        int target = progressService.getTarget(achievement);
        int ap = progressService.getAp(achievement);

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(achievement.description()));
        lore.add(Component.text("category: " + achievement.categoryId()));
        lore.add(Component.text("progress: " + current + "/" + target));
        lore.add(Component.text("ap: " + ap));
        meta.lore(lore);

        if (progressService.isCompleted(player, achievement)) {
            meta.addEnchant(Enchantment.LUCK, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        item.setItemMeta(meta);
        return item;
    }

    private void fillBottomRow(Inventory inventory) {
        ItemStack filler = fillerItem();
        for (int slot = 45; slot <= 53; slot++) {
            if (inventory.getItem(slot) == null) {
                inventory.setItem(slot, filler);
            }
        }
    }

    private ItemStack fillerItem() {
        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(" "));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack namedItem(Material material, String name) {
        return namedItem(material, name, List.of());
    }

    private ItemStack namedItem(Material material, String name, List<String> loreLines) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(name));
        if (!loreLines.isEmpty()) {
            List<Component> lore = new ArrayList<>();
            for (String line : loreLines) {
                lore.add(Component.text(line));
            }
            meta.lore(lore);
        }
        item.setItemMeta(meta);
        return item;
    }

    private String onOff(boolean value) {
        return value ? "on" : "off";
    }
}
