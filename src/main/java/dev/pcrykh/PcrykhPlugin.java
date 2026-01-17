package dev.pcrykh;

import dev.pcrykh.runtime.AchievementCatalog;
import dev.pcrykh.runtime.ConfigLoader;
import dev.pcrykh.runtime.ConfigLoader.LoadResult;
import dev.pcrykh.runtime.PcrykhCommand;
import dev.pcrykh.runtime.RuntimeConfig;
import java.io.File;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class PcrykhPlugin extends JavaPlugin {
    private AchievementCatalog catalog;
    private RuntimeConfig config;

    @Override
    public void onEnable() {
        ensureDefaultResources();
        try {
            ConfigLoader loader = new ConfigLoader();
            LoadResult result = loader.load(getDataFolder());
            this.catalog = result.catalog();
            this.config = result.config();
        } catch (Exception ex) {
            getLogger().severe("Failed to load configuration: " + ex.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        PluginCommand command = getCommand("pcrykh");
        if (command == null) {
            getLogger().severe("Command /pcrykh is not registered.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        command.setExecutor(new PcrykhCommand(catalog));
    }

    @Override
    public void onDisable() {
    }

    private void ensureDefaultResources() {
        File dataFolder = getDataFolder();
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            getLogger().severe("Failed to create data folder.");
            return;
        }

        List<String> resources = List.of(
            "config.json",
            "achievements/packs/core.json"
        );

        for (String path : resources) {
            File target = new File(dataFolder, path);
            if (!target.exists()) {
                saveResource(path, false);
            }
        }
    }
}
