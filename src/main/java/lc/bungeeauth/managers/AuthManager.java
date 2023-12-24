package lc.bungeeauth.managers;

import lc.bungeeauth.LCBungeeAuth;
import lc.bungeeauth.configuration.ConfigManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.ArrayList;
import java.util.List;

public class AuthManager {
    private static ServerInfo LobbyServer;
    private static ServerInfo AuthServer;
    private static boolean kickwrongpass;
    private static boolean loginspam;
    private static boolean registerspam;
    private static Integer delayspam;
    private static boolean unregisteradmin;
    private static boolean Encrypt;
    private static boolean premiumkick;
    private static boolean changepasswordverify;
    private static boolean autoverification;
    private static int maxperip;
    private static int maxtry;
    private static int minpasslenght;
    private static String Clave;
    private static List<String> blacklistname = new ArrayList<String>();
    private static List<String> blacklistip = new ArrayList<String>();
    private static List<String> blacklistuuid = new ArrayList<String>();
    private static ConfigManager config;


    public static void loadConfiguration() {
        config = new ConfigManager();
        try{
            setBlacklistip(config.getConfig().getStringList("config.auth.blacklistip"));
            setBlacklistname(config.getConfig().getStringList("config.auth.blacklistname"));
            setBlacklistuuid(config.getConfig().getStringList("config.auth.blacklistuuid"));
            setUnregisteradmin(Boolean.parseBoolean(config.getConfig().getString("config.unregister.adminmode")));
            setAutoverification(Boolean.parseBoolean(config.getConfig().getString("config.auth.autoverification")));
            setMinpasslenght(Integer.parseInt(config.getConfig().getString("config.auth.minpasslenght")));
            setLobbyServer(LCBungeeAuth.getInstance().getProxy().getServerInfo(config.getConfig().getString("config.auth.lobby")));
            setAuthServer(LCBungeeAuth.getInstance().getProxy().getServerInfo(config.getConfig().getString("config.auth.noprem_lobby")));
            setKickwrongpass(Boolean.parseBoolean(config.getConfig().getString("config.auth.kickwrong_pass")));
            setLoginspam(Boolean.parseBoolean(config.getConfig().getString("config.auth.login_spam")));
            setRegisterspam(Boolean.parseBoolean(config.getConfig().getString("config.auth.register_spam")));
            setDelayspam(Integer.parseInt(config.getConfig().getString("config.auth.spam_delay")));
            setPremiumkick(Boolean.parseBoolean(config.getConfig().getString("config.auth.premium_kick")));
            setEncrypt(Boolean.parseBoolean(config.getConfig().getString("config.auth.encrypt")));
            setClave(config.getConfig().getString("config.auth.secretkey"));
            setMaxperip(Integer.parseInt(config.getConfig().getString("config.auth.minpasslenght")));
            setMaxtry(Integer.parseInt(config.getConfig().getString("config.auth.maxtry")));
            setChangepasswordverify(Boolean.parseBoolean(config.getConfig().getString("config.auth.change_pass.oldpassverify")));
        }catch (Exception e){
            e.printStackTrace();
            LCBungeeAuth.getInstance().getProxy().getConsole().sendMessage(ChatColor.RED + "Error al cargar los datos de config.yml, verifica que sean correctos.");
            LCBungeeAuth.getInstance().getProxy().stop();
        }
    }
    public static void setLobbyServer(ServerInfo lobbyServer) {
        LobbyServer = lobbyServer;
    }
    public static ServerInfo getLobbyServer(){
        return LobbyServer;
    }

    public static ServerInfo getAuthServer() {
        return AuthServer;
    }

    public static void setAuthServer(ServerInfo authServer) {
        AuthServer = authServer;
    }

    public static boolean Kickwrongpass() {
        return kickwrongpass;
    }

    public static void setKickwrongpass(boolean kickwrongpass) {
        AuthManager.kickwrongpass = kickwrongpass;
    }

    public static boolean Loginspam() {
        return loginspam;
    }

    public static void setLoginspam(boolean loginspam) {
        AuthManager.loginspam = loginspam;
    }

    public static boolean Registerspam() {
        return registerspam;
    }

    public static void setRegisterspam(boolean registerspam) {
        AuthManager.registerspam = registerspam;
    }
    public static boolean isChangepasswordverify() {
        return changepasswordverify;
    }

    public static void setChangepasswordverify(boolean changepasswordverify) {
        AuthManager.changepasswordverify = changepasswordverify;
    }
    public static boolean Encrypt() {
        return Encrypt;
    }

    public static void setEncrypt(boolean encrypt) {
        Encrypt = encrypt;
    }

    public static String getClave() {
        return Clave;
    }

    public static void setClave(String clave) {
        Clave = clave;
    }

    public static boolean Premiumkick() {
        return premiumkick;
    }

    public static void setPremiumkick(boolean premiumkick) {
        AuthManager.premiumkick = premiumkick;
    }

    public static int getMaxperip() {
        return maxperip;
    }

    public static void setMaxperip(int maxperip) {
        AuthManager.maxperip = maxperip;
    }

    public static int getMaxtry() {
        return maxtry;
    }

    public static void setMaxtry(int maxtry) {
        AuthManager.maxtry = maxtry;
    }

    public static int getMinpasslenght() {
        return minpasslenght;
    }

    public static void setMinpasslenght(int minpasslenght) {
        AuthManager.minpasslenght = minpasslenght;
    }

    public static List<String> getBlacklistname() {
        return blacklistname;
    }

    public static void setBlacklistname(List<String> blacklistname) {
        AuthManager.blacklistname = blacklistname;
    }
    public static void addBlacklistName(String blacklistname) {
        AuthManager.blacklistname.add(blacklistname);
    }

    public static List<String> getBlacklistip() {
        return blacklistip;
    }

    public static void setBlacklistip(List<String> blacklistip) {
        AuthManager.blacklistip = blacklistip;
    }

    public static void addBlacklistip(String blacklistip) {
         AuthManager.blacklistip.add(blacklistip);
    }

    public static List<String> getBlacklistuuid() {
        return blacklistuuid;
    }

    public static void setBlacklistuuid(List<String> blacklistuuid) {
        AuthManager.blacklistuuid = blacklistuuid;
    }
    public static void addBlacklistuuid(String blacklistuuid) {
        AuthManager.blacklistuuid.add(blacklistuuid);
    }


    public static boolean isAutoverification() {
        return autoverification;
    }

    public static void setAutoverification(boolean autoverification) {
        AuthManager.autoverification = autoverification;
    }

    public static Integer getDelayspam() {
        return delayspam;
    }

    public static void setDelayspam(Integer delayspam) {
        AuthManager.delayspam = delayspam;
    }

    public static Boolean getLoginspam(){
        return loginspam;
    }
    public static Boolean getregisterspam(){
        return registerspam;
    }

    public static boolean isUnregisteradmin() {
        return unregisteradmin;
    }

    public static void setUnregisteradmin(boolean unregisteradmin) {
        AuthManager.unregisteradmin = unregisteradmin;
    }
}
