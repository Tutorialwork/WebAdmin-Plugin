package de.tutorialwork.webadmin.commands;

import de.tutorialwork.webadmin.main.Main;
import de.tutorialwork.webadmin.utils.BCrypt;
import de.tutorialwork.webadmin.utils.ServerManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WebAccount implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender instanceof Player){
            Player p = (Player) sender;
            if(p.hasPermission("webadmin.web")){
                if(args.length == 0){
                    p.sendMessage(Main.Prefix+"/web <§9Password§7>");
                } else {
                    if(args[0].length() > 5){
                        String password = BCrypt.hashpw(args[0], BCrypt.gensalt());
                        ServerManager.setPassword(p.getUniqueId().toString(), password);
                        p.sendMessage(Main.Prefix+"§aSuccessfully updated the password for your web account");
                    } else {
                        p.sendMessage(Main.Prefix+"§cYour password must be at least 6 characters");
                    }
                }
            } else {
                p.sendMessage(Main.NoPerms);
            }
        } else {
            Bukkit.getConsoleSender().sendMessage("§cThis command can you only use as player");
        }
        return false;
    }
}
