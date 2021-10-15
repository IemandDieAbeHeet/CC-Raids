package com.abevriens.commands;

import com.abevriens.TextUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;

public class Factions_HelpError {
    public String error;
    public CommandContext commandContext;

    public Factions_HelpError(CommandContext _commandContext, String _error) {
        commandContext = _commandContext;
        error = _error;

        command_HelpError();
    }

    private void command_HelpError() {
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

        commandContext.player.spigot().sendMessage(components);
    }
}
