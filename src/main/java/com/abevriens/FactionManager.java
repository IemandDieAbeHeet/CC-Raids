package com.abevriens;

import com.abevriens.jda.DiscordIdEnum;
import org.bson.codecs.configuration.CodecConfigurationException;
import org.bukkit.*;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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
                JoinStatus.OPEN, null, null, 0, 0, null,
            null, new RaidAlert(
                    "None", 360, false, new ArrayList<>(), new ArrayList<>()));

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

        EnumMap<DiscordIdEnum, String> hashMapToEnumMap = new EnumMap<>(DiscordIdEnum.class);
        for(String id : pojo_faction.discordIdMap.keySet()) {
            hashMapToEnumMap.put(DiscordIdEnum.valueOf(id), pojo_faction.discordIdMap.get(id));
        }

        RaidAlert raidAlert = new RaidAlert(pojo_faction.factionName, pojo_faction.pojo_raidAlert.countdown,
                pojo_faction.pojo_raidAlert.started, pojo_faction.pojo_raidAlert.enteredPlayerList,
                pojo_faction.pojo_raidAlert.enteredFactionList);

        faction = new Faction(
                pojo_faction.factionOwner,
                pojo_faction.factionName,
                cc_players,
                pojo_faction.joinStatus,
                pojo_faction.playerJoinRequests,
                fCoreFromPOJO,
                pojo_faction.xSize,
                pojo_faction.ySize,
                occupiedLocationsFromPojo,
                hashMapToEnumMap,
                raidAlert
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

        HashMap<String, String> enumMapToHashMap = new HashMap<>();
        for(DiscordIdEnum discordId : faction.discordIdMap.keySet()) {
            enumMapToHashMap.put(discordId.toString(), faction.discordIdMap.get(discordId));
        }

        POJO_RaidAlert pojo_raidAlert = new POJO_RaidAlert();
        pojo_raidAlert.alertedFactionName = faction.factionName;
        pojo_raidAlert.countdown = faction.raidAlert.countdown;
        pojo_raidAlert.started = faction.raidAlert.started;
        pojo_raidAlert.enteredFactionList = faction.raidAlert.enteredFactionList;
        pojo_raidAlert.enteredFactionList = faction.raidAlert.enteredPlayerList;

        pojo_faction.factionName = faction.factionName;
        pojo_faction.factionOwner = faction.factionOwner;
        pojo_faction.players = pojo_players;
        pojo_faction.joinStatus = faction.joinStatus;
        pojo_faction.playerJoinRequests = faction.playerJoinRequests;
        pojo_faction.factionCore = pojo_factionCore;
        pojo_faction.xSize = faction.xSize;
        pojo_faction.ySize = faction.ySize;
        pojo_faction.occupiedLocations = pojo_locations;
        pojo_faction.discordIdMap = enumMapToHashMap;
        pojo_faction.pojo_raidAlert = pojo_raidAlert;
        return pojo_faction;
    }

    public static String generateCountdownTimeString(int timeInMinutes) {
        int hours = (int) Math.floor((double) timeInMinutes / 60);
        int minutes = timeInMinutes - (hours * 60);
        String timerString;
        if(hours > 0) {
            if(minutes == 1) {
                timerString = hours + " uur, 1 minuut";
            } else {
                timerString = hours + " uur, " + minutes + " minuten";
            }
        } else {
            if(minutes < 1) {
                timerString = "1 minuut!";
            } else {
                timerString = minutes + " minuten";
            }
        }
        return timerString;
    }
}
