package com.abevriens;

import com.abevriens.jda.DiscordIdEnum;
import com.abevriens.jda.RaidAlertEmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RaidAlert {
    public static final Color COLOR_ALERT = Color.RED;
    public static final Color COLOR_OPEN = Color.ORANGE;
    public static final Color COLOR_END = Color.getHSBColor(92, 67, 48);

    public String alertedFactionName;
    public int raidCountdown;
    public int maxRaidCountdown;
    public int openCountdown;
    public int maxOpenCountdown;
    public boolean raidCountdownStarted;
    public boolean openCountdownStarted;
    public List<String> enteredPlayerList;
    public List<String> enteredFactionList;
    public List<String> playersAllowedToConfirm = new ArrayList<>();

    private final Timer timer = new Timer(true);

    public RaidAlert(String _alertedFactionName, int _raidCountdown, int _maxRaidCountdown,
                     int _openCountdown, int _maxOpenCountdown, boolean _raidCountdownStarted,
                     boolean _openCountdownStarted, List<String> _enteredPlayerList, List<String> _enteredFactionList) {
        alertedFactionName = _alertedFactionName;
        raidCountdown = _raidCountdown;
        maxRaidCountdown = _maxRaidCountdown;
        openCountdown = _openCountdown;
        maxOpenCountdown = _maxOpenCountdown;
        raidCountdownStarted = _raidCountdownStarted;
        openCountdownStarted = _openCountdownStarted;
        enteredPlayerList = _enteredPlayerList;
        enteredFactionList = _enteredFactionList;
    }

    public void runRaidTimer() {
        raidCountdownStarted = true;

        Faction faction = CrackCityRaids.instance.factionManager.getFaction(alertedFactionName);
        TextChannel infoChannel = CrackCityRaids.instance.discordManager.getGuild().getTextChannelById(
                faction.discordIdMap.get(DiscordIdEnum.INFO_CHANNEL));

        if(infoChannel == null) return;

        boolean nullChecked = false;
        if(faction.discordIdMap.get(DiscordIdEnum.TIMER) == null) {
            sendRaidAlertEmbed(infoChannel, faction);
            nullChecked = true;
        }

        if(!nullChecked) {
            infoChannel.retrieveMessageById(faction.discordIdMap.get(DiscordIdEnum.TIMER)).queue(null, (error) -> {
                if (error instanceof ErrorResponseException) {
                    ErrorResponseException ex = (ErrorResponseException) error;
                    if (ex.getErrorResponse() == ErrorResponse.UNKNOWN_MESSAGE) {
                        sendRaidAlertEmbed(infoChannel, faction);
                    }
                }
            });
        }

        timer.schedule(new TimerTask() {
            public void run() {
                if(raidCountdown < 1 || openCountdownStarted) {
                    raidCountdownStarted = false;
                    raidCountdown = maxRaidCountdown;
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
        Faction faction = CrackCityRaids.instance.factionManager.getFaction(alertedFactionName);
        TextChannel infoChannel = CrackCityRaids.instance.discordManager.getGuild().getTextChannelById(
                faction.discordIdMap.get(DiscordIdEnum.INFO_CHANNEL));

        if(infoChannel == null) return;

        if(!openCountdownStarted) {
            Role role = CrackCityRaids.instance.discordManager.getGuild().getRoleById(
                    faction.discordIdMap.get(DiscordIdEnum.ROLE));

            if(role != null) {
                infoChannel.sendMessage(role.getAsMention() + " de raid is gestart!").queue((message -> {
                    message.delete().queueAfter(1, TimeUnit.MINUTES);
                }));
            }
        }

        for(String playerName : enteredPlayerList) {
            CC_Player player = CrackCityRaids.instance.playerManager.getCCPlayer(playerName);
            if(player != null) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(player.uuid));

                if (offlinePlayer.isOnline()) {
                    ComponentBuilder successMsg = TextUtil.GenerateSuccessMsg("De raid bij de faction "
                            + faction.factionName + " is gestart!");
                    Objects.requireNonNull(offlinePlayer.getPlayer()).spigot().sendMessage(successMsg.create());
                }
            }
        }

        openCountdownStarted = true;

        boolean nullChecked = false;
        if(faction.discordIdMap.get(DiscordIdEnum.TIMER) == null) {
            sendOpenAlertEmbed(infoChannel, faction);
            nullChecked = true;
        }

        if(!nullChecked) {
            infoChannel.retrieveMessageById(faction.discordIdMap.get(DiscordIdEnum.TIMER)).queue(null, (error) -> {
                if (error instanceof ErrorResponseException) {
                    ErrorResponseException ex = (ErrorResponseException) error;
                    if (ex.getErrorResponse() == ErrorResponse.UNKNOWN_MESSAGE) {
                        sendOpenAlertEmbed(infoChannel, faction);
                    }
                }
            });
        }

        timer.schedule(new TimerTask() {
            public void run() {
                if(openCountdown < 1 || raidCountdownStarted) {
                    openCountdownStarted = false;
                    openCountdown = maxOpenCountdown;
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

        editRaidAlertEmbed(raidAlertEmbedBuilder, faction);
    }

    public void updateOpenTimerMessage() {
        Faction faction = CrackCityRaids.instance.factionManager.getFaction(alertedFactionName);
        RaidAlertEmbedBuilder raidAlertEmbedBuilder = new RaidAlertEmbedBuilder(this, "Raid begonnen!");
        raidAlertEmbedBuilder.addTimerField(openCountdown, "Raid stopt over:");
        raidAlertEmbedBuilder.setColor(COLOR_OPEN);

        editRaidAlertEmbed(raidAlertEmbedBuilder, faction);
    }

    private void endOpenTimerMessage() {
        enteredFactionList.clear();
        enteredPlayerList.clear();
        playersAllowedToConfirm.clear();
        Faction faction = CrackCityRaids.instance.factionManager.getFaction(alertedFactionName);
        RaidAlertEmbedBuilder raidAlertEmbedBuilder = new RaidAlertEmbedBuilder(this, "Raid is gestopt.");
        raidAlertEmbedBuilder.setColor(COLOR_END);

        TextChannel infoChannel = CrackCityRaids.instance.discordManager.getGuild().getTextChannelById(
                faction.discordIdMap.get(DiscordIdEnum.INFO_CHANNEL));

        if(infoChannel == null) return;

        infoChannel.retrieveMessageById(faction.discordIdMap.get(DiscordIdEnum.TIMER)).queue(message -> {
            message.editMessage(raidAlertEmbedBuilder.build()).queue();
            message.delete().queueAfter(10, TimeUnit.MINUTES);
            faction.discordIdMap.remove(DiscordIdEnum.TIMER);
        });
    }

    private void editRaidAlertEmbed(RaidAlertEmbedBuilder raidAlertEmbedBuilder, Faction faction) {
        TextChannel infoChannel = CrackCityRaids.instance.discordManager.getGuild().getTextChannelById(
                faction.discordIdMap.get(DiscordIdEnum.INFO_CHANNEL));

        String timerMessageId = faction.discordIdMap.get(DiscordIdEnum.TIMER);

        if(infoChannel == null || timerMessageId == null) return;

        infoChannel.retrieveMessageById(timerMessageId).queue(message -> {
            message.editMessage(raidAlertEmbedBuilder.build()).queue();
        });
    }

    private void sendRaidAlertEmbed(TextChannel channel, Faction faction) {
        RaidAlertEmbedBuilder raidAlertEmbedBuilder = new RaidAlertEmbedBuilder(this, "Raid alert!");
        raidAlertEmbedBuilder.addTimerField(raidCountdown, "Raid begint in:");
        raidAlertEmbedBuilder.setColor(COLOR_ALERT);

        channel.sendMessage(raidAlertEmbedBuilder.build()).queue(message -> {
            faction.discordIdMap.put(DiscordIdEnum.TIMER, message.getId());
            CrackCityRaids.instance.dbHandler.updateFaction(FactionManager.FactionToPOJO(faction));
        });
    }

    private void sendOpenAlertEmbed(TextChannel channel, Faction faction) {
        RaidAlertEmbedBuilder raidAlertEmbedBuilder = new RaidAlertEmbedBuilder(this, "Raid begonnen!");
        raidAlertEmbedBuilder.addTimerField(openCountdown, "Raid stopt over:");
        raidAlertEmbedBuilder.setColor(COLOR_OPEN);

        channel.sendMessage(raidAlertEmbedBuilder.build()).queue(message -> {
            faction.discordIdMap.put(DiscordIdEnum.TIMER, message.getId());
            CrackCityRaids.instance.dbHandler.updateFaction(FactionManager.FactionToPOJO(faction));
        });
    }
}