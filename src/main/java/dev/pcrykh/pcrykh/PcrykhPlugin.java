package dev.pcrykh.pcrykh;

import dev.pcrykh.pcrykh.achievement.AchievementService;
import dev.pcrykh.pcrykh.command.PcrykhCommand;
import dev.pcrykh.pcrykh.config.ConfigLoader;
import dev.pcrykh.pcrykh.config.PluginConfig;
import dev.pcrykh.pcrykh.gui.GuiService;
import dev.pcrykh.pcrykh.storage.DataStore;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Files;

public class PcrykhPlugin extends JavaPlugin implements Listener {
    private DataStore dataStore;
    private PluginConfig config;
    private AchievementService achievementService;
    private GuiService guiService;

    @Override
    public void onEnable() {
        try {
            loadConfigJson();
        } catch (Exception e) {
            getLogger().severe("Config load failed: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.dataStore = new DataStore(new File(getDataFolder(), "pcrykh.db"));
        this.achievementService = new AchievementService(this, dataStore, config, null);
        this.guiService = new GuiService(this, achievementService);
        this.achievementService.setGuiService(guiService);

        getServer().getPluginManager().registerEvents(guiService, this);
        getServer().getPluginManager().registerEvents(this, this);

        PcrykhCommand command = new PcrykhCommand(this, achievementService, guiService, dataStore, getDataFolder());
        getCommand("pcrykh").setExecutor(command);
        getCommand("pcrykh").setTabCompleter(command);

        scheduleAutosave();
        getLogger().info("pcrykh enabled.");
    }

    @Override
    public void onDisable() {
        if (achievementService != null) {
            achievementService.flushAll();
        }
        if (dataStore != null) {
            dataStore.close();
        }
        getLogger().info("pcrykh disabled.");
    }

    private void loadConfigJson() throws Exception {
        File configFile = new File(getDataFolder(), "config.json");
        if (!configFile.exists()) {
            getDataFolder().mkdirs();
            File defaultConfig = new File(getDataFolder(), "config.json");
            try (var in = getResource("config.json")) {
                if (in == null) {
                    throw new IllegalStateException("Default config.json missing from jar");
                }
                Files.copy(in, defaultConfig.toPath());
            }
        }

        ConfigLoader loader = new ConfigLoader();
        PluginConfig loaded = loader.load(configFile);
        loader.validate(loaded);
        this.config = loaded;
    }

    public PluginConfig getConfigModel() {
        return config;
    }

    public void reloadRuntime() throws Exception {
        loadConfigJson();
        achievementService.flushAll();
        achievementService.clearCache();
        achievementService.updateConfig(config);
    }

    private void scheduleAutosave() {
        if (!config.runtime.autosave.enabled) {
            return;
        }
        long intervalTicks = config.runtime.autosave.intervalSeconds * 20L;
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            if (achievementService != null) {
                achievementService.flushAll();
            }
        }, intervalTicks, intervalTicks);
    }

    @org.bukkit.event.EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        achievementService.onBlockBreak(event);
    }

    @org.bukkit.event.EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        achievementService.onEntityDeath(event);
    }

    @org.bukkit.event.EventHandler
    public void onCraft(CraftItemEvent event) {
        achievementService.onCraft(event);
    }

    @org.bukkit.event.EventHandler
    public void onFish(PlayerFishEvent event) {
        achievementService.onFish(event);
    }

    @org.bukkit.event.EventHandler
    public void onMove(PlayerMoveEvent event) {
        achievementService.onTravel(event);
    }
}
