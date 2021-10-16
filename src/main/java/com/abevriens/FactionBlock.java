package com.abevriens;

import org.bukkit.Location;

import java.time.Instant;

public class FactionBlock {
    public Location blockLocation;
    public String factionName;
    public Instant lastChange;

    public FactionBlock(Location _blockLocation, String _factionName) {
        blockLocation = _blockLocation;
        factionName = _factionName;
        lastChange = Instant.now().minusSeconds(10);
    }
}
