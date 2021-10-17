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
        CC_Player cc_player = CrackCityRaids.instance.playerManager.getCCPlayer(player);
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if(block.getType() == Material.STRUCTURE_BLOCK) {
                FactionBlock factionBlock = CrackCityRaids.instance.factionBlockManager.getFactionBlock(block);
                if(factionBlock == null) {
                    ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg("Kan het aangeklikte factionblok niet vinden, " +
                            "waarschijnlijk is dit blok niet door een faction geplaatst");

                    player.spigot().sendMessage(errorMsg.create());
                } else if(!player.getGameMode().equals(GameMode.SURVIVAL)) {
                    ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg("Je moet in survival zitten als je het " +
                            "faction blok wil gebruiken!");

                    player.spigot().sendMessage(errorMsg.create());
                } else if(!cc_player.faction.factionName.equals(factionBlock.factionName)) {
                    ComponentBuilder errorMsg = TextUtil.GenerateErrorMsg("Je zit niet in de faction waar dit faction " +
                            "blok van is!");

                    player.spigot().sendMessage(errorMsg.create());
                }
            }
        }
    }
}
