package com.abevriens.commands;

import com.abevriens.CrackCityRaids;
import com.abevriens.FactionManager;
import com.abevriens.JoinStatus;
import com.abevriens.TextUtil;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class Factions_SetJoinStatus extends Factions_Base {
    JoinStatus status;

    public Factions_SetJoinStatus(Factions_Base factions_base, JoinStatus _status) {
        super(factions_base.cc_player, factions_base.player, factions_base.pojo_player,
                factions_base.factionManager, factions_base.playerManager);

        status = _status;

        command_SetJoinStatus();
    }

    private void command_SetJoinStatus() {
        if(cc_player.faction.factionName.equals(FactionManager.emptyFaction.factionName)) {
            ComponentBuilder errorMessage = TextUtil.GenerateErrorMsg("Je zit niet in een faction, maak een nieuwe faction" +
                    "  met /factions create of join er een via /factions list");
            player.spigot().sendMessage(errorMessage.create());
        } else if(!cc_player.faction.factionOwner.uuid.equals(cc_player.uuid)) {
            ComponentBuilder errorMessage = TextUtil.GenerateErrorMsg("Je bent niet de owner van de faction. Als je het echt" +
                    " graag wilt moet je aan " + cc_player.faction.factionOwner.displayName + " vragen of ze jou owner geven.");
            player.spigot().sendMessage(errorMessage.create());
        } else {
            cc_player.faction.joinStatus = status;
            CrackCityRaids.instance.dbHandler.updateFaction(FactionManager.FactionToPOJO(cc_player.faction));
            ComponentBuilder successMessage = TextUtil.GenerateSuccessMsg("Join status van de faction is succesvol aangepast");
            player.spigot().sendMessage(successMessage.create());
        }
    }
}
