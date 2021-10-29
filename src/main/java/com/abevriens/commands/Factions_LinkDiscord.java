package com.abevriens.commands;

import com.abevriens.CrackCityRaids;
import com.abevriens.TextUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.md_5.bungee.api.chat.ComponentBuilder;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Factions_LinkDiscord {
    public String discordName;
    public CommandContext commandContext;

    public Factions_LinkDiscord(CommandContext _commandContext, String _discordName) {
        discordName = _discordName;
        commandContext = _commandContext;

        command_LinkDiscord();
    }

    private void command_LinkDiscord() {
        if(commandContext.cc_player.discordId != null) {
            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg("Je hebt je Discord account al gelinkt!");
            commandContext.player.spigot().sendMessage(errorMsg.create());
            return;
        }

        List<Member> memberList = CrackCityRaids.instance.discordManager.getGuild().loadMembers().get();
        Member member = null;
        for(Member guildMember : memberList) {
            if(guildMember.getEffectiveName().equals(discordName)) {
                member = guildMember;
            }
        }

        if(member == null) {
            ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg("Discord user niet gevonden, heb je de goede naam " +
                    "opgegeven en zit je wel in de Discord server?");
            commandContext.player.spigot().sendMessage(errorMsg.create());
            return;
        }

        CrackCityRaids.instance.playerManager.addDiscordRequest(member.getId(), commandContext.cc_player.uuid);

        member.getUser().openPrivateChannel().queue(privateChannel -> {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Discord Link Aanvraag");
            embedBuilder.setColor(Color.ORANGE);
            embedBuilder.setThumbnail("https://crafatar.com/renders/head/" + commandContext.cc_player.uuid + "?overlay");
            MessageEmbed.Field infoField = new MessageEmbed.Field("Aanvraag",
                    "Speler " + commandContext.cc_player.displayName + " heeft gevraagd om dit Discord account " +
                            "te linken aan hun Minecraft account.", false);
            embedBuilder.setFooter("Klik op  \uD83D\uDC4D  om te accepteren of  \uD83D\uDC4E  om te weigeren.");
            embedBuilder.addField(infoField);
            privateChannel.sendMessage(embedBuilder.build()).queue(message -> {
                message.addReaction("\uD83D\uDC4D").queue(reaction -> message.addReaction("\uD83D\uDC4E").queue());
            });
        });
    }
}
