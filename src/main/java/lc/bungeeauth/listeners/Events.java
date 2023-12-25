package lc.bungeeauth.listeners;

import lc.bungeeauth.LCBungeeAuth;
import lc.bungeeauth.database.Database;
import lc.bungeeauth.managers.AuthManager;
import lc.bungeeauth.managers.AuthPlayer;
import lc.bungeecore.utilidades.Util;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import javax.xml.crypto.Data;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Events implements Listener {

    @EventHandler(priority = 64)
    public void PreloginPremiumEvent(final PreLoginEvent e) {
        if (e.isCancelled())
            return;
        final String name = e.getConnection().getName();
        e.registerIntent((Plugin) LCBungeeAuth.getInstance());
        LCBungeeAuth.getInstance().getProxy().getScheduler().runAsync((Plugin)LCBungeeAuth.getInstance(), new Runnable() {
            public void run() {
                try {

                    final AuthPlayer jug = AuthPlayer.getAuthPlayer(name);
                    jug.setIp(e.getConnection().getAddress().getHostString());
                    Database.loadAuthPlayerInfo(e.getConnection().getName());
                    if (jug.isPremium()) {
                        e.getConnection().setOnlineMode(true);
                        
                        return;
                    }
                    if(e.getConnection().getAddress().getHostString().equals(jug.getLastip()) && Database.hasPassword(name)){
                        jug.setAuthverify(true);
                        jug.setCaptcha(true);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    e.completeIntent((Plugin) LCBungeeAuth.getInstance());
                }
            }
        });
    }
    @EventHandler(priority = 64)
    public void onComands(ChatEvent e){
        if(!e.isCommand()){
            return;
        }
        if(e.getSender() instanceof ProxiedPlayer){
            AuthPlayer ap = AuthPlayer.getAuthPlayer(((ProxiedPlayer) e.getSender()).getName());
            if(!(ap.isRegister() || ap.isAuthverify()) && !(e.getMessage().toLowerCase().startsWith("/captcha") ||
                    !e.getMessage().toLowerCase().startsWith("/register") ||
                    !e.getMessage().toLowerCase().startsWith("/login"))){
                e.setCancelled(true);
                ap.getProxyPlayer().sendMessage(LCBungeeAuth.getInstance().getMessagebyString("message.commanddeny"));
            }

        }
    }
    @EventHandler
    public void onPostLogin(final PostLoginEvent e) {
        LCBungeeAuth.getInstance().getProxy().getScheduler().runAsync(LCBungeeAuth.getInstance(), () -> {
            AuthPlayer jug = AuthPlayer.getAuthPlayer(e.getPlayer().getName());
            Database.loadAuthPlayerInfo(e.getPlayer().getName());
            if (e.getPlayer().getPendingConnection().isOnlineMode()){
                jug.setAuthverify(true);
                jug.setCaptcha(true);
                jug.setLastip(e.getPlayer().getPendingConnection().getAddress().getHostString());
                Database.saveAllPlayerAuthData(jug);
                if(!LCBungeeAuth.premiumPlayers.contains(e.getPlayer().getName())){
                    LCBungeeAuth.premiumPlayers.add(e.getPlayer().getName());
                }
                return;
            }
            System.out.println( Database.hasPassword(jug.getName()));
            if(e.getPlayer().getPendingConnection().getAddress().getHostString().equals(jug.getLastip()) && Database.hasPassword(jug.getName())){
                jug.setAuthverify(true);
                jug.setCaptcha(true);
                jug.setLastip(e.getPlayer().getPendingConnection().getAddress().getHostString());
                Database.saveAllPlayerAuthData(jug);
            }

        });

    }

    @EventHandler
    public void pluginmessage(PluginMessageEvent e){
        if(e.getTag().equalsIgnoreCase("captcha:channel")){
           AuthPlayer ap = AuthPlayer.getAuthPlayer(e.getReceiver().toString());
           ap.setCaptcha(true);
           if(!ap.isRegister()) ap.getProxyPlayer().sendMessage(LCBungeeAuth.getInstance().getMessagebyString("message.register.message"));
           else ap.getProxyPlayer().sendMessage(LCBungeeAuth.getInstance().getMessagebyString("message.login.message"));
        }
    }

    @EventHandler
    public void onConnect(ServerConnectEvent e) {
        AuthPlayer jug = AuthPlayer.getAuthPlayer(e.getPlayer().getName());
        if(e.getTarget() != AuthManager.getAuthServer()){
            if(!jug.isRegister()){
                e.setTarget(AuthManager.getAuthServer());
            }else if(!jug.isAuthverify()){
                e.setTarget(AuthManager.getAuthServer());
            }
            return;
        }
         if(AuthManager.getBlacklistname().contains(jug.getProxyPlayer().getName())) {
            e.getPlayer().disconnect(LCBungeeAuth.getInstance().getMessagebyString("message.kick.blacklistname"));
         }
         else if(AuthManager.getBlacklistuuid().contains(jug.getProxyPlayer().getUniqueId())){
             e.getPlayer().disconnect(LCBungeeAuth.getInstance().getMessagebyString("message.kick.blacklistuuid"));
         }
         else if(AuthManager.getBlacklistip().contains(e.getPlayer().getPendingConnection().getAddress().getHostString())){
             e.getPlayer().disconnect(LCBungeeAuth.getInstance().getMessagebyString("message.kick.blacklistip"));
            } else {
                if(!jug.isRegister() && jug.isCaptcha()){
                    jug.getProxyPlayer().sendMessage(LCBungeeAuth.getInstance().getMessagebyString("message.register.message"));
                    return;
                }
                if((jug.isAuthverify() || jug.isPremium()) && jug.isCaptcha()){
                    e.setTarget(AuthManager.getLobbyServer());
                    if (e.getPlayer().getPendingConnection().isOnlineMode()) {
                        jug.setAuthverify(true);
                        e.getPlayer().sendMessage(LCBungeeAuth.getInstance().getMessagebyString("message.premium.sucess"));
                        return;
                    }
                    e.getPlayer().sendMessage(LCBungeeAuth.getInstance().getMessagebyString("message.login.sucess"));
                } else if (!jug.isCaptcha()){
                    e.getPlayer().sendMessage(Util.color("&aCompleta el captcha."));

                }else {
                    e.getPlayer().sendMessage(LCBungeeAuth.getInstance().getMessagebyString("message.login.message"));

                }
            }



    }

    @EventHandler
    public void onKick(ServerKickEvent e) {
        if (e.getKickedFrom() == AuthManager.getLobbyServer() || e.getKickedFrom() == AuthManager.getAuthServer()) {
            e.setKickReasonComponent(e.getKickReasonComponent());
            e.getPlayer().disconnect(e.getKickReasonComponent());
        }
        if (e.getKickReason().toLowerCase().equalsIgnoreCase(LCBungeeAuth.getInstance().getMessagebyString("message.premium.kick"))) {
            String name = e.getPlayer().getName().toLowerCase();
            if (LCBungeeAuth.premiumPlayers.contains(name)) { LCBungeeAuth.premiumPlayers.remove(name); }
            else { LCBungeeAuth.premiumPlayers.add(name); }
                e.getPlayer().disconnect(ChatColor.translateAlternateColorCodes('&', e.getKickReason()));
         
        } else if(e.getKickReason().toLowerCase().equalsIgnoreCase(LCBungeeAuth.getInstance().getMessagebyString("message.login.kick"))){
            e.getPlayer().disconnect(ChatColor.translateAlternateColorCodes('&', e.getKickReason()));
        }

    }


    @EventHandler
    public void onLeave(PlayerDisconnectEvent e) {
        AuthPlayer jug = AuthPlayer.getAuthPlayer(e.getPlayer().getName());
        //save last connection
        Date date = new Date();
        DateFormat hourdateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        jug.setLastconnection(hourdateFormat.format(date));
        Database.saveAllPlayerAuthData(jug);
        AuthPlayer.removeAuthPlayer(e.getPlayer().getName());
    }



}
