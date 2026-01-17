package dev.pcrykh.pcrykh.gui;

import dev.pcrykh.pcrykh.achievement.AchievementService;
import dev.pcrykh.pcrykh.model.AchievementDefinition;
import dev.pcrykh.pcrykh.storage.DataStore.PlayerState;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class GuiService implements Listener {
    private static final String TITLE_MAIN = "Pcrykh: Main";
    private static final String TITLE_ACHIEVEMENTS = "Pcrykh: Achievements";
    private static final String TITLE_ACHIEVEMENT_PREFIX = "Pcrykh: Achievement: ";
    private static final String TITLE_ADMIN = "Pcrykh: Admin";

    private final JavaPlugin plugin;
    private final AchievementService achievementService;
    private final NamespacedKey menuKey;
    private final NamespacedKey achievementKey;
    private final NamespacedKey hotbarKey;
    private final Map<UUID, Integer> achievementsPage = new HashMap<>();
    private static final int ACHIEVEMENTS_PAGE_SIZE = 28;

    public GuiService(JavaPlugin plugin, AchievementService achievementService) {
        this.plugin = plugin;
        this.achievementService = achievementService;
        this.menuKey = new NamespacedKey(plugin, "menu");
        this.achievementKey = new NamespacedKey(plugin, "achievement");
        this.hotbarKey = new NamespacedKey(plugin, "hotbar");
    }

    public void openMainMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, TITLE_MAIN);
        PlayerState state = achievementService.getOrLoad(player);

        ItemStack profile = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta profileMeta = profile.getItemMeta();
        profileMeta.displayName(Component.text("Profile"));
        List<Component> profileLore = new ArrayList<>();
        profileLore.add(Component.text("Level: " + state.playerLevel));
        profileLore.add(Component.text("AP: " + state.apTotal));
        List<String> topAchievements = topAchievements(state, 3);
        if (!topAchievements.isEmpty()) {
            profileLore.add(Component.text("Top Achievements:"));
            for (String entry : topAchievements) {
                profileLore.add(Component.text("- " + entry));
            }
        }
        profileMeta.lore(profileLore);
        profile.setItemMeta(profileMeta);
        inv.setItem(4, profile);

        ItemStack achievements = new ItemStack(Material.BOOK);
        ItemMeta achievementsMeta = achievements.getItemMeta();
        achievementsMeta.displayName(Component.text("Achievements"));
        achievementsMeta.getPersistentDataContainer().set(menuKey, PersistentDataType.STRING, "achievements");
        achievements.setItemMeta(achievementsMeta);
        inv.setItem(20, achievements);

        ItemStack settings = new ItemStack(Material.REPEATER);
        ItemMeta settingsMeta = settings.getItemMeta();
        settingsMeta.displayName(Component.text("Settings (Coming Soon)"));
        settings.setItemMeta(settingsMeta);
        inv.setItem(24, settings);

        addFooter(inv, "back_main");
        player.openInventory(inv);
    }

    public void openAchievementsMenu(Player player) {
        openAchievementsMenu(player, achievementsPage.getOrDefault(player.getUniqueId(), 0));
    }

    public void openAchievementsMenu(Player player, int requestedPage) {
        Inventory inv = Bukkit.createInventory(null, 54, TITLE_ACHIEVEMENTS);
        List<AchievementDefinition> defs = new ArrayList<>(achievementService.getAchievements());

        int pageCount = Math.max(1, (int) Math.ceil(defs.size() / (double) ACHIEVEMENTS_PAGE_SIZE));
        int page = Math.max(0, Math.min(requestedPage, pageCount - 1));
        achievementsPage.put(player.getUniqueId(), page);

        PlayerState state = achievementService.getOrLoad(player);
        int start = page * ACHIEVEMENTS_PAGE_SIZE;
        int end = Math.min(defs.size(), start + ACHIEVEMENTS_PAGE_SIZE);

        int slot = 10;
        for (int i = start; i < end; i++) {
            AchievementDefinition def = defs.get(i);
            Material iconMaterial = Material.matchMaterial(def.icon);
            ItemStack item = new ItemStack(iconMaterial == null ? Material.BOOK : iconMaterial);
            ItemMeta meta = item.getItemMeta();
            meta.displayName(Component.text(def.name));
            meta.getPersistentDataContainer().set(achievementKey, PersistentDataType.STRING, def.id);
            var progress = state.achievementProgress.get(def.id);
                long required = achievementService.requiredAmount(def.criteria);
                long amount = progress.progressAmount;
                String statusLine = progress.completed ? "Status: Completed" : "Status: In Progress";
                String progressLine = progress.completed ? "Progress: " + required + "/" + required + " (100%)"
                    : "Progress: " + amount + "/" + required + " (" + percent(amount, required) + ")";
                meta.lore(List.of(
                    Component.text(def.title),
                    Component.text(statusLine),
                    Component.text(progressLine)
                ));
            item.setItemMeta(meta);
                if (progress.completed) {
                applyCompletedGlow(item);
                }
            inv.setItem(slot, item);
            slot = nextGridSlot(slot);
            if (slot < 0) {
                break;
            }
        }

        addFooter(inv, "back_main");
        addPagination(inv, page, pageCount);
        player.openInventory(inv);
    }

    public void openAchievementDetail(Player player, AchievementDefinition def) {
        Inventory inv = Bukkit.createInventory(null, 54, TITLE_ACHIEVEMENT_PREFIX + def.name);
        PlayerState state = achievementService.getOrLoad(player);
        var progress = state.achievementProgress.get(def.id);

        Material iconMaterial = Material.matchMaterial(def.icon);
        ItemStack header = new ItemStack(iconMaterial == null ? Material.BOOK : iconMaterial);
        ItemMeta headerMeta = header.getItemMeta();
        headerMeta.displayName(Component.text(def.name));
        List<Component> headerLore = new ArrayList<>();
        long required = achievementService.requiredAmount(def.criteria);
        long amount = progress.progressAmount;
        headerLore.add(Component.text(def.title));
        if (progress.completed) {
            headerLore.add(Component.text("Status: Completed"));
            headerLore.add(Component.text("Progress: " + required + "/" + required + " (100%)"));
        } else {
            headerLore.add(Component.text("Status: In Progress"));
            headerLore.add(Component.text("Progress: " + amount + "/" + required + " (" + percent(amount, required) + ")"));
        }
        headerMeta.lore(headerLore);
        header.setItemMeta(headerMeta);
        if (progress.completed) {
            applyCompletedGlow(header);
        }
        inv.setItem(4, header);

        ItemStack objective = new ItemStack(Material.PAPER);
        ItemMeta objectiveMeta = objective.getItemMeta();
        objectiveMeta.displayName(Component.text(def.title));
        List<Component> objectiveLore = new ArrayList<>();
        objectiveLore.add(Component.text(def.description));
        objectiveLore.add(Component.text("Progress: " + amount + "/" + required + " (" + percent(amount, required) + ")"));
        objectiveLore.add(Component.text("Reward: " + def.rewards.ap + " AP"));
        if (progress.completed) {
            objectiveLore.add(Component.text("Status: Completed"));
        }
        objectiveMeta.lore(objectiveLore);
        objective.setItemMeta(objectiveMeta);
        if (progress.completed) {
            applyCompletedGlow(objective);
        }
        inv.setItem(22, objective);

        addFooter(inv, "back_achievements");
        player.openInventory(inv);
    }

    public void openAdminMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, TITLE_ADMIN);
        inv.setItem(20, labeled(Material.REDSTONE, "Reload config"));
        inv.setItem(22, labeled(Material.PLAYER_HEAD, "Player debug"));
        inv.setItem(24, labeled(Material.EMERALD, "Grant AP"));
        addFooter(inv, "back_main");
        player.openInventory(inv);
    }

    public void refreshOpenMenus(Player player) {
        if (player.getOpenInventory() == null || player.getOpenInventory().getTopInventory() == null) {
            return;
        }
        String title = player.getOpenInventory().getTitle();
        if (TITLE_MAIN.equals(title)) {
            openMainMenu(player);
        } else if (TITLE_ACHIEVEMENTS.equals(title)) {
            openAchievementsMenu(player);
        } else if (title.startsWith(TITLE_ACHIEVEMENT_PREFIX)) {
            String name = title.substring(TITLE_ACHIEVEMENT_PREFIX.length());
            for (AchievementDefinition def : achievementService.getAchievements()) {
                if (def.name.equals(name)) {
                    openAchievementDetail(player, def);
                    break;
                }
            }
        }
    }

    private void addFooter(Inventory inv, String backAction) {
        ItemStack back = labeled(Material.ARROW, "Back");
        ItemMeta meta = back.getItemMeta();
        meta.getPersistentDataContainer().set(menuKey, PersistentDataType.STRING, backAction);
        back.setItemMeta(meta);

        ItemStack close = labeled(Material.BARRIER, "Close");
        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.getPersistentDataContainer().set(menuKey, PersistentDataType.STRING, "close");
        close.setItemMeta(closeMeta);

        inv.setItem(45, back);
        inv.setItem(49, close);
    }

    private void addPagination(Inventory inv, int page, int pageCount) {
        if (pageCount <= 1) {
            return;
        }
        ItemStack prev = labeled(Material.ARROW, "Previous Page");
        ItemMeta prevMeta = prev.getItemMeta();
        prevMeta.getPersistentDataContainer().set(menuKey, PersistentDataType.STRING, "page_prev");
        prev.setItemMeta(prevMeta);
        inv.setItem(47, prev);

        ItemStack next = labeled(Material.ARROW, "Next Page");
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.getPersistentDataContainer().set(menuKey, PersistentDataType.STRING, "page_next");
        next.setItemMeta(nextMeta);
        inv.setItem(53, next);

        ItemStack pageItem = labeled(Material.PAPER, "Page " + (page + 1) + "/" + pageCount);
        inv.setItem(51, pageItem);
    }

    private ItemStack labeled(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(name));
        item.setItemMeta(meta);
        return item;
    }

    private void applyCompletedGlow(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.LUCK, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
    }

    private int nextGridSlot(int slot) {
        int row = slot / 9;
        int col = slot % 9;
        col++;
        if (col >= 8) {
            row++;
            col = 1;
        }
        if (row > 4) {
            return -1;
        }
        return row * 9 + col;
    }

    private String percent(long amount, long required) {
        if (required <= 0) {
            return "0%";
        }
        int pct = (int) Math.min(100, Math.round((amount * 100.0) / required));
        return pct + "%";
    }

    private List<String> topAchievements(PlayerState state, int limit) {
        List<String> completed = new ArrayList<>();
        for (Map.Entry<String, dev.pcrykh.pcrykh.storage.DataStore.AchievementProgress> entry : state.achievementProgress.entrySet()) {
            if (entry.getValue().completed) {
                AchievementDefinition def = achievementService.getAchievement(entry.getKey());
                if (def != null) {
                    completed.add(def.name);
                }
            }
        }
        completed.sort(String::compareToIgnoreCase);
        return completed.subList(0, Math.min(limit, completed.size()));
    }

    private ItemStack createHotbarItem() {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("Pcrykh Menu"));
        meta.getPersistentDataContainer().set(hotbarKey, PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
        return item;
    }

    private boolean isHotbarItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer().has(hotbarKey, PersistentDataType.BYTE);
    }

    private void enforceHotbarItem(Player player) {
        ItemStack hotbarItem = createHotbarItem();
        ItemStack current = player.getInventory().getItem(8);
        if (current == null || current.getType() == Material.AIR || isHotbarItem(current)) {
            player.getInventory().setItem(8, hotbarItem);
            return;
        }
        int empty = player.getInventory().firstEmpty();
        if (empty >= 0) {
            player.getInventory().setItem(empty, current);
        } else {
            player.getWorld().dropItemNaturally(player.getLocation(), current);
        }
        player.getInventory().setItem(8, hotbarItem);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        enforceHotbarItem(event.getPlayer());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> enforceHotbarItem(event.getPlayer()), 1L);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (isHotbarItem(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 1f);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (isHotbarItem(event.getItem())) {
            openMainMenu(event.getPlayer());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        String title = event.getView().getTitle();
        if (!title.startsWith("Pcrykh: ")) {
            if (event.getCurrentItem() != null && isHotbarItem(event.getCurrentItem())) {
                openMainMenu(player);
                event.setCancelled(true);
            }
            return;
        }
        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) {
            return;
        }
        ItemMeta meta = clicked.getItemMeta();
        if (meta.getPersistentDataContainer().has(menuKey, PersistentDataType.STRING)) {
            String action = meta.getPersistentDataContainer().get(menuKey, PersistentDataType.STRING);
            if ("achievements".equals(action)) {
                openAchievementsMenu(player);
            } else if ("back_main".equals(action)) {
                openMainMenu(player);
            } else if ("back_achievements".equals(action)) {
                openAchievementsMenu(player);
            } else if ("close".equals(action)) {
                player.closeInventory();
            } else if ("page_next".equals(action)) {
                int page = achievementsPage.getOrDefault(player.getUniqueId(), 0);
                openAchievementsMenu(player, page + 1);
            } else if ("page_prev".equals(action)) {
                int page = achievementsPage.getOrDefault(player.getUniqueId(), 0);
                openAchievementsMenu(player, page - 1);
            }
            return;
        }
        if (meta.getPersistentDataContainer().has(achievementKey, PersistentDataType.STRING)) {
            String achievementId = meta.getPersistentDataContainer().get(achievementKey, PersistentDataType.STRING);
            AchievementDefinition def = achievementService.getAchievement(achievementId);
            if (def != null) {
                openAchievementDetail(player, def);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player player) {
            enforceHotbarItem(player);
        }
    }
}
