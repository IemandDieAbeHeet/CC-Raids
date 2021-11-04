package com.abevriens.commands;

import com.abevriens.CrackCityRaids;
import com.abevriens.Faction;
import com.abevriens.FactionManager;
import com.abevriens.TextUtil;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class Factions_ConfirmRaidAlert {
    public String factionName;
    public CommandContext commandContext;

    public Factions_ConfirmRaidAlert(CommandContext _commandContext, String _factionName) {
        factionName = _factionName;
        commandContext = _commandContext;

        command_confirmRaidAlert();
    }

    private  void command_confirmRaidAlert() {
        Faction faction = CrackCityRaids.factionManager.getFaction(factionName);

        if(faction.raidAlert.raidCountdownStarted || faction.raidAlert.openCountdownStarted) {
            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg("Er is al een raid alert verstuurd naar deze faction.");

            commandContext.player.spigot().sendMessage(errorMsg.create());
        } else if(!faction.raidAlert.playersAllowedToConfirm.contains(commandContext.cc_player.uuid)) {
            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg("Je bent niet bij deze faction in de buurt geweest.");

            commandContext.player.spigot().sendMessage(errorMsg.create());
        } else {
            ComponentBuilder successMsg = TextUtil.GenerateSuccessMsg(
                    "De raid alert is verstuurd. Je kunt beginnen met raiden in " +
                            FactionManager.generateCountdownTimeString(faction.raidAlert.maxRaidCountdown) + ".");

            if(!faction.raidAlert.enteredFactionList.contains(commandContext.cc_player.faction.factionName)) {
                faction.raidAlert.enteredFactionList.add(commandContext.cc_player.faction.factionName);
            }

            if(!faction.raidAlert.enteredPlayerList.contains(commandContext.cc_player.displayName)) {
                faction.raidAlert.enteredPlayerList.add(commandContext.cc_player.displayName);
            }

            faction.raidAlert.runRaidTimer();
            commandContext.player.spigot().sendMessage(successMsg.create());
        }
    }
}
