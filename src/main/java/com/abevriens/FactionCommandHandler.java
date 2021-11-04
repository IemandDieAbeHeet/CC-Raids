package com.abevriens;

import com.abevriens.commands.*;
import com.abevriens.commands.factioncore.Factions_CreateFactionCore;
import com.abevriens.commands.factioncore.Factions_DeleteFactionCore;
import com.abevriens.commands.factioncore.Factions_SetFactionCore;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FactionCommandHandler implements CommandExecutor {

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;

            CommandContext commandContext = new CommandContext(player, CrackCityRaids.factionManager, CrackCityRaids.playerManager);

            if(args.length > 0) {
                label:
                switch (args[0].toLowerCase()) {
                    case "create":
                        if(args.length == 2) {
                            new Factions_Create(commandContext, args[1]);
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
                            new Factions_Info(commandContext, args[1]);
                        } else {
                            new Factions_Info(commandContext, CrackCityRaids.playerManager.getCCPlayer(player).faction.factionName);
                        }
                        break;
                    case "help":
                        if(args.length > 1) {
                            new Factions_Help(commandContext, Integer.parseInt(args[1]));
                        } else {
                            new Factions_Help(commandContext, 1);
                        }
                        break;
                    case "join":
                        if(args.length >= 2) {
                            new Factions_Join(commandContext, args[1]);
                        } else {
                            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg(
                                    "Geen faction naam opgegeven, gebruik het commando als volgt:",
                                    "/factions join [naam]");

                            player.spigot().sendMessage(errorMsg.create());
                        }
                        break;
                    case "leave":
                        new Factions_Leave(commandContext);
                        break;
                    case "list":
                        if(args.length > 1) {
                            if(StringUtils.isNumeric(args[1])) {
                                new Factions_List(commandContext, Integer.parseInt(args[1]));
                            } else {
                                ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg(
                                        "Incorrect nummer opgegeven als tweede argument, gebruik het commando als volgt:",
                                        "/factions list [paginanummer]");

                                player.spigot().sendMessage(errorMsg.create());
                            }
                        } else {
                            new Factions_List(commandContext, 1);
                        }
                        break;
                    case "delete":
                        new Factions_Delete(commandContext);
                        break;
                    case "joinstatus":
                        if(args.length < 2) {
                            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg(
                                    "Geen status opgegeven, gebruik het commando als volgt:",
                                    "/factions joinstatus [open, request, close]");

                            player.spigot().sendMessage(errorMsg.create());
                            break;
                        }

                        switch (args[1].toLowerCase()) {
                            case "open":
                            case "openbaar":
                                new Factions_SetJoinStatus(commandContext, JoinStatus.OPEN);
                                break label;
                            case "request":
                                new Factions_SetJoinStatus(commandContext, JoinStatus.REQUEST);
                                break label;
                            case "close":
                            case "closed":
                                new Factions_SetJoinStatus(commandContext, JoinStatus.CLOSED);
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
                            new Factions_Accept(commandContext, args[1]);
                        } else {
                            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg(
                                    "Geen spelernaam opgegeven om te accepteren, gebruik het commando als volgt:",
                                    "/factions accept [naam]");

                            player.spigot().sendMessage(errorMsg.create());
                        }
                        break;
                    case "request":
                    case "requests":
                        if(args.length > 1) {
                            if(StringUtils.isNumeric(args[1])) {
                                new Factions_Requests(commandContext, Integer.parseInt(args[1]));
                            } else {
                                ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg(
                                        "Incorrect nummer opgegeven als tweede argument, gebruik het commando als volgt:",
                                        "/factions requests [paginanummer]");

                                player.spigot().sendMessage(errorMsg.create());
                            }
                        } else {
                            new Factions_Requests(commandContext, 1);
                        }
                        break;
                    case "setowner":
                        if(args.length > 1) {
                            new Factions_SetOwner(commandContext, args[1]);
                            break;
                        } else {
                            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg(
                                    "Geen spelernaam opgegeven om owner te geven, gebruik het commando als volgt:",
                                    "/factions setowner [naam]");

                            player.spigot().sendMessage(errorMsg.create());
                        }
                        break;
                    case "kick":
                        if(args.length > 1) {
                            new Factions_Kick(commandContext, args[1]);
                            break;
                        } else {
                            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg(
                                    "Geen spelernaam opgegeven om te kicken, gebruik het commando als volgt:",
                                    "/factions setowner [naam]");

                            player.spigot().sendMessage(errorMsg.create());
                        }
                        break;
                    case "core":
                    case "factioncore":
                        if(args.length < 2) {
                            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg(
                                    "Geen subcommando opgegeven, gebruik het commando als volgt:",
                                    "/factions core [commando]");

                            player.spigot().sendMessage(errorMsg.create());
                            break;
                        }

                        switch(args[1].toLowerCase()) {
                            case "create":
                                new Factions_CreateFactionCore(commandContext);
                                break;
                            case "set":
                                new Factions_SetFactionCore(commandContext);
                                break;
                            case "delete":
                                new Factions_DeleteFactionCore(commandContext);
                                break;
                            default:
                                ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg(
                                        "Incorrect subcommando opgegeven, bekijk alle subcommando's met /factions help");

                                player.spigot().sendMessage(errorMsg.create());
                                break;
                        }
                        break;
                    case "linkdiscord":
                    case "link":
                        if(args.length > 1) {
                            new Factions_LinkDiscord(commandContext, args[1]);
                        } else {
                            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg(
                                    "Geen Discord username opgegeven, gebruik het commando als volgt:",
                                    "/factions link [naam]");
                            player.spigot().sendMessage(errorMsg.create());
                        }
                        break;
                    case "confirmalert":
                        if(args.length > 1) {
                            new Factions_ConfirmRaidAlert(commandContext, args[1]);
                        } else {
                            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg(
                                    "Geen faction naam opgegeven, gebruik het commando als volgt:",
                                    "/factions confirmalert [faction]");
                            player.spigot().sendMessage(errorMsg.create());
                        }
                        break;
                    default:
                        new Factions_HelpError(commandContext, "Commando argument niet gevonden, probeer iets anders.");
                }
            } else {
                new Factions_Help(commandContext, 1);
            }
            return  true;
        } else {
            return false;
        }
    }
}