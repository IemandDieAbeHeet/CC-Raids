package com.abevriens;

import com.abevriens.commands.Factions_List;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FactionCommandTabCompleter implements TabCompleter {
    final ArrayList<String> allCommands = new ArrayList<String>() {
        {
            add("joinstatus");
            add("requests");
            add("join");
            add("list");
            add("kick");
            add("create");
            add("info");
            add("accept");
            add("leave");
            add("help");
            add("delete");
            add("setowner");
        }
    };

    final ArrayList<String> noFactionCommands = new ArrayList<String>() {
        {
            add("join");
            add("list");
            add("create");
            add("info");
            add("help");
        }
    };

    final ArrayList<String> normalFactionCommands = new ArrayList<String>() {
        {
            add("requests");
            add("list");
            add("leave");
            add("info");
            add("help");
        }
    };

    final ArrayList<String> ownerCommands = new ArrayList<String>() {
        {
            add("joinstatus");
            add("requests");
            add("list");
            add("kick");
            add("info");
            add("accept");
            add("help");
            add("delete");
            add("setowner");
        }
    };


    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            List<String> names = new ArrayList<>();
            for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                names.add(offlinePlayer.getName());
            }

            CC_Player cc_player = CrackCityRaids.instance.playerManager.getCCPlayer(player);
            if (args.length == 1) {
                if (cc_player.faction.factionName.equals(FactionManager.emptyFaction.factionName)) {
                    return noFactionCommands;
                } else if (cc_player.faction.factionOwner.uuid.equals(cc_player.uuid)) {
                    return ownerCommands;
                } else {
                    return normalFactionCommands;
                }
            } else if(args.length == 2) {
                switch(args[0]) {
                    case "joinstatus":
                        return GetJoinStatusStrings();
                    case "requests":
                        return GetPageStrings((int)Math.ceil((double) cc_player.faction.playerJoinRequests.size() / 8));
                    default:
                        return GetOnlinePlayerNames();
                }
            }

            return names;
        }

        return GetOnlinePlayerNames();
    }

    public List<String> GetPageStrings(int lastPage) {
        List<String> pages = new ArrayList<>();
        for(int i = 1; i < lastPage; i++) {
            pages.add(Integer.toString(i));
        }
        return pages;
    }

    public List<String> GetJoinStatusStrings() {
        List<String> statuses = new ArrayList<>();
        for(JoinStatus joinStatus : JoinStatus.values()) {
            switch (joinStatus) {
                case OPEN:
                    statuses.add("open");
                case CLOSED:
                    statuses.add("closed");
                case REQUEST:
                    statuses.add("request");
            }
        }
        return  statuses;
    }

    public List<String> GetOnlinePlayerNames() {
        List<String> names = new ArrayList<>();
        for(Player player : Bukkit.getOnlinePlayers()) {
            names.add(player.getName());
        }
        return names;
    }

    public List<String> GetOfflinePlayerNames() {
        List<String> names = new ArrayList<>();
        for(OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            names.add(offlinePlayer.getName());
        }
        return names;
    }
}