package com.abevriens.discord;

import com.abevriens.CrackCityRaids;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;

public class DiscordManager {
    public JDA jda;

    public DiscordManager(String token) throws LoginException {
        jda = JDABuilder.create(token,
                GatewayIntent.GUILD_MEMBERS, GatewayIntent.DIRECT_MESSAGE_REACTIONS, GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.DIRECT_MESSAGES).build();
        jda.addEventListener(new DiscordReadyListener(), new DiscordLinkReactionClicked());
    }

    public Guild getGuild() {
        return jda.getGuildById((Long) CrackCityRaids.configurationManager.getDiscordConfig().get("guild_id"));
    }
}