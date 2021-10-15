package com.abevriens.commands;

import com.abevriens.*;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Factions_Create {
    public String name;
    public CommandContext commandContext;

    public Factions_Create(CommandContext _commandContext, String _name) {
        name = _name;
        commandContext = _commandContext;

        command_Create();
    }

    private void command_Create() {
        if(!commandContext.cc_player.faction.factionName.equals(FactionManager.emptyFaction.factionName)) {
            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg("Je zit al in een faction dus je kunt geen nieuwe " +
                    "faction aanmaken, verlaat deze met /factions leave.");
            commandContext.player.spigot().sendMessage(errorMsg.create());
        } else if(commandContext.factionManager.factionNameList.contains(name)) {
            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg("Een faction met deze naam bestaat al, " +
                    "kies een andere naam.");
            commandContext.player.spigot().sendMessage(errorMsg.create());
        } else {
            Faction faction = new Faction(
                    commandContext.pojo_player,
                    name,
                    new ArrayList<CC_Player>() {
                        {
                            add(commandContext.cc_player);
                        }
                    },
                    new ArrayList<Chunk>(),
                    JoinStatus.REQUEST,
                    new ArrayList<String>());

            POJO_Faction pojo_faction = FactionManager.FactionToPOJO(faction);
            commandContext.pojo_player.factionName = faction.factionName;
            commandContext.cc_player.faction = faction;
            CrackCityRaids.instance.dbHandler.insertFaction(pojo_faction);
            CrackCityRaids.instance.dbHandler.updatePlayer(commandContext.pojo_player);
            commandContext.factionManager.factionNameList.add(faction.factionName);
            commandContext.factionManager.factionList.add(faction);

            TextComponent successMessage = new TextComponent("Faction is succesvol aangemaakt!");
            successMessage.setColor(ChatColor.GREEN);
            commandContext.player.spigot().sendMessage(successMessage);
        }
    }
}
