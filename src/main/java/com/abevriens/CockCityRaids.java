package com.abevriens;

import org.bukkit.plugin.java.JavaPlugin;

public class CockCityRaids extends JavaPlugin {
    MongoDBHandler dbHandler;

    @Override
    public void onEnable() {
        dbHandler = new MongoDBHandler();
        this.getCommand("startoutline").setExecutor(new StartBaseOutline());
        dbHandler.connect("mongodb://localhost:27017/?readPreference=primary&ssl=false");
        for(int i = 0; i < 100; i++) {
            getLogger().info(dbHandler.poep);
        }
    }

    @Override
    public void onDisable() {

    }

}