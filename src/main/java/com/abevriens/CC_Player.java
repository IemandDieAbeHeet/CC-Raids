package com.abevriens;

import org.bukkit.entity.Player;

public class CC_Player {
    public String displayName;
    public String uuid;
    public String factionName;

    public CC_Player(String _displayName, String _uuid, String _factionName) {
        displayName = _displayName;
        uuid = _uuid;
        factionName = _factionName;
    }
}
