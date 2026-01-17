package dev.pcrykh.gui;

import dev.pcrykh.runtime.ConfigSaver;
import dev.pcrykh.runtime.RuntimeConfig;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.nio.file.Path;

public class AchievementMenuListener implements Listener {
    private final AchievementMenuService menuService;
    private final RuntimeConfig config;
    private final ConfigSaver configSaver;
    private final Path dataFolder;

    public AchievementMenuListener(AchievementMenuService menuService, RuntimeConfig config, ConfigSaver configSaver, Path dataFolder) {
        this.menuService = menuService;
        this.config = config;
        this.configSaver = configSaver;
        this.dataFolder = dataFolder;
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
        switch (holder.type()) {
            case MAIN -> handleMainMenu(player, slot);
            case ACHIEVEMENTS -> handleAchievementsMenu(player, holder, slot, clicked);
            case PROFILE -> handleProfileMenu(player, slot);
            case SETTINGS -> handleSettingsMenu(player, slot, clicked);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (menuService.isMenu(event.getView().getTopInventory())) {
            event.setCancelled(true);
        }
    }

    private void handleMainMenu(Player player, int slot) {
        if (slot == 11) {
            menuService.openProfileMenu(player);
        } else if (slot == 13) {
            menuService.openAchievementsMenu(player, 0);
        } else if (slot == 15) {
            menuService.openSettingsMenu(player);
        }
    }

    private void handleAchievementsMenu(Player player, AchievementMenuHolder holder, int slot, ItemStack clicked) {
        if (slot == 45 && clicked.getType() == Material.ARROW && holder.page() > 0) {
            menuService.openAchievementsMenu(player, holder.page() - 1);
        } else if (slot == 53 && clicked.getType() == Material.ARROW && holder.page() < holder.totalPages() - 1) {
            menuService.openAchievementsMenu(player, holder.page() + 1);
        } else if (slot == 49 && clicked.getType() == Material.BARRIER) {
            menuService.openMainMenu(player);
        }
    }

    private void handleProfileMenu(Player player, int slot) {
        if (slot == 15) {
            menuService.openMainMenu(player);
        }
    }

    private void handleSettingsMenu(Player player, int slot, ItemStack clicked) {
        if (slot == 11 && clicked.getType() == Material.NAME_TAG) {
            config.chat().setAnnounceAchievements(!config.chat().announceAchievements());
            configSaver.save(dataFolder, config);
            menuService.openSettingsMenu(player);
        } else if (slot == 13 && clicked.getType() == Material.BOOK) {
            config.chat().setFactsEnabled(!config.chat().factsEnabled());
            configSaver.save(dataFolder, config);
            menuService.openSettingsMenu(player);
        } else if (slot == 15 && clicked.getType() == Material.GLOWSTONE_DUST) {
            config.actionBar().setProgressEnabled(!config.actionBar().progressEnabled());
            configSaver.save(dataFolder, config);
            menuService.openSettingsMenu(player);
        } else if (slot == 26 && clicked.getType() == Material.BARRIER) {
            menuService.openMainMenu(player);
        }
    }

}
