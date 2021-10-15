package com.abevriens.commands;

import com.abevriens.CrackCityRaids;
import com.abevriens.Faction;
import com.abevriens.FactionManager;
import com.abevriens.TextUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

public class Factions_Info
{
    public String name;
    public CommandContext commandContext;

    public Factions_Info(CommandContext _commandContext, String _name) {
        name = _name;
        commandContext = _commandContext;

        command_Info();
    }

    private void command_Info() {
        Faction faction = CrackCityRaids.instance.factionManager.getFaction(name);

        if(name.equals(FactionManager.emptyFaction.factionName)) {
            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg(
                    "Je zit nog niet in een faction, gebruik /factions join om er een te joinen of /factions create om een" +
                            "faction aan te maken.");
            commandContext.player.spigot().sendMessage(errorMsg.create());
        } else if(!CrackCityRaids.instance.factionManager.factionNameList.contains(name)) {
            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg(
                    "De faction die je probeert op te zoeken bestaat niet. Je kunt alle factions bekijken met de " +
                            "command /factions list"
            );
            commandContext.player.spigot().sendMessage(errorMsg.create());
        } else {
            ComponentBuilder header = TextUtil.GenerateHeaderMsg("Info");
            ComponentBuilder footer = TextUtil.GenerateFooterMsg();

            BaseComponent[] nameInfo = new ComponentBuilder("Naam: ")
                    .color(ChatColor.GOLD).bold(true)
                    .append(new TextComponent(name))
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

            TextComponent joinStatusText = new TextComponent();

            switch(faction.joinStatus) {
                case REQUEST:
                    joinStatusText.setText("Request");
                case CLOSED:
                    joinStatusText.setText("Closed");
                case OPEN:
                    joinStatusText.setText("Open");
            }

            BaseComponent[] joinstatusInfo = new ComponentBuilder("Join status: ")
                    .color(ChatColor.GOLD).bold(true)
                    .append(joinStatusText)
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

            commandContext.player.spigot().sendMessage(components);
        }
    }
}
