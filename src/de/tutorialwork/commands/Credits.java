package de.tutorialwork.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Credits implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender instanceof Player){
            Player p = (Player) sender;
            p.sendMessage("");
            p.sendMessage("§8[]===================================[]");
            p.sendMessage("§9§lWebAdmin §8• §7Version §8» §c"+ Bukkit.getPluginManager().getPlugin("WebAdmin").getDescription().getVersion());
            p.sendMessage("§7Developer §8» §e§lTutorialwork");
            p.sendMessage("§5YT §7Channel §8» §cyoutube.com/Tutorialwork");
            p.sendMessage("§8[]===================================[]");
            p.sendMessage("");
        }
        return false;
    }
}
