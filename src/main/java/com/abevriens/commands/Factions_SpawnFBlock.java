package com.abevriens.commands;

import com.abevriens.CrackCityRaids;
import com.abevriens.FactionManager;
import com.abevriens.TextUtil;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;

import java.util.List;

public class Factions_SpawnFBlock {
    public CommandContext commandContext;

    public Factions_SpawnFBlock(CommandContext _commandContext) {
        commandContext = _commandContext;

        command_CreateFBlock();
    }

    private void command_CreateFBlock() {
        List<Block> facingBlocks = commandContext.player.getLastTwoTargetBlocks(null,  5);


        if(facingBlocks.isEmpty()) {
            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg("Geen blok gevonden om het factions blok te plaatsen!");
            commandContext.player.spigot().sendMessage(errorMsg.create());
            return;
        }

        BlockFace face = facingBlocks.get(1).getFace(facingBlocks.get(0));

        if(face == null) {
            return;
        }

        Location check = facingBlocks.get(1).getLocation().add(face.getDirection());

        if(!check.getBlock().isEmpty()) {
            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg("Het blok waar je de factions block probeert te plaatsen is " +
                    "niet leeg!");
            commandContext.player.spigot().sendMessage(errorMsg.create());
        } else {
            Block block = check.getBlock();
            Material structureMat = Material.STRUCTURE_BLOCK;
            block.setType(structureMat);
            commandContext.cc_player.faction.fBlockLocation = block.getLocation();
            CrackCityRaids.instance.dbHandler.updateFaction(
                    FactionManager.FactionToPOJO(commandContext.cc_player.faction));
            BlockData data = block.getBlockData();
            commandContext.player.sendBlockChange(block.getLocation(), data);
        }
    }
}
