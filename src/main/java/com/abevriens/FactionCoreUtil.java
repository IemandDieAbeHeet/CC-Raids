package com.abevriens;

import com.abevriens.commands.CommandContext;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;

import java.time.Instant;
import java.util.List;

public class FactionCoreUtil {
    public static void PlaceCore(CommandContext commandContext) {
        List<Block> facingBlocks = commandContext.player.getLastTwoTargetBlocks(null,  5);

        if(facingBlocks.isEmpty()) {
            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg("Geen blok gevonden om de factions core te plaatsen!");
            commandContext.player.spigot().sendMessage(errorMsg.create());
            return;
        } else if(facingBlocks.get(1).isEmpty()) {
            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg("Geen blok gevonden om de factions core te plaatsen!");
            commandContext.player.spigot().sendMessage(errorMsg.create());
            return;
        }

        BlockFace face = facingBlocks.get(1).getFace(facingBlocks.get(0));

        if(face == null) {
            return;
        }

        Location check = facingBlocks.get(1).getLocation().add(face.getDirection());

        Block oldBlock = commandContext.cc_player.faction.factionCore.blockLocation.getBlock();

        if(!check.getBlock().isEmpty()) {
            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg("Het blok waar je de factions core probeert te " +
                    "plaatsen is niet leeg!");
            commandContext.player.spigot().sendMessage(errorMsg.create());
        } else {
            Block block = check.getBlock();
            Material structureMat = Material.STRUCTURE_BLOCK;
            block.setType(structureMat);
            oldBlock.setType(Material.AIR);
            commandContext.cc_player.faction.factionCore.blockLocation = block.getLocation();
            CrackCityRaids.instance.factionCoreManager.updateFactionCoreLocation(commandContext.cc_player.faction.factionCore);
            CrackCityRaids.instance.dbHandler.updateFaction(
                    FactionManager.FactionToPOJO(commandContext.cc_player.faction));
            BlockData data = block.getBlockData();
            commandContext.player.sendBlockChange(block.getLocation(), data);
            commandContext.cc_player.faction.factionCore.lastChange = Instant.now();
        }
    }

    public static void RemoveCore(CommandContext commandContext) {
        FactionCore factionCore = commandContext.cc_player.faction.factionCore;

        if(factionCore == null) {
            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg("Je faction heeft geen faction core geplaatst!");
            commandContext.player.spigot().sendMessage(errorMsg.create());
            return;
        }

        Block oldBlock = commandContext.cc_player.faction.factionCore.blockLocation.getBlock();

        oldBlock.setType(Material.AIR);
        commandContext.cc_player.faction.factionCore.blockLocation =
                GenerateEmptyFactionCore(commandContext.cc_player.faction.factionName).blockLocation;
        CrackCityRaids.instance.factionCoreManager.updateFactionCoreLocation(commandContext.cc_player.faction.factionCore);
        CrackCityRaids.instance.dbHandler.updateFaction(
                FactionManager.FactionToPOJO(commandContext.cc_player.faction));
        BlockData data = oldBlock.getBlockData();
        commandContext.player.sendBlockChange(oldBlock.getLocation(), data);
        commandContext.cc_player.faction.factionCore.lastChange = Instant.now();
    }

    public static FactionCore GenerateEmptyFactionCore(String factionName) {
        return new FactionCore(new Location(Bukkit.getWorld("world"), 0, 0, 0), factionName);
    }
}
