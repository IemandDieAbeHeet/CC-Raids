package com.abevriens;

import org.bson.codecs.configuration.CodecConfigurationException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FactionManager {
    public List<Faction> factionList = new ArrayList<>();
    public List<String> factionNameList = new ArrayList<>();

    public void LoadFactions() {
        try {
            Iterable<POJO_Faction> factions = CrackCityRaids.instance.dbHandler.factionCollection.find();

            for (POJO_Faction faction : factions) {
                factionList.add(POJOToFaction(faction));
                factionNameList.add(faction.factionName);
            }
        } catch(CodecConfigurationException e) {
            CrackCityRaids.instance.getLogger().info(e.getMessage());
            CrackCityRaids.instance.getLogger().info(ChatColor.RED + "Couldn't load factions from database, a database entry might be corrupted.");
        }
    }

    public Faction getFaction(String factionName) {
        for(Faction faction : factionList) {
            if(faction.factionName.equals(factionName)) {
                return faction;
            }
        }
        return emptyFaction;
    }

    public static Faction emptyFaction = new Faction(null, "None", null,
            null, JoinStatus.OPEN, null, null);

    public static Faction POJOToFaction(@NotNull POJO_Faction pojo_faction) {
        Faction faction;

        List<CC_Player> cc_players = new ArrayList<>();

        for(POJO_Player pojo_player : pojo_faction.players) {
            cc_players.add(PlayerManager.POJOToCC(pojo_player));
        }

        Location fBlockLocationFromPOJO = new Vector(pojo_faction.fBlockLocation.x, pojo_faction.fBlockLocation.y,
                pojo_faction.fBlockLocation.z).toLocation(Objects.requireNonNull(Bukkit.getWorld("world")));

        faction = new Faction(
                pojo_faction.factionOwner,
                pojo_faction.factionName,
                cc_players,
                pojo_faction.occupiedChunks,
                pojo_faction.joinStatus,
                pojo_faction.playerJoinRequests,
                fBlockLocationFromPOJO
        );

        return faction;
    }

    public static POJO_Faction FactionToPOJO(Faction faction) {
        POJO_Faction pojo_faction = new POJO_Faction();

        List<POJO_Player> pojo_players = new ArrayList<>();

        for(CC_Player cc_player : faction.players) {
            pojo_players.add(PlayerManager.CCToPOJO(cc_player));
        }

        pojo_faction.factionName = faction.factionName;
        pojo_faction.factionOwner = faction.factionOwner;
        pojo_faction.occupiedChunks = faction.occupiedChunks;
        pojo_faction.players = pojo_players;
        pojo_faction.joinStatus = faction.joinStatus;
        pojo_faction.playerJoinRequests = faction.playerJoinRequests;
        pojo_faction.fBlockLocation = new POJO_Vector(faction.fBlockLocation.toVector());

        return pojo_faction;
    }
}
