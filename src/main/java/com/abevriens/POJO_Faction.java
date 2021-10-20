package com.abevriens;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class POJO_Faction {
    public String factionName;

    public List<POJO_Player> players = new ArrayList<>();

    public List<String> playerJoinRequests = new ArrayList<>();

    public POJO_Player factionOwner;

    public JoinStatus joinStatus;

    public POJO_FactionCore factionCore;

    public int xSize;

    public int ySize;

    public List<POJO_Vector> occupiedLocations;

    public POJO_Faction() {
    }
}
