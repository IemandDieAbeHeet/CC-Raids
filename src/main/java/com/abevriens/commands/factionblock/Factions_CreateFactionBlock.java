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
            return;
        } else if(!commandContext.cc_player.faction.factionOwner.uuid.equals(commandContext.cc_player.uuid)) {
            ComponentBuilder errorMessage = TextUtil.GenerateErrorMsg("Je bent niet de owner van de faction." +
                    " Als je het echt graag wilt moet je aan " +
                    commandContext.cc_player.faction.factionOwner.displayName + " vragen of ze jou owner geven.");
            commandContext.player.spigot().sendMessage(errorMessage.create());
            return;
        } else if(!Objects.equals(commandContext.cc_player.faction.factionBlock.blockLocation,
                new Location(Bukkit.getWorld("world"), 0, 0, 0))) {
            ComponentBuilder errorMessage = TextUtil.GenerateErrorMsg("Je faction heeft al een faction blok" +
                    "geplaatst, verplaats je faction blok dan met /factions factionblock set.");
            commandContext.player.spigot().sendMessage(errorMessage.create());
            return;
        }

        FactionBlockUtil.PlaceBlock(commandContext);
    }
}
