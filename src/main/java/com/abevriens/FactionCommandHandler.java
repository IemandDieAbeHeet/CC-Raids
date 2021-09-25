package com.abevriens;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FactionCommandHandler implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            switch (args[0]) {
                case "create":
                    Faction faction = new Faction(player.getUniqueId(), args[1]);
                    CockCityRaids.instance.dbHandler.insertFaction(faction);
            };
            return  true;
        } else {
            return false;
        }
    }
}
