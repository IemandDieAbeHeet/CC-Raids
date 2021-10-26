package com.abevriens.jda;

import com.abevriens.CrackCityRaids;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class DiscordReadyListener extends ListenerAdapter {
    @Override
    public void onReady(@NotNull ReadyEvent event) {
        JDAManager jdaManager = CrackCityRaids.instance.jdaManager;
        HashMap<String, Object> discordConfig = CrackCityRaids.instance.configurationManager.getDiscordConfig();

        jdaManager.guild = jdaManager.jda.getGuildById((Long) discordConfig.get("guild_id"));
        jdaManager.infoChannel = jdaManager.jda.getTextChannelById((Long) discordConfig.get("info_channel_id"));
    }
}
