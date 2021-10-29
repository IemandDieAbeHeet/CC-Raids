package com.abevriens;

import java.util.ArrayList;
import java.util.List;

public class POJO_Player {
    public String uuid;
    public String displayName;
    public String factionName;
    public List<String> pendingRequests = new ArrayList<>();
    public String discordId;

    public POJO_Player() {
    }
}