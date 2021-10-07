package com.abevriens;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class CockCityRaids extends JavaPlugin {
    public static CockCityRaids instance;

    public MongoDBHandler dbHandler;

    public PlayerManager playerManager;

    public FactionManager factionManager;

    @Override
    public void onEnable() {
        instance = this;
        dbHandler = new MongoDBHandler();
        playerManager = new PlayerManager();
        factionManager = new FactionManager();

        this.getCommand("factions").setExecutor(new FactionCommandHandler());
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);

        dbHandler.connect("mongodb://localhost:27017/?readPreference=primary&ssl=false");

        //Always load factions before players. LoadPlayers converts POJO_Players
        //to CC_Players which requires factions to be loaded
        factionManager.LoadFactions();
        playerManager.LoadPlayers();
    }

    @Override
    public void onDisable() {
        dbHandler.disconnect();
    }

}