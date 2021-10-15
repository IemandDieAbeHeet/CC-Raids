package com.abevriens.commands;

import com.abevriens.TextUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

public class Factions_Help {
    public int page;
    public CommandContext commandContext;

    public Factions_Help(CommandContext _commandContext, int _page) {
        page = _page;
        commandContext = _commandContext;

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

        commandContext.player.spigot().sendMessage(components);
    }

}
