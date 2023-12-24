package lc.bungeeauth.commands;

import lc.bungeeauth.LCBungeeAuth;
import lc.bungeeauth.database.Database;
import lc.bungeeauth.managers.AuthManager;
import lc.bungeeauth.managers.AuthPlayer;
import lc.bungeeauth.managers.SecureUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class Login extends Command {
    public Login(String name) {
        super(name, "", "l");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer){
            ProxiedPlayer pp = (ProxiedPlayer) sender;
            AuthPlayer ap = AuthPlayer.getAuthPlayer(sender.getName());
            if(!ap.isRegister()) {
                ap.getProxyPlayer().sendMessage(LCBungeeAuth.getInstance().getMessagebyString("message.register.message")); return;
            }

            if(ap.isAuthverify()){
                   ap.getProxyPlayer().sendMessage(LCBungeeAuth.getInstance().getMessagebyString("message.login.already"));
                   return;
            }
            if(args.length <=0){
                ap.getProxyPlayer().sendMessage( LCBungeeAuth.getInstance().getMessagebyString("message.login.arguments"));
                return;
            }
            if(ap.isCaptcha()){

                 String loginpass = SecureUtils.encriptPassword(args[0]);
                    if(loginpass.equals(ap.getPassword())){
                        ap.setLastip(pp.getAddress().getHostString());
                        ap.setAuthverify(true);
                        Database.saveAllPlayerAuthData(ap);
                        if(pp.getServer().getInfo() == AuthManager.getAuthServer()){
                            pp.connect(AuthManager.getLobbyServer());
                            ap.getProxyPlayer().sendMessage(LCBungeeAuth.getInstance().getMessagebyString("message.login.sucess"));
                        }
                    } else {
                        ap.getProxyPlayer().sendMessage(LCBungeeAuth.getInstance().getMessagebyString("message.login.wrong"));
                        ap.setTries(ap.getTries() + 1);
                            if(ap.getTries() >= AuthManager.getMaxtry()){
                                ap.setTries(0);
                                ap.setCaptcha(false);
                                Database.saveAllPlayerAuthData(ap);
                                pp.disconnect(LCBungeeAuth.getInstance().getMessagebyString("message.login.kick"));
                            }
                           }

                   } else {
                       ap.getProxyPlayer().sendMessage( LCBungeeAuth.getInstance().getMessagebyString("message.capchat_message"));
                   }






        }
    }
}
