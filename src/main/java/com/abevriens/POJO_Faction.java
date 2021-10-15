package com.abevriens;

import org.bukkit.Chunk;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class POJO_Faction {
    public String factionName;

    public List<Chunk> occupiedChunks = new ArrayList<>();

    public List<POJO_Player> players = new ArrayList<>();

    public List<String> playerJoinRequests = new ArrayList<>();

    public POJO_Player factionOwner;

    public JoinStatus joinStatus;

    public POJO_Vector fBlockLocation;

    public POJO_Faction() {
    }
}
