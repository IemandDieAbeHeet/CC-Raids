package com.abevriens.commands;

import com.abevriens.CC_Player;
import com.abevriens.CrackCityRaids;
import com.abevriens.FactionManager;
import com.abevriens.TextUtil;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.UUID;

public class Factions_Delete extends Factions_Base {

    public Factions_Delete(Factions_Base factions_base) {
        super(factions_base.cc_player, factions_base.player, factions_base.pojo_player,
                factions_base.factionManager, factions_base.playerManager);

        command_Delete();
    }

    public void command_Delete() {
        String factionName = cc_player.faction.factionName;
        if(factionName.equals(FactionManager.emptyFaction.factionName)) {
            ComponentBuilder errorMessage = TextUtil.GenerateErrorMsg("Je zit niet in een faction," +
                    " join er een met /factions join.");
            player.spigot().sendMessage(errorMessage.create());
        }
        if(!cc_player.faction.factionOwner.uuid.equals(cc_player.uuid)) {
            ComponentBuilder errorMessage = TextUtil.GenerateErrorMsg("Je bent niet de owner van de faction. Als je " +
                    "het echt graag wilt moet je aan " + cc_player.faction.factionOwner.displayName +
                    " vragen of ze jou owner geven.");
            player.spigot().sendMessage(errorMessage.create());
        } else {
            factionManager.factionList.remove(cc_player.faction);
            factionManager.factionNameList.remove(factionName);

            ArrayList<CC_Player> factionMembers = new ArrayList<>(cc_player.faction.players);

            for(CC_Player factionMember : factionMembers) {
                CrackCityRaids.instance.playerManager.setPlayerFaction(Bukkit.getOfflinePlayer(UUID.fromString(factionMember.uuid)),
                        FactionManager.emptyFaction);
            }

            CrackCityRaids.instance.dbHandler.deleteFaction(factionName);

            ComponentBuilder successMessage = TextUtil.GenerateSuccessMsg("Faction is succesvol verwijderd!");
            player.spigot().sendMessage(successMessage.create());
        }
    }
}
