package com.abevriens;

import net.md_5.bungee.api.chat.ComponentBuilder;
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
        CC_Player cc_player = CrackCityRaids.instance.playerManager.getCCPlayer(player);

        FactionCore closestCore = CrackCityRaids.instance.factionCoreManager.getClosestFactionCore(event.getFrom());

        if(closestCore == null) return;

        Faction closestFaction = CrackCityRaids.instance.factionManager.getFaction(closestCore.factionName);

        if(cc_player.faction == closestFaction) return;

        if(isWithinBounds(event.getFrom(), closestCore)) {
            player.teleport(cc_player.previousLocation);
            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg(
                    "Je probeert een faction te betreden die niet van jou is");
            CrackCityRaids.instance.jdaManager.infoChannel.sendMessage("Ze zijn binnendfn!F#@$##!#!").queue();
            player.spigot().sendMessage(errorMsg.create());
        }

        cc_player.previousLocation = event.getFrom();
    }

    public boolean isWithinBounds(Location location, FactionCore factionCore) {
        int xLoc = location.getBlockX();
        int yLoc = location.getBlockY();
        int zLoc = location.getBlockZ();

        Faction faction = CrackCityRaids.instance.factionManager.getFaction(factionCore.factionName);
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
