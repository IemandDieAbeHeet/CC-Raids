package com.abevriens;

import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.UUID;

public class Faction {
    public String factionName;

    public List<Chunk> occupiedChunks;

    public List<CC_Player> players;

    public List<String> playerJoinRequests;

    public POJO_Player factionOwner;

    public JoinStatus joinStatus;

    public Location fBlockLocation;

    public Faction(POJO_Player _factionOwner, String _factionName, List<CC_Player> _players,
                   List<Chunk> _occupiedChunks, JoinStatus _joinStatus, List<String> _playerJoinRequests,
                    Location _fBlockLocation) {
        factionOwner = _factionOwner;
        factionName = _factionName;
        players = _players;
        occupiedChunks = _occupiedChunks;
        joinStatus = _joinStatus;
        playerJoinRequests = _playerJoinRequests;
        fBlockLocation = _fBlockLocation;
    }

    public boolean isFull() {
        return players.size() >= 3;
    }

    public boolean isJoinable() {
        return !isFull() && joinStatus != JoinStatus.CLOSED;
    }

    public void sendMessageToPlayers(ComponentBuilder message) {
        for(CC_Player cc_player : players) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(cc_player.uuid));
            if(offlinePlayer.isOnline()) {
                offlinePlayer.getPlayer().spigot().sendMessage(message.create());
            }
        }
    }
}
