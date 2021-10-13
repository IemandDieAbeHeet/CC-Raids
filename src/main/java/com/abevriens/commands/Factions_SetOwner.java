package com.abevriens.commands;

import com.abevriens.*;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class Factions_SetOwner extends Factions_Base {
    String name;

    public Factions_SetOwner(Factions_Base factions_base, String _name) {
        super(factions_base.cc_player, factions_base.player, factions_base.pojo_player,
                factions_base.factionManager, factions_base.playerManager);
        name = _name;

        command_SetOwner();
    }

    private void command_SetOwner() {
        CC_Player cc_ownerplayer = CrackCityRaids.instance.playerManager.getCCPlayer(name);

        if(cc_player.faction.factionName.equals(FactionManager.emptyFaction.factionName)) {
            ComponentBuilder errorMessage = TextUtil.GenerateErrorMsg("Je zit niet in een faction," +
                    " join er een met /factions join.");
            player.spigot().sendMessage(errorMessage.create());
        } else if(!cc_player.faction.factionOwner.uuid.equals(cc_player.uuid)) {
            ComponentBuilder errorMessage = TextUtil.GenerateErrorMsg("Je bent niet de owner van deze faction, vraag aan " +
                    cc_player.faction.factionOwner.displayName + " of ze de faction owner willen veranderen!");

            player.spigot().sendMessage(errorMessage.create());
        } else if(cc_ownerplayer == null) {
            ComponentBuilder errorMessage = TextUtil.GenerateErrorMsg("De speler die je probeert owner te maken bestaat niet!");

            player.spigot().sendMessage(errorMessage.create());
        } else if(!cc_ownerplayer.faction.factionName.equals(cc_player.faction.factionName)) {
            ComponentBuilder errorMessage = TextUtil.GenerateErrorMsg("De speler die je probeert owner te maken zit niet in je faction! Bekijk alle leden met /factions info");

            player.spigot().sendMessage(errorMessage.create());
        } else {
            int oldIndex = 0;
            for(int i = 0; i < cc_ownerplayer.faction.players.size(); i++) {
                if(cc_ownerplayer.faction.players.get(i).uuid.equals(cc_ownerplayer.uuid)) {
                    oldIndex = i;
                }
            }
            cc_ownerplayer.faction.players.set(0, cc_ownerplayer);
            cc_ownerplayer.faction.players.set(oldIndex, cc_player);

            POJO_Player pojo_playerowner = PlayerManager.CCToPOJO(cc_ownerplayer);
            cc_player.faction.factionOwner = pojo_playerowner;
            OfflinePlayer offlinePlayerOwner = Bukkit.getOfflinePlayer(UUID.fromString(cc_ownerplayer.uuid));
            CrackCityRaids.instance.dbHandler.updateFaction(FactionManager.FactionToPOJO(cc_player.faction));
            ComponentBuilder successMsg = TextUtil.GenerateSuccessMsg( pojo_playerowner.displayName + " is de owner van " +
                    "de faction geworden!");
            cc_player.faction.sendMessageToPlayers(successMsg);
        }
    }
}
