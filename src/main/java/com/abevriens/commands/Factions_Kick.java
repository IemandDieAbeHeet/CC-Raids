package com.abevriens.commands;

import com.abevriens.*;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class Factions_Kick {
    String name;
    CommandContext commandContext;

    public Factions_Kick(CommandContext _commandContext, String _name) {
        name = _name;
        commandContext = _commandContext;

        command_Kick();
    }

    private void command_Kick() {
        CC_Player cc_kickplayer = CrackCityRaids.instance.playerManager.getCCPlayer(name);

        if(commandContext.cc_player.faction.factionName.equals(FactionManager.emptyFaction.factionName)) {
            ComponentBuilder errorMessage = TextUtil.GenerateErrorMsg("Je zit niet in een faction," +
                    " join er een met /factions join.");
            commandContext.player.spigot().sendMessage(errorMessage.create());
        } else if(!commandContext.cc_player.faction.factionOwner.uuid.equals(commandContext.cc_player.uuid)) {
            ComponentBuilder errorMessage = TextUtil.GenerateErrorMsg("Je bent niet de owner van deze faction, vraag aan " +
                    commandContext.cc_player.faction.factionOwner.displayName + " of ze de speler willen kicken!");

            commandContext.player.spigot().sendMessage(errorMessage.create());
        } else if(cc_kickplayer == null) {
            ComponentBuilder errorMessage = TextUtil.GenerateErrorMsg("De speler die je probeert te kicken bestaat niet!");

            commandContext.player.spigot().sendMessage(errorMessage.create());
        } else if(!cc_kickplayer.faction.factionName.equals(commandContext.cc_player.faction.factionName)) {
            ComponentBuilder errorMessage = TextUtil.GenerateErrorMsg("De speler die je probeert te kicken zit " +
                    "niet in je faction! Bekijk alle leden met /factions info");

            commandContext.player.spigot().sendMessage(errorMessage.create());
        } else if(cc_kickplayer.uuid.equals(commandContext.cc_player.uuid)) {
            ComponentBuilder errorMessage = TextUtil.GenerateErrorMsg("Je kunt jezelf niet kicken, gebruik /factions " +
                    "delete om de faction te verwijderen en verlaten");

            commandContext.player.spigot().sendMessage(errorMessage.create());
        } else {
            OfflinePlayer offlineKickPlayer = Bukkit.getOfflinePlayer(UUID.fromString(cc_kickplayer.uuid));
            CrackCityRaids.instance.playerManager.setPlayerFaction(offlineKickPlayer, FactionManager.emptyFaction);
            ComponentBuilder successMsg = TextUtil.GenerateSuccessMsg( cc_kickplayer.displayName + " is uit de " +
                    "faction gekickt!");
            commandContext.cc_player.faction.sendMessageToPlayers(successMsg);
        }
    }
}