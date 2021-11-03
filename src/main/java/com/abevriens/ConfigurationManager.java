package com.abevriens;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ConfigurationManager {
    public FileConfiguration config = CrackCityRaids.instance.getConfig();

    public ConfigurationManager() {
        config.options().copyDefaults(true);
        CrackCityRaids.instance.saveConfig();
    }

    public HashMap<String, Object> getDiscordConfig() {
        return (HashMap<String, Object>) Objects.requireNonNull(config.getConfigurationSection("discord")).getValues(false);
    }
}
