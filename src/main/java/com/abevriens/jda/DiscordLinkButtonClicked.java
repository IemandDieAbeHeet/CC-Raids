package com.abevriens.jda;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class DiscordLinkButtonClicked extends ListenerAdapter {
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        if(event.isFromType(ChannelType.PRIVATE)) {
            event.getChannel().sendTyping().queue();
            event.getChannel().sendMessage("Ok").queue();
        }
    }
}
