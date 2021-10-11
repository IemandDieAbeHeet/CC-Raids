package com.abevriens.commands;

import com.abevriens.*;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;

public class Factions_Join extends Factions_Base {
    public String name;

    public Factions_Join(Factions_Base factions_base, String _name) {
        super(factions_base.cc_player, factions_base.player, factions_base.pojo_player,
                factions_base.factionManager, factions_base.playerManager);
        name = _name;

        command_Join();
    }

    private void command_Join() {
        TextComponent errorMessage = new TextComponent();
        errorMessage.setColor(ChatColor.RED);
        Faction faction = CrackCityRaids.instance.factionManager.getFaction(name);
        if(!cc_player.faction.factionName.equals(FactionManager.emptyFaction.factionName)) {
            errorMessage.setText("Je zit al in een faction, gebruik eerst /factions leave om je faction te verlaten.");
            player.spigot().sendMessage(errorMessage);
        } else if(!factionManager.factionNameList.contains(name)) {
            errorMessage.setText("De opgegeven faction bestaat niet!");
            player.spigot().sendMessage(errorMessage);
        } else if(faction.isFull()) {
            errorMessage.setText("De faction die je probeert te joinen zit vol!");
            player.spigot().sendMessage(errorMessage);
        } else if(faction.joinStatus == JoinStatus.CLOSED) {
            errorMessage.setText("De faction die je probeert te joinen staat op gesloten!");
            player.spigot().sendMessage(errorMessage);
        } else if(faction.joinStatus == JoinStatus.REQUEST) {
            faction.playerJoinRequests.add(cc_player.uuid);
            ComponentBuilder successMessage = TextUtil.GenerateSuccessMsg("Faction join request is verstuurd!");
            player.spigot().sendMessage(successMessage.create());
            CrackCityRaids.instance.dbHandler.updateFaction(FactionManager.FactionToPOJO(faction));

            TextComponent requestMessage = new TextComponent(cc_player.displayName + " heeft gevraagd of ze je faction " +
                    "mogen joinen!");
            requestMessage.setColor(ChatColor.GOLD);
            faction.sendMessageToPlayers(new ComponentBuilder(requestMessage));
        } else {
            Faction newFaction = CrackCityRaids.instance.factionManager.getFaction(name);
            playerManager.setPlayerFaction(Bukkit.getOfflinePlayer(player.getUniqueId()), newFaction);
            ComponentBuilder successMessage = TextUtil.GenerateSuccessMsg("Faction succesvol gejoined!");
            player.spigot().sendMessage(successMessage.create());
        }
    }
}
