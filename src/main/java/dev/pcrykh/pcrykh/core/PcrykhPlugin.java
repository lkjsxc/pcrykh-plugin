package dev.pcrykh.pcrykh.core;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PcrykhPlugin extends JavaPlugin implements CommandExecutor {

    @Override
    public void onEnable() {
        var command = getCommand("pcrykh");
        if (command != null) {
            command.setExecutor(this);
        }
        getLogger().info("pcrykh enabled (docs-first skeleton).");
    }

    @Override
    public void onDisable() {
        getLogger().info("pcrykh disabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            sender.sendMessage("pcrykh is running in docs-first mode. See /docs for the spec.");
        } else {
            sender.sendMessage("pcrykh is running in docs-first mode.");
        }
        return true;
    }
}
