package com.abevriens;

import com.abevriens.discord.DiscordIdEnum;
import net.dv8tion.jda.api.entities.TextChannel;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Objects;

public class PlayerChatListener implements Listener {
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        CC_Player cc_player = CrackCityRaids.instance.playerManager.getCCPlayer(event.getPlayer());
        if(!cc_player.factionChatEnabled) {
            CrackCityRaids.instance.discordManager.getMinecraftChatChannel().sendMessage(
                    "[Minecraft] " + event.getPlayer().getDisplayName() + ":\n" + event.getMessage()).queue();
        } else {
            TextChannel factionChat = CrackCityRaids.instance.discordManager.getGuild()
                    .getTextChannelById(cc_player.faction.discordIdMap.get(DiscordIdEnum.CHAT_CHANNEL));

            if(factionChat != null) {
                factionChat.sendMessage("[Minecraft] " + event.getPlayer().getDisplayName()
                        + ":\n" + event.getMessage()).queue();
            }

            ComponentBuilder factionMessage = new ComponentBuilder();

            TextComponent fName = new TextComponent("[" + cc_player.faction.factionName + "] ");
            fName.setColor(ChatColor.AQUA);
            factionMessage.append(fName);
            TextComponent text = new TextComponent(cc_player.displayName + ": " + event.getMessage());
            text.setColor(ChatColor.WHITE);
            factionMessage.append(text);

            cc_player.faction.sendMessageToPlayers(factionMessage);

            event.setCancelled(true);
        }
    }
}
