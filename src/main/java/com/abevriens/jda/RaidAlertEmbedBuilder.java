package com.abevriens.jda;

import com.abevriens.FactionManager;
import com.abevriens.RaidAlert;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class RaidAlertEmbedBuilder extends EmbedBuilder {
    public RaidAlertEmbedBuilder(RaidAlert raidAlert) {
        super();

        int i = 0;
        MessageEmbed.Field playersField = new MessageEmbed.Field("Spelers", "", false);
        StringBuilder players = new StringBuilder();
        for(String playerName : raidAlert.enteredPlayerList) {
            if(i == 0) {
                players.append(playerName);
            } else {
                players.append(", ").append(playerName);
            }
            i++;
        }
        playersField = new MessageEmbed.Field(playersField.getName(),
                players.toString(), false);

        int j = 0;
        MessageEmbed.Field factionsField = new MessageEmbed.Field("Factions", "", false);
        StringBuilder factions = new StringBuilder();
        for(String factionName : raidAlert.enteredFactionList) {
            if(j == 0) {
                factions.append(factionName);
            } else {
                factions.append(", ").append(factionName);
            }
            j++;
        }
        factionsField = new MessageEmbed.Field(factionsField.getName(),
                factions.toString(), false);

        this.setTitle("Raid alert!");
        this.addField(playersField);
        this.addField(factionsField);
    }

    public void addTimerField(int countdown, String text) {
        MessageEmbed.Field timerField = new MessageEmbed.Field("Timer",
                text + FactionManager.generateCountdownTimeString(countdown), false);

        this.addField(timerField);
    }
}