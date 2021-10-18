package com.abevriens;

import org.bukkit.plugin.java.JavaPlugin;

public class CrackCityRaids extends JavaPlugin {
    public static CrackCityRaids instance;

    public MongoDBHandler dbHandler;
    public PlayerManager playerManager;
    public FactionManager factionManager;
    public FactionCoreManager factionCoreManager;

    @Override
    public void onEnable() {
        instance = this;
        dbHandler = new MongoDBHandler();
        playerManager = new PlayerManager();
        factionManager = new FactionManager();
        factionCoreManager = new FactionCoreManager();

        this.getCommand("factions").setExecutor(new FactionCommandHandler());
        this.getCommand("factions").setTabCompleter(new FactionCommandTabCompleter());
        getServer().getPluginManager().registerEvents(new BlockClickEventListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);

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