package com.abevriens;

import com.abevriens.discord.DiscordIdEnum;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.util.EnumMap;
import java.util.List;
import java.util.UUID;

public class Faction {
    public String factionName;

    public List<CC_Player> players;

    public List<String> playerJoinRequests;

    public POJO_Player factionOwner;

    public JoinStatus joinStatus;

    public FactionCore factionCore;

    public int xSize;

    public int ySize;

    public List<Location> occupiedBlocks;

    public EnumMap<DiscordIdEnum, String> discordIdMap;

    public RaidAlert raidAlert;

    public Faction(POJO_Player _factionOwner, String _factionName, List<CC_Player> _players,
                   JoinStatus _joinStatus, List<String> _playerJoinRequests, FactionCore _factionCore,
                   int _xSize, int _ySize, List<Location> _occupiedBlocks, EnumMap<DiscordIdEnum, String> _discordIdMap,
                   RaidAlert _raidAlert) {
        factionOwner = _factionOwner;
        factionName = _factionName;
        players = _players;
        joinStatus = _joinStatus;
        playerJoinRequests = _playerJoinRequests;
        factionCore = _factionCore;
        xSize = _xSize;
        ySize = _ySize;
        occupiedBlocks = _occupiedBlocks;
        discordIdMap = _discordIdMap;
        raidAlert = _raidAlert;
    }

    public boolean isFull() {
        return players.size() >= 3;
    }

    public boolean isJoinable() {
        return !isFull() && joinStatus != JoinStatus.CLOSED;
    }

    public void sendMessageToPlayers(ComponentBuilder message) {
        for(CC_Player cc_player : players) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(cc_player.uuid));
            if(offlinePlayer.isOnline()) {
                offlinePlayer.getPlayer().spigot().sendMessage(message.create());
            }
        }
    }

    public boolean isRaidable() { return !raidAlert.openCountdownStarted && raidAlert.openCountdown > 1;  }

    public boolean isEmptyFaction() {
        return factionName.equals(FactionManager.emptyFaction.factionName);
    }
}
