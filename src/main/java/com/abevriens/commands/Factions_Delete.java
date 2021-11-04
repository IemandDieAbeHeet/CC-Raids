package com.abevriens.commands;

import com.abevriens.*;
import com.abevriens.discord.DiscordIdEnum;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class Factions_Delete {
    public CommandContext commandContext;

    public Factions_Delete(CommandContext _commandContext) {
        commandContext = _commandContext;

        command_Delete();
    }

    public void command_Delete() {
        String factionName = commandContext.cc_player.faction.factionName;
        if (factionName.equals(FactionManager.emptyFaction.factionName)) {
            ComponentBuilder errorMessage = TextUtil.GenerateErrorMsg("Je zit niet in een faction," +
                    " join er een met /factions join.");
            commandContext.player.spigot().sendMessage(errorMessage.create());
        } else if (!commandContext.cc_player.faction.factionOwner.uuid.equals(commandContext.cc_player.uuid)) {
            ComponentBuilder errorMessage = TextUtil.GenerateErrorMsg("Je bent niet de owner van de faction. Als je " +
                    "het echt graag wilt moet je aan " + commandContext.cc_player.faction.factionOwner.displayName +
                    " vragen of ze jou owner geven.");
            commandContext.player.spigot().sendMessage(errorMessage.create());
        } else {
            ArrayList<CC_Player> factionMembers = new ArrayList<>(commandContext.cc_player.faction.players);

            FactionCoreUtil.RemoveCore(commandContext);

            if (commandContext.cc_player.discordId != null && !commandContext.cc_player.faction.discordIdMap.isEmpty()) {
                String roleId = commandContext.cc_player.faction.discordIdMap.get(DiscordIdEnum.ROLE);
                String infoChannelId = commandContext.cc_player.faction.discordIdMap.get(DiscordIdEnum.INFO_CHANNEL);
                String chatChannelId = commandContext.cc_player.faction.discordIdMap.get(DiscordIdEnum.CHAT_CHANNEL);
                String categoryId = commandContext.cc_player.faction.discordIdMap.get(DiscordIdEnum.CATEGORY);

                Guild guild = CrackCityRaids.discordManager.getGuild();

                if (guild != null) {
                    if (roleId != null && infoChannelId != null && chatChannelId != null && categoryId != null) {
                        Role role = guild.getRoleById(roleId);
                        TextChannel infoChannel = guild.getTextChannelById(infoChannelId);
                        TextChannel chatChannel = guild.getTextChannelById(chatChannelId);
                        Category category = guild.getCategoryById(categoryId);

                        ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg(
                                "Er is iets fout gegaan tijdens het verwijderen van je faction.");
                        if(role == null || infoChannel == null || chatChannel == null || category == null) {
                            commandContext.player.spigot().sendMessage(errorMsg.create());
                            return;
                        }

                        role.delete().submit()
                            .thenCompose((v) -> infoChannel.delete().submit())
                            .thenCompose((v) -> chatChannel.delete().submit())
                            .thenCompose((v) -> category.delete().submit())
                            .whenComplete((s, error) -> {
                                if (error == null) {
                                    Faction faction = commandContext.cc_player.faction;

                                    for (CC_Player factionMember : factionMembers) {
                                        CrackCityRaids.playerManager.setPlayerFaction(Bukkit.getOfflinePlayer(
                                                UUID.fromString(factionMember.uuid)), FactionManager.emptyFaction);
                                    }

                                    commandContext.factionManager.factionList.remove(faction);
                                    commandContext.factionManager.factionNameList.remove(factionName);

                                    CrackCityRaids.dbHandler.deleteFaction(factionName);

                                    ComponentBuilder successMessage = TextUtil.GenerateSuccessMsg(
                                            "Faction is succesvol verwijderd!");
                                    commandContext.player.spigot().sendMessage(successMessage.create());
                                } else {
                                    commandContext.player.spigot().sendMessage(errorMsg.create());
                                }
                            });
                    }
                }
            }
        }
    }
}