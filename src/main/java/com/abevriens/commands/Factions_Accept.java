package com.abevriens.commands;

import com.abevriens.*;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Factions_Accept {
    public String name;
    public CommandContext commandContext;

    public Factions_Accept(CommandContext _commandContext, String _name) {
        name = _name;
        commandContext = _commandContext;

        command_Accept();
    }

    private void command_Accept() {
        CC_Player cc_requestingplayer = null;
        OfflinePlayer offlinePlayer = null;

        if(commandContext.cc_player.faction.isEmptyFaction()) {
            ComponentBuilder errorMessage = TextUtil.GenerateErrorMsg("Je zit niet in een faction," +
                    " join er een met /factions join.");
            commandContext.player.spigot().sendMessage(errorMessage.create());
            return;
        } else if(!commandContext.cc_player.faction.factionOwner.uuid.equals(commandContext.cc_player.uuid)) {
            ComponentBuilder errorMessage = TextUtil.GenerateErrorMsg("Je bent niet de owner van deze faction, vraag aan" +
                    commandContext.cc_player.faction.factionOwner.displayName + " of ze " + name + " willen accepteren!");

            commandContext.player.spigot().sendMessage(errorMessage.create());
            return;
        }

        for(String uuid : commandContext.cc_player.faction.playerJoinRequests) {
            offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
            if(name.equals(offlinePlayer.getName())) {
                cc_requestingplayer = CrackCityRaids.playerManager.getCCPlayer(offlinePlayer);
                break;
            }
        }

        if(cc_requestingplayer == null) {
            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg("Speler niet gevonden binnen de join requests van " +
                    "je faction. Zie alle join requests met /factions requests");

            commandContext.player.spigot().sendMessage(errorMsg.create());
        } else if(commandContext.cc_player.faction.players.size() > 2) {
            ComponentBuilder errorMessage = TextUtil.GenerateErrorMsg("Je faction zit vol!");

            commandContext.player.spigot().sendMessage(errorMessage.create());
        } else {
            ComponentBuilder factionSuccessMsg = TextUtil.GenerateSuccessMsg(
                    name + " is lid geworden van je faction!");
            commandContext.cc_player.faction.sendMessageToPlayers(factionSuccessMsg);

            ComponentBuilder playerSuccessMsg = TextUtil.GenerateSuccessMsg(
                    "Je bent officïeel lid geworden van faction: " + commandContext.cc_player.faction.factionName);

            if(offlinePlayer.isOnline()) {
                Objects.requireNonNull(offlinePlayer.getPlayer()).spigot().sendMessage(playerSuccessMsg.create());
            }

            cc_requestingplayer.deleteRequests();
            cc_requestingplayer.lastFactionChange = Instant.now();
            CrackCityRaids.playerManager.setPlayerFaction(offlinePlayer, commandContext.cc_player.faction);
        }
    }
}
