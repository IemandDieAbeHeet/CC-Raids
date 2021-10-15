package com.abevriens.commands.factionblock;

import com.abevriens.CrackCityRaids;
import com.abevriens.FactionManager;
import com.abevriens.TextUtil;
import com.abevriens.commands.CommandContext;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;

import java.util.List;
import java.util.Objects;

public class Factions_CreateFactionBlock {
    public CommandContext commandContext;

    public Factions_CreateFactionBlock(CommandContext _commandContext) {
        commandContext = _commandContext;

        command_CreateFBlock();
    }

    private void command_CreateFBlock() {
        if(commandContext.cc_player.faction.factionName.equals(FactionManager.emptyFaction.factionName)) {
            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg("Je zit niet in een faction," +
                    " join er een met /factions join.");
            commandContext.player.spigot().sendMessage(errorMsg.create());
        } else if(commandContext.cc_player.faction.factionOwner.uuid.equals(commandContext.cc_player.uuid)) {
            ComponentBuilder errorMessage = TextUtil.GenerateErrorMsg("Je bent niet de owner van de faction." +
                    " Als je het echt graag wilt moet je aan " +
                    commandContext.cc_player.faction.factionOwner.displayName + " vragen of ze jou owner geven.");
            commandContext.player.spigot().sendMessage(errorMessage.create());
        } else if(!Objects.equals(commandContext.cc_player.faction.fBlockLocation,
                new Location(Bukkit.getWorld("world"), 0, 0, 0))) {
            ComponentBuilder errorMessage = TextUtil.GenerateErrorMsg("Je faction heeft al een faction blok" +
                    "geplaatst, verplaats je faction blok dan met /factions factionblock set ");
            commandContext.player.spigot().sendMessage(errorMessage.create());
        }

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
