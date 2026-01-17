package dev.pcrykh;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.pcrykh.gui.AchievementMenuListener;
import dev.pcrykh.gui.AchievementMenuService;
import dev.pcrykh.gui.HotbarBeaconListener;
import dev.pcrykh.gui.HotbarBeaconService;
import dev.pcrykh.runtime.AchievementCatalog;
import dev.pcrykh.runtime.AchievementDefinitionLoader;
import dev.pcrykh.runtime.AchievementProgressListener;
import dev.pcrykh.runtime.AchievementProgressService;
import dev.pcrykh.runtime.AchievementSourceResolver;
import dev.pcrykh.runtime.CategoryLoader;
import dev.pcrykh.runtime.CategoryCatalog;
import dev.pcrykh.runtime.CategorySourceResolver;
import dev.pcrykh.runtime.ConfigException;
import dev.pcrykh.runtime.ConfigLoader;
import dev.pcrykh.runtime.ConfigSaver;
import dev.pcrykh.runtime.FactsBroadcaster;
import dev.pcrykh.runtime.RuntimeConfig;
import dev.pcrykh.runtime.command.PcrykhCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;
import java.util.List;

public class PcrykhPlugin extends JavaPlugin {
    private AchievementCatalog catalog;
    private AchievementMenuService menuService;
    private HotbarBeaconService hotbarService;

    @Override
    public void onEnable() {
        try {
            ensureDefaultResources();

            ObjectMapper mapper = new ObjectMapper();
            ConfigLoader configLoader = new ConfigLoader(mapper);
            RuntimeConfig config = configLoader.load(getDataFolder().toPath());
            ConfigSaver configSaver = new ConfigSaver(mapper);

            CategorySourceResolver categoryResolver = new CategorySourceResolver();
            List<Path> categoryFiles = categoryResolver.resolve(getDataFolder().toPath(), config.categorySources());
            CategoryLoader categoryLoader = new CategoryLoader(mapper);
            CategoryCatalog categoryCatalog = new CategoryCatalog(categoryLoader.loadAll(categoryFiles));

            AchievementSourceResolver resolver = new AchievementSourceResolver();
            List<Path> achievementFiles = resolver.resolve(getDataFolder().toPath(), config.achievementSources());
            AchievementDefinitionLoader achievementLoader = new AchievementDefinitionLoader(mapper);
            catalog = new AchievementCatalog(achievementLoader.loadAll(achievementFiles), categoryCatalog);

            AchievementProgressService progressService = new AchievementProgressService(catalog, config);

            menuService = new AchievementMenuService(catalog, progressService, config);
            hotbarService = new HotbarBeaconService(this, menuService);

            Bukkit.getPluginManager().registerEvents(new AchievementMenuListener(menuService, config, configSaver, getDataFolder().toPath()), this);
            Bukkit.getPluginManager().registerEvents(new HotbarBeaconListener(hotbarService), this);
            Bukkit.getPluginManager().registerEvents(new AchievementProgressListener(progressService), this);

            if (getCommand("pcrykh") != null) {
                getCommand("pcrykh").setExecutor(new PcrykhCommand(menuService));
            }

            new FactsBroadcaster(this, config).start();
            hotbarService.startEnforcementTask();
        } catch (ConfigException ex) {
            getLogger().severe("Config error: " + ex.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
        } catch (Exception ex) {
            getLogger().severe("Startup failure: " + ex.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    private void ensureDefaultResources() {
        if (!getDataFolder().exists() && !getDataFolder().mkdirs()) {
            throw new ConfigException("Failed to create data folder");
        }
        saveResource("config.json", false);
        saveResource("achievements/packs/mining.json", false);
        saveResource("achievements/packs/harvest.json", false);
        saveResource("achievements/packs/crafting.json", false);
        saveResource("achievements/packs/hunting.json", false);
        saveResource("achievements/packs/fishing.json", false);
        saveResource("facts/packs/trivia-001.json", false);
        saveResource("facts/packs/trivia-002.json", false);
    }
}
