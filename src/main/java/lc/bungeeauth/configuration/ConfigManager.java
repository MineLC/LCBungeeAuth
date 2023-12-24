package lc.bungeeauth.configuration;

import lc.bungeeauth.LCBungeeAuth;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;

public class ConfigManager {
    Configuration configurationConfig;
    Configuration configurationMessage;

    public Configuration getConfig() {
        if (this.configurationConfig == null)
            reloadConfig();
        return this.configurationConfig;
    }

    public void reloadConfig() {
        registerConfig();
        try {
            this.configurationConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(LCBungeeAuth.getInstance().getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(this.configurationConfig, new File(LCBungeeAuth.getInstance().getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registerConfig() {
        File file = new File(LCBungeeAuth.getInstance().getDataFolder(), "config.yml");
        if (!file.exists())
            try (InputStream in = LCBungeeAuth.getInstance().getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
                this.configurationConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(LCBungeeAuth.getInstance().getDataFolder(), "config.yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
    }



    /// message

    public Configuration getMessage() {
        if (this.configurationMessage == null)
            reloadMessage();
        return this.configurationMessage;
    }

    public void reloadMessage() {
        registerMessage();
        try {
            this.configurationMessage = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(LCBungeeAuth.getInstance().getDataFolder(), "message.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveMessage() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(this.configurationMessage, new File(LCBungeeAuth.getInstance().getDataFolder(), "message.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registerMessage() {
        File file = new File(LCBungeeAuth.getInstance().getDataFolder(), "message.yml");
        if (!file.exists())
            try (InputStream in = LCBungeeAuth.getInstance().getResourceAsStream("message.yml")) {
                Files.copy(in, file.toPath(), new CopyOption[0]);
                this.configurationMessage = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(LCBungeeAuth.getInstance().getDataFolder(), "message.yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

}
