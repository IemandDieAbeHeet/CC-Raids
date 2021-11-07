package com.abevriens.commands.factioncore;

import com.abevriens.FactionCoreUtil;
import com.abevriens.TextUtil;
import com.abevriens.commands.CommandContext;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.time.Instant;
import java.util.Objects;

public class Factions_SetFactionCore {
    public CommandContext commandContext;

    public Factions_SetFactionCore(CommandContext _commandContext) {
        commandContext = _commandContext;

        command_CreateFCore();
    }

    private void command_CreateFCore() {
        if(commandContext.cc_player.faction.isEmptyFaction()) {
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
        } else if(Objects.equals(commandContext.cc_player.faction.factionCore.blockLocation,
                new Location(Bukkit.getWorld("world"), 0, 0, 0))) {
            ComponentBuilder errorMessage = TextUtil.GenerateErrorMsg("Je faction heeft nog geen faction core " +
                    "geplaatst, plaats deze met /factions core create.");
            commandContext.player.spigot().sendMessage(errorMessage.create());
            return;
        } else if(!commandContext.cc_player.faction.factionCore.lastChange.isBefore(Instant.now().minusSeconds(10))) {
            ComponentBuilder errorMessage = TextUtil.GenerateErrorMsg("Je verplaatst je faction core te snel, wacht 10 " +
                    "seconden en probeer het dan opnieuw.");
            commandContext.player.spigot().sendMessage(errorMessage.create());
            return;
        }

        FactionCoreUtil.PlaceCore(commandContext);
    }
}
