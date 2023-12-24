package lc.bungeeauth.commands;

import lc.bungeeauth.LCBungeeAuth;
import lc.bungeeauth.database.Database;
import lc.bungeeauth.managers.AuthManager;
import lc.bungeeauth.managers.AuthPlayer;
import lc.bungeeauth.managers.SecureUtils;
import lc.bungeecore.entidades.Jugador;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class UnRegister extends Command {
    public UnRegister(String name) {
        super(name, "auth.unregister", "unregister");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer){
            ProxiedPlayer pp = (ProxiedPlayer) sender;
            if(!Jugador.getJugador(pp).isAdmin()){
                sender.sendMessage(LCBungeeAuth.getInstance().getMessagebyString("message.nopermission"));

                return;
            }
        }
        if(args.length <=0){
            sender.sendMessage(LCBungeeAuth.getInstance().getMessagebyString("message.unregister.arguments"));
            return;
        }
        String nick = args[0];
        if(!Database.playerexits(nick)){
            sender.sendMessage(LCBungeeAuth.getInstance().getMessagebyString("message.unregister.error"));
            return;
        }
        AuthPlayer ap = AuthPlayer.getAuthPlayer(nick);
        ap.setPremium(false);
        if(AuthManager.isUnregisteradmin()){
            String newpass= SecureUtils.generateRandomPassword();
            ap.setPassword(SecureUtils.encriptPassword(newpass));
            sender.sendMessage(LCBungeeAuth.getInstance().getMessagebyString("message.unregister.pass").replaceAll("%clave%", newpass));
        }else {
            ap.setRegister(false);
            ap.setPassword(null);
        }
        Database.saveAllPlayerAuthData(ap);
        sender.sendMessage(LCBungeeAuth.getInstance().getMessagebyString("message.unregister.sucess"));


    }
}
