package com.abevriens;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FactionCommandHandler implements CommandExecutor {
    @NotNull Player player;
    @NotNull POJO_Player pojo_player;
    @NotNull CC_Player cc_player;
    @NotNull PlayerManager playerManager = CrackCityRaids.instance.playerManager;
    @NotNull FactionManager factionManager = CrackCityRaids.instance.factionManager;

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            player = (Player) sender;
            cc_player = playerManager.getCCPlayer(player);
            pojo_player = playerManager.getPOJOPlayer(player);
            if(args.length > 0) {
                switch (args[0]) {
                    case "create":
                        if(args.length > 1) {
                            command_Create(args[1]);
                        } else {
                            ComponentBuilder components = TextUtil.GenerateErrorMsg(
                                    "Geen faction naam opgegeven, gebruik het commando als volgt:\n");

                            TextComponent commandText = new TextComponent("/factions create [naam]");
                            commandText.setBold(true);

                            player.spigot().sendMessage(components.append(commandText).create());
                        }
                        break;
                    case "info":
                        if(args.length > 1) {
                            command_Info(args[1]);
                        } else {
                            command_Info(cc_player.faction.factionName);
                        }
                        break;
                    case "help":
                        command_Help();
                        break;
                    case "join":
                        command_Join(args[1]);
                        break;
                    case "leave":
                        command_Leave();
                        break;
                    case "list":
                        if(args.length > 1) {
                            command_List(Integer.parseInt(args[1]));
                        } else {
                            command_List(1);
                        }
                        break;
                    default:
                        command_Help("Commando argument niet gevonden, probeer iets anders.");
                }
            } else {
                command_Help();
            }
            return  true;
        } else {
            return false;
        }
    }

    private void command_Info(String factionName) {
        Faction faction = CrackCityRaids.instance.factionManager.getFaction(factionName);

        if(faction.factionName == FactionManager.emptyFaction.factionName) {
            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg(
                    "Je zit nog niet in een faction, gebruik /factions join om er een te joinen.");

            player.spigot().sendMessage(errorMsg.create());
        } else {
            ComponentBuilder header = TextUtil.GenerateHeaderMsg("Info");
            ComponentBuilder footer = TextUtil.GenerateFooterMsg();

            BaseComponent[] nameInfo = new ComponentBuilder("Naam: ")
                    .color(ChatColor.GOLD).bold(true)
                    .append(new TextComponent(faction.factionName))
                    .color(ChatColor.WHITE).bold(false).create();

            BaseComponent[] ownerInfo = new ComponentBuilder("Owner: ")
                        .color(ChatColor.GOLD).bold(true)
                    .append(new TextComponent(faction.factionOwner.displayName))
                        .color(ChatColor.WHITE).bold(false).create();

            BaseComponent[] components = new ComponentBuilder()
                    .append(header.create())
                    .append(TextUtil.newLine)
                    .append(nameInfo)
                    .append(TextUtil.newLine)
                    .append(ownerInfo)
                    .append(TextUtil.newLine)
                    .append(footer.create()).create();

            player.spigot().sendMessage(components);
        }
    }

    private void command_Create(String name) {
        if(factionManager.factionNameList.contains(name)) {
            TextComponent errorMessage = new TextComponent("Een faction met deze naam bestaat al, kies een andere naam.");
            errorMessage.setColor(ChatColor.RED);
            player.spigot().sendMessage(errorMessage);
        } else {
            Faction faction = new Faction(
                    pojo_player,
                    name,
                    new ArrayList<POJO_Player>() {
                        {
                            add(pojo_player);
                        }
                    },
                    new ArrayList<Chunk>());

            POJO_Faction pojo_faction = FactionManager.FactionToPOJO(faction);
            pojo_player.factionName = faction.factionName;
            cc_player.faction = faction;
            CrackCityRaids.instance.dbHandler.insertFaction(pojo_faction);
            CrackCityRaids.instance.dbHandler.updatePlayer(pojo_player);
            factionManager.factionNameList.add(faction.factionName);

            TextComponent successMessage = new TextComponent("Faction is succesvol aangemaakt!");
            successMessage.setColor(ChatColor.GREEN);
            player.spigot().sendMessage(successMessage);
        }
    }

    private void command_Leave() {
        if(cc_player.faction.factionName == FactionManager.emptyFaction.factionName) {
            TextComponent errorMessage = new TextComponent("Je ziet niet in een faction, gebruik /faction join om een faction te joinen.");
            errorMessage.setColor(ChatColor.RED);
            player.spigot().sendMessage(errorMessage);
        } else {
            playerManager.setPlayerFaction(player, FactionManager.emptyFaction);
            TextComponent leaveMessage = new TextComponent("Faction succesvol verlaten.");
            leaveMessage.setColor(ChatColor.GREEN);
            player.spigot().sendMessage(leaveMessage);
        }
    }

    private void command_Join(String factionName) {
        TextComponent errorMessage = new TextComponent();
        errorMessage.setColor(ChatColor.RED);
        if(cc_player.faction.factionName != FactionManager.emptyFaction.factionName) {
            errorMessage.setText("Je zit al in een faction, gebruik eerst /factions leave om je faction te verlaten.");
            player.spigot().sendMessage(errorMessage);
        } else if(!factionManager.factionNameList.contains(factionName)) {
            errorMessage.setText("De opgegeven faction bestaat niet!");
            player.spigot().sendMessage(errorMessage);
        } else {
            Faction newFaction = CrackCityRaids.instance.factionManager.getFaction(factionName);
            playerManager.setPlayerFaction(player, newFaction);
        }
    }

    private void command_List(int page) {
        ComponentBuilder header = TextUtil.GenerateHeaderMsg("List");
        ComponentBuilder footer = TextUtil.GenerateFooterMsg();

        ComponentBuilder componentBuilder = new ComponentBuilder().append(header.create());

        int i = 1;
        ArrayList<Faction> list = (ArrayList<Faction>) CrackCityRaids.instance.factionManager.factionList;

        for(int j = (page-1) * 5; j < 5 + (page-1) * 5; j++) {
            if(list.get(j) == null) {
                break;
            }

            componentBuilder.append(TextUtil.newLine);
            TextComponent factionNumber = new TextComponent(i + ": ");
            factionNumber.setColor(ChatColor.GOLD);
            factionNumber.setBold(true);
            TextComponent factionInfo = new TextComponent(list.get(j).factionName + " - " + list.get(j).players.size());
            factionInfo.setBold(false);

            componentBuilder.append(factionNumber);
            componentBuilder.append(factionInfo);
            componentBuilder.append(TextUtil.newLine);
            i++;
        }

        componentBuilder.append(TextUtil.newLine)
                .append(footer.create());

        player.spigot().sendMessage(componentBuilder.create());
    }

    private void command_Help(String error) {
        TextComponent errorMessage = new TextComponent(error);
        errorMessage.setColor(ChatColor.RED);
        TextComponent helpText1 = new TextComponent("\n\nKlik ");
        TextComponent helpClick = new TextComponent("hier");
        helpClick.setBold(true);
        helpClick.setUnderlined(true);
        TextComponent helpText2 = new TextComponent(" voor alle commands.");
        helpText2.setBold(false);
        helpText2.setUnderlined(false);

        helpClick.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/factions help"));
        helpClick.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Alle commands")));

        BaseComponent[] components = new ComponentBuilder()
                .append(errorMessage)
                .append(helpText1)
                .append(helpClick)
                .append(helpText2)
                .event((ClickEvent) null)
                .event((HoverEvent) null)
                .create();

        player.spigot().sendMessage(components);
    }

    private void command_Help() {
        TextComponent messageHeader = new TextComponent("===================  Help  ====================");
        messageHeader.setColor(ChatColor.AQUA);
        messageHeader.setBold(true);
        TextComponent messageFooter = new TextComponent("=============================================");
        messageFooter.setColor(ChatColor.AQUA);
        messageFooter.setBold(true);
        TextComponent newLine = new TextComponent("\n\n");

        TextComponent helpText = new TextComponent("Deze command moet nog gemaakt worden :O");
        helpText.setBold(false);
        helpText.setColor(ChatColor.GREEN);

        BaseComponent[] components = new ComponentBuilder()
                .append(messageHeader)
                .append(newLine)
                .append(helpText)
                .append(newLine)
                .append(messageFooter).create();

        player.spigot().sendMessage(components);
    }
}
