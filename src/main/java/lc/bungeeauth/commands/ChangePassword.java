package lc.bungeeauth.commands;

import lc.bungeeauth.LCBungeeAuth;
import lc.bungeeauth.database.Database;
import lc.bungeeauth.managers.AuthManager;
import lc.bungeeauth.managers.AuthPlayer;
import lc.bungeeauth.managers.SecureUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;


public class ChangePassword extends Command {
    public ChangePassword(String name) {
       super(name, "", "changepass");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer){
            AuthPlayer ap = AuthPlayer.getAuthPlayer(sender.getName());
            if(!ap.isAuthverify()) {
                ap.getProxyPlayer().sendMessage(LCBungeeAuth.getInstance().getMessagebyString("message.logout.error"));
                return;
            }
            if(args.length <=0){
                ap.getProxyPlayer().sendMessage(LCBungeeAuth.getInstance().getMessagebyString("message.changepass.arguments"));
                return;
            }
            if(AuthManager.isChangepasswordverify()){
                if(args.length <=1){
                    ap.getProxyPlayer().sendMessage(LCBungeeAuth.getInstance().getMessagebyString("message.changepass.arguments"));
                    return;
                }
                 if(SecureUtils.encriptPassword(args[0]).equals(ap.getPassword())){
                     if(args[1].length() <= AuthManager.getMinpasslenght()){
                         ap.getProxyPlayer().sendMessage(LCBungeeAuth.getInstance().getMessagebyString("message.changepass.short"));
                         return;
                     }
                     ap.setPassword(SecureUtils.encriptPassword(args[1]));
                     Database.saveAllPlayerAuthData(ap);
                     ap.getProxyPlayer().sendMessage(LCBungeeAuth.getInstance().getMessagebyString("message.changepass.sucess"));

                 }else { ap.getProxyPlayer().sendMessage(LCBungeeAuth.getInstance().getMessagebyString("message.changepass.wrong")); }

                return;
            }
            if(args[1].length() <= AuthManager.getMinpasslenght()){
                ap.getProxyPlayer().sendMessage(LCBungeeAuth.getInstance().getMessagebyString("message.changepass.short"));
                return;
            }
            ap.setPassword(args[1]);
            Database.saveAllPlayerAuthData(ap);
            ap.getProxyPlayer().sendMessage(LCBungeeAuth.getInstance().getMessagebyString("message.changepass.sucess"));




        }
    }
}
