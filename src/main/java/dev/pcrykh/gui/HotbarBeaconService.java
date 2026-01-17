package dev.pcrykh.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class HotbarBeaconService {
    private static final int HOTBAR_SLOT = 8;
    private static final String BEACON_NAME = "Pcrykh Menu";

    private final Plugin plugin;
    private final AchievementMenuService menuService;
    private final ItemStack beaconItem;

    public HotbarBeaconService(Plugin plugin, AchievementMenuService menuService) {
        this.plugin = plugin;
        this.menuService = menuService;
        this.beaconItem = createBeaconItem();
    }

    public void apply(Player player) {
        if (!player.hasPermission("pcrykh.use")) {
            return;
        }
        player.getInventory().setItem(HOTBAR_SLOT, beaconItem.clone());
    }

    public void openMenu(Player player) {
        if (!player.hasPermission("pcrykh.use")) {
            return;
        }
        menuService.openMenu(player);
    }

    public void startEnforcementTask() {
        plugin.getServer().getScheduler().runTaskTimer(plugin, () ->
                plugin.getServer().getOnlinePlayers().forEach(this::apply), 20L, 200L);
    }

    public boolean isBeaconItem(ItemStack item) {
        if (item == null || item.getType() != Material.BEACON) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.hasDisplayName() && Component.text(BEACON_NAME).equals(meta.displayName());
    }

    private ItemStack createBeaconItem() {
        ItemStack item = new ItemStack(Material.BEACON);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(BEACON_NAME));
        meta.lore(List.of(Component.text("Open the catalog")));
        item.setItemMeta(meta);
        return item;
    }
}
