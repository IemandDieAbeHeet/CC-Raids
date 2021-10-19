package com.abevriens;

import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FactionCoreGUI implements Listener {
    private final Inventory inventory;
    private Map<ItemStack, FactionCoreGUINavigation> navigationItemMap = new HashMap<>();

    public FactionCoreGUI(FactionCore factionCore) {
        inventory = Bukkit.createInventory(null, 45, ("Faction: " + factionCore.factionName));

        initializeItems(factionCore);
    }

    public void initializeItems(FactionCore factionCore) {
        ItemStack decreaseX = createGUIItem(Material.RED_CONCRETE, "Verklein X as",
                "Verklein de zone van je base in de X as");
        ItemStack increaseX = createGUIItem(Material.GREEN_CONCRETE, "Vergroot X as",
                "Vergroot de zone van je base in de X as");
        ItemStack decreaseY = createGUIItem(Material.RED_CONCRETE, "Verklein Y as",
                "Verklein de zone van je base in de Y as");
        ItemStack increaseY = createGUIItem(Material.GREEN_CONCRETE, "Vergroot Y as",
                "Vergroot de zone van je base in de Y as");
        inventory.addItem(decreaseX);
        inventory.addItem(increaseX);
        inventory.addItem(decreaseY);
        inventory.addItem(increaseY);
        navigationItemMap.put(decreaseX, FactionCoreGUINavigation.DECREASE_SIZE_X);
        navigationItemMap.put(increaseX, FactionCoreGUINavigation.INCREASE_SIZE_X);
        navigationItemMap.put(decreaseY, FactionCoreGUINavigation.DECREASE_SIZE_Y);
        navigationItemMap.put(increaseY, FactionCoreGUINavigation.INCREASE_SIZE_Y);
    }

    protected ItemStack createGUIItem(final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);

        meta.setLore(Arrays.asList(lore));

        item.setItemMeta(meta);

        return item;
    }

    public void openInventory(final HumanEntity entity) {
        entity.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (e.getInventory() != inventory) return;

        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();

        if (clickedItem == null || clickedItem.getType().isAir()) return;

        final Player player = (Player) e.getWhoClicked();

        FactionCoreGUINavigation navigation = navigationItemMap.get(clickedItem);
        if(navigation != null) {
            switch (navigation) {
                case DECREASE_SIZE_X:
                    ComponentBuilder successMsg = TextUtil.GenerateSuccessMsg("Je hebt de X grootte verkleind");
                    player.spigot().sendMessage(successMsg.create());
                    break;
                case DECREASE_SIZE_Y:
                    successMsg = TextUtil.GenerateSuccessMsg("Je hebt de Y grootte verkleind");
                    player.spigot().sendMessage(successMsg.create());
                    break;
                case INCREASE_SIZE_X:
                    successMsg = TextUtil.GenerateSuccessMsg("Je hebt de X grootte vergroot");
                    player.spigot().sendMessage(successMsg.create());
                    break;
                case INCREASE_SIZE_Y:
                    successMsg = TextUtil.GenerateSuccessMsg("Je hebt de Y grootte vergroot");
                    player.spigot().sendMessage(successMsg.create());
                    break;
            }
        }
    }

    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getInventory().equals(inventory)) {
            e.setCancelled(true);
        }
    }
}
