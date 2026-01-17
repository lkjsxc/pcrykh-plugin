package dev.pcrykh;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.pcrykh.gui.AchievementMenuListener;
import dev.pcrykh.gui.AchievementMenuService;
import dev.pcrykh.gui.HotbarBeaconListener;
import dev.pcrykh.gui.HotbarBeaconService;
import dev.pcrykh.runtime.AchievementCatalog;
import dev.pcrykh.runtime.AchievementSourceResolver;
import dev.pcrykh.runtime.ConfigException;
import dev.pcrykh.runtime.ConfigLoader;
import dev.pcrykh.runtime.PackLoader;
import dev.pcrykh.runtime.RuntimeConfig;
import dev.pcrykh.runtime.TemplateExpander;
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

            AchievementSourceResolver resolver = new AchievementSourceResolver();
            List<Path> packFiles = resolver.resolve(getDataFolder().toPath(), config.achievementSources());

            PackLoader packLoader = new PackLoader(mapper);
            TemplateExpander expander = new TemplateExpander(mapper);
            catalog = new AchievementCatalog(expander.expandAll(packLoader.loadAll(packFiles)));

            menuService = new AchievementMenuService(catalog);
            hotbarService = new HotbarBeaconService(this, menuService);

            Bukkit.getPluginManager().registerEvents(new AchievementMenuListener(menuService), this);
            Bukkit.getPluginManager().registerEvents(new HotbarBeaconListener(hotbarService), this);

            if (getCommand("pcrykh") != null) {
                getCommand("pcrykh").setExecutor(new PcrykhCommand(menuService));
            }

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
        saveResource("achievements/packs/core.json", false);
    }
}
