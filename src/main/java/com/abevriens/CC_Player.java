package com.abevriens;

import org.bukkit.entity.Player;

public class CC_Player {
    public Player player;

    public Faction currentFaction;

    public CC_Player(Player _player, Faction _faction) {
        player = _player;
        currentFaction = _faction;
    }
}
