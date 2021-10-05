package com.abevriens;

public class CC_Player {
    public String displayName;
    public String uuid;
    public Faction faction;

    public CC_Player(String _displayName, String _uuid, Faction _faction) {
        displayName = _displayName;
        uuid = _uuid;
        faction = _faction;
    }
}
