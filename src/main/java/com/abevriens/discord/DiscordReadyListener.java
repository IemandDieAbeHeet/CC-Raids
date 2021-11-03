package com.abevriens.discord;

import com.abevriens.CrackCityRaids;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class DiscordReadyListener extends ListenerAdapter {
    @Override
    public void onReady(@NotNull ReadyEvent event) {
        DiscordManager discordManager = CrackCityRaids.instance.discordManager;

        discordManager.getGuild().upsertCommand("clearmc",
                "Clear the Minecraft chat channel of all it's messages").queue();

        //Always load factions before players. LoadPlayers converts POJO_Players
        //to CC_Players which requires factions to be loaded
        CrackCityRaids.instance.factionManager.LoadFactions();
        CrackCityRaids.instance.playerManager.LoadPlayers();
        CrackCityRaids.instance.factionCoreManager.LoadFactionCores();
    }
}
