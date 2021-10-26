package com.abevriens.jda;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;

public class JDAManager {
    public JDA jda;
    public Guild guild;
    public TextChannel infoChannel;

    public JDAManager(String token) throws LoginException {
        jda = JDABuilder.createDefault(token).build();
        jda.addEventListener(new DiscordReadyListener());
    }
}