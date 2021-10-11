package com.abevriens.commands;

import com.abevriens.FactionManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;

public class Factions_Leave extends Factions_Base {

    public Factions_Leave(Factions_Base factions_base) {
        super(factions_base.cc_player, factions_base.player, factions_base.pojo_player,
                factions_base.factionManager, factions_base.playerManager);

        command_Leave();
    }

    private void command_Leave() {
        if(cc_player.faction.factionName.equals(FactionManager.emptyFaction.factionName)) {
            TextComponent errorMessage = new TextComponent("Je ziet niet in een faction, gebruik /factions" +
                    " join om een faction te joinen.");
            errorMessage.setColor(ChatColor.RED);
            player.spigot().sendMessage(errorMessage);
        } else if(cc_player.faction.factionOwner.uuid.equals(cc_player.uuid)) {
            TextComponent errorMessage = new TextComponent("Je bent de owner van deze faction, gebruik /factions delete om" +
                    " de faction te verwijderen of /factions setowner" +
                    " om iemand anders owner te maken.");
            errorMessage.setColor(ChatColor.RED);
            player.spigot().sendMessage(errorMessage);
        } else {
            playerManager.setPlayerFaction(Bukkit.getOfflinePlayer(player.getUniqueId()), FactionManager.emptyFaction);
            TextComponent leaveMessage = new TextComponent("Faction succesvol verlaten.");
            leaveMessage.setColor(ChatColor.GREEN);
            player.spigot().sendMessage(leaveMessage);
        }
    }
}
