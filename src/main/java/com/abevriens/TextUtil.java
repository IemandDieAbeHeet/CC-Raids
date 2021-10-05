package com.abevriens;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

public class TextUtil {
    public static ComponentBuilder GenerateErrorMsg(String errorString) {
        TextComponent errorText = new TextComponent(errorString);
        errorText.setColor(ChatColor.RED);

        ComponentBuilder components = new ComponentBuilder()
                .append(errorText);

        return components;
    }
}
