package com.abevriens;

import org.bson.codecs.configuration.CodecConfigurationException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerManager {
    private HashMap<Player, CC_Player> CCPlayerHashMap = new HashMap<>();
    private Map<Player, POJO_Player> POJOplayerHashMap = new HashMap<>();

    public CC_Player getCCPlayer(Player player) {
        return CCPlayerHashMap.get(player);
    }

    public POJO_Player getPOJOPlayer(Player player) {
        return POJOplayerHashMap.get(player);
    }

    public void LoadPlayers() {
        try {
            Iterable<POJO_Player> players = CockCityRaids.instance.dbHandler.playerCollection.find();

            for (POJO_Player player : players) {
                CCPlayerHashMap.put(POJOToPlayer(player), POJOToCCPlayer(player));
            }
        } catch(CodecConfigurationException e) {
            CockCityRaids.instance.getLogger().info(e.getMessage());
            CockCityRaids.instance.getLogger().info(ChatColor.RED + "Couldn't load players from database, a database entry might be corrupted.");
        }
    }

    public static CC_Player POJOToCCPlayer(POJO_Player pojo_player) {
        CC_Player cc_player = new CC_Player(
                POJOToPlayer(pojo_player),
                FactionManager.POJOToFaction(pojo_player.faction)
        );
        return cc_player;
    }

    public static Player POJOToPlayer(POJO_Player pojo_player) {
        return Bukkit.getPlayer(pojo_player.uuid);
    }

    public static POJO_Player PlayerToPOJO(Player player, CC_Player cc_player) {
        POJO_Player pojo_player = new POJO_Player();

        pojo_player.faction = FactionManager.FactionToPOJO(cc_player.currentFaction);
        pojo_player.uuid = player.getUniqueId().toString();
        pojo_player.displayName = player.getDisplayName();

        return pojo_player;
    }

    public static POJO_Player PlayerToPOJO(Player player) {
        POJO_Player pojo_player = new POJO_Player();

        pojo_player.uuid = player.getUniqueId().toString();
        pojo_player.displayName = player.getDisplayName();

        return pojo_player;
    }
}