package com.abevriens;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.util.List;

public class Faction {
    public String factionName;

    public List<Chunk> occupiedChunks;

    public List<CC_Player> players;

    public Player owner;

    public Faction(Player _owner, String _factionName) {
        owner = _owner;
        factionName = _factionName;
    }
}
