package dev.pcrykh.runtime;

import dev.pcrykh.gui.AchievementMenuService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class PcrykhCommand implements CommandExecutor {
    private final AchievementMenuService menuService;

    public PcrykhCommand(AchievementMenuService menuService) {
        this.menuService = menuService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("pcrykh.use")) {
            return true;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage("pcrykh menu is player-only.");
            return true;
        }
        menuService.open(player, 0);
        return true;
    }
}
