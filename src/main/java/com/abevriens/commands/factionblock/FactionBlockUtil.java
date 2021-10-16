package com.abevriens.commands.factionblock;

import com.abevriens.CrackCityRaids;
import com.abevriens.FactionManager;
import com.abevriens.TextUtil;
import com.abevriens.commands.CommandContext;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;

import java.time.Instant;
import java.util.List;

public class FactionBlockUtil {
    public static void PlaceBlock(CommandContext commandContext) {
        List<Block> facingBlocks = commandContext.player.getLastTwoTargetBlocks(null,  5);

        if(facingBlocks.isEmpty()) {
            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg("Geen blok gevonden om het factions blok te plaatsen!");
            commandContext.player.spigot().sendMessage(errorMsg.create());
            return;
        } else if(facingBlocks.get(1).isEmpty()) {
            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg("Geen blok gevonden om het factions blok te plaatsen!");
            commandContext.player.spigot().sendMessage(errorMsg.create());
            return;
        }

        BlockFace face = facingBlocks.get(1).getFace(facingBlocks.get(0));

        if(face == null) {
            return;
        }

        Location check = facingBlocks.get(1).getLocation().add(face.getDirection());

        Block oldBlock = commandContext.cc_player.faction.fBlockLocation.getBlock();

        if(!check.getBlock().isEmpty()) {
            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg("Het blok waar je de factions block probeert te " +
                    "plaatsen is niet leeg!");
            commandContext.player.spigot().sendMessage(errorMsg.create());
        } else {
            Block block = check.getBlock();
            Material structureMat = Material.STRUCTURE_BLOCK;
            block.setType(structureMat);
            oldBlock.setType(Material.AIR);
            commandContext.cc_player.faction.fBlockLocation = block.getLocation();
            CrackCityRaids.instance.dbHandler.updateFaction(
                    FactionManager.FactionToPOJO(commandContext.cc_player.faction));
            BlockData data = block.getBlockData();
            commandContext.player.sendBlockChange(block.getLocation(), data);
            commandContext.cc_player.faction.lastfBlockChange = Instant.now();
        }
    }
}
