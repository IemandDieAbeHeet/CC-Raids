package com.abevriens;

import com.abevriens.jda.DiscordIdEnum;
import com.abevriens.jda.RaidAlertEmbedBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class RaidAlert {
    public static final Color COLOR_ALERT = Color.RED;
    public static final Color COLOR_OPEN = Color.ORANGE;
    public static final Color COLOR_END = Color.getHSBColor(92, 67, 48);

    public String alertedFactionName;
    public int raidCountdown;
    public int openCountdown;
    public boolean raidCountdownStarted;
    public boolean openCountdownStarted;
    public List<String> enteredPlayerList;
    public List<String> enteredFactionList;

    private final Timer timer = new Timer(true);

    public RaidAlert(String _alertedFactionName, int _raidCountdown, int _raidingCountdown, boolean _raidCountdownStarted,
                     boolean _openCountdownStarted, List<String> _enteredPlayerList, List<String> _enteredFactionList) {
        alertedFactionName = _alertedFactionName;
        raidCountdown = _raidCountdown;
        openCountdown = _raidingCountdown;
        raidCountdownStarted = _raidCountdownStarted;
        openCountdownStarted = _openCountdownStarted;
        enteredPlayerList = _enteredPlayerList;
        enteredFactionList = _enteredFactionList;
    }

    public void runRaidTimer() {
        raidCountdownStarted = true;

        timer.schedule(new TimerTask() {
            public void run() {
                if(raidCountdown < 1 || openCountdownStarted) {
                    raidCountdownStarted = false;
                    raidCountdown = 360;
                    cancel();
                    runOpenTimer();
                } else {
                    raidCountdown--;
                    updateRaidTimerMessage();
                    POJO_Faction pojo_faction =
                            FactionManager.FactionToPOJO(CrackCityRaids.instance.factionManager.getFaction(alertedFactionName));
                    CrackCityRaids.instance.dbHandler.updateFaction(pojo_faction);
                }
            }
        }, 0, 60 * 1000);
    }

    public void runOpenTimer() {
        openCountdownStarted = true;

        timer.schedule(new TimerTask() {
            public void run() {
                if(openCountdown < 1 || raidCountdownStarted) {
                    openCountdownStarted = false;
                    openCountdown = 360;
                    cancel();
                    endOpenTimerMessage();
                } else {
                    openCountdown--;
                    updateOpenTimerMessage();
                    POJO_Faction pojo_faction =
                            FactionManager.FactionToPOJO(CrackCityRaids.instance.factionManager.getFaction(alertedFactionName));
                    CrackCityRaids.instance.dbHandler.updateFaction(pojo_faction);
                }
            }
        }, 0 , 60 * 1000);
    }

    public void updateRaidTimerMessage() {
        Faction faction = CrackCityRaids.instance.factionManager.getFaction(alertedFactionName);
        RaidAlertEmbedBuilder raidAlertEmbedBuilder = new RaidAlertEmbedBuilder(this, "Raid alert!");
        raidAlertEmbedBuilder.addTimerField(raidCountdown, "Raid begint in:");
        raidAlertEmbedBuilder.setColor(COLOR_ALERT);

        sendRaidAlertEmbed(raidAlertEmbedBuilder, faction);
    }

    public void updateOpenTimerMessage() {
        Faction faction = CrackCityRaids.instance.factionManager.getFaction(alertedFactionName);
        RaidAlertEmbedBuilder raidAlertEmbedBuilder = new RaidAlertEmbedBuilder(this, "Raid begonnen!");
        raidAlertEmbedBuilder.addTimerField(openCountdown, "Raid stopt over:");
        raidAlertEmbedBuilder.setColor(COLOR_OPEN);

        sendRaidAlertEmbed(raidAlertEmbedBuilder, faction);
    }

    private void endOpenTimerMessage() {
        Faction faction = CrackCityRaids.instance.factionManager.getFaction(alertedFactionName);
        RaidAlertEmbedBuilder raidAlertEmbedBuilder = new RaidAlertEmbedBuilder(this, "Raid is gestopt.");
        raidAlertEmbedBuilder.setColor(COLOR_END);

        TextChannel infoChannel = CrackCityRaids.instance.discordManager.getGuild().getTextChannelById(
                faction.discordIdMap.get(DiscordIdEnum.INFO_CHANNEL));

        if(infoChannel == null) return;

        infoChannel.retrieveMessageById(faction.discordIdMap.get(DiscordIdEnum.TIMER)).queue(message -> {
            message.editMessage(raidAlertEmbedBuilder.build()).queue();
            message.delete().queueAfter(10, TimeUnit.MINUTES);
        });
    }

    private void sendRaidAlertEmbed(RaidAlertEmbedBuilder raidAlertEmbedBuilder, Faction faction) {
        TextChannel infoChannel = CrackCityRaids.instance.discordManager.getGuild().getTextChannelById(
                faction.discordIdMap.get(DiscordIdEnum.INFO_CHANNEL));

        String timerMessageId = faction.discordIdMap.get(DiscordIdEnum.TIMER);

        if(infoChannel == null || timerMessageId == null) return;

        infoChannel.retrieveMessageById(timerMessageId).queue(message -> {
            message.editMessage(raidAlertEmbedBuilder.build()).queue();
        });
    }
}
