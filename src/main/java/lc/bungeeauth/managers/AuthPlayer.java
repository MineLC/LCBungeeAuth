package lc.bungeeauth.managers;

import lc.bungeeauth.LCBungeeAuth;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;

public class AuthPlayer {
    private String name;
    private ProxiedPlayer pp;
    private boolean register;
    private boolean authverify;
    private boolean premium;
    private boolean captcha = false;
    private String lastip;
    private Integer tries = 0;
    private String ip;
    private String password;
    private String lastconnection;
    public static HashMap<String, AuthPlayer> jugadores = new HashMap<String, AuthPlayer>();

    public AuthPlayer(String name){
        this.name = name;
        jugadores.put(name,this);
    }
    public static void removeAuthPlayer(ProxiedPlayer pp){
        jugadores.remove(pp);
    }
    public static void removeAuthPlayer(String pp){
        jugadores.remove(pp);
    }

    public static AuthPlayer getAuthPlayer(String name){
        if(jugadores.containsKey((name))){
            return jugadores.get(name);
        }
        return new AuthPlayer(name);
    }

    public boolean isPremium(){return premium;}
    public void setPremium(boolean bol){this.premium = bol;}

    public boolean isRegister(){return register;}
    public void setRegister(boolean bol){this.register = bol;}

    public boolean isAuthverify(){return this.authverify;}
    public void setAuthverify(boolean bol){this.authverify = bol;}

    public ProxiedPlayer getProxyPlayer(){
        return LCBungeeAuth.getInstance().getProxy().getPlayer(this.name);
    }
    public String getLastip(){return this.lastip;}
    public void setLastip(String ip){this.lastip = ip;}

    public String getLastconnection() {return this.lastconnection;}
    public void setLastconnection(String l){ this.lastconnection = l;}

    public String getPassword(){ return this.password;}
    public void setPassword(String p){ this.password = p;}


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public boolean isCaptcha() {
        return captcha;
    }

    public void setCaptcha(boolean captcha) {
        this.captcha = captcha;
    }

    public Integer getTries() {
        return tries;
    }

    public void setTries(Integer tries) {
        this.tries = tries;
    }

    public String getName() {
        return name;
    }
}
