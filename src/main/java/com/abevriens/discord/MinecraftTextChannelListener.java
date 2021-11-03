package com.abevriens.discord;

import com.abevriens.CC_Player;
import com.abevriens.CrackCityRaids;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Objects;

public class MinecraftTextChannelListener extends ListenerAdapter {
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if(event.getAuthor().isBot()) return;

        Message message = event.getMessage();
        String content = message.getContentRaw();
        CC_Player cc_player = CrackCityRaids.instance.playerManager.getPlayerFromDiscordId(
                Objects.requireNonNull(message.getMember()).getId());

        if(event.getChannel().getId().equals(CrackCityRaids.instance.discordManager.minecraftChatChannel.getId())) {
            CrackCityRaids.instance.getServer().broadcastMessage(
                    ChatColor.BLUE + "[Discord Chat] " +  ChatColor.RESET + cc_player.displayName + ": " + content);
        } else if(event.getChannel().getId().equals(cc_player.faction.discordIdMap.get(DiscordIdEnum.CHAT_CHANNEL))) {
            ComponentBuilder factionMessage = new ComponentBuilder();
            TextComponent discord = new TextComponent("[Faction Chat] ");
            discord.setColor(ChatColor.BLUE);
            TextComponent text = new TextComponent(cc_player.displayName + ": " + content);
            text.setColor(ChatColor.WHITE);
            factionMessage.append(discord);
            factionMessage.append(text);

            cc_player.faction.sendMessageToPlayers(factionMessage);
        }
    }
}
