package com.abevriens.commands;

import com.abevriens.*;
import com.abevriens.jda.DiscordIdEnum;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import net.dv8tion.jda.api.requests.restaction.RoleAction;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.function.Consumer;

public class Factions_Create {
    public String name;
    public CommandContext commandContext;

    public Factions_Create(CommandContext _commandContext, String _name) {
        name = _name;
        commandContext = _commandContext;

        command_Create();
    }

    private void command_Create() {
        if(commandContext.cc_player.discordId == null) {
            TextUtil.SendDiscordLinkError(commandContext.player);
        } else if(!commandContext.cc_player.faction.isEmptyFaction()) {
            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg("Je zit al in een faction dus je kunt geen nieuwe " +
                    "faction aanmaken, verlaat deze met /factions leave.");
            commandContext.player.spigot().sendMessage(errorMsg.create());
        } else if(commandContext.factionManager.factionNameList.contains(name)) {
            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg("Een faction met deze naam bestaat al, " +
                    "kies een andere naam.");
            commandContext.player.spigot().sendMessage(errorMsg.create());
        } else {
            EnumMap<DiscordIdEnum, String> discordIdMap = new EnumMap<>(DiscordIdEnum.class);

            Faction faction = new Faction(
                    commandContext.pojo_player,
                    name,
                    new ArrayList<CC_Player>() {
                        {
                            add(commandContext.cc_player);
                        }
                    },
                    JoinStatus.REQUEST,
                    new ArrayList<>(),
                    FactionCoreUtil.GenerateEmptyFactionCore(name),
                    10, 10,
                    new ArrayList<>(),
                    discordIdMap
            );

            POJO_Faction pojo_faction = FactionManager.FactionToPOJO(faction);
            commandContext.pojo_player.factionName = faction.factionName;
            commandContext.cc_player.faction = faction;
            CrackCityRaids.instance.dbHandler.insertFaction(pojo_faction);
            CrackCityRaids.instance.dbHandler.updatePlayer(commandContext.pojo_player);
            commandContext.factionManager.factionNameList.add(faction.factionName);
            commandContext.factionManager.factionList.add(faction);

            RoleAction createRole = CrackCityRaids.instance.discordManager.getGuild().createRole();
            createRole.setName(name).queue();
            Consumer<Role> roleCallback = (response) -> {
                Faction callbackFaction = CrackCityRaids.instance.factionManager.getFaction(name);
                callbackFaction.discordIdMap.put(DiscordIdEnum.ROLE, response.getId());
                CrackCityRaids.instance.dbHandler.updateFaction(FactionManager.FactionToPOJO(callbackFaction));
            };
            createRole.queue(roleCallback);

            ChannelAction<Category> createCategory = CrackCityRaids.instance.discordManager.getGuild().createCategory(
                    "Faction: " + name);
            Consumer<Category> categoryCallback = (response) -> {
                Faction callbackFaction = CrackCityRaids.instance.factionManager.getFaction(name);
                callbackFaction.discordIdMap.put(DiscordIdEnum.CATEGORY, response.getId());
                CrackCityRaids.instance.dbHandler.updateFaction(FactionManager.FactionToPOJO(callbackFaction));

                ChannelAction<TextChannel> createInfoChannel = response.createTextChannel("info");
                Consumer<TextChannel> infoCallback = (infoResponse) -> {
                    Faction infoCallbackFaction = CrackCityRaids.instance.factionManager.getFaction(name);
                    infoCallbackFaction.discordIdMap.put(DiscordIdEnum.INFO_CHANNEL, infoResponse.getId());
                    CrackCityRaids.instance.dbHandler.updateFaction(FactionManager.FactionToPOJO(infoCallbackFaction));
                    infoResponse.sendMessage("Dit is het info kanaal van jouw faction, hier krijg je bijvoorbeeld " +
                            "notificaties binnen over mensen die je faction hebben betreden.").queue();
                };
                createInfoChannel.queue(infoCallback);

                ChannelAction<TextChannel> createChatChannel = response.createTextChannel("chat");
                Consumer<TextChannel> chatCallback = (chatResponse) -> {
                    Faction chatCallbackFaction = CrackCityRaids.instance.factionManager.getFaction(name);
                    chatCallbackFaction.discordIdMap.put(DiscordIdEnum.CHAT_CHANNEL, chatResponse.getId());
                    CrackCityRaids.instance.dbHandler.updateFaction(FactionManager.FactionToPOJO(chatCallbackFaction));
                };
                createChatChannel.queue(chatCallback);

                TextComponent successMessage = new TextComponent("Faction is succesvol aangemaakt!");
                successMessage.setColor(ChatColor.GREEN);
                commandContext.player.spigot().sendMessage(successMessage);
            };
            createCategory.queue(categoryCallback);
        }
    }
}
