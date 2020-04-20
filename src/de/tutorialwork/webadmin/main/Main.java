package de.tutorialwork.webadmin.main;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.tutorialwork.webadmin.commands.Credits;
import de.tutorialwork.webadmin.commands.WebAccount;
import de.tutorialwork.webadmin.listener.JoinListener;
import de.tutorialwork.webadmin.listener.ServerListener;
import de.tutorialwork.webadmin.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Main extends JavaPlugin {

    public static MySQLConnect mysql;
    public static String Prefix = "§8[§9WebAdmin§8] §7";
    public static String NoPerms = Prefix + "§cSorry, but you don't can use this command";

    public static Cache nameCache;

    @Override
    public void onEnable() {
        initFile();
        initPlugin();
        Metrics metrics = new Metrics(this);
        startWebChecker();
        initOnlinePlayers();
    }

    private void initFile() {
        File folder = new File(getDataFolder().getPath());
        if(!folder.exists()){
            folder.mkdir();
        }
        File file = new File(getDataFolder(), "mysql.yml");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        cfg.options().copyDefaults(true);
        cfg.options().header("Please set up also the MySQL database at your webserver in modules/mysql.php");
        cfg.addDefault("MYSQL.HOST", "localhost");
        cfg.addDefault("MYSQL.DATABASE", "webadmin");
        cfg.addDefault("MYSQL.USER", "root");
        cfg.addDefault("MYSQL.PASSWORD", "yourpassword");
        cfg.addDefault("MYSQL.PORT", 3306);
        try {
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        /*
        On server reload or server shutdown save all onlinetimes
         */
        for(Player all : Bukkit.getOnlinePlayers()){
            ServerManager.calcOnlinetime(all);
        }
    }

    private void startWebChecker() {
        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                /*
                Sync data with web
                 */
                updateOP();
                updateWhitelist();
                /*
                Operators
                 */
                Gson gson = new Gson();
                Type baseType = new TypeToken<List<String>>() {}.getType();
                ArrayList<String> uuids = gson.fromJson(ServerManager.getOP(), baseType);
                for(String id : uuids){
                    OfflinePlayer op;
                    if(ServerManager.getName(id) != null){
                        op = Bukkit.getOfflinePlayer(ServerManager.getName(id));
                    } else {
                        String name;
                        if(!nameCache.isCached(id)){
                            name = NameFetcher.getName(id);
                            nameCache.cacheName(name, id);
                        } else {
                            name = nameCache.getNameCache().get(id);
                        }
                        op = Bukkit.getOfflinePlayer(name);
                    }
                    op.setOp(true);
                }
                /*
                Whitelist
                 */
                if(ServerManager.isEnabled()){
                    Bukkit.setWhitelist(true);
                    ArrayList<String> uuids_whitelist = gson.fromJson(ServerManager.getWhitelist(), baseType);
                    for(String id : uuids_whitelist){
                        OfflinePlayer op;
                        if(ServerManager.getName(id) != null){
                            op = Bukkit.getOfflinePlayer(ServerManager.getName(id));
                        } else {
                            op = Bukkit.getOfflinePlayer(NameFetcher.getName(id));
                        }
                        op.setWhitelisted(true);
                    }
                } else {
                    Bukkit.setWhitelist(false);
                }
                Bukkit.reloadWhitelist();
            }
        }, 3 * 20L, 3* 20L);
    }

    private void initPlugin() {
        /*
        Commands
         */
        Bukkit.getPluginCommand("web").setExecutor(new WebAccount());
        Bukkit.getPluginCommand("webadmin").setExecutor(new Credits());
        /*
        Listener
         */
        Bukkit.getPluginManager().registerEvents(new ServerListener(), this);
        Bukkit.getPluginManager().registerEvents(new JoinListener(), this);
        /*
        Connect to mysql database
         */
        File file = new File(getDataFolder(), "mysql.yml");
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        MySQLConnect.HOST = cfg.getString("MYSQL.HOST");
        MySQLConnect.DATABASE = cfg.getString("MYSQL.DATABASE");
        MySQLConnect.USER = cfg.getString("MYSQL.USER");
        MySQLConnect.PASSWORD = cfg.getString("MYSQL.PASSWORD");
        MySQLConnect.PORT = cfg.getInt("MYSQL.PORT");
        mysql = new MySQLConnect(MySQLConnect.HOST, MySQLConnect.DATABASE, MySQLConnect.USER, MySQLConnect.PASSWORD, MySQLConnect.PORT);
        mysql.update("CREATE TABLE IF NOT EXISTS webadmin_players(UUID varchar(255) UNIQUE, NAME varchar(255), WEB_PASSWORD varchar(255), FIRSTJOIN DATETIME, LASTJOIN DATETIME, ONLINETIME int(255), IP varchar(255), STATUS int(11));");
        mysql.update("CREATE TABLE IF NOT EXISTS webadmin_settings(STRING varchar(255) UNIQUE, VALUE varchar(5000));");
        mysql.update("INSERT INTO webadmin_settings (STRING, VALUE) VALUES ('WHITELIST_STATUS', 'false')");
        mysql.update("INSERT INTO webadmin_settings (STRING, VALUE) VALUES ('WHITELIST', null)");
        mysql.update("INSERT INTO webadmin_settings (STRING, VALUE) VALUES ('WHITELIST_MSG', '§c§lSorry§7, but you are not at the whitelist')");
        mysql.update("INSERT INTO webadmin_settings (STRING, VALUE) VALUES ('OP', null)");
        mysql.update("INSERT INTO webadmin_settings (STRING, VALUE) VALUES ('SLOTS', 100)");
        mysql.update("INSERT INTO webadmin_settings (STRING, VALUE) VALUES ('MOTD1', '§9WebAdmin §7developed by §c§lTutorialwork')");
        mysql.update("INSERT INTO webadmin_settings (STRING, VALUE) VALUES ('MOTD2', '§8§lChange MOTD in the webinterface')");
        mysql.update("INSERT INTO webadmin_settings (STRING, VALUE) VALUES ('FULL_MSG', '§cThe maximum of players is reached. §7Try again later.')");
        /*
        Objects
         */
        nameCache = new Cache();

        updateWhitelist();
        updateOP();
    }

    private void updateOP() {
        ServerManager.setOP();
    }

    private static void updateWhitelist(){
        ServerManager.setWhitelistPlayer();
    }

    private void initOnlinePlayers() {
        for(Player all : Bukkit.getOnlinePlayers()){
            JoinListener.joins.put(all, System.currentTimeMillis());
        }
    }

}
