package com.abevriens;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class Faction {
    public String factionName;

    public List<Chunk> occupiedChunks;

    public List<CC_Player> players;

    public UUID ownerUUID;

    public Faction(UUID _ownerUUID, String _factionName) {
        ownerUUID = _ownerUUID;
        factionName = _factionName;
    }
}
