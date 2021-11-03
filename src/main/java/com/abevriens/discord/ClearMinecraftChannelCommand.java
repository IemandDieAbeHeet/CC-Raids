package com.abevriens.discord;

import com.abevriens.CrackCityRaids;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ClearMinecraftChannelCommand extends ListenerAdapter {
    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        if(!event.getName().equals("clearmc")) return;
        event.deferReply().queue();
        TextChannel minecraftChannel = CrackCityRaids.discordManager.getMinecraftChatChannel();

        minecraftChannel.getHistory().retrievePast(50)
                .queue(retrieved -> minecraftChannel.deleteMessages(retrieved).queue(
                        deleted -> event.getHook().sendMessage("Berichten verwijderd!").queue(
                                success -> success.delete().queueAfter(10, TimeUnit.SECONDS))));
    }
}
