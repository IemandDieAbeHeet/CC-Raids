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

        CC_Player cc_player = playerManager.getCCPlayer(player);

        if(cc_player != null) {
            CockCityRaids.instance.getLogger().info(cc_player.currentFaction.factionName);
        } else {
            POJO_Player pojo_player = new POJO_Player();
            pojo_player.faction = null;
            pojo_player.displayName = player.getDisplayName();
            pojo_player.uuid = player.getUniqueId().toString();
            CockCityRaids.instance.dbHandler.insertPlayer(pojo_player);
        }
    }
}