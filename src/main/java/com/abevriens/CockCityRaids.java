package com.abevriens;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class CockCityRaids extends JavaPlugin {
    public static CockCityRaids instance;

    private MongoDBHandler dbHandler;

    @Override
    public void onEnable() {
        instance = this;
        dbHandler = new MongoDBHandler();
        this.getCommand("startoutline").setExecutor(new StartBaseOutline());

        if(dbHandler.connect("mongodb://localhost:27017/?readPreference=primary&ssl=false")) {
            Faction newFaction = new Faction((Player) Bukkit.getOfflinePlayer(UUID.randomUUID()), "Poep");
            dbHandler.insertFaction(newFaction);
        }
    }

    @Override
    public void onDisable() {

    }

}