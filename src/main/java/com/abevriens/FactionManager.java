package com.abevriens;

import org.bson.codecs.configuration.CodecConfigurationException;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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

    public static Faction emptyFaction = new Faction(null, "None", null, null, JoinStatus.OPEN, null);

    public static Faction POJOToFaction(@NotNull POJO_Faction pojo_faction) {
        Faction faction;

        List<CC_Player> cc_players = new ArrayList<>();
        List<String> playerJoinRequests = new ArrayList<>();

        for(POJO_Player pojo_player : pojo_faction.players) {
            cc_players.add(PlayerManager.POJOToCC(pojo_player));
        }

        for(String uuid : pojo_faction.playerJoinRequests) {
            playerJoinRequests.add(uuid);
        }

        faction = new Faction(
                pojo_faction.factionOwner,
                pojo_faction.factionName,
                cc_players,
                pojo_faction.occupiedChunks,
                pojo_faction.joinStatus,
                playerJoinRequests
        );

        return faction;
    }

    public static POJO_Faction FactionToPOJO(Faction faction) {
        POJO_Faction pojo_faction = new POJO_Faction();

        List<POJO_Player> pojo_players = new ArrayList<>();
        List<String> playerJoinRequests = new ArrayList<>();

        for(CC_Player cc_player : faction.players) {
            pojo_players.add(PlayerManager.CCToPOJO(cc_player));
        }

        for(String uuid : faction.playerJoinRequests) {
            playerJoinRequests.add(uuid);
        }

        pojo_faction.factionName = faction.factionName;
        pojo_faction.factionOwner = faction.factionOwner;
        pojo_faction.occupiedChunks = faction.occupiedChunks;
        pojo_faction.players = pojo_players;
        pojo_faction.joinStatus = faction.joinStatus;
        pojo_faction.playerJoinRequests = playerJoinRequests;

        return pojo_faction;
    }
}
