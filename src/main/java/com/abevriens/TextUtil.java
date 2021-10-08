package com.abevriens;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class TextUtil {
    public static ChatColor footerColor = ChatColor.AQUA;
    public static ChatColor headerColor = ChatColor.AQUA;
    public static ChatColor errorColor = ChatColor.RED;
    public static ChatColor successColor = ChatColor.GREEN;

    public static TextComponent newLine = new TextComponent("\n\n");

    public static ComponentBuilder GenerateErrorMsg(String errorString) {
        TextComponent errorText = new TextComponent(errorString);
        errorText.setColor(errorColor);

        ComponentBuilder components = new ComponentBuilder()
                .append(errorText);

        return components;
    }

    public static ComponentBuilder GenerateSuccessMsg(String succesString) {
        TextComponent errorText = new TextComponent(succesString);
        errorText.setColor(successColor);

        ComponentBuilder components = new ComponentBuilder()
                .append(errorText);

        return components;
    }

    public static ComponentBuilder GenerateHeaderMsg(String headerString) {
        String header = "\n";

        int k = 0;
        if(headerString.length() % 2 == 0) k = 1;

        for (int j = 0; j < 22 - k - (int)Math.ceil((double)headerString.length()/2.0); j++) {
            header += "=";
        }

        header += "  ";
        header += headerString;
        header += "  ";

        for (int j = 0; j < 22 - (int)Math.ceil((double)headerString.length()/2.0); j++) {
            header += "=";
        }

        TextComponent headerText = new TextComponent(header);
        headerText.setBold(true);
        headerText.setColor(headerColor);

        ComponentBuilder components = new ComponentBuilder()
                .append(headerText);

        return components;
    }

    public static ComponentBuilder GenerateFooterMsg() {
        TextComponent footerText = new TextComponent("=============================================");
        footerText.setBold(true);
        footerText.setColor(footerColor);

        ComponentBuilder components = new ComponentBuilder()
                .append(footerText);

        return components;
    }

    public static ComponentBuilder GenerateFooterButtonMsg(String cmd1, String cmd2, String h1, String h2) {
        ComponentBuilder footer = new ComponentBuilder();

        TextComponent start = new TextComponent("= ");

        start.setColor(TextUtil.footerColor);
        start.setBold(true);

        TextComponent middle = new TextComponent(" ==================================== ");
        TextComponent end = new TextComponent(" =");

        TextComponent backButton = new TextComponent("[<]");
        TextComponent nextButton = new TextComponent("[>]");
        backButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(h1)));
        backButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd1));
        nextButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(h2)));
        nextButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd2));

        footer.append(start)
                .append(backButton)
                .append(middle)
                .event((ClickEvent) null)
                .event((HoverEvent) null)
                .append(nextButton)
                .append(end)
                .event((ClickEvent) null)
                .event((HoverEvent) null);

        return footer;
    }
}
