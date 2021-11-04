package com.abevriens.discord;

import com.abevriens.CC_Player;
import com.abevriens.CrackCityRaids;
import com.abevriens.PlayerManager;
import net.dv8tion.jda.api.events.message.priv.react.PrivateMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class DiscordLinkReactionClicked extends ListenerAdapter {
    @Override
    public void onPrivateMessageReactionAdd(@NotNull PrivateMessageReactionAddEvent event) {
        if(event.getUser() == null || event.getUser().isBot()) return;
        if(event.getReactionEmote().getEmoji().equals("\uD83D\uDC4D")) {
            CC_Player requestingPlayer = CrackCityRaids.playerManager.getCCPlayer(Bukkit.getOfflinePlayer(
                    UUID.fromString(CrackCityRaids.playerManager.getDiscordRequest(event.getUserId()))));
            requestingPlayer.discordId = event.getUserId();
            CrackCityRaids.dbHandler.updatePlayer(PlayerManager.CCToPOJO(requestingPlayer));
            CrackCityRaids.playerManager.addPlayer(
                    Objects.requireNonNull(Bukkit.getPlayer(UUID.fromString(requestingPlayer.uuid))),
                    PlayerManager.CCToPOJO(requestingPlayer), requestingPlayer);
            String messageId = event.getMessageId();
            event.getChannel().deleteMessageById(messageId).queue();
        } else if(event.getReactionEmote().getEmoji().equals("\uD83D\uDC4E")) {
            String messageId = event.getMessageId();
            event.getChannel().deleteMessageById(messageId).queue();
        }
    }
}
