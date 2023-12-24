package lc.bungeeauth.commands;

import lc.bungeeauth.LCBungeeAuth;
import lc.bungeeauth.database.Database;
import lc.bungeeauth.managers.AuthPlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class Logout extends Command {
    public Logout(String name) {
        super(name, "", "logout");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer){
            ProxiedPlayer pp = (ProxiedPlayer) sender;
            AuthPlayer ap = AuthPlayer.getAuthPlayer(sender.getName());
            if(ap.isAuthverify()){
                ap.setPremium(false);
                ap.setLastip(ap.getIp() + ".");
                Database.saveAllPlayerAuthData(ap);
                pp.disconnect( LCBungeeAuth.getInstance().getMessagebyString("message.logout.sucess"));
            } else {
                pp.sendMessage(LCBungeeAuth.getInstance().getMessagebyString("message.logout.error"));
            }

        }
    }
}
