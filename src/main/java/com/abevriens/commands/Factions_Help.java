package com.abevriens.commands;

import com.abevriens.TextUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

public class Factions_Help extends Factions_Base {
    public int page;

    public Factions_Help(Factions_Base factions_base, int _page) {
        super(factions_base.cc_player, factions_base.player, factions_base.pojo_player,
                factions_base.factionManager, factions_base.playerManager);
        page = _page;

        command_Help();
    }

    private void command_Help() {
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
