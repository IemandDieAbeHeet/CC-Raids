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

import java.util.UUID;

public class Factions_Join extends Factions_Base {
    public String name;

    public Factions_Join(Factions_Base factions_base, String _name) {
        super(factions_base.cc_player, factions_base.player, factions_base.pojo_player,
                factions_base.factionManager, factions_base.playerManager);
        name = _name;

        command_Join();
    }

    private void command_Join() {
        Faction faction = CrackCityRaids.instance.factionManager.getFaction(name);
        if(!cc_player.faction.factionName.equals(FactionManager.emptyFaction.factionName)) {
            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg("Je zit al in een faction, " +
                    "gebruik eerst /factions leave om je faction te verlaten.");
            player.spigot().sendMessage(errorMsg.create());
        } else if(!factionManager.factionNameList.contains(name)) {
            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg("De opgegeven faction bestaat niet!");
            player.spigot().sendMessage(errorMsg.create());
        } else if(faction.joinStatus == JoinStatus.REQUEST && cc_player.pendingRequests.contains(faction.factionName)) {
            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg("Je hebt al een join request open staan bij deze " +
                    "faction!");
            player.spigot().sendMessage(errorMsg.create());
        } else if(faction.isFull()) {
            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg("De faction die je probeert te joinen zit vol!");
            player.spigot().sendMessage(errorMsg.create());
        } else if(faction.joinStatus == JoinStatus.CLOSED) {
            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg("De faction die je probeert te joinen " +
                    "staat op gesloten!");
            player.spigot().sendMessage(errorMsg.create());
        } else if(faction.joinStatus == JoinStatus.REQUEST) {
            faction.playerJoinRequests.add(cc_player.uuid);
            cc_player.pendingRequests.add(faction.factionName);
            ComponentBuilder successMessage = TextUtil.GenerateSuccessMsg("Faction join request is verstuurd!");
            player.spigot().sendMessage(successMessage.create());
            CrackCityRaids.instance.dbHandler.updateFaction(FactionManager.FactionToPOJO(faction));

            TextComponent requestMsgText = new TextComponent(cc_player.displayName + " heeft gevraagd of ze je faction " +
                    "mogen joinen! ");
            requestMsgText.setColor(ChatColor.GOLD);

            TextComponent requestMsgButton = new TextComponent("[Accept]");
            requestMsgButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/factions accept "
                    + cc_player.displayName));
            requestMsgButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new Text("Klik om te accepteren!")));

            for(CC_Player factionMember : faction.players) {
                OfflinePlayer memberOfflinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(factionMember.uuid));
                if(memberOfflinePlayer.isOnline()) {
                    ComponentBuilder requestMsg = new ComponentBuilder().append(requestMsgText);
                    if(factionMember.uuid.equals(faction.factionOwner.uuid)) {
                        requestMsg.append(requestMsgButton);
                    }

                    memberOfflinePlayer.getPlayer().spigot().sendMessage(requestMsg.create());
                }
            }
        } else {
            Faction newFaction = CrackCityRaids.instance.factionManager.getFaction(name);
            playerManager.setPlayerFaction(Bukkit.getOfflinePlayer(player.getUniqueId()), newFaction);
            ComponentBuilder successMessage = TextUtil.GenerateSuccessMsg("Faction succesvol gejoined!");
            player.spigot().sendMessage(successMessage.create());
        }
    }
}
