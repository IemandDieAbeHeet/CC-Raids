package com.abevriens.commands;

import com.abevriens.TextUtil;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class Factions_Chat {
    public CommandContext commandContext;
    public boolean enable;

    public Factions_Chat(CommandContext _commandContext, boolean _enable) {
        commandContext = _commandContext;
        enable = _enable;

        command_Chat();
    }

    private void command_Chat() {
        ComponentBuilder successMsg;
        if (enable) {
            successMsg = TextUtil.GenerateSuccessMsg("Faction chat aangezet, " +
                    "je kunt het weer uitzetten met /factions chat off");
            commandContext.cc_player.factionChatEnabled = true;
            commandContext.player.spigot().sendMessage(successMsg.create());
        } else {
            successMsg = TextUtil.GenerateSuccessMsg("Faction chat uitgezet, " +
                    "je kunt het weer aanzetten met /factions chat on");

            commandContext.cc_player.factionChatEnabled = false;
            commandContext.player.spigot().sendMessage(successMsg.create());
        }
    }
}
