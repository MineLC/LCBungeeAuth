package lc.bungeeauth.commands;

import lc.bungeeauth.LCBungeeAuth;
import lc.bungeeauth.database.Database;
import lc.bungeeauth.managers.AuthManager;
import lc.bungeeauth.managers.AuthPlayer;
import lc.bungeeauth.managers.SecureUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import javax.xml.crypto.Data;


public class Register extends Command {
    public Register(String name) {
        super(name, "", "r");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer){
            AuthPlayer ap = AuthPlayer.getAuthPlayer(sender.getName());
            if(Database.getPlayerbyIP(ap).size() > AuthManager.getMaxperip()){
                ap.getProxyPlayer().sendMessage(LCBungeeAuth.getInstance().getMessagebyString("message.register.max_acount"));
                return;
            }
            if(!ap.isCaptcha()){
                ap.getProxyPlayer().sendMessage( LCBungeeAuth.getInstance().getMessagebyString("message.capchat_message"));
                return;
            }
            if(ap.isRegister()){
                ap.getProxyPlayer().sendMessage(LCBungeeAuth.getInstance().getMessagebyString("message.login.message"));
                return;
            }
            if(args.length <=1){
                ap.getProxyPlayer().sendMessage(LCBungeeAuth.getInstance().getMessagebyString("message.register.arguments"));
            } else {
                if(!args[0].equals(args[1])){
                    ap.getProxyPlayer().sendMessage( LCBungeeAuth.getInstance().getMessagebyString("message.register.error"));
                    return;
                }
                if(args[0].length() < AuthManager.getMinpasslenght()){
                    ap.getProxyPlayer().sendMessage(LCBungeeAuth.getInstance().getMessagebyString("message.register.shortpass"));
                    return;
                }
                    ap.setPassword(SecureUtils.encriptPassword(args[0]));
                    ap.setRegister(true);
                    ap.setAuthverify(true);
                    ap.getProxyPlayer().connect(AuthManager.getLobbyServer());
                    Database.saveAllPlayerAuthData(ap);
                    ap.getProxyPlayer().sendMessage(LCBungeeAuth.getInstance().getMessagebyString("message.register.sucess"));

            }

         }
    }
}
