package lc.bungeeauth.commands;

import lc.bungeeauth.LCBungeeAuth;
import lc.bungeeauth.database.Database;
import lc.bungeeauth.managers.AuthPlayer;
import lc.bungeecore.entidades.Jugador;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class UnPremium extends Command {
    public UnPremium(String name) {
        super(name, "auth.unpremium", "unpremium");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer){
            AuthPlayer ap = AuthPlayer.getAuthPlayer(sender.getName());
            if(!Jugador.getJugador((ProxiedPlayer) sender).isAdmin()){
                ap.getProxyPlayer().sendMessage(LCBungeeAuth.getInstance().getMessagebyString("message.nopermission"));
                return;
            }
            if(args.length <=0){
                ap.getProxyPlayer().sendMessage(LCBungeeAuth.getInstance().getMessagebyString("message.premium.arguments"));
                return;
            }

            if(!Database.playerexits(args[0])){
                ap.getProxyPlayer().sendMessage(LCBungeeAuth.getInstance().getMessagebyString("message.premium.disallow_other_error").replaceAll("%player%", args[0]));
                return;
            }
            AuthPlayer user = AuthPlayer.getAuthPlayer(args[0]);
            user.setPremium(false);
            Database.saveAllPlayerAuthData(user);
            ap.getProxyPlayer().sendMessage(LCBungeeAuth.getInstance().getMessagebyString("message.premium.disallow_other").replaceAll("%player%", args[0]));


        }
    }
}
