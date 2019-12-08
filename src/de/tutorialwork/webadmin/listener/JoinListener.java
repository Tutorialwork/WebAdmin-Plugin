package de.tutorialwork.webadmin.listener;

import de.tutorialwork.webadmin.utils.ServerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;

public class JoinListener implements Listener {

    public static HashMap<Player, Long> joins = new HashMap<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        ServerManager.createPlayer(p.getUniqueId().toString(), p.getName(), p.getAddress().getAddress().toString());
        joins.put(p, System.currentTimeMillis());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Player p = e.getPlayer();
        ServerManager.calcOnlinetime(p);
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent e){
        Player p = e.getPlayer();
        if(ServerManager.isEnabled()){
            if(!p.isWhitelisted()){
                e.setResult(PlayerLoginEvent.Result.KICK_WHITELIST);
                e.setKickMessage(ServerManager.getRAWString("WHITELIST_MSG"));
            }
        }
        if(Bukkit.getOnlinePlayers().size() >= ServerManager.getSlots()){
            if(!p.hasPermission("webadmin.bypass.slots")){
                e.setResult(PlayerLoginEvent.Result.KICK_FULL);
                e.setKickMessage(ServerManager.getRAWString("FULL_MSG"));
            }
        }
    }

}
