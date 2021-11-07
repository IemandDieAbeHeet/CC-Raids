package com.abevriens.commands;

import com.abevriens.*;
import com.abevriens.discord.DiscordIdEnum;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import net.dv8tion.jda.api.requests.restaction.RoleAction;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

import java.awt.*;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Random;
import java.util.concurrent.TimeUnit;
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
                    new ArrayList<>() {
                        {
                            add(commandContext.cc_player);
                        }
                    },
                    JoinStatus.REQUEST,
                    new ArrayList<>(),
                    FactionCoreUtil.GenerateEmptyFactionCore(name),
                    10, 10,
                    new ArrayList<>(),
                    discordIdMap,
                    new RaidAlert(name, 360, 360, 360, 360,
                            false, false, new ArrayList<>(), new ArrayList<>())
            );

            POJO_Faction pojo_faction = FactionManager.FactionToPOJO(faction);
            commandContext.pojo_player.factionName = faction.factionName;
            commandContext.cc_player.faction = faction;
            CrackCityRaids.dbHandler.insertFaction(pojo_faction);
            CrackCityRaids.dbHandler.updatePlayer(commandContext.pojo_player);
            commandContext.factionManager.factionNameList.add(faction.factionName);
            commandContext.factionManager.factionList.add(faction);

            RoleAction createRole = CrackCityRaids.discordManager.getGuild().createRole();
            Consumer<Role> roleCallback = (roleResponse) -> {
                Faction callbackFaction = CrackCityRaids.factionManager.getFaction(name);
                callbackFaction.discordIdMap.put(DiscordIdEnum.ROLE, roleResponse.getId());
                CrackCityRaids.dbHandler.updateFaction(FactionManager.FactionToPOJO(callbackFaction));
                Random rg = new Random();
                float r = rg.nextFloat();
                float g = rg.nextFloat();
                float b = rg.nextFloat();
                roleResponse.getManager()
                        .setColor(new Color(r, g, b))
                        .setHoisted(true)
                        .setName(name).queue(changeRole -> {
                            CrackCityRaids.discordManager.getGuild().addRoleToMember(commandContext.cc_player.discordId, roleResponse).queue();

                            ChannelAction<Category> createCategory = CrackCityRaids.discordManager.getGuild().createCategory(
                                            "Faction: " + name)
                                    .addPermissionOverride(CrackCityRaids.discordManager.getGuild().getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL))
                                    .addPermissionOverride(roleResponse, EnumSet.of(Permission.VIEW_CHANNEL), null);
                            Consumer<Category> categoryCallback = (categoryResponse) -> {
                                Faction categoryCallbackFaction = CrackCityRaids.factionManager.getFaction(name);
                                categoryCallbackFaction.discordIdMap.put(DiscordIdEnum.CATEGORY, categoryResponse.getId());
                                CrackCityRaids.dbHandler.updateFaction(FactionManager.FactionToPOJO(categoryCallbackFaction));

                                ChannelAction<TextChannel> createInfoChannel = categoryResponse.createTextChannel("info")
                                        .addPermissionOverride(roleResponse, EnumSet.of(Permission.VIEW_CHANNEL),
                                                EnumSet.of(Permission.MESSAGE_WRITE));
                                Consumer<TextChannel> infoCallback = (infoResponse) -> {
                                    Faction infoCallbackFaction = CrackCityRaids.factionManager.getFaction(name);
                                    infoCallbackFaction.discordIdMap.put(DiscordIdEnum.INFO_CHANNEL, infoResponse.getId());
                                    CrackCityRaids.dbHandler.updateFaction(FactionManager.FactionToPOJO(infoCallbackFaction));
                                    infoResponse.sendMessage("Dit is het info kanaal van jouw faction, hier krijg je bijvoorbeeld " +
                                            "notificaties binnen over mensen die je faction hebben betreden.").queue((message ->
                                            message.delete().queueAfter(10, TimeUnit.MINUTES)));
                                };
                                createInfoChannel.queue(infoCallback);

                                ChannelAction<TextChannel> createChatChannel = categoryResponse.createTextChannel("chat");
                                Consumer<TextChannel> chatCallback = (chatResponse) -> {
                                    Faction chatCallbackFaction = CrackCityRaids.factionManager.getFaction(name);
                                    chatCallbackFaction.discordIdMap.put(DiscordIdEnum.CHAT_CHANNEL, chatResponse.getId());
                                    CrackCityRaids.dbHandler.updateFaction(FactionManager.FactionToPOJO(chatCallbackFaction));
                                };
                                createChatChannel.queue(chatCallback);

                                TextComponent successMessage = new TextComponent("Faction is succesvol aangemaakt!");
                                successMessage.setColor(ChatColor.GREEN);
                                commandContext.player.spigot().sendMessage(successMessage);
                            };
                            createCategory.queue(categoryCallback);
                        });
            };
            createRole.queue(roleCallback);
        }
    }
}
