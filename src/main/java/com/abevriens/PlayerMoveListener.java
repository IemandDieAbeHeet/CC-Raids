package com.abevriens;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        CC_Player cc_player = CrackCityRaids.playerManager.getCCPlayer(player);

        FactionCore closestCore = CrackCityRaids.factionCoreManager.getClosestFactionCore(event.getFrom());

        if(closestCore == null) return;

        if(!player.getWorld().getName().equals("world")) return;

        Faction closestFaction = CrackCityRaids.factionManager.getFaction(closestCore.factionName);

        if(cc_player.faction == closestFaction) return;

        if(isWithinBounds(event.getFrom(), closestCore)) {
            ComponentBuilder errorMsg;
            if(closestFaction.raidAlert.openCountdownStarted) {
                ComponentBuilder successMsg = TextUtil.GenerateSuccessMsg("Je bent de faction " +
                        closestFaction.factionName + " betreden. Raid alles binnen " + FactionManager.generateCountdownTimeString(
                                closestFaction.raidAlert.openCountdown));

                if(!cc_player.isWithinFactionBounds) {
                    player.spigot().sendMessage(successMsg.create());
                }

                cc_player.isWithinFactionBounds = true;
            } else if(closestFaction.raidAlert.raidCountdownStarted) {
                errorMsg = TextUtil.GenerateErrorMsg("Je probeert een faction te betreden die niet " +
                        "van jou is. Er is al een raid timer gestart en je kunt over " +
                        FactionManager.generateCountdownTimeString(closestFaction.raidAlert.raidCountdown) +
                        " beginnen met raiden.");

                if(!closestFaction.raidAlert.enteredPlayerList.contains(cc_player.displayName)) {
                    closestFaction.raidAlert.enteredPlayerList.add(cc_player.displayName);
                }
                if(!closestFaction.raidAlert.enteredFactionList.contains(cc_player.faction.factionName)) {
                    closestFaction.raidAlert.enteredFactionList.add(cc_player.faction.factionName);
                }
                
                if (closestFaction.discordIdMap != null) {
                    closestFaction.raidAlert.updateRaidTimerMessage();
                }
                player.spigot().sendMessage(errorMsg.create());
                player.teleport(cc_player.previousLocation);
            } else {
                closestFaction.raidAlert.playersAllowedToConfirm.add(cc_player.uuid);

                ComponentBuilder confirmRaidAlertMsg = new ComponentBuilder();

                TextComponent confirmMsgText = new TextComponent("Je bent binnen de base van faction "
                        + closestFaction.factionName + "!\n Wil je een raid alert versturen? ");
                confirmMsgText.setColor(ChatColor.GOLD);

                TextComponent requestMsgButton = new TextComponent("[Verstuur]");
                requestMsgButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/factions confirmalert "
                        + closestFaction.factionName));
                requestMsgButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new Text("Klik om te versturen!")));
                requestMsgButton.setBold(true);

                confirmRaidAlertMsg.append(confirmMsgText).append(requestMsgButton);

                player.spigot().sendMessage(confirmRaidAlertMsg.create());
                player.teleport(cc_player.previousLocation);
            }
        } else {
            cc_player.isWithinFactionBounds = false;
        }

        cc_player.previousLocation = event.getFrom();
    }

    public boolean isWithinBounds(Location location, FactionCore factionCore) {
        int xLoc = location.getBlockX();
        int yLoc = location.getBlockY();
        int zLoc = location.getBlockZ();

        Faction faction = CrackCityRaids.factionManager.getFaction(factionCore.factionName);
        int blockMinX = factionCore.blockLocation.getBlockX() - faction.xSize/2;
        int blockMaxX = factionCore.blockLocation.getBlockX() + faction.xSize/2;
        int blockMinY = factionCore.blockLocation.getBlockY() - faction.ySize/2;
        int blockMaxY = factionCore.blockLocation.getBlockY() + faction.ySize/2;
        int blockMinZ = factionCore.blockLocation.getBlockZ() - faction.xSize/2;
        int blockMaxZ = factionCore.blockLocation.getBlockZ() + faction.xSize/2;
        return xLoc > blockMinX && xLoc < blockMaxX &&
                yLoc > blockMinY && yLoc < blockMaxY &&
                zLoc > blockMinZ && zLoc < blockMaxZ;
    }
}
