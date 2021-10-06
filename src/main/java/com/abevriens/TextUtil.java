package com.abevriens;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

public class TextUtil {
    public static ChatColor footerColor = ChatColor.AQUA;
    public static ChatColor headerColor = ChatColor.AQUA;
    public static ChatColor errorColor = ChatColor.RED;

    public static TextComponent newLine = new TextComponent("\n\n");

    public static ComponentBuilder GenerateErrorMsg(String errorString) {
        TextComponent errorText = new TextComponent(errorString);
        errorText.setColor(errorColor);

        ComponentBuilder components = new ComponentBuilder()
                .append(errorText);

        return components;
    }

    public static ComponentBuilder GenerateHeaderMsg(String headerString) {
        String header = "";

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

    public static  ComponentBuilder GenerateFooterMsg() {
        TextComponent footerText = new TextComponent("=============================================");
        footerText.setBold(true);
        footerText.setColor(footerColor);

        ComponentBuilder components = new ComponentBuilder()
                .append(footerText);

        return components;
    }
}
