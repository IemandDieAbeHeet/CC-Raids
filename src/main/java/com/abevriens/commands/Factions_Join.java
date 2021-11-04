package com.abevriens.commands;

import com.abevriens.*;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Factions_Join {
    public String name;
    public CommandContext commandContext;

    public Factions_Join(CommandContext _commandContext, String _name) {
        name = _name;
        commandContext = _commandContext;

        command_Join();
    }

    private void command_Join() {
        Faction faction = CrackCityRaids.factionManager.getFaction(name);
        if(commandContext.cc_player.discordId == null) {
            TextUtil.SendDiscordLinkError(commandContext.player);
        } else if(!commandContext.cc_player.faction.isEmptyFaction()) {
            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg("Je zit al in een faction, " +
                    "gebruik eerst /factions leave om je faction te verlaten.");
            commandContext.player.spigot().sendMessage(errorMsg.create());
        } else if(!commandContext.factionManager.factionNameList.contains(name)) {
            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg("De opgegeven faction bestaat niet!");
            commandContext.player.spigot().sendMessage(errorMsg.create());
        } else if(faction.joinStatus == JoinStatus.REQUEST && commandContext.cc_player.pendingRequests.contains(faction.factionName)) {
            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg("Je hebt al een join request open staan bij deze " +
                    "faction!");
            commandContext.player.spigot().sendMessage(errorMsg.create());
        } else if(!commandContext.cc_player.lastFactionChange.isBefore(Instant.now().minusSeconds(10))) {
            ComponentBuilder errorMessage = TextUtil.GenerateErrorMsg("Je verandert te snel van faction, wacht 10 " +
                    "seconden en probeer het dan opnieuw.");
            commandContext.player.spigot().sendMessage(errorMessage.create());
        } else if(faction.isFull()) {
            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg("De faction die je probeert te joinen zit vol!");
            commandContext.player.spigot().sendMessage(errorMsg.create());
        } else if(faction.joinStatus == JoinStatus.CLOSED) {
            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg("De faction die je probeert te joinen " +
                    "staat op gesloten!");
            commandContext.player.spigot().sendMessage(errorMsg.create());
        } else if(faction.joinStatus == JoinStatus.REQUEST) {
            faction.playerJoinRequests.add(commandContext.cc_player.uuid);
            commandContext.cc_player.pendingRequests.add(faction.factionName);
            ComponentBuilder successMessage = TextUtil.GenerateSuccessMsg("Faction join request is verstuurd!");
            commandContext.player.spigot().sendMessage(successMessage.create());
            CrackCityRaids.dbHandler.updateFaction(FactionManager.FactionToPOJO(faction));

            TextComponent requestMsgText = new TextComponent(commandContext.cc_player.displayName + " heeft gevraagd of ze je faction " +
                    "mogen joinen! ");
            requestMsgText.setColor(ChatColor.GOLD);

            TextComponent requestMsgButton = new TextComponent("[Accept]");
            requestMsgButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/factions accept "
                    + commandContext.cc_player.displayName));
            requestMsgButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new Text("Klik om te accepteren!")));

            for(CC_Player factionMember : faction.players) {
                OfflinePlayer memberOfflinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(factionMember.uuid));
                if(memberOfflinePlayer.isOnline()) {
                    ComponentBuilder requestMsg = new ComponentBuilder().append(requestMsgText);
                    if(factionMember.uuid.equals(faction.factionOwner.uuid)) {
                        requestMsg.append(requestMsgButton);
                    }

                    Objects.requireNonNull(memberOfflinePlayer.getPlayer()).spigot().sendMessage(requestMsg.create());
                }
            }
        } else {
            Faction newFaction = CrackCityRaids.factionManager.getFaction(name);
            commandContext.cc_player.lastFactionChange = Instant.now();
            commandContext.playerManager.setPlayerFaction(Bukkit.getOfflinePlayer(commandContext.player.getUniqueId()), newFaction);
            ComponentBuilder successMessage = TextUtil.GenerateSuccessMsg("Faction succesvol gejoined!");
            commandContext.player.spigot().sendMessage(successMessage.create());
        }
    }
}
