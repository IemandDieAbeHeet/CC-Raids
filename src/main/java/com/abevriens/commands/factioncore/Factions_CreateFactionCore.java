package com.abevriens.commands.factioncore;

import com.abevriens.FactionCoreUtil;
import com.abevriens.FactionManager;
import com.abevriens.TextUtil;
import com.abevriens.commands.CommandContext;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Objects;

public class Factions_CreateFactionCore {
    public CommandContext commandContext;

    public Factions_CreateFactionCore(CommandContext _commandContext) {
        commandContext = _commandContext;

        command_CreateFCore();
    }

    private void command_CreateFCore() {
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
        } else if(!Objects.equals(commandContext.cc_player.faction.factionCore.blockLocation,
                new Location(Bukkit.getWorld("world"), 0, 0, 0))) {
            ComponentBuilder errorMessage = TextUtil.GenerateErrorMsg("Je faction heeft al een faction core " +
                    "geplaatst, verplaats je faction core dan met /factions core set.");
            commandContext.player.spigot().sendMessage(errorMessage.create());
            return;
        }

        FactionCoreUtil.PlaceCore(commandContext);
    }
}
