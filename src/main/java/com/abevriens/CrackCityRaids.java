package com.abevriens;


import com.abevriens.discord.DiscordManager;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.util.Objects;

public class CrackCityRaids extends JavaPlugin {
    public static CrackCityRaids instance;

    public static MongoDBHandler dbHandler;
    public static PlayerManager playerManager;
    public static FactionManager factionManager;
    public static FactionCoreManager factionCoreManager;
    public static DiscordManager discordManager;
    public static ConfigurationManager configurationManager;

    @Override
    public void onEnable() {
        instance = this;
        dbHandler = new MongoDBHandler();
        playerManager = new PlayerManager();
        factionManager = new FactionManager();
        factionCoreManager = new FactionCoreManager();
        configurationManager = new ConfigurationManager();

        try {
            discordManager = new DiscordManager(configurationManager.getDiscordConfig().get("token").toString());
        } catch (LoginException e) {
            this.getLogger().warning("Couldn't connect to the Discord Bot!");
            CrackCityRaids.instance.getLogger().warning(ChatColor.RED + "Disabling plugin...");
            CrackCityRaids.instance.getPluginLoader().disablePlugin(CrackCityRaids.instance);
        }

        Objects.requireNonNull(this.getCommand("factions")).setExecutor(new FactionCommandHandler());
        Objects.requireNonNull(this.getCommand("factions")).setTabCompleter(new FactionCommandTabCompleter());
        getServer().getPluginManager().registerEvents(new BlockClickEventListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerChatListener(), this);

        dbHandler.connect("mongodb://localhost:27017/?readPreference=primary&ssl=false");
    }

    @Override
    public void onDisable() {
        dbHandler.disconnect();
    }

}