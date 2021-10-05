package com.abevriens;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        PlayerManager playerManager = CockCityRaids.instance.playerManager;
        Player player = event.getPlayer();

        if (playerManager.playerExists(player)) return;

        POJO_Player pojo_player = new POJO_Player();
        pojo_player.factionName = FactionManager.emptyFaction.factionName;
        pojo_player.displayName = player.getDisplayName();
        pojo_player.uuid = player.getUniqueId().toString();

        CC_Player cc_player = new CC_Player(
                player.getDisplayName(),
                player.getUniqueId().toString(),
                FactionManager.emptyFaction
        );

        playerManager.addPlayer(player, pojo_player, cc_player);
        CockCityRaids.instance.dbHandler.insertPlayer(pojo_player);
    }
}