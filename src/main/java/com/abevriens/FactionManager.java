package com.abevriens;

import org.bson.codecs.configuration.CodecConfigurationException;
import org.bukkit.*;
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
                JoinStatus.OPEN, null, null, 0, 0, null);

    public static Faction POJOToFaction(@NotNull POJO_Faction pojo_faction) {
        Faction faction;

        List<CC_Player> cc_players = new ArrayList<>();

        for(POJO_Player pojo_player : pojo_faction.players) {
            cc_players.add(PlayerManager.POJOToCC(pojo_player));
        }

        Location locationFromPojo = new Vector(pojo_faction.factionCore.locationVector.x,
                pojo_faction.factionCore.locationVector.y,
                pojo_faction.factionCore.locationVector.z).toLocation(Objects.requireNonNull(Bukkit.getWorld("world")));

        FactionCore fCoreFromPOJO = new FactionCore(locationFromPojo, pojo_faction.factionName);

        List<Location> occupiedLocationsFromPojo = new ArrayList<>();

        for(POJO_Vector pojo_vector : pojo_faction.occupiedLocations) {
            occupiedLocationsFromPojo.add(pojo_vector.pojoVectorToLocation());
        }

        faction = new Faction(
                pojo_faction.factionOwner,
                pojo_faction.factionName,
                cc_players,
                pojo_faction.joinStatus,
                pojo_faction.playerJoinRequests,
                fCoreFromPOJO,
                pojo_faction.xSize,
                pojo_faction.ySize,
                occupiedLocationsFromPojo
        );

        return faction;
    }

    public static POJO_Faction FactionToPOJO(Faction faction) {
        POJO_Faction pojo_faction = new POJO_Faction();

        List<POJO_Player> pojo_players = new ArrayList<>();

        for(CC_Player cc_player : faction.players) {
            pojo_players.add(PlayerManager.CCToPOJO(cc_player));
        }

        POJO_FactionCore pojo_factionCore = new POJO_FactionCore();
        pojo_factionCore.locationVector = new POJO_Vector(faction.factionCore.blockLocation.toVector());
        pojo_factionCore.factionName = faction.factionName;

        List<POJO_Vector> pojo_locations = new ArrayList<>();
        for(Location location : faction.occupiedBlocks) {
            pojo_locations.add(new POJO_Vector(location.toVector()));
        }

        pojo_faction.factionName = faction.factionName;
        pojo_faction.factionOwner = faction.factionOwner;
        pojo_faction.players = pojo_players;
        pojo_faction.joinStatus = faction.joinStatus;
        pojo_faction.playerJoinRequests = faction.playerJoinRequests;
        pojo_faction.factionCore = pojo_factionCore;
        pojo_faction.xSize = faction.xSize;
        pojo_faction.ySize = faction.ySize;
        pojo_faction.occupiedLocations = pojo_locations;

        return pojo_faction;
    }
}
