package com.abevriens;

import java.util.List;

public class POJO_RaidAlert {
    public String alertedFactionName;
    public int raidCountdown;
    public int maxRaidCountdown;
    public int openCountdown;
    public int maxOpenCountdown;
    public boolean raidCountdownStarted;
    public boolean openCountdownStarted;
    public List<String> enteredPlayerList;
    public List<String> enteredFactionList;

    public POJO_RaidAlert() {}
}
