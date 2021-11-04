package com.abevriens;

import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class BlockClickEventListener implements Listener {
    @EventHandler
    public void onBlockClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        CC_Player cc_player = CrackCityRaids.playerManager.getCCPlayer(player);
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();

            if(block == null) return;

            if(block.getType() == Material.STRUCTURE_BLOCK) {
                FactionCore factionCore = CrackCityRaids.factionCoreManager.getFactionCore(block);
                if(factionCore == null) {
                    ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg("Kan de aangeklikte faction core " +
                            "niet vinden, waarschijnlijk is dit blok niet door een faction geplaatst");

                    player.spigot().sendMessage(errorMsg.create());
                    event.setCancelled(true);
                    return;
                } else if(!cc_player.faction.factionName.equals(factionCore.factionName)) {
                    ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg("Je zit niet in de faction waar deze faction " +
                            "core van is!");

                    player.spigot().sendMessage(errorMsg.create());
                    event.setCancelled(true);
                    return;
                }

                FactionCoreGUI gui = new FactionCoreGUI(factionCore);
                gui.openInventory(player);
                CrackCityRaids.instance.getServer().getPluginManager().registerEvents(gui, CrackCityRaids.instance);
            }
        }
    }
}
