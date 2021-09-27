package com.abevriens;

import org.bukkit.Chunk;

import java.util.ArrayList;
import java.util.List;

public class POJO_Faction {
    public String factionName;

    public List<Chunk> occupiedChunks = new ArrayList<>();

    public List<POJO_Player> players = new ArrayList<>();

    public POJO_Player factionOwner;

    public POJO_Faction() {
    }
}
