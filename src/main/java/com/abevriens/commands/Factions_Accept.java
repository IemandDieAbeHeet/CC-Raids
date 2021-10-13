package com.abevriens.commands;

import com.abevriens.*;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class Factions_Accept extends Factions_Base {
    public String name;

    public Factions_Accept(Factions_Base factions_base, String _name) {
        super(factions_base.cc_player, factions_base.player, factions_base.pojo_player,
                factions_base.factionManager, factions_base.playerManager);
        name = _name;

        command_Accept();
    }

    private void command_Accept() {
        CC_Player cc_requestingplayer = null;
        OfflinePlayer offlinePlayer = null;

        if(cc_player.faction.factionName.equals(FactionManager.emptyFaction.factionName)) {
            ComponentBuilder errorMessage = TextUtil.GenerateErrorMsg("Je zit niet in een faction," +
                    " join er een met /factions join.");
            player.spigot().sendMessage(errorMessage.create());
            return;
        } else if(!cc_player.faction.factionOwner.uuid.equals(cc_player.uuid)) {
            ComponentBuilder errorMessage = TextUtil.GenerateErrorMsg("Je bent niet de owner van deze faction, vraag aan" +
                    cc_player.faction.factionOwner.displayName + " of ze " + name + " willen accepteren!");

            player.spigot().sendMessage(errorMessage.create());
            return;
        }

        for(String uuid : cc_player.faction.playerJoinRequests) {
            offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
            if(offlinePlayer.getName().equals(name)) {
                cc_requestingplayer = CrackCityRaids.instance.playerManager.getCCPlayer(offlinePlayer);
                break;
            }
        }

        if(cc_requestingplayer == null) {
            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg("Speler niet gevonden binnen de join requests van " +
                    "je faction. Zie alle join requests met /factions requests");

            player.spigot().sendMessage(errorMsg.create());
        } else if(cc_player.faction.players.size() > 2) {
            ComponentBuilder errorMessage = TextUtil.GenerateErrorMsg("Je faction zit vol!");

            player.spigot().sendMessage(errorMessage.create());
        } else {

            ComponentBuilder factionSuccessMsg = TextUtil.GenerateSuccessMsg(
                    name + " is lid geworden van je faction!");
            cc_player.faction.sendMessageToPlayers(factionSuccessMsg);

            ComponentBuilder playerSuccessMsg = TextUtil.GenerateSuccessMsg(
                    "Je bent officïeel lid geworden van faction: " + cc_player.faction.factionName);

            if(offlinePlayer.isOnline()) {
                offlinePlayer.getPlayer().spigot().sendMessage(playerSuccessMsg.create());
            }

            cc_requestingplayer.deleteRequests();

            CrackCityRaids.instance.playerManager.setPlayerFaction(offlinePlayer, cc_player.faction);
            CrackCityRaids.instance.dbHandler.updatePlayer(PlayerManager.CCToPOJO(cc_requestingplayer));
        }
    }
}
