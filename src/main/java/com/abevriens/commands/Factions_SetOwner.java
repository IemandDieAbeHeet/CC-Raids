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
        CC_Player cc_kickplayer = CrackCityRaids.instance.playerManager.getCCPlayer(name);

        if(cc_player.faction.factionName.equals(FactionManager.emptyFaction.factionName)) {
            ComponentBuilder errorMessage = TextUtil.GenerateErrorMsg("Je zit niet in een faction," +
                    " join er een met /factions join.");
            player.spigot().sendMessage(errorMessage.create());
        } else if(!cc_player.faction.factionOwner.uuid.equals(cc_player.uuid)) {
            ComponentBuilder errorMessage = TextUtil.GenerateErrorMsg("Je bent niet de owner van deze faction, vraag aan" +
                    cc_player.faction.factionOwner.displayName + " of ze " + name + " willen accepteren!");

            player.spigot().sendMessage(errorMessage.create());
        } else if(cc_kickplayer == null) {
            ComponentBuilder errorMessage = TextUtil.GenerateErrorMsg("De speler die je probeert owner te maken bestaat niet!");

            player.spigot().sendMessage(errorMessage.create());
        } else if(!cc_kickplayer.faction.factionName.equals(cc_player.faction.factionName)) {
            ComponentBuilder errorMessage = TextUtil.GenerateErrorMsg("De speler die je probeert owner te maken zit niet in je faction! Bekijk alle leden met /factions info");

            player.spigot().sendMessage(errorMessage.create());
        } else {
            POJO_Player pojo_playerowner = PlayerManager.CCToPOJO(cc_player);
            cc_player.faction.factionOwner = pojo_playerowner;
            CrackCityRaids.instance.dbHandler.updateFaction(FactionManager.FactionToPOJO(cc_player.faction));
            CrackCityRaids.instance.dbHandler.updatePlayer(pojo_playerowner);
        }
    }
}
