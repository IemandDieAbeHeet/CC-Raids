package com.abevriens;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class CC_Player {
    public String displayName;
    public String uuid;
    public Faction faction;
    public List<String> pendingRequests;
    public Location previousLocation = new Vector(0, 0, 0).toLocation(
            Objects.requireNonNull(Bukkit.getWorld("world")));
    public String discordId;
    public boolean isWithinFactionBounds = false;
    public boolean factionChatEnabled = false;
    public Instant lastFactionChange;

    public CC_Player(String _displayName, String _uuid, Faction _faction, List<String> _pendingRequests, String _discordId) {
        displayName = _displayName;
        uuid = _uuid;
        faction = _faction;
        pendingRequests = _pendingRequests;
        discordId = _discordId;
        lastFactionChange = Instant.now().minusSeconds(10);
    }

    /**
     * Update database after using!
     */
    public void deleteRequests() {
        for(String request : pendingRequests) {
            Faction requestFaction = CrackCityRaids.factionManager.getFaction(request);
            requestFaction.playerJoinRequests.remove(uuid);
        }
        pendingRequests.clear();
    }
}
