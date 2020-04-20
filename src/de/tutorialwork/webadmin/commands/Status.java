package de.tutorialwork.webadmin.commands;

import de.tutorialwork.webadmin.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Status implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender instanceof Player){
            Player p = (Player) sender;
            if(p.hasPermission("webadmin.status")){
                boolean enabled = Bukkit.hasWhitelist();
                p.sendMessage(Main.Prefix+"Server status");
                if(enabled){
                    p.sendMessage("§7Whitelist: §a§lOn");
                } else {
                    p.sendMessage("§7Whitelist: §c§lOff");
                }

                String whitelisted = "";
                for (OfflinePlayer op : Bukkit.getWhitelistedPlayers()){
                    whitelisted += " §9"+op.getName();
                }
                if(whitelisted == ""){
                    whitelisted = "§c§lNone";
                }

                String operators = "";
                for (OfflinePlayer op : Bukkit.getOperators()){
                    operators += " §9"+op.getName();
                }
                if(operators == ""){
                    operators = "§c§lNone";
                }

                p.sendMessage("§7Whitelisted players: "+whitelisted);
                p.sendMessage("§7Operators: "+operators);
            } else {
                p.sendMessage(Main.NoPerms);
            }
        } else {
            Bukkit.getConsoleSender().sendMessage("§cThis command can you only use as player");
        }
        return false;
    }
}
