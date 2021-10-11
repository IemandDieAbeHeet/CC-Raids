package com.abevriens;

import com.abevriens.commands.*;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

public class FactionCommandHandler implements CommandExecutor {
    Player player;
    POJO_Player pojo_player;
    CC_Player cc_player;
    @NotNull PlayerManager playerManager = CrackCityRaids.instance.playerManager;
    @NotNull FactionManager factionManager = CrackCityRaids.instance.factionManager;

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(sender instanceof Player) {
            player = (Player) sender;
            cc_player = playerManager.getCCPlayer(player);
            pojo_player = playerManager.getPOJOPlayer(player);
            Factions_Base factions_base = new Factions_Base(cc_player, player, pojo_player, factionManager, playerManager);
            if(args.length > 0) {
                label:
                switch (args[0].toLowerCase()) {
                    case "create":
                        if(args.length == 2) {
                            new Factions_Create(factions_base, args[1]);
                        } else if(args.length < 2) {
                            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg(
                                    "Geen faction naam opgegeven, gebruik het commando als volgt:",
                                    "/factions create [naam]");

                            player.spigot().sendMessage(errorMsg.create());
                        } else {
                            ComponentBuilder components = TextUtil.GenerateErrorMsg(
                                    "De naam van de faction mag geen spaties bevatten!");

                            player.spigot().sendMessage(components.create());
                        }
                        break;
                    case "info":
                        if(args.length > 1) {
                            new Factions_Info(factions_base, args[1]);
                        } else {
                            new Factions_Info(factions_base, cc_player.faction.factionName);
                        }
                        break;
                    case "help":
                        if(args.length > 1) {
                            new Factions_Help(factions_base, Integer.parseInt(args[1]));
                        } else {
                            new Factions_Help(factions_base, 1);
                        }
                        break;
                    case "join":
                        if(args.length >= 2) {
                            new Factions_Join(factions_base, args[1]);
                        } else {
                            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg(
                                    "Geen faction naam opgegeven, gebruik het commando als volgt:",
                                    "/factions join [naam]");

                            player.spigot().sendMessage(errorMsg.create());
                        }
                        break;
                    case "leave":
                        new Factions_Leave(factions_base);
                        break;
                    case "list":
                        if(args.length > 1) {
                            if(StringUtils.isNumeric(args[1])) {
                                new Factions_List(factions_base, Integer.parseInt(args[1]));
                            } else {
                                ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg(
                                        "Incorrect nummer opgegeven als tweede argument, gebruik het commando als volgt:",
                                        "/factions list [paginanummer]");

                                player.spigot().sendMessage(errorMsg.create());
                            }
                        } else {
                            new Factions_List(factions_base, 1);
                        }
                        break;
                    case "delete":
                        new Factions_Delete(factions_base);
                        break;
                    case "joinstatus":
                        if(args.length < 2) {
                            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg(
                                    "Geen status opgegeven, gebruik het commando als volgt:",
                                    "/factions joinstatus [open, request, close]");

                            player.spigot().sendMessage(errorMsg.create());
                            break;
                        }

                        switch (args[1]) {
                            case "open":
                            case "openbaar":
                                new Factions_SetJoinStatus(factions_base, JoinStatus.OPEN);
                                break label;
                            case "request":
                                new Factions_SetJoinStatus(factions_base, JoinStatus.REQUEST);
                                break label;
                            case "close":
                            case "closed":
                                new Factions_SetJoinStatus(factions_base, JoinStatus.CLOSED);
                                break label;
                            default:
                                ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg(
                                        "Incorrecte status opgegeven, gebruik het commando als volgt:",
                                        "/factions joinstatus [open, privé]");

                                player.spigot().sendMessage(errorMsg.create());
                                break label;
                        }
                    case "accept":
                        if(args.length > 1) {
                            new Factions_Accept(factions_base, args[1]);
                        } else {
                            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg(
                                    "Geen spelernaam opgegeven om te accepteren, gebruik het commando als volgt:",
                                    "/factions accept [naam]");

                            player.spigot().sendMessage(errorMsg.create());
                        }
                        break;
                    default:
                        new Factions_HelpError(factions_base, "Commando argument niet gevonden, probeer iets anders.");
                }
            } else {
                new Factions_Help(factions_base, 1);
            }
            return  true;
        } else {
            return false;
        }
    }
}
