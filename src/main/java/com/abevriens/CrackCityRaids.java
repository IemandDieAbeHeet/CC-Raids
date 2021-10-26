package com.abevriens;


import com.abevriens.jda.JDAManager;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;

public class CrackCityRaids extends JavaPlugin {
    public static CrackCityRaids instance;

    public MongoDBHandler dbHandler;
    public PlayerManager playerManager;
    public FactionManager factionManager;
    public FactionCoreManager factionCoreManager;
    public JDAManager jdaManager;
    public ConfigurationManager configurationManager;

    @Override
    public void onEnable() {
        instance = this;
        dbHandler = new MongoDBHandler();
        playerManager = new PlayerManager();
        factionManager = new FactionManager();
        factionCoreManager = new FactionCoreManager();
        configurationManager = new ConfigurationManager();

        try {
            jdaManager = new JDAManager(configurationManager.getDiscordConfig().get("token").toString());
        } catch (LoginException e) {
            this.getLogger().warning("Couldn't connect to the Discord Bot!");
            CrackCityRaids.instance.getLogger().warning(ChatColor.RED + "Disabling plugin...");
            CrackCityRaids.instance.getPluginLoader().disablePlugin(CrackCityRaids.instance);
        }

        this.getCommand("factions").setExecutor(new FactionCommandHandler());
        this.getCommand("factions").setTabCompleter(new FactionCommandTabCompleter());
        getServer().getPluginManager().registerEvents(new BlockClickEventListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(), this);

        dbHandler.connect("mongodb://localhost:27017/?readPreference=primary&ssl=false");

        //Always load factions before players. LoadPlayers converts POJO_Players
        //to CC_Players which requires factions to be loaded
        factionManager.LoadFactions();
        playerManager.LoadPlayers();
        factionCoreManager.LoadFactionCores();
    }

    @Override
    public void onDisable() {
        dbHandler.disconnect();
    }

}