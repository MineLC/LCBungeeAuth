package lc.bungeeauth.commands;

import lc.bungeeauth.LCBungeeAuth;
import lc.bungeeauth.managers.AuthPlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class Captcha extends Command {
    public Captcha(String name) {
        super(name, "", "captcha");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof ProxiedPlayer)){
            if(args.length >=1){
                AuthPlayer ap = AuthPlayer.getAuthPlayer(args[0]);
                if(LCBungeeAuth.getInstance().getProxy().getPlayer(ap.getName()).isConnected()){
                    ap.setCaptcha(true);
                }
            }
        }
    }
}
