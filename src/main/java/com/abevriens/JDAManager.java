package com.abevriens;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;

public class JDAManager {
    public JDA jda;

    public JDAManager() throws LoginException {
        jda = JDABuilder.createDefault("").build();
    }
}
