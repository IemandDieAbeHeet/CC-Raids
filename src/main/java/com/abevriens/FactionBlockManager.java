package com.abevriens;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FactionBlockManager {
    public List<FactionBlock> factionBlockList = new ArrayList<>();
    private Map<Location, FactionBlock> vectorFactionBlockHashMap = new HashMap<>();

    public void LoadFactionBlocks() {
        for(Faction faction : CrackCityRaids.instance.factionManager.factionList) {
            factionBlockList.add(faction.factionBlock);
            vectorFactionBlockHashMap.put(faction.factionBlock.blockLocation, faction.factionBlock);
        }
    }

    public FactionBlock getFactionBlock(Block block) {
        return vectorFactionBlockHashMap.get(block.getLocation());
    }

    public void updateFactionBlockLocation(FactionBlock factionBlock) {
        vectorFactionBlockHashMap.put(factionBlock.blockLocation, factionBlock);
    }
}
