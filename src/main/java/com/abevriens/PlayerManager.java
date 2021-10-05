package com.abevriens;

import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bson.codecs.configuration.CodecConfigurationException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import static com.mongodb.client.model.Filters.eq;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {
    private HashMap<OfflinePlayer, CC_Player> CCPlayerHashMap = new HashMap<>();
    private HashMap<OfflinePlayer, POJO_Player> POJOPlayerHashMap = new HashMap<>();

    public void LoadPlayers() {
        MongoCursor<POJO_Player> cursor = CockCityRaids.instance.dbHandler.playerCollection.find().iterator();
        while (cursor.hasNext()) {
            POJO_Player pojo_player = cursor.next();
            CC_Player cc_player = POJOToCC(pojo_player);
            OfflinePlayer player = POJOToPlayer(pojo_player);

            POJOPlayerHashMap.put(player, pojo_player);
            CCPlayerHashMap.put(player, cc_player);
        }
    }

    public boolean playerExists(Player player) {
        if (getPOJOPlayer(player) == null) {
            return false;
        } else {
            return true;
        }
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

    public CC_Player getCCPlayer(Player player) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
        return CCPlayerHashMap.get(offlinePlayer);
    }

    public void setPlayerFaction(Player player, Faction faction) {
        POJO_Player pojo_player = getPOJOPlayer(player);
        CC_Player cc_player = getCCPlayer(player);

        cc_player.faction = faction;
        pojo_player.factionName = faction.factionName;

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
        CCPlayerHashMap.put(offlinePlayer, cc_player);
        POJOPlayerHashMap.put(offlinePlayer, pojo_player);
        CockCityRaids.instance.dbHandler.updatePlayer(pojo_player);
    }

    public static OfflinePlayer POJOToPlayer(POJO_Player pojo_player) {
        return Bukkit.getOfflinePlayer(UUID.fromString(pojo_player.uuid));
    }

    public static CC_Player POJOToCC(POJO_Player pojo_player) {
        Faction faction = CockCityRaids.instance.factionManager.getFaction(pojo_player.factionName);
        CC_Player cc_player = new CC_Player(
            pojo_player.displayName,
            pojo_player.uuid,
            faction
        );

        return cc_player;
    }
}