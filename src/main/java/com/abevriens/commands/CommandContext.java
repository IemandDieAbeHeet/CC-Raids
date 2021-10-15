package com.abevriens.commands;

import com.abevriens.CC_Player;
import com.abevriens.FactionManager;
import com.abevriens.POJO_Player;
import com.abevriens.PlayerManager;
import org.bukkit.entity.Player;

public class CommandContext {
    Player player;
    CC_Player cc_player;
    POJO_Player pojo_player;
    FactionManager factionManager;
    PlayerManager playerManager;

    public CommandContext(Player _player, FactionManager _factionManager, PlayerManager _playerManager) {
        player = _player;
        factionManager = _factionManager;
        playerManager = _playerManager;
        cc_player = playerManager.getCCPlayer(player);
        pojo_player = playerManager.getPOJOPlayer(player);
    }
}
