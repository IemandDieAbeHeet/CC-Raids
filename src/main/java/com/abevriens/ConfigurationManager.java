package com.abevriens;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class ConfigurationManager {
    public FileConfiguration config = CrackCityRaids.instance.getConfig();

    public ConfigurationManager() {
        config.options().copyDefaults(true);
        CrackCityRaids.instance.saveConfig();
    }

    public HashMap<String, Object> getDiscordConfig() {
        return (HashMap<String, Object>) config.getConfigurationSection("discord").getValues(false);
    }
}
