package com.abevriens.commands;

import com.abevriens.*;
import com.abevriens.jda.DiscordIdEnum;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import net.dv8tion.jda.api.requests.restaction.RoleAction;
import net.dv8tion.jda.internal.requests.CompletedRestAction;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class Factions_Create {
    public String name;
    public CommandContext commandContext;

    public Factions_Create(CommandContext _commandContext, String _name) {
        name = _name;
        commandContext = _commandContext;

        command_Create();
    }

    private void command_Create() {
        if(!commandContext.cc_player.faction.isEmptyFaction()) {
            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg("Je zit al in een faction dus je kunt geen nieuwe " +
                    "faction aanmaken, verlaat deze met /factions leave.");
            commandContext.player.spigot().sendMessage(errorMsg.create());
        } else if(commandContext.factionManager.factionNameList.contains(name)) {
            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg("Een faction met deze naam bestaat al, " +
                    "kies een andere naam.");
            commandContext.player.spigot().sendMessage(errorMsg.create());
        } else {
            EnumMap<DiscordIdEnum, String> discordIdMap = new EnumMap<>(DiscordIdEnum.class);

            RoleAction createRole = CrackCityRaids.instance.discordManager.guild.createRole();
            createRole.setName(name).complete();
            Role role = createRole.complete();
            discordIdMap.put(DiscordIdEnum.ROLE, role.getId());

            ChannelAction<Category> createCategory = CrackCityRaids.instance.discordManager.guild.createCategory(
                    "Faction: " + name);
            Category category = createCategory.complete();
            discordIdMap.put(DiscordIdEnum.CATEGORY, category.getId());

            ChannelAction<TextChannel> createInfoChannel = category.createTextChannel("info");
            TextChannel infoChannel =  createInfoChannel.complete();
            discordIdMap.put(DiscordIdEnum.INFO_CHANNEL, infoChannel.getId());

            ChannelAction<TextChannel> createChatChannel = category.createTextChannel("chat");
            TextChannel chatChannel = createChatChannel.complete();
            discordIdMap.put(DiscordIdEnum.CHAT_CHANNEL, chatChannel.getId());

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

            TextComponent successMessage = new TextComponent("Faction is succesvol aangemaakt!");
            successMessage.setColor(ChatColor.GREEN);
            commandContext.player.spigot().sendMessage(successMessage);
        }
    }
}
