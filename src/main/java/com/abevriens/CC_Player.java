package com.abevriens;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class CC_Player implements Listener {
    @EventHandler (priority = EventPriority.LOW)
    public void onPlayerMove(PlayerMoveEvent event) {
        Chunk currChunk = event.getPlayer().getLocation().getChunk();
    }
}
