package de.tutorialwork.listener;

import de.tutorialwork.utils.ServerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerListener implements Listener {

    @EventHandler
    public void onPing(ServerListPingEvent e){
        e.setMotd(ServerManager.getMotd(1)+"\n"+ ServerManager.getMotd(2));
        e.setMaxPlayers(ServerManager.getSlots());
    }

}
