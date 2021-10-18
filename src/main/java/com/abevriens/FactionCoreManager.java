package com.abevriens;

import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FactionCoreManager {
    public List<FactionCore> factionCoreList = new ArrayList<>();
    private final Map<Location, FactionCore> vectorFactionCoreHashMap = new HashMap<>();

    public void LoadFactionCores() {
        for(Faction faction : CrackCityRaids.instance.factionManager.factionList) {
            factionCoreList.add(faction.factionCore);
            vectorFactionCoreHashMap.put(faction.factionCore.blockLocation, faction.factionCore);
        }
    }

    public FactionCore getFactionCore(Block block) {
        return vectorFactionCoreHashMap.get(block.getLocation());
    }

    public void updateFactionCoreLocation(FactionCore factionCore) {
        vectorFactionCoreHashMap.put(factionCore.blockLocation, factionCore);
    }
}
