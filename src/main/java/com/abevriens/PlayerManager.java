package com.abevriens;

import com.abevriens.discord.DiscordIdEnum;
import net.dv8tion.jda.api.entities.Role;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerManager {
    private final HashMap<OfflinePlayer, CC_Player> CCPlayerHashMap = new HashMap<>();
    private final HashMap<OfflinePlayer, POJO_Player> POJOPlayerHashMap = new HashMap<>();
    private final HashMap<String, CC_Player> CCPlayerStringHashMap = new HashMap<>();
    private final HashMap<String, String> playerDiscordRequests = new HashMap<>();

    public void LoadPlayers() {
        for (POJO_Player pojo_player : CrackCityRaids.dbHandler.playerCollection.find()) {
            if (!CrackCityRaids.factionManager.factionNameList.contains(pojo_player.factionName)) {
                pojo_player.factionName = FactionManager.emptyFaction.factionName;
            }
            CrackCityRaids.dbHandler.updatePlayer(pojo_player);
            CC_Player cc_player = POJOToCC(pojo_player);
            OfflinePlayer player = POJOToPlayer(pojo_player);

            POJOPlayerHashMap.put(player, pojo_player);
            CCPlayerHashMap.put(player, cc_player);
            CCPlayerStringHashMap.put(cc_player.displayName, cc_player);
        }
    }

    public boolean playerExists(Player player) {
        return getPOJOPlayer(player) != null;
    }

    public void addPlayer(Player player, POJO_Player pojo_player, CC_Player cc_player) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
        CCPlayerHashMap.put(offlinePlayer, cc_player);
        POJOPlayerHashMap.put(offlinePlayer, pojo_player);
        CCPlayerStringHashMap.put(cc_player.displayName, cc_player);
    }

    public POJO_Player getPOJOPlayer(Player player) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
        return POJOPlayerHashMap.get(offlinePlayer);
    }

    public POJO_Player getPOJOPlayer(OfflinePlayer offlinePlayer) {
        return POJOPlayerHashMap.get(offlinePlayer);
    }

    public CC_Player getCCPlayer(Player player) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
        return CCPlayerHashMap.get(offlinePlayer);
    }

    public CC_Player getCCPlayer(String displayName) {
        return CCPlayerStringHashMap.get(displayName);
    }

    public CC_Player getCCPlayer(OfflinePlayer offlinePlayer) {
        return CCPlayerHashMap.get(offlinePlayer);
    }

    public void setPlayerFaction(OfflinePlayer offlinePlayer, Faction faction) {
        POJO_Player pojo_player = getPOJOPlayer(offlinePlayer);
        CC_Player cc_player = getCCPlayer(offlinePlayer);

        if(faction.isEmptyFaction()) {
            cc_player.faction.players.remove(cc_player);
            Role role = CrackCityRaids.discordManager.getGuild().getRoleById(cc_player.faction.discordIdMap.get(DiscordIdEnum.ROLE));
            if(role != null) {
                CrackCityRaids.discordManager.getGuild().removeRoleFromMember(cc_player.discordId, role).queue();
            }
            CrackCityRaids.dbHandler.updateFaction(FactionManager.FactionToPOJO(cc_player.faction));
        } else {
            faction.players.add(cc_player);
            Role role = CrackCityRaids.discordManager.getGuild().getRoleById(faction.discordIdMap.get(DiscordIdEnum.ROLE));
            if(role != null) {
                CrackCityRaids.discordManager.getGuild().addRoleToMember(cc_player.discordId, role).queue();
            }
            CrackCityRaids.dbHandler.updateFaction(FactionManager.FactionToPOJO(faction));
        }

        cc_player.faction = faction;
        pojo_player.factionName = faction.factionName;

        CCPlayerHashMap.put(offlinePlayer, cc_player);
        POJOPlayerHashMap.put(offlinePlayer, pojo_player);
        CCPlayerStringHashMap.put(cc_player.displayName, cc_player);
        CrackCityRaids.dbHandler.updatePlayer(pojo_player);
    }

    public static OfflinePlayer POJOToPlayer(POJO_Player pojo_player) {
        return Bukkit.getOfflinePlayer(UUID.fromString(pojo_player.uuid));
    }

    public static CC_Player POJOToCC(POJO_Player pojo_player) {
        Faction faction = CrackCityRaids.factionManager.getFaction(pojo_player.factionName);

        return new CC_Player(
            pojo_player.displayName,
            pojo_player.uuid,
            faction,
            pojo_player.pendingRequests,
            pojo_player.discordId
        );
    }

    public static POJO_Player CCToPOJO(CC_Player cc_player) {
        POJO_Player pojo_player = new POJO_Player();

        pojo_player.displayName = cc_player.displayName;
        pojo_player.factionName = cc_player.faction.factionName;
        pojo_player.uuid = cc_player.uuid;
        pojo_player.pendingRequests = cc_player.pendingRequests;
        pojo_player.discordId = cc_player.discordId;

        return pojo_player;
    }

    public void addDiscordRequest(String discordId, String playerUUID) {
        playerDiscordRequests.put(discordId, playerUUID);
    }

    public boolean containsDiscordRequest(String playerUUID) {
        return playerDiscordRequests.containsValue(playerUUID);
    }

    public String getDiscordRequest(String discordId) {
        return playerDiscordRequests.get(discordId);
    }

    public void removeDiscordRequest(String discordId) {
        playerDiscordRequests.remove(discordId);
    }
}