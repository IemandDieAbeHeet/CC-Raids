package com.abevriens.commands;

import com.abevriens.*;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Factions_Create extends Factions_Base {
    public String name;

    public Factions_Create(Factions_Base factions_base, String _name) {
        super(factions_base.cc_player, factions_base.player, factions_base.pojo_player,
                factions_base.factionManager, factions_base.playerManager);
        name = _name;

        command_Create();
    }

    private void command_Create() {
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
}
