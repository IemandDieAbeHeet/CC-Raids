package com.abevriens.commands;

import com.abevriens.CC_Player;
import com.abevriens.FactionManager;
import com.abevriens.POJO_Player;
import com.abevriens.PlayerManager;
import org.bukkit.entity.Player;

public class Factions_Base {
    CC_Player cc_player;
    Player player;
    POJO_Player pojo_player;
    FactionManager factionManager;
    PlayerManager playerManager;

    public Factions_Base(CC_Player _cc_player, Player _player, POJO_Player _pojo_player,
                         FactionManager _factionManager, PlayerManager _playerManager) {
        cc_player = _cc_player;
        player = _player;
        pojo_player = _pojo_player;
        factionManager = _factionManager;
        playerManager = _playerManager;
    }
}
