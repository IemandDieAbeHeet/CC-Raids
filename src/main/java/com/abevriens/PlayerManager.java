package com.abevriens;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerManager {
    private HashMap<OfflinePlayer, CC_Player> CCPlayerHashMap = new HashMap<>();
    private HashMap<OfflinePlayer, POJO_Player> POJOPlayerHashMap = new HashMap<>();

    public void LoadPlayers() {
        for (POJO_Player pojo_player : CrackCityRaids.instance.dbHandler.playerCollection.find()) {
            if (!CrackCityRaids.instance.factionManager.factionNameList.contains(pojo_player.factionName)) {
                pojo_player.factionName = FactionManager.emptyFaction.factionName;
            }
            CrackCityRaids.instance.dbHandler.updatePlayer(pojo_player);
            CC_Player cc_player = POJOToCC(pojo_player);
            OfflinePlayer player = POJOToPlayer(pojo_player);

            POJOPlayerHashMap.put(player, pojo_player);
            CCPlayerHashMap.put(player, cc_player);
        }
    }

    public boolean playerExists(Player player) {
        return getPOJOPlayer(player) != null;
    }

    public void addPlayer(Player player, POJO_Player pojo_player, CC_Player cc_player) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
        CCPlayerHashMap.put(offlinePlayer, cc_player);
        POJOPlayerHashMap.put(offlinePlayer, pojo_player);
    }

    public POJO_Player getPOJOPlayer(Player player) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
        return POJOPlayerHashMap.get(offlinePlayer);
    }

    public POJO_Player getPOJOPlayer(OfflinePlayer offlinePlayer) {
        return POJOPlayerHashMap.get(offlinePlayer);
    }

    public CC_Player getCCPlayer(Player player) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
        return CCPlayerHashMap.get(offlinePlayer);
    }

    public CC_Player getCCPlayer(OfflinePlayer offlinePlayer) {
        return CCPlayerHashMap.get(offlinePlayer);
    }

    public void setPlayerFaction(OfflinePlayer offlinePlayer, Faction faction) {
        POJO_Player pojo_player = getPOJOPlayer(offlinePlayer);
        CC_Player cc_player = getCCPlayer(offlinePlayer);

        if(faction.factionName.equals(FactionManager.emptyFaction.factionName)) {
            cc_player.faction.players.remove(cc_player);
            CrackCityRaids.instance.dbHandler.updateFaction(FactionManager.FactionToPOJO(cc_player.faction));
        } else {
            faction.players.add(cc_player);
            CrackCityRaids.instance.dbHandler.updateFaction(FactionManager.FactionToPOJO(faction));
        }

        cc_player.faction = faction;
        pojo_player.factionName = faction.factionName;

        CCPlayerHashMap.put(offlinePlayer, cc_player);
        POJOPlayerHashMap.put(offlinePlayer, pojo_player);
        CrackCityRaids.instance.dbHandler.updatePlayer(pojo_player);
    }

    public static OfflinePlayer POJOToPlayer(POJO_Player pojo_player) {
        return Bukkit.getOfflinePlayer(UUID.fromString(pojo_player.uuid));
    }

    public static CC_Player POJOToCC(POJO_Player pojo_player) {
        Faction faction = CrackCityRaids.instance.factionManager.getFaction(pojo_player.factionName);

        return new CC_Player(
            pojo_player.displayName,
            pojo_player.uuid,
            faction
        );
    }

    public static POJO_Player CCToPOJO(CC_Player cc_player) {
        POJO_Player pojo_player = new POJO_Player();

        pojo_player.displayName = cc_player.displayName;
        pojo_player.factionName = cc_player.faction.factionName;
        pojo_player.uuid = cc_player.uuid;

        return pojo_player;
    }
}