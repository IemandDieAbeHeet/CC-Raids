package com.abevriens;

import com.mongodb.client.MongoCollection;
import org.bson.codecs.configuration.CodecConfigurationException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FactionManager {
    public List<Faction> factionList = new ArrayList<>();
    public HashMap<Faction, POJO_Faction> factionHashMap = new HashMap<>();

    public void LoadFactions() {
        try {
            Iterable<POJO_Faction> factions = CockCityRaids.instance.dbHandler.factionCollection.find();

            for (POJO_Faction faction : factions) {
                factionList.add(POJOToFaction(faction));
            }
        } catch(CodecConfigurationException e) {
            CockCityRaids.instance.getLogger().info(e.getMessage());
            CockCityRaids.instance.getLogger().info(ChatColor.RED + "Couldn't load factions from database, a database entry might be corrupted.");
        }
    }

    public static Faction emptyFaction = new Faction(null, "None", null, null);

    public static Faction POJOToFaction(POJO_Faction pojo_faction) {
        Faction faction;

        if(pojo_faction != null) {
            faction = new Faction(
                    pojo_faction.factionOwner,
                    pojo_faction.factionName,
                    pojo_faction.players,
                    pojo_faction.occupiedChunks
            );
        } else {
            faction = emptyFaction;
        }

        return faction;
    }

    public static POJO_Faction FactionToPOJO(Faction faction) {
        POJO_Faction pojo_faction = new POJO_Faction();

        pojo_faction.factionName = faction.factionName;
        pojo_faction.factionOwner = faction.factionOwner;
        pojo_faction.occupiedChunks = faction.occupiedChunks;
        pojo_faction.players = faction.players;

        return pojo_faction;
    }
}
