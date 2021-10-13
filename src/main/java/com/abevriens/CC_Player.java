package com.abevriens;

import java.util.List;

public class CC_Player {
    public String displayName;
    public String uuid;
    public Faction faction;
    public List<String> pendingRequests;

    public CC_Player(String _displayName, String _uuid, Faction _faction, List<String> _pendingRequests) {
        displayName = _displayName;
        uuid = _uuid;
        faction = _faction;
        pendingRequests = _pendingRequests;
    }

    /**
     * Update database after using!
     */
    public void deleteRequests() {
        pendingRequests.clear();
        for(String request : pendingRequests) {
            Faction requestFaction = CrackCityRaids.instance.factionManager.getFaction(request);
            requestFaction.playerJoinRequests.remove(uuid);
        }
    }
}
