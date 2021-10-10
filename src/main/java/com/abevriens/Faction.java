package com.abevriens;

import org.bukkit.Chunk;

import java.util.List;

public class Faction {
    public String factionName;

    public List<Chunk> occupiedChunks;

    public List<CC_Player> players;

    public List<CC_Player> playerJoinRequests;

    public POJO_Player factionOwner;

    public JoinStatus joinStatus;

    public Faction(POJO_Player _factionOwner, String _factionName, List<CC_Player> _players, List<Chunk> _occupiedChunks, JoinStatus _joinStatus, List<CC_Player> _playerJoinRequests) {
        factionOwner = _factionOwner;
        factionName = _factionName;
        players = _players;
        occupiedChunks = _occupiedChunks;
        joinStatus = _joinStatus;
        playerJoinRequests = _playerJoinRequests;
    }

    public boolean isFull() {
        return players.size() > 3;
    }

    public boolean isJoinable() {
        return isFull() && joinStatus != JoinStatus.CLOSED;
    }
}
