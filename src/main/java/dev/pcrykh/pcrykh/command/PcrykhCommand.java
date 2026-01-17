package dev.pcrykh.pcrykh.command;

import dev.pcrykh.pcrykh.achievement.AchievementService;
import dev.pcrykh.pcrykh.PcrykhPlugin;
import dev.pcrykh.pcrykh.gui.GuiService;
import dev.pcrykh.pcrykh.model.AchievementDefinition;
import dev.pcrykh.pcrykh.storage.DataStore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.LinkedHashMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PcrykhCommand implements CommandExecutor, TabCompleter {
    private final AchievementService achievementService;
    private final GuiService guiService;
    private final DataStore dataStore;
    private final File dataFolder;

    private final PcrykhPlugin plugin;

    public PcrykhCommand(PcrykhPlugin plugin, AchievementService achievementService, GuiService guiService, DataStore dataStore, File dataFolder) {
        this.plugin = plugin;
        this.achievementService = achievementService;
        this.guiService = guiService;
        this.dataStore = dataStore;
        this.dataFolder = dataFolder;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Player only.");
            return true;
        }
        if (!sender.hasPermission("pcrykh.use")) {
            sender.sendMessage("No permission.");
            return true;
        }

        if (args.length == 0 || "menu".equalsIgnoreCase(args[0])) {
            guiService.openMainMenu(player);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "help" -> {
                player.sendMessage("/pcrykh achievements, /pcrykh achievement <id>");
                return true;
            }
            case "profile" -> {
                if (args.length >= 2 && !sender.hasPermission("pcrykh.use.others")) {
                    sender.sendMessage("No permission.");
                    return true;
                }
                player.sendMessage("Profile view is GUI-only in this version.");
                return true;
            }
            case "achievements" -> {
                guiService.openAchievementsMenu(player);
                return true;
            }
            case "achievement" -> {
                if (args.length < 2) {
                    sender.sendMessage("Usage: /pcrykh achievement <achievement_id>");
                    return true;
                }
                AchievementDefinition def = achievementService.getAchievement(args[1]);
                if (def == null) {
                    sender.sendMessage("Achievement not found.");
                    return true;
                }
                guiService.openAchievementDetail(player, def);
                return true;
            }
            case "admin" -> {
                return handleAdmin(player, args);
            }
            default -> {
                sender.sendMessage("Unknown subcommand.");
                return true;
            }
        }
    }

    private boolean handleAdmin(Player player, String[] args) {
        if (!player.hasPermission("pcrykh.admin")) {
            player.sendMessage("No permission.");
            return true;
        }
        if (args.length < 2) {
            guiService.openAdminMenu(player);
            return true;
        }
        switch (args[1].toLowerCase()) {
            case "reload" -> {
                try {
                    plugin.reloadRuntime();
                    player.sendMessage("Config reloaded.");
                } catch (Exception e) {
                    player.sendMessage("Reload failed: " + e.getMessage());
                }
                return true;
            }
            case "export" -> {
                if (args.length < 3) {
                    player.sendMessage("Usage: /pcrykh admin export <player>");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[2]);
                if (target == null) {
                    player.sendMessage("Player not found.");
                    return true;
                }
                exportPlayer(target.getUniqueId(), target.getName(), player);
                return true;
            }
            case "setap" -> {
                if (args.length < 4) {
                    player.sendMessage("Usage: /pcrykh admin setap <player> <amount>");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[2]);
                if (target == null) {
                    player.sendMessage("Player not found.");
                    return true;
                }
                int amount;
                try {
                    amount = Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    player.sendMessage("Invalid amount.");
                    return true;
                }
                if (amount < 0) {
                    player.sendMessage("Amount must be >= 0.");
                    return true;
                }
                var state = achievementService.getOrLoad(target);
                state.apTotal = amount;
                state.achievementTierSum = achievementService.recomputeTierSum(state);
                state.playerLevel = state.achievementTierSum;
                dataStore.savePlayer(state, plugin.getConfigModel().specVersion);
                player.sendMessage("AP set.");
                return true;
            }
            case "complete" -> {
                if (args.length < 5) {
                    player.sendMessage("Usage: /pcrykh admin complete <player> <achievement_id> <tier>");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[2]);
                if (target == null) {
                    player.sendMessage("Player not found.");
                    return true;
                }
                AchievementDefinition def = achievementService.getAchievement(args[3]);
                if (def == null) {
                    player.sendMessage("Achievement not found.");
                    return true;
                }
                int tier;
                try {
                    tier = Integer.parseInt(args[4]);
                } catch (NumberFormatException e) {
                    player.sendMessage("Invalid tier.");
                    return true;
                }
                if (tier < 1) {
                    player.sendMessage("Tier must be >= 1.");
                    return true;
                }
                var state = achievementService.getOrLoad(target);
                var progress = state.achievementProgress.get(def.id);
                if (progress == null) {
                    player.sendMessage("Achievement progress missing.");
                    return true;
                }
                if (!achievementService.adminComplete(target, def, tier)) {
                    player.sendMessage("Tier out of order or invalid.");
                    return true;
                }
                dataStore.savePlayer(state, plugin.getConfigModel().specVersion);
                player.sendMessage("Tier completed.");
                return true;
            }
            case "reset" -> {
                if (args.length < 3) {
                    player.sendMessage("Usage: /pcrykh admin reset <player>");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[2]);
                if (target == null) {
                    player.sendMessage("Player not found.");
                    return true;
                }
                achievementService.resetPlayer(target.getUniqueId());
                player.sendMessage("Player reset.");
                return true;
            }
            default -> {
                player.sendMessage("Unknown admin subcommand.");
                return true;
            }
        }
    }

    private void exportPlayer(UUID playerId, String name, Player admin) {
        try {
            File dir = new File(dataFolder, "exports");
            dir.mkdirs();
            File out = new File(dir, name + "-" + playerId + ".json");
            var state = achievementService.getOrLoad(playerId);
            var payload = new LinkedHashMap<String, Object>();
            payload.put("player", name);
            payload.put("uuid", playerId.toString());
            payload.put("ap_total", state.apTotal);
            payload.put("achievement_tier_sum", state.achievementTierSum);
            payload.put("player_level", state.playerLevel);
            var progress = new LinkedHashMap<String, Object>();
            state.achievementProgress.forEach((id, prog) -> {
                var entry = new LinkedHashMap<String, Object>();
                entry.put("current_tier", prog.currentTier);
                entry.put("next_tier", prog.nextTier);
                entry.put("progress_amount", prog.progressAmount);
                progress.put(id, entry);
            });
            payload.put("achievements", progress);
            new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(out, payload);
            admin.sendMessage("Exported to " + out.getAbsolutePath());
        } catch (Exception e) {
            admin.sendMessage("Export failed.");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> options = new ArrayList<>();
        if (args.length == 1) {
            options.add("menu");
            options.add("help");
            options.add("profile");
            options.add("achievements");
            options.add("achievement");
            options.add("admin");
        } else if (args.length == 2 && "achievement".equalsIgnoreCase(args[0])) {
            for (AchievementDefinition def : achievementService.getAchievements()) {
                options.add(def.id);
            }
        } else if (args.length == 2 && "admin".equalsIgnoreCase(args[0])) {
            options.add("reload");
            options.add("export");
            options.add("setap");
            options.add("complete");
            options.add("reset");
        }
        return options;
    }
}
