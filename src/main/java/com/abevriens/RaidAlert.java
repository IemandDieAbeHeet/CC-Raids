package com.abevriens;

import com.abevriens.jda.DiscordIdEnum;
import com.abevriens.jda.RaidAlertEmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class RaidAlert {
    public String alertedFactionName;
    public int raidCountdown;
    public int raidingCountdown;
    public boolean raidCountdownStarted;
    public boolean raidingCountdownStarted;
    public List<String> enteredPlayerList;
    public List<String> enteredFactionList;

    private final Timer timer = new Timer(true);

    public RaidAlert(String _alertedFactionName, int _raidCountdown, int _raidingCountdown, boolean _raidCountdownStarted,
                     boolean _raidingCountdownStarted, List<String> _enteredPlayerList, List<String> _enteredFactionList) {
        alertedFactionName = _alertedFactionName;
        raidCountdown = _raidCountdown;
        raidingCountdown = _raidingCountdown;
        raidCountdownStarted = _raidCountdownStarted;
        raidingCountdownStarted = _raidingCountdownStarted;
        enteredPlayerList = _enteredPlayerList;
        enteredFactionList = _enteredFactionList;
    }

    public void runRaidTimer() {
        raidCountdownStarted = true;

        timer.schedule(new TimerTask() {
            public void run() {
                if(raidCountdown < 1) {
                    raidCountdownStarted = false;
                    raidCountdown = 360;
                    cancel();
                    endTimerMessage();
                } else {
                    raidCountdown--;
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

    public void endTimerMessage() {
        Faction faction = CrackCityRaids.instance.factionManager.getFaction(alertedFactionName);
        RaidAlertEmbedBuilder raidAlertEmbedBuilder = new RaidAlertEmbedBuilder(this);

        TextChannel infoChannel = CrackCityRaids.instance.discordManager.getGuild().getTextChannelById(
                faction.discordIdMap.get(DiscordIdEnum.INFO_CHANNEL));

        if(infoChannel == null) return;

        infoChannel.retrieveMessageById(faction.discordIdMap.get(DiscordIdEnum.TIMER)).queue(message -> {
            message.editMessage(raidAlertEmbedBuilder.build()).queue();
            message.delete().queueAfter(6, TimeUnit.HOURS);
        });
    }
}
