package com.abevriens;

import com.abevriens.jda.DiscordIdEnum;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;

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

    public HashMap<String, String> discordIdMap;

    public POJO_Faction() {
    }
}
