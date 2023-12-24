package lc.bungeeauth.commands;

import lc.bungeeauth.LCBungeeAuth;
import lc.bungeeauth.database.Database;
import lc.bungeeauth.managers.AuthPlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import javax.xml.crypto.Data;
import java.util.ArrayList;

public class Accounts extends Command {
    public Accounts(String name) {
        super(name, "auth.accounts", "cuentas");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            if(!sender.hasPermission("auth.accounts")){
                return;
            }
            AuthPlayer ap = AuthPlayer.getAuthPlayer(sender.getName());
            if (args.length <= 0) {
                ap.getProxyPlayer().sendMessage(LCBungeeAuth.getInstance().getMessagebyString("message.commands.accounts.use"));
                return;
            }
            String player = args[0];
            if(!Database.playerexits(player)){
                ap.getProxyPlayer().sendMessage(LCBungeeAuth.getInstance().getMessagebyString("message.commands.accounts.error"));

                return;
            }
            ArrayList<String> jugadores = Database.getPlayerbyIP(ap);
            int i = 1;
            ap.getProxyPlayer().sendMessage(LCBungeeAuth.getInstance().getMessagebyString("message.commands.accounts.header").replaceAll("%player%", args[0]));
            for (String p : jugadores) {
                i++;
                ap.getProxyPlayer().sendMessage(
                        LCBungeeAuth.getInstance().getMessagebyString("message.commands.accounts.message").replaceAll("%index%", Integer.toString(i))
                        .replaceAll("%account%", p).replaceAll("lastcon",Database.getLastConnection(p)));

            }
            ap.getProxyPlayer().sendMessage(LCBungeeAuth.getInstance().getMessagebyString("message.commands.accounts.footer"));


        }
    }
}
