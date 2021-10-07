package com.abevriens;

public class CC_Player {
    public String displayName;
    public String uuid;
    public Faction faction;
    public boolean confirmLeave = false;

    public CC_Player(String _displayName, String _uuid, Faction _faction) {
        displayName = _displayName;
        uuid = _uuid;
        faction = _faction;
    }
}
