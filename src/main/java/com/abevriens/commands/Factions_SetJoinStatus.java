package com.abevriens.commands;

import com.abevriens.CrackCityRaids;
import com.abevriens.FactionManager;
import com.abevriens.JoinStatus;
import com.abevriens.TextUtil;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class Factions_SetJoinStatus {
    JoinStatus status;
    CommandContext commandContext;

    public Factions_SetJoinStatus(CommandContext _commandContext, JoinStatus _status) {
        status = _status;
        commandContext = _commandContext;

        command_SetJoinStatus();
    }

    private void command_SetJoinStatus() {
        if(commandContext.cc_player.faction.isEmptyFaction()) {
            ComponentBuilder errorMessage = TextUtil.GenerateErrorMsg("Je zit niet in een faction, maak een nieuwe faction" +
                    "  met /factions create of join er een via /factions list");
            commandContext.player.spigot().sendMessage(errorMessage.create());
        } else if(!commandContext.cc_player.faction.factionOwner.uuid.equals(commandContext.cc_player.uuid)) {
            ComponentBuilder errorMessage = TextUtil.GenerateErrorMsg("Je bent niet de owner van de faction. Als je het echt" +
                    " graag wilt moet je aan " + commandContext.cc_player.faction.factionOwner.displayName + " vragen of ze jou owner geven.");
            commandContext.player.spigot().sendMessage(errorMessage.create());
        } else {
            commandContext.cc_player.faction.joinStatus = status;
            CrackCityRaids.dbHandler.updateFaction(FactionManager.FactionToPOJO(commandContext.cc_player.faction));
            ComponentBuilder successMessage = TextUtil.GenerateSuccessMsg("Join status van de faction is succesvol aangepast");
            commandContext.player.spigot().sendMessage(successMessage.create());
        }
    }
}
