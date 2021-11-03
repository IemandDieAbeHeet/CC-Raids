package com.abevriens.commands;

import com.abevriens.*;
import com.abevriens.discord.DiscordIdEnum;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;

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
        if(factionName.equals(FactionManager.emptyFaction.factionName)) {
            ComponentBuilder errorMessage = TextUtil.GenerateErrorMsg("Je zit niet in een faction," +
                    " join er een met /factions join.");
            commandContext.player.spigot().sendMessage(errorMessage.create());
        }
        if(!commandContext.cc_player.faction.factionOwner.uuid.equals(commandContext.cc_player.uuid)) {
            ComponentBuilder errorMessage = TextUtil.GenerateErrorMsg("Je bent niet de owner van de faction. Als je " +
                    "het echt graag wilt moet je aan " + commandContext.cc_player.faction.factionOwner.displayName +
                    " vragen of ze jou owner geven.");
            commandContext.player.spigot().sendMessage(errorMessage.create());
        } else {
            commandContext.factionManager.factionList.remove(commandContext.cc_player.faction);
            commandContext.factionManager.factionNameList.remove(factionName);

            ArrayList<CC_Player> factionMembers = new ArrayList<>(commandContext.cc_player.faction.players);

            FactionCoreUtil.RemoveCore(commandContext);

            Objects.requireNonNull(CrackCityRaids.instance.discordManager.getGuild().getRoleById(commandContext.cc_player.faction.discordIdMap.get(DiscordIdEnum.ROLE))).delete().queue();
            Objects.requireNonNull(CrackCityRaids.instance.discordManager.getGuild().getTextChannelById(commandContext.cc_player.faction.discordIdMap.get(DiscordIdEnum.INFO_CHANNEL))).delete().queue();
            Objects.requireNonNull(CrackCityRaids.instance.discordManager.getGuild().getTextChannelById(commandContext.cc_player.faction.discordIdMap.get(DiscordIdEnum.CHAT_CHANNEL))).delete().queue();
            Objects.requireNonNull(CrackCityRaids.instance.discordManager.getGuild().getCategoryById(commandContext.cc_player.faction.discordIdMap.get(DiscordIdEnum.CATEGORY))).delete().queue();

            for(CC_Player factionMember : factionMembers) {
                CrackCityRaids.instance.playerManager.setPlayerFaction(Bukkit.getOfflinePlayer(UUID.fromString(factionMember.uuid)),
                        FactionManager.emptyFaction);
                factionMember.factionChatEnabled = false;
            }

            CrackCityRaids.instance.dbHandler.deleteFaction(factionName);

            ComponentBuilder successMessage = TextUtil.GenerateSuccessMsg("Faction is succesvol verwijderd!");
            commandContext.player.spigot().sendMessage(successMessage.create());
        }
    }
}
