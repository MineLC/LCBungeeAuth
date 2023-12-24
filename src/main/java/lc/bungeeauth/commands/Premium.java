package lc.bungeeauth.commands;

import lc.bungeeauth.LCBungeeAuth;
import lc.bungeeauth.database.Database;
import lc.bungeeauth.managers.AuthManager;
import lc.bungeeauth.managers.AuthPlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class Premium extends Command {
    public Premium(String name) {
            super(name, "", "premium");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer){
            AuthPlayer ap = AuthPlayer.getAuthPlayer(sender.getName());
            if(!ap.isAuthverify()) {
                ap.getProxyPlayer().sendMessage(LCBungeeAuth.getInstance().getMessagebyString("message.logout.error"));
                return;
            }
            if(ap.isPremium()){
                ap.setPremium(false);
                LCBungeeAuth.premiumPlayers.remove(ap.getName());
                Database.saveAllPlayerAuthData(ap);
                ap.getProxyPlayer().sendMessage(LCBungeeAuth.getInstance().getMessagebyString("message.premium.disallow"));
                return;
            }
            ap.setPremium(true);
            LCBungeeAuth.premiumPlayers.add(ap.getName());
            Database.saveAllPlayerAuthData(ap);
            if(AuthManager.Premiumkick()){
                ap.getProxyPlayer().disconnect(LCBungeeAuth.getInstance().getMessagebyString("message.premium.kick"));
            }
            ap.getProxyPlayer().sendMessage(LCBungeeAuth.getInstance().getMessagebyString("message.premium.check"));

        }
    }
}
