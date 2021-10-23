package com.abevriens.commands;

import com.abevriens.FactionManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;

public class Factions_Leave {
    CommandContext commandContext;

    public Factions_Leave(CommandContext _commandContext) {
        commandContext = _commandContext;

        command_Leave();
    }

    private void command_Leave() {
        if(commandContext.cc_player.faction.isEmptyFaction()) {
            TextComponent errorMessage = new TextComponent("Je ziet niet in een faction, gebruik /factions" +
                    " join om een faction te joinen.");
            errorMessage.setColor(ChatColor.RED);
            commandContext.player.spigot().sendMessage(errorMessage);
        } else if(commandContext.cc_player.faction.factionOwner.uuid.equals(commandContext.cc_player.uuid)) {
            TextComponent errorMessage = new TextComponent("Je bent de owner van deze faction, gebruik /factions delete om" +
                    " de faction te verwijderen of /factions setowner" +
                    " om iemand anders owner te maken.");
            errorMessage.setColor(ChatColor.RED);
            commandContext.player.spigot().sendMessage(errorMessage);
        } else {
            commandContext.playerManager.setPlayerFaction(Bukkit.getOfflinePlayer(commandContext.player.getUniqueId()), FactionManager.emptyFaction);
            TextComponent leaveMessage = new TextComponent("Faction succesvol verlaten.");
            leaveMessage.setColor(ChatColor.GREEN);
            commandContext.player.spigot().sendMessage(leaveMessage);
        }
    }
}
