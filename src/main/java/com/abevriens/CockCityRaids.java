package com.abevriens;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class CockCityRaids extends JavaPlugin {
    public static CockCityRaids instance;

    public MongoDBHandler dbHandler;

    @Override
    public void onEnable() {
        instance = this;
        dbHandler = new MongoDBHandler();
        this.getCommand("startoutline").setExecutor(new StartBaseOutline());
        this.getCommand("factions").setExecutor(new FactionCommandHandler());

        dbHandler.connect("mongodb://localhost:27017/?readPreference=primary&ssl=false");
    }

    @Override
    public void onDisable() {

    }

}