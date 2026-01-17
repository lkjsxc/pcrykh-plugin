package dev.pcrykh.runtime;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public final class PcrykhCommand implements CommandExecutor {
    private final AchievementCatalog catalog;

    public PcrykhCommand(AchievementCatalog catalog) {
        this.catalog = catalog;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("pcrykh.use")) {
            return true;
        }
        sender.sendMessage("pcrykh active. Achievements loaded: " + catalog.count() + ".");
        return true;
    }
}
