package com.abevriens;

import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class FactionCommandHandler implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            PlayerManager playerManager = CockCityRaids.instance.playerManager;
            final POJO_Player pojo_player = playerManager.getPOJOPlayer(player);
            CC_Player cc_player = playerManager.getCCPlayer(player);
            switch (args[0]) {
                case "create":
                    Faction faction = new Faction(
                            pojo_player,
                            args[1],
                            new ArrayList<POJO_Player>() { { add(pojo_player); } },
                            new ArrayList<Chunk>());

                    POJO_Faction pojo_faction = FactionManager.FactionToPOJO(faction);

                    cc_player.currentFaction = faction;
                    pojo_player.faction = pojo_faction;
                    CockCityRaids.instance.dbHandler.insertFaction(pojo_faction);
                    CockCityRaids.instance.dbHandler.updatePlayer(pojo_player);

                case "info":
                    player.sendMessage(cc_player.currentFaction.factionName);
                    player.sendMessage(cc_player.currentFaction.factionOwner.displayName);
            };
            return  true;
        } else {
            return false;
        }
    }
}
