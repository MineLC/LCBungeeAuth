package lc.bungeeauth;

import lc.bungeeauth.commands.*;
import lc.bungeeauth.configuration.ConfigManager;
import lc.bungeeauth.database.Database;
import lc.bungeeauth.listeners.Events;
import lc.bungeeauth.managers.AuthManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;

public final class LCBungeeAuth extends Plugin {

    public static ConfigurationProvider cp = ConfigurationProvider.getProvider(YamlConfiguration.class);
    public static ArrayList<String> premiumPlayers = new ArrayList<String>();
    public static ArrayList<String> lobbyPlayers = new ArrayList<String>();
    private static LCBungeeAuth instance;
    private boolean dbexist = false;
    public ConfigManager config;
    public Connection connection;
    public String host;
    public String port;
    public String database;
    public String authtable;
    public String user;
    public String pass;
    private ScheduledTask task;
    public void onEnable() {

        instance = this;
        this.config = new ConfigManager();
        loadDirectory();
        registerConfiguration();
        AuthManager.loadConfiguration();
        getProxy().getConsole().sendMessage(ChatColor.GREEN + "[BungeeAuth] conectando con la MySQL...");
        mysqlupdate();
        createMsyqlResource();
        premiumPlayers.addAll(Database.getPremiums());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new Events());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, (Command)new ChangePassword("changepass"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, (Command)new Login("login"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, (Command)new Logout("logout"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, (Command)new Premium("premium"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, (Command)new Register("register"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, (Command)new UnPremium("unpremium"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, (Command)new UnRegister("unregister"));
        getProxy().getConsole().sendMessage(ChatColor.GREEN + "[BungeeAuth] Registrando canal de captcha.");
        getProxy().registerChannel( "captcha:channel");

    }
    private void createMsyqlResource() {
        try {
            String createAuthTable = "CREATE TABLE IF NOT EXISTS " + this.authtable + " (Player VARCHAR(30) UNIQUE,Password NVARCHAR(500),IP NVARCHAR(500), Time NVARCHAR(500), Premium INTEGER );  ";
            Statement stmt = getConnection().createStatement();
            stmt.execute(createAuthTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public String getMessagebyString(String path){
        return ChatColor.translateAlternateColorCodes('&', config.getMessage().getString("message.prefix") + config.getMessage().getString(path));
    }

    private void loadDirectory() {
        File dir = new File(getDataFolder().toString());
        if (!dir.exists())
            dir.mkdir();
    }
    private void mysqlupdate() {
        this.host = this.config.getConfig().getString("config.mysql.host");
        this.user = this.config.getConfig().getString("config.mysql.user");
        this.port = this.config.getConfig().getString("config.mysql.port");
        this.database = this.config.getConfig().getString("config.mysql.database");
        this.authtable = this.config.getConfig().getString("config.mysql.authtable");
        this.pass = this.config.getConfig().getString("config.mysql.pass");
        try {
            synchronized (this) {
                if (getConnection() != null && !getConnection().isClosed())
                    return;
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port, this.user, this.pass);
                if(con != null){
                    getProxy().getConsole().sendMessage(ChatColor.GREEN + "[BungeeAuth] MySQL Cargada correctamente.");
                    getProxy().getConsole().sendMessage(ChatColor.GREEN + "[BungeeAuth] Conectando con la base de datos.");
                    ResultSet rs = con.getMetaData().getCatalogs();
                    while(rs.next()){
                        String db = rs.getString(1);
                        if(this.database.equals(db)){
                            dbexist = true;
                            getProxy().getConsole().sendMessage(ChatColor.GREEN + "[BungeeAuth] Database encontrada.");
                            setConnection(DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?autoReconnect=true", this.user, this.pass));
                            break;
                        }
                    }
                    if(!dbexist){
                        Statement stmt = con.createStatement();
                        stmt.executeUpdate("CREATE DATABASE " + this.database +";");
                        getProxy().getConsole().sendMessage(ChatColor.GREEN + "[BungeeAuth] Database creada correctamente.");
                        setConnection(DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?autoReconnect=true", this.user, this.pass));

                    }
                    con.close();
                }

            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            getProxy().getConsole().sendMessage(ChatColor.RED + "[BungeeAuth] error al conectar con la MySQL...");
            getInstance().getProxy().stop();

        }
    }

    private void registerConfiguration() {
        getProxy().getConsole().sendMessage(ChatColor.GREEN + "[BungeeAuth] Cargando configuracion...");
        config.registerConfig();
        config.registerMessage();
    }


    public Connection getConnection() {
        Connection con = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?autoReconnect=true", this.user, this.pass);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return con;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
    public static LCBungeeAuth getInstance() {
        return instance;
    }
}
