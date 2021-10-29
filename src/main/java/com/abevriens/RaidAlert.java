package com.abevriens;

import com.abevriens.jda.DiscordIdEnum;
import com.abevriens.jda.RaidAlertEmbedBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class RaidAlert {
    public String alertedFactionName;
    public int countdown;
    public boolean started;
    public List<String> enteredPlayerList;
    public List<String> enteredFactionList;

    private final Timer timer = new Timer(true);

    public RaidAlert(String _alertedFactionName, int _countdown, boolean _started, List<String> _enteredPlayerList,
                     List<String> _enteredFactionList) {
        alertedFactionName = _alertedFactionName;
        countdown = _countdown;
        started = _started;
        enteredPlayerList = _enteredPlayerList;
        enteredFactionList = _enteredFactionList;
    }

    public void runTimer() {
        started = true;

        timer.schedule(new TimerTask() {
            public void run() {
                if(countdown < 1) {
                    started = false;
                    cancel();
                } else {
                    countdown--;
                    updateTimerMessage();
                    POJO_Faction pojo_faction =
                            FactionManager.FactionToPOJO(CrackCityRaids.instance.factionManager.getFaction(alertedFactionName));
                    CrackCityRaids.instance.dbHandler.updateFaction(pojo_faction);
                }
            }
        }, 0, 60 * 1000);
    }

    public void updateTimerMessage() {
        Faction faction = CrackCityRaids.instance.factionManager.getFaction(alertedFactionName);
        RaidAlertEmbedBuilder raidAlertEmbedBuilder = new RaidAlertEmbedBuilder(this);

        TextChannel infoChannel = CrackCityRaids.instance.discordManager.getGuild().getTextChannelById(
                faction.discordIdMap.get(DiscordIdEnum.INFO_CHANNEL));

        if(infoChannel == null) return;

        infoChannel.retrieveMessageById(faction.discordIdMap.get(DiscordIdEnum.TIMER)).queue(message -> {
            message.editMessage(raidAlertEmbedBuilder.build()).queue();
        });
    }
}
