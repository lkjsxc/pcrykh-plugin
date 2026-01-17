package dev.pcrykh.pcrykh.core;

import dev.pcrykh.pcrykh.achievement.AchievementCatalog;
import dev.pcrykh.pcrykh.achievement.AchievementDefinition;
import dev.pcrykh.pcrykh.config.ConfigLoader;
import dev.pcrykh.pcrykh.config.ConfigValidator;
import dev.pcrykh.pcrykh.config.RuntimeConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class PcrykhPlugin extends JavaPlugin implements CommandExecutor {
    private AchievementCatalog achievementCatalog;

    @Override
    public void onEnable() {
        if (!ensureDefaultResources()) {
            getLogger().severe("Failed to prepare config resources.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        RuntimeConfig config;
        List<AchievementDefinition> achievements;
        try {
            var loader = new ConfigLoader();
            config = loader.loadConfig(getDataFolder().toPath());
            var validationErrors = new ConfigValidator().validate(config);
            if (!validationErrors.isEmpty()) {
                validationErrors.forEach(error -> getLogger().severe("Config error: " + error));
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
            achievements = loader.loadAchievements(config, getDataFolder().toPath());
        } catch (Exception ex) {
            getLogger().severe("Failed to load config: " + ex.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        achievementCatalog = new AchievementCatalog(achievements);

        var command = getCommand("pcrykh");
        if (command != null) {
            command.setExecutor(this);
        }
        getLogger().info("pcrykh enabled. Achievements loaded: " + achievementCatalog.size());
        if (achievementCatalog.size() < 450 || achievementCatalog.size() > 550) {
            getLogger().warning("Achievement count is outside the target range (450-550). Current: " + achievementCatalog.size());
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("pcrykh disabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        int count = achievementCatalog == null ? 0 : achievementCatalog.size();
        if (sender instanceof Player) {
            sender.sendMessage("pcrykh active. Achievements loaded: " + count + ".");
        } else {
            sender.sendMessage("pcrykh active. Achievements loaded: " + count + ".");
        }
        return true;
    }

    private boolean ensureDefaultResources() {
        try {
            if (!getDataFolder().exists() && !getDataFolder().mkdirs()) {
                return false;
            }
            ensureResource("config.json");
            ensureResource("achievements/packs/core.json");
            return true;
        } catch (Exception ex) {
            getLogger().severe("Resource bootstrap failed: " + ex.getMessage());
            return false;
        }
    }

    private void ensureResource(String resourcePath) throws Exception {
        Path target = getDataFolder().toPath().resolve(resourcePath);
        if (Files.exists(target)) {
            return;
        }
        saveResource(resourcePath, false);
    }
}
