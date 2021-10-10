package com.abevriens;

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
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

public class FactionCommandHandler implements CommandExecutor {
    Player player;
    POJO_Player pojo_player;
    CC_Player cc_player;
    @NotNull PlayerManager playerManager = CrackCityRaids.instance.playerManager;
    @NotNull FactionManager factionManager = CrackCityRaids.instance.factionManager;
    final int LIST_CHAT_SIZE = 8;

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(sender instanceof Player) {
            player = (Player) sender;
            cc_player = playerManager.getCCPlayer(player);
            pojo_player = playerManager.getPOJOPlayer(player);
            if(args.length > 0) {
                switch (args[0].toLowerCase()) {
                    case "create":
                        if(args.length == 2) {
                            command_Create(args[1]);
                        } else if(args.length < 2) {
                            ComponentBuilder components = TextUtil.GenerateErrorMsg(
                                    "Geen faction naam opgegeven, gebruik het commando als volgt:\n");

                            TextComponent commandText = new TextComponent("/factions create [naam]");
                            commandText.setBold(true);

                            player.spigot().sendMessage(components.append(commandText).create());
                        } else if(args.length > 2) {
                            ComponentBuilder components = TextUtil.GenerateErrorMsg(
                                    "De naam van de faction mag geen spaties bevatten!");

                            player.spigot().sendMessage(components.create());
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
                        if(args.length > 1) {
                            command_Help(Integer.parseInt(args[1]));
                        } else {
                            command_Help(1);
                        }
                        break;
                    case "join":
                        if(args.length >= 2) {
                            command_Join(args[1]);
                        } else if(args.length < 2) {
                            ComponentBuilder components = TextUtil.GenerateErrorMsg(
                                    "Geen faction naam opgegeven, gebruik het commando als volgt:\n");

                            TextComponent commandText = new TextComponent("/factions join [naam]");
                            commandText.setBold(true);
                            components.append(commandText);
                            player.spigot().sendMessage(components.create());
                        }
                        break;
                    case "leave":
                        command_Leave();
                        break;
                    case "list":
                        if(args.length > 1) {
                            if(StringUtils.isNumeric(args[1])) {
                                command_List(Integer.parseInt(args[1]));
                            } else {
                                ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg("Geef een correct nummer op als tweede " +
                                        "argument!");

                                player.spigot().sendMessage(errorMsg.create());
                            }
                        } else {
                            command_List(1);
                        }
                        break;
                    case "delete":
                        command_Delete();
                        break;
                    case "joinstatus":
                        if(args.length < 2) {
                            ComponentBuilder components = TextUtil.GenerateErrorMsg(
                                    "Geen status opgegeven, gebruik het commando als volgt:\n");

                            TextComponent commandText = new TextComponent("/factions joinstatus [open, request, close]");
                            commandText.setBold(true);

                            player.spigot().sendMessage(components.append(commandText).create());
                            break;
                        }

                        if(args[1].equals("open") || args[1].equals("openbaar")) {
                            command_SetJoinStatus(JoinStatus.OPEN);
                            break;
                        } else if(args[1].equals("request")) {
                            command_SetJoinStatus(JoinStatus.REQUEST);
                            break;
                        } else if(args[1].equals("close") || args[1].equals("closed")) {
                            command_SetJoinStatus(JoinStatus.CLOSED);
                            break;
                        } else {
                            ComponentBuilder components = TextUtil.GenerateErrorMsg(
                                    "Incorrecte status opgegeven, gebruik het commando als volgt:\n");

                            TextComponent commandText = new TextComponent("/factions joinstatus [open, privé]");
                            commandText.setBold(true);

                            player.spigot().sendMessage(components.append(commandText).create());
                            break;
                        }
                    default:
                        command_Help("Commando argument niet gevonden, probeer iets anders.");
                }
            } else {
                command_Help(1);
            }
            return  true;
        } else {
            return false;
        }
    }

    private void command_SetJoinStatus(JoinStatus status) {
        if(!cc_player.faction.factionOwner.uuid.equals(cc_player.uuid)) {
            ComponentBuilder errorMessage = TextUtil.GenerateErrorMsg("Je bent niet de owner van de faction. Als je het echt" +
                    " graag wilt moet je aan " + cc_player.faction.factionOwner.displayName + " vragen of hij jou owner geeft.");
            player.spigot().sendMessage(errorMessage.create());
        } else {
            cc_player.faction.joinStatus = status;
            CrackCityRaids.instance.dbHandler.updateFaction(FactionManager.FactionToPOJO(cc_player.faction));
            ComponentBuilder successMessage = TextUtil.GenerateSuccessMsg("Join status van de faction is succesvol aangepast");
            player.spigot().sendMessage(successMessage.create());
        }
    }

    private void command_Info(String factionName) {
        Faction faction = CrackCityRaids.instance.factionManager.getFaction(factionName);

        if(factionName.equals(FactionManager.emptyFaction.factionName)) {
            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg(
                    "Je zit nog niet in een faction, gebruik /factions join om er een te joinen of /factions create om een" +
                            "faction aan te maken.");
            player.spigot().sendMessage(errorMsg.create());
        } else {
            ComponentBuilder header = TextUtil.GenerateHeaderMsg("Info");
            ComponentBuilder footer = TextUtil.GenerateFooterMsg();

            BaseComponent[] nameInfo = new ComponentBuilder("Naam: ")
                    .color(ChatColor.GOLD).bold(true)
                    .append(new TextComponent(factionName))
                    .color(ChatColor.WHITE).bold(false).create();

            BaseComponent[] ownerInfo = new ComponentBuilder("Owner: ")
                    .color(ChatColor.GOLD).bold(true)
                    .append(new TextComponent(faction.factionOwner.displayName))
                    .color(ChatColor.WHITE).bold(false).create();

            ComponentBuilder spelerInfo = new ComponentBuilder("Spelers:\n")
                    .color(ChatColor.GOLD).bold(true);

            StringBuilder str = new StringBuilder();

            for(int i = 0; i < faction.players.size(); i++) {
                if(i < faction.players.size()-1) {
                    str.append((i+1) + ". " + faction.players.get(i).displayName + " - ");
                } else {
                    str.append((i+1) + ". " + faction.players.get(i).displayName);
                }
            }

            TextComponent spelers = new TextComponent(str.toString());
            spelers.setColor(ChatColor.AQUA);
            spelers.setBold(false);
            spelerInfo.append(spelers);

            BaseComponent[] components = new ComponentBuilder()
                    .append(header.create())
                    .append(TextUtil.newLine)
                    .append(nameInfo)
                    .append(TextUtil.newLine)
                    .append(ownerInfo)
                    .append(TextUtil.newLine)
                    .append(spelerInfo.create())
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
                    new ArrayList<CC_Player>() {
                        {
                            add(cc_player);
                        }
                    },
                    new ArrayList<Chunk>(),
                    JoinStatus.REQUEST,
                    new ArrayList<CC_Player>());

            POJO_Faction pojo_faction = FactionManager.FactionToPOJO(faction);
            pojo_player.factionName = faction.factionName;
            cc_player.faction = faction;
            CrackCityRaids.instance.dbHandler.insertFaction(pojo_faction);
            CrackCityRaids.instance.dbHandler.updatePlayer(pojo_player);
            factionManager.factionNameList.add(faction.factionName);
            factionManager.factionList.add(faction);

            TextComponent successMessage = new TextComponent("Faction is succesvol aangemaakt!");
            successMessage.setColor(ChatColor.GREEN);
            player.spigot().sendMessage(successMessage);
        }
    }

    public void command_Delete() {
        if(!cc_player.faction.factionOwner.uuid.equals(cc_player.uuid)) {
            ComponentBuilder errorMessage = TextUtil.GenerateErrorMsg("Je bent niet de owner van de faction. Als je het echt" +
                    " graag wilt moet je aan " + cc_player.faction.factionOwner.displayName + " vragen of hij jou owner geeft.");
            player.spigot().sendMessage(errorMessage.create());
        } else {
            factionManager.factionList.remove(cc_player.faction);
            factionManager.factionNameList.remove(cc_player.faction.factionName);
            CrackCityRaids.instance.dbHandler.deleteFaction(cc_player.faction.factionName);

            pojo_player.factionName = FactionManager.emptyFaction.factionName;
            CrackCityRaids.instance.dbHandler.updatePlayer(pojo_player);
            cc_player.faction = FactionManager.emptyFaction;

            ComponentBuilder successMessage = TextUtil.GenerateSuccessMsg("Faction is succesvol verwijderd!");
            player.spigot().sendMessage(successMessage.create());
        }
    }

    private void command_Leave() {
        if(cc_player.faction.factionName.equals(FactionManager.emptyFaction.factionName)) {
            TextComponent errorMessage = new TextComponent("Je ziet niet in een faction, gebruik /faction" +
                    " join om een faction te joinen.");
            errorMessage.setColor(ChatColor.RED);
            player.spigot().sendMessage(errorMessage);
        } else if(cc_player.faction.factionOwner.uuid.equals(cc_player.uuid)) {
            TextComponent errorMessage = new TextComponent("Je bent de owner van deze faction, gebruik /faction delete om" +
                    " de faction te verwijderen of /faction setOwner" +
                    " om iemand anders owner te maken.");
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
        Faction faction = CrackCityRaids.instance.factionManager.getFaction(factionName);
        if(!cc_player.faction.factionName.equals(FactionManager.emptyFaction.factionName)) {
            errorMessage.setText("Je zit al in een faction, gebruik eerst /factions leave om je faction te verlaten.");
            player.spigot().sendMessage(errorMessage);
        } else if(!factionManager.factionNameList.contains(factionName)) {
            errorMessage.setText("De opgegeven faction bestaat niet!");
            player.spigot().sendMessage(errorMessage);
        } else if(faction.isFull()) {
            errorMessage.setText("De faction die je probeert te joinen zit vol!");
            player.spigot().sendMessage(errorMessage);
        } else if(faction.joinStatus == JoinStatus.CLOSED) {
            errorMessage.setText("De faction die je probeert te joinen staat op gesloten!");
            player.spigot().sendMessage(errorMessage);
        } else if(faction.joinStatus == JoinStatus.REQUEST) {
            faction.playerJoinRequests.add(cc_player);
            ComponentBuilder successMessage = TextUtil.GenerateSuccessMsg("Faction join request is verstuurd!");
            player.spigot().sendMessage(successMessage.create());
            CrackCityRaids.instance.dbHandler.updateFaction(FactionManager.FactionToPOJO(faction));

            for(CC_Player cc_member : faction.players) {
                OfflinePlayer offlinePlayer = Bukkit.getPlayer(UUID.fromString(cc_member.uuid));
                if(offlinePlayer.isOnline()) {
                    TextComponent requestMessage = new TextComponent(cc_player.displayName + " heeft gevraagd of ze je faction mogen joinen!");
                    requestMessage.setColor(ChatColor.GOLD);
                    offlinePlayer.getPlayer().spigot().sendMessage(requestMessage);
                }
            }
        } else {
            Faction newFaction = CrackCityRaids.instance.factionManager.getFaction(factionName);
            playerManager.setPlayerFaction(player, newFaction);
            ComponentBuilder successMessage = TextUtil.GenerateSuccessMsg("Faction succesvol gejoined!");
            player.spigot().sendMessage(successMessage.create());
        }
    }

    private void command_List(int page) {
        ArrayList<Faction> list = (ArrayList<Faction>) CrackCityRaids.instance.factionManager.factionList;

        if(list.size() < 1) {
            player.spigot().sendMessage(TextUtil.GenerateErrorMsg("Geen factions gevonden, ben de eerste" +
                    " faction door /faction create te gebruiken!").create());

            return;
        }

        int lastPage = (int)Math.ceil((double) list.size() / LIST_CHAT_SIZE);

        if(page > lastPage) {
            page = lastPage;
        } else if(page < 1) {
            page = 1;
        }

        ComponentBuilder header = TextUtil.GenerateHeaderMsg("List [" + page + "/" + lastPage + "]");
        ComponentBuilder footer = TextUtil.GenerateFooterButtonMsg("/factions list " + (page-1),
                "/factions list " + (page+1),
                "Ga een pagina terug",
                "Ga een pagina verder");

        header.append(TextUtil.newLine);

        ComponentBuilder componentBuilder = new ComponentBuilder().append(TextUtil.newLine);

        componentBuilder.append(header.create());

        for(int j = (page-1) * LIST_CHAT_SIZE; j < LIST_CHAT_SIZE + (page-1) * LIST_CHAT_SIZE; j++) {
            if(list.size()-1 < j) {
                break;
            }

            TextComponent factionNumber = new TextComponent(j + 1 + ": ");
            factionNumber.setColor(ChatColor.GOLD);
            factionNumber.setBold(true);
            TextComponent factionInfo = new TextComponent(list.get(j).factionName + " - " + list.get(j).players.size());
            factionInfo.setBold(false);

            factionNumber.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Klik voor meer info")));
            factionNumber.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/factions info " + list.get(j).factionName));

            TextComponent joinButton = new TextComponent(" [Join]");
            joinButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Klik hier om de faction te joinen")));
            joinButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/factions join " + list.get(j).factionName));
            joinButton.setColor(ChatColor.DARK_AQUA);

            componentBuilder.append(factionNumber);
            componentBuilder.append(factionInfo);

            if(cc_player.faction.factionName.equals(FactionManager.emptyFaction.factionName) && list.get(j).isJoinable()) {
                componentBuilder.append(joinButton);
            }

            if(j == (LIST_CHAT_SIZE + (page-1) * LIST_CHAT_SIZE) - 1 || list.size()-1 == j) {
                componentBuilder.append("\n").event((ClickEvent) null).event((HoverEvent) null);
            } else {
                componentBuilder.append(TextUtil.newLine).event((ClickEvent) null).event((HoverEvent) null);
            }
        }

        player.spigot().sendMessage(componentBuilder.create());
        player.spigot().sendMessage(footer.create());
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

        helpClick.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/factions help 1"));
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

    private void command_Help(int page) {
        TextComponent helpText = new TextComponent("Deze command moet nog gemaakt worden :O (miss website) ");
        helpText.setBold(false);
        helpText.setColor(ChatColor.GREEN);

        BaseComponent[] components = new ComponentBuilder()
                .append(TextUtil.GenerateHeaderMsg("Help").create())
                .append(TextUtil.newLine)
                .append(helpText)
                .append(TextUtil.newLine)
                .append(TextUtil.GenerateFooterMsg().create()).create();

        player.spigot().sendMessage(components);
    }
}
