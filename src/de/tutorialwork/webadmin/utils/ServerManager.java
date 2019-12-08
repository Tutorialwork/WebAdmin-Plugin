package de.tutorialwork.webadmin.utils;

import com.google.gson.Gson;
import de.tutorialwork.webadmin.listener.JoinListener;
import de.tutorialwork.webadmin.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Set;

public class ServerManager {

    /*
    Player
     */
    public static boolean playerExists(String UUID){
        try {

            ResultSet rs = Main.mysql.query("SELECT * FROM webadmin_players WHERE UUID='" + UUID + "'");
            if(rs.next()){
                return rs.getString("UUID") != null;
            }

        } catch (SQLException exc){

        }

        return false;

    }

    public static void createPlayer(String UUID, String Name, String IP){
        if(!playerExists(UUID)){
            Main.mysql.update("INSERT INTO webadmin_players(UUID, NAME, WEB_PASSWORD, FIRSTJOIN, LASTJOIN, ONLINETIME, IP, STATUS) " +
                    "VALUES ('" + UUID + "', '" + Name + "', null, NOW(), NOW(), 0, '" + IP + "', 1)");
        } else {
            updateName(UUID, Name);
            updateLastLogin(UUID);
            updateIP(UUID, IP);
            setOnlinestatus(UUID, 1);
        }
    }

    public static void updateName(String UUID, String newName){
        if(playerExists(UUID)){
            Main.mysql.update("UPDATE webadmin_players SET NAME='" + newName + "' WHERE UUID='" + UUID + "'");
        }
    }
    public static void updateIP(String UUID, String IP){
        if(playerExists(UUID)){
            Main.mysql.update("UPDATE webadmin_players SET IP='" + IP + "' WHERE UUID='" + UUID + "'");
        }
    }

    public static void updateLastLogin(String UUID){
        if(playerExists(UUID)){
            Main.mysql.update("UPDATE webadmin_players SET LASTJOIN = NOW() WHERE UUID = '"+UUID+"'");
        }
    }

    public static long getOnlinetime(String UUID){
        try {
            ResultSet rs = Main.mysql.query("SELECT * FROM webadmin_players WHERE UUID='"+UUID+"'");
            if(rs.next()){
                return rs.getLong("ONLINETIME");
            }
        } catch (SQLException exc){

        }
        return 0;
    }

    public static void updateOnlinetime(String UUID, long time){
        if(playerExists(UUID)){
            Main.mysql.update("UPDATE webadmin_players SET ONLINETIME = '" + time + "' WHERE UUID = '"+UUID+"'");
        }
    }

    public static void setPassword(String UUID, String Password){
        if(playerExists(UUID)){
            Main.mysql.update("UPDATE webadmin_players SET WEB_PASSWORD = '" + Password + "' WHERE UUID = '"+UUID+"'");
        }
    }

    public static void setOnlinestatus(String UUID, int status){
        if(playerExists(UUID)){
            Main.mysql.update("UPDATE webadmin_players SET STATUS = '" + status + "' WHERE UUID = '"+UUID+"'");
        }
    }

    public static String getName(String UUID){
        try {
            ResultSet rs = Main.mysql.query("SELECT * FROM webadmin_players WHERE UUID='"+UUID+"'");
            if(rs.next()){
                return rs.getString("NAME");
            }
        } catch (SQLException exc){

        }
        return null;
    }

    public static void calcOnlinetime(Player p){
        long now = System.currentTimeMillis();
        long join = JoinListener.joins.get(p);
        long onlinetime = ServerManager.getOnlinetime(p.getUniqueId().toString()) + now - join;
        ServerManager.updateOnlinetime(p.getUniqueId().toString(), onlinetime);
        ServerManager.setOnlinestatus(p.getUniqueId().toString(), 0);
    }

    /*
    Whitelist
     */

    public static boolean isEnabled(){
        try {
            ResultSet rs = Main.mysql.query("SELECT * FROM webadmin_settings WHERE STRING='WHITELIST_STATUS'");
            if(rs.next()){
                if(rs.getString("VALUE").equals("true")){
                    return true;
                } else if(rs.getString("VALUE").equals("false")){
                    return false;
                }
            }
        } catch (SQLException exc){

        }
        return false;
    }

    public static void setWhitelist(boolean status){
        Main.mysql.update("UPDATE webadmin_settings SET VALUE='"+status+"' WHERE STRING='WHITELIST_STATUS'");
        Bukkit.getServer().setWhitelist(status);
    }

    public static String getWhitelist(){
        try {
            ResultSet rs = Main.mysql.query("SELECT * FROM webadmin_settings WHERE STRING='WHITELIST'");
            if(rs.next()){
                return rs.getString("VALUE");
            }
        } catch (SQLException exc){

        }
        return null;
    }

    public static void setWhitelistPlayer(Set players){
        Gson gson = new Gson();
        ArrayList<String> whitelist = new ArrayList<>();
        for(OfflinePlayer all : Bukkit.getWhitelistedPlayers()){
            whitelist.add(all.getUniqueId().toString());
        }
        Main.mysql.update("UPDATE webadmin_settings SET VALUE='"+gson.toJson(whitelist)+"' WHERE STRING='WHITELIST'");
    }

    /*
    MOTD
     */

    public static void setMotd(int line, String motd){
        if(line == 1){
            Main.mysql.update("UPDATE webadmin_settings SET VALUE='"+motd+"' WHERE STRING='MOTD1'");
        } else if(line == 2){
            Main.mysql.update("UPDATE webadmin_settings SET VALUE='"+motd+"' WHERE STRING='MOTD2'");
        }
    }

    public static String getMotd(int line){
        try {
            ResultSet rs = Main.mysql.query("SELECT * FROM webadmin_settings WHERE STRING='MOTD"+line+"'");
            if(rs.next()){
                return rs.getString("VALUE");
            }
        } catch (SQLException exc){

        }
        return null;
    }

    public static Integer getSlots(){
        try {
            ResultSet rs = Main.mysql.query("SELECT * FROM webadmin_settings WHERE STRING='SLOTS'");
            if(rs.next()){
                return rs.getInt("VALUE");
            }
        } catch (SQLException exc){

        }
        return null;
    }

    /*
    OP
     */

    public static void setOP(Set players){
        Gson gson = new Gson();
        ArrayList<String> ops = new ArrayList<>();
        for(OfflinePlayer all : Bukkit.getOperators()){
            ops.add(all.getUniqueId().toString());
        }
        Main.mysql.update("UPDATE webadmin_settings SET VALUE='"+gson.toJson(ops)+"' WHERE STRING='OP'");
    }

    public static String getOP(){
        try {
            ResultSet rs = Main.mysql.query("SELECT * FROM webadmin_settings WHERE STRING='OP'");
            if(rs.next()){
                return rs.getString("VALUE");
            }
        } catch (SQLException exc){

        }
        return null;
    }

    /*
    Utils
     */

    public static String getRAWString(String setting){
        try {
            ResultSet rs = Main.mysql.query("SELECT * FROM webadmin_settings WHERE STRING='"+setting+"'");
            if(rs.next()){
                return rs.getString("VALUE");
            }
        } catch (SQLException exc){

        }
        return null;
    }

}
