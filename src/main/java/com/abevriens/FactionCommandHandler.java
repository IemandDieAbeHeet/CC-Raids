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
                label:
                switch (args[0].toLowerCase()) {
                    case "create":
                        if(args.length == 2) {
                            command_Create(args[1]);
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
                        } else {
                            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg(
                                    "Geen faction naam opgegeven, gebruik het commando als volgt:",
                                    "/factions join [naam]");

                            player.spigot().sendMessage(errorMsg.create());
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
                                ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg(
                                        "Incorrect nummer opgegeven als tweede argument, gebruik het commando als volgt:",
                                        "/factions list [paginanummer]");

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
                            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg(
                                    "Geen status opgegeven, gebruik het commando als volgt:",
                                    "/factions joinstatus [open, request, close]");

                            player.spigot().sendMessage(errorMsg.create());
                            break;
                        }

                        switch (args[1]) {
                            case "open":
                            case "openbaar":
                                command_SetJoinStatus(JoinStatus.OPEN);
                                break label;
                            case "request":
                                command_SetJoinStatus(JoinStatus.REQUEST);
                                break label;
                            case "close":
                            case "closed":
                                command_SetJoinStatus(JoinStatus.CLOSED);
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
                            command_Accept(args[1]);
                        } else {
                            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg(
                                    "Geen spelernaam opgegeven om te accepteren, gebruik het commando als volgt:",
                                    "/factions accept [naam]");

                            player.spigot().sendMessage(errorMsg.create());
                        }
                        break;
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

    private void command_Accept(String playerName) {
        CC_Player cc_requestingplayer = null;
        OfflinePlayer offlinePlayer = null;

        if(cc_player.faction.factionName.equals(FactionManager.emptyFaction.factionName)) {
            ComponentBuilder errorMessage = TextUtil.GenerateErrorMsg("Je zit niet in een faction," +
                    " join er een met /factions join.");
            player.spigot().sendMessage(errorMessage.create());
        } else if(!cc_player.faction.factionOwner.uuid.equals(cc_player.uuid)) {
            ComponentBuilder errorMessage = TextUtil.GenerateErrorMsg("Je bent niet de owner van deze faction, vraag aan" +
                    cc_player.faction.factionOwner.displayName + " of ze " + playerName + " willen accepteren!");

            player.spigot().sendMessage(errorMessage.create());
        }

        for(String uuid : cc_player.faction.playerJoinRequests) {
            offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
            if(offlinePlayer.getName().equals(playerName)) {
                cc_requestingplayer = CrackCityRaids.instance.playerManager.getCCPlayer(offlinePlayer);
                break;
            }
        }

        if(cc_requestingplayer == null || offlinePlayer == null) {
            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg("Speler niet gevonden binnen de join requests van " +
                    "je faction. Zie alle join requests met /factions requests");

            player.spigot().sendMessage(errorMsg.create());
        } else {
            cc_player.faction.playerJoinRequests.remove(cc_requestingplayer.uuid);

            ComponentBuilder factionSuccessMsg = TextUtil.GenerateSuccessMsg(
                    playerName + " is lid geworden van je faction!");
            cc_player.faction.sendMessageToPlayers(factionSuccessMsg);

            ComponentBuilder playerSuccessMsg = TextUtil.GenerateSuccessMsg(
                    "Je bent officïeel lid geworden van faction: " + cc_player.faction.factionName);

            if(offlinePlayer.isOnline()) {
                offlinePlayer.getPlayer().spigot().sendMessage(playerSuccessMsg.create());
            }

            CrackCityRaids.instance.playerManager.setPlayerFaction(offlinePlayer, cc_player.faction);
            CrackCityRaids.instance.dbHandler.updatePlayer(PlayerManager.CCToPOJO(cc_requestingplayer));
        }
    }

    private void command_SetJoinStatus(JoinStatus status) {
        if(cc_player.faction.factionName.equals(FactionManager.emptyFaction.factionName)) {
            ComponentBuilder errorMessage = TextUtil.GenerateErrorMsg("Je zit niet in een faction, maak een nieuwe faction" +
                    "  met /factions create of join er een via /factions list");
            player.spigot().sendMessage(errorMessage.create());
        } else if(!cc_player.faction.factionOwner.uuid.equals(cc_player.uuid)) {
            ComponentBuilder errorMessage = TextUtil.GenerateErrorMsg("Je bent niet de owner van de faction. Als je het echt" +
                    " graag wilt moet je aan " + cc_player.faction.factionOwner.displayName + " vragen of ze jou owner geven.");
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
        } else if(!CrackCityRaids.instance.factionManager.factionNameList.contains(factionName)) {
            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg(
                    "De faction die je probeert op te zoeken bestaat niet. Je kunt alle factions bekijken met de " +
                             "command /factions list"
            );
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
                    str.append(i + 1).append(". ").append(faction.players.get(i).displayName).append(" - ");
                } else {
                    str.append(i + 1).append(". ").append(faction.players.get(i).displayName);
                }
            }

            TextComponent spelers = new TextComponent(str.toString());
            spelers.setColor(ChatColor.AQUA);
            spelers.setBold(false);
            spelerInfo.append(spelers);

            BaseComponent[] joinstatusInfo = new ComponentBuilder("Join status: ")
                    .color(ChatColor.GOLD).bold(true)
                    .append(new TextComponent(faction.joinStatus.toString()))
                    .color(ChatColor.WHITE).bold(false).create();

            BaseComponent[] components = new ComponentBuilder()
                    .append(header.create())
                    .append("\n")
                    .append(nameInfo)
                    .append(TextUtil.newLine)
                    .append(ownerInfo)
                    .append(TextUtil.newLine)
                    .append(spelerInfo.create())
                    .append(TextUtil.newLine)
                    .append(joinstatusInfo)
                    .append("\n")
                    .append(footer.create()).create();

            player.spigot().sendMessage(components);
        }
    }

    private void command_Create(String name) {
        if(!cc_player.faction.factionName.equals(FactionManager.emptyFaction.factionName)) {
            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg("Je zit al in een faction dus je kunt geen nieuwe " +
                    "faction aanmaken, verlaat deze met /factions leave.");
            player.spigot().sendMessage(errorMsg.create());
        } else if(factionManager.factionNameList.contains(name)) {
            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg("Een faction met deze naam bestaat al, " +
                    "kies een andere naam.");
            player.spigot().sendMessage(errorMsg.create());
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
                    new ArrayList<String>());

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
        String factionName = cc_player.faction.factionName;
        if(factionName.equals(FactionManager.emptyFaction.factionName)) {
            ComponentBuilder errorMessage = TextUtil.GenerateErrorMsg("Je zit niet in een faction," +
                    " join er een met /factions join.");
            player.spigot().sendMessage(errorMessage.create());
        }
        if(!cc_player.faction.factionOwner.uuid.equals(cc_player.uuid)) {
            ComponentBuilder errorMessage = TextUtil.GenerateErrorMsg("Je bent niet de owner van de faction. Als je " +
                    "het echt graag wilt moet je aan " + cc_player.faction.factionOwner.displayName +
                    " vragen of ze jou owner geven.");
            player.spigot().sendMessage(errorMessage.create());
        } else {
            factionManager.factionList.remove(cc_player.faction);
            factionManager.factionNameList.remove(factionName);

            ArrayList<CC_Player> factionMembers = new ArrayList<>(cc_player.faction.players);

            for(CC_Player factionMember : factionMembers) {
                CrackCityRaids.instance.playerManager.setPlayerFaction(Bukkit.getOfflinePlayer(UUID.fromString(factionMember.uuid)),
                        FactionManager.emptyFaction);
            }

            CrackCityRaids.instance.dbHandler.deleteFaction(factionName);

            ComponentBuilder successMessage = TextUtil.GenerateSuccessMsg("Faction is succesvol verwijderd!");
            player.spigot().sendMessage(successMessage.create());
        }
    }

    private void command_Leave() {
        if(cc_player.faction.factionName.equals(FactionManager.emptyFaction.factionName)) {
            TextComponent errorMessage = new TextComponent("Je ziet niet in een faction, gebruik /factions" +
                    " join om een faction te joinen.");
            errorMessage.setColor(ChatColor.RED);
            player.spigot().sendMessage(errorMessage);
        } else if(cc_player.faction.factionOwner.uuid.equals(cc_player.uuid)) {
            TextComponent errorMessage = new TextComponent("Je bent de owner van deze faction, gebruik /factions delete om" +
                    " de faction te verwijderen of /factions setowner" +
                    " om iemand anders owner te maken.");
            errorMessage.setColor(ChatColor.RED);
            player.spigot().sendMessage(errorMessage);
        } else {
            playerManager.setPlayerFaction(Bukkit.getOfflinePlayer(player.getUniqueId()), FactionManager.emptyFaction);
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
            faction.playerJoinRequests.add(cc_player.uuid);
            ComponentBuilder successMessage = TextUtil.GenerateSuccessMsg("Faction join request is verstuurd!");
            player.spigot().sendMessage(successMessage.create());
            CrackCityRaids.instance.dbHandler.updateFaction(FactionManager.FactionToPOJO(faction));

            TextComponent requestMessage = new TextComponent(cc_player.displayName + " heeft gevraagd of ze je faction " +
                    "mogen joinen!");
            requestMessage.setColor(ChatColor.GOLD);
            faction.sendMessageToPlayers(new ComponentBuilder(requestMessage));
        } else {
            Faction newFaction = CrackCityRaids.instance.factionManager.getFaction(factionName);
            playerManager.setPlayerFaction(Bukkit.getOfflinePlayer(player.getUniqueId()), newFaction);
            ComponentBuilder successMessage = TextUtil.GenerateSuccessMsg("Faction succesvol gejoined!");
            player.spigot().sendMessage(successMessage.create());
        }
    }

    private void command_List(int page) {
        ArrayList<Faction> list = (ArrayList<Faction>) CrackCityRaids.instance.factionManager.factionList;

        if(list.size() < 1) {
            player.spigot().sendMessage(TextUtil.GenerateErrorMsg("Geen factions gevonden, ben de eerste" +
                    " faction door /factions create te gebruiken!").create());

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
