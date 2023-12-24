package lc.bungeeauth.commands;

import lc.bungeeauth.LCBungeeAuth;
import lc.bungeeauth.database.Database;
import lc.bungeeauth.managers.AuthPlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class AuthInformation extends Command {
    public AuthInformation(String name) {
        super(name, "auth.info", "info" , "playerinfo");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            if (!sender.hasPermission("auth.info")) {
                return;
            }
            AuthPlayer ap = AuthPlayer.getAuthPlayer(sender.getName());
            if (args.length <= 0) {
                ap.getProxyPlayer().sendMessage(LCBungeeAuth.getInstance().getMessagebyString("message.commands.info.use"));
                return;
            }
            String player = args[0];
            if (!Database.playerexits(player)) {
                ap.getProxyPlayer().sendMessage(LCBungeeAuth.getInstance().getMessagebyString("message.commands.accounts.error"));

                return;
            }
            AuthPlayer jug = AuthPlayer.getAuthPlayer(player);

            Database.loadAuthPlayerInfo(jug.getName());
            ap.getProxyPlayer().sendMessage(
                    LCBungeeAuth.getInstance().getMessagebyString("message.commands.info.message").replaceAll("%player%", player)
                            .replaceAll("%ip%", jug.getLastip()).replaceAll(" %lastcon%", jug.getLastconnection()
                            .replaceAll("%accounts%", Integer.toString(Database.getPlayerbyIP(jug).size()))
                            .replaceAll("%premium%", Boolean.toString(jug.isPremium()))
                    ));
        }
    }
}
