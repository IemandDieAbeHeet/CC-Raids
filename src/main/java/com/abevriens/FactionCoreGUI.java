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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class FactionCoreGUI implements Listener {
    private final Inventory inventory;
    private final Map<ItemStack, FactionCoreGUIMenuItems> menuItemMap = new HashMap<>();
    private final List<Material> incrementMaterials = new ArrayList<>() {{
        add(Material.COAL);
        add(Material.IRON_INGOT);
        add(Material.GOLD_INGOT);
        add(Material.DIAMOND);
    }};

    private final List<Integer> increments = new ArrayList<>() {{
        add(1);
        add(5);
        add(10);
        add(20);
    }};

    private final Map<FactionCoreGUIMenuItems, Integer> menuInventoryPositionMap =
            new HashMap<>() {{
                put(FactionCoreGUIMenuItems.DECREASE_SIZE_X, 21);
                put(FactionCoreGUIMenuItems.INCREASE_SIZE_X, 23);
                put(FactionCoreGUIMenuItems.DECREASE_SIZE_Y, 31);
                put(FactionCoreGUIMenuItems.INCREASE_SIZE_Y, 13);
                put(FactionCoreGUIMenuItems.CHANGE_RAIDTIMER, 19);
            }};

    private int currentIncrement = 0;

    private ItemStack changeIncrementItem;

    public FactionCoreGUI(FactionCore factionCore) {
        inventory = Bukkit.createInventory(null, 45, ("Faction: " + factionCore.factionName));

        initializeItems(factionCore);
    }

    public void initializeItems(FactionCore factionCore) {
        changeIncrementItem = generateIncrementItem();
        updateMenuItems(factionCore);
        inventory.setItem(22, changeIncrementItem);
        menuItemMap.put(changeIncrementItem, FactionCoreGUIMenuItems.CHANGE_INCREMENT);
    }

    protected ItemStack generateIncrementItem() {
        return createGUIItem(incrementMaterials.get(currentIncrement),
                ("Toename/afname: " + increments.get(currentIncrement)),
                "Verander de toename/afname van de grootte per klik.");
    }

    protected ItemStack generateMenuItem(FactionCoreGUIMenuItems menu, FactionCore factionCore) {
        Faction faction = CrackCityRaids.instance.factionManager.getFaction(factionCore.factionName);
        switch (menu) {
            case DECREASE_SIZE_X:
                return createGUIItem(Material.RED_CONCRETE,
                        "Verklein X as (x: " + faction.xSize + ", y: " + faction.ySize + ")",
                        "Verklein de zone van je base in de X as");
            case INCREASE_SIZE_X:
                return createGUIItem(Material.GREEN_CONCRETE,
                        "Vergroot X as (x: " + faction.xSize + ", y: " + faction.ySize + ")",
                        "Vergroot de zone van je base in de X as");
            case DECREASE_SIZE_Y:
                return createGUIItem(Material.RED_CONCRETE,
                        "Verklein Y as (x: " + faction.xSize + ", y: " + faction.ySize + ")",
                        "Verklein de zone van je base in de Y as");
            case INCREASE_SIZE_Y:
                return  createGUIItem(Material.GREEN_CONCRETE,
                        "Vergroot Y as (x: " + faction.xSize + ", y: " + faction.ySize + ")",
                        "Vergroot de zone van je base in de Y as");
            case CHANGE_RAIDTIMER:
                return createGUIItem(Material.CLOCK,
                        "Verander de raid timer tijd. (" +
                                FactionManager.generateShortCountdownTimeString(faction.raidAlert.maxRaidCountdown) + ")",
                        "Klik op de linkermuisknop om met 1 uur te verhogen,",
                                "en de rechtermuisknop om met 1 uur te verlagen.");
        }
        return null;
    }

    protected void updateMenuItems(FactionCore factionCore) {
        for(FactionCoreGUIMenuItems menuItem : menuInventoryPositionMap.keySet()) {
            ItemStack item;
            ItemStack currentItem;
            item = generateMenuItem(menuItem, factionCore);
            currentItem = inventory.getItem(menuInventoryPositionMap.get(menuItem));
            menuItemMap.remove(currentItem);
            menuItemMap.put(item, menuItem);
            inventory.setItem(menuInventoryPositionMap.get(menuItem), item);
        }
    }

    protected ItemStack createGUIItem(final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        if(meta != null) {
            meta.setDisplayName(name);

            meta.setLore(Arrays.asList(lore));

            item.setItemMeta(meta);
        }

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
        CC_Player cc_player = CrackCityRaids.instance.playerManager.getCCPlayer(player);

        FactionCoreGUIMenuItems navigation = menuItemMap.get(clickedItem);
        if (navigation != null) {
            ComponentBuilder errorMsg;
            ComponentBuilder successMsg;
            switch (navigation) {
                case DECREASE_SIZE_X:
                    if(cc_player.faction.xSize - increments.get(currentIncrement) < 10) {
                        errorMsg = TextUtil.GenerateErrorMsg("Je kunt de X grootte niet kleiner maken dan 10");
                        player.spigot().sendMessage(errorMsg.create());
                    } else {
                        cc_player.faction.xSize -= increments.get(currentIncrement);
                        updateMenuItems(cc_player.faction.factionCore);
                        successMsg = TextUtil.GenerateSuccessMsg("Je hebt de X grootte verkleind");
                        player.spigot().sendMessage(successMsg.create());
                    }
                    break;
                case DECREASE_SIZE_Y:
                    if(cc_player.faction.ySize - increments.get(currentIncrement) < 5) {
                        errorMsg = TextUtil.GenerateErrorMsg("Je kunt de Y grootte niet kleiner maken dan 5");
                        player.spigot().sendMessage(errorMsg.create());
                    } else {
                        cc_player.faction.ySize -= increments.get(currentIncrement);
                        updateMenuItems(cc_player.faction.factionCore);
                        successMsg = TextUtil.GenerateSuccessMsg("Je hebt de Y grootte verkleind");
                        player.spigot().sendMessage(successMsg.create());
                    }
                    break;
                case INCREASE_SIZE_X:
                    if(cc_player.faction.xSize + increments.get(currentIncrement) > 300) {
                        errorMsg = TextUtil.GenerateErrorMsg("Je kunt de X grootte niet groter maken dan 300");
                        player.spigot().sendMessage(errorMsg.create());
                    } else {
                        cc_player.faction.xSize += increments.get(currentIncrement);
                        updateMenuItems(cc_player.faction.factionCore);
                        successMsg = TextUtil.GenerateSuccessMsg("Je hebt de X grootte vergroot");
                        player.spigot().sendMessage(successMsg.create());
                    }
                    break;
                case INCREASE_SIZE_Y:
                    if(cc_player.faction.ySize + increments.get(currentIncrement) > 200) {
                        errorMsg = TextUtil.GenerateErrorMsg("Je kunt de Y grootte niet kleiner maken dan 200");
                        player.spigot().sendMessage(errorMsg.create());
                    } else {
                        cc_player.faction.ySize += increments.get(currentIncrement);
                        updateMenuItems(cc_player.faction.factionCore);
                        successMsg = TextUtil.GenerateSuccessMsg("Je hebt de Y grootte vergroot");
                        player.spigot().sendMessage(successMsg.create());
                    }
                    break;
                case CHANGE_INCREMENT:
                    if (e.isLeftClick()) {
                        if (currentIncrement < increments.size() - 1) {
                            currentIncrement++;
                        } else {
                            currentIncrement = 0;
                        }
                    } else {
                        if (currentIncrement - 1 >= 0) {
                            currentIncrement--;
                        } else {
                            currentIncrement = increments.size() - 1;
                        }
                    }
                    ItemStack newIncrementItem = generateIncrementItem();
                    inventory.setItem(22, newIncrementItem);
                    menuItemMap.remove(changeIncrementItem);
                    menuItemMap.put(newIncrementItem, FactionCoreGUIMenuItems.CHANGE_INCREMENT);
                    changeIncrementItem = newIncrementItem;
                case CHANGE_RAIDTIMER:
                    if(e.isLeftClick()) {
                        if((cc_player.faction.raidAlert.maxRaidCountdown + 60) <= 960) {
                            cc_player.faction.raidAlert.maxRaidCountdown += 60;
                        } else {
                            errorMsg = TextUtil.GenerateErrorMsg("Je kunt de raid countdown niet " +
                                    "hoger zetten dan 16 uur!");
                            player.spigot().sendMessage(errorMsg.create());
                        }
                    } else {
                        if(cc_player.faction.raidAlert.maxRaidCountdown > 360) {
                            cc_player.faction.raidAlert.maxRaidCountdown -= 60;
                        } else {
                            errorMsg = TextUtil.GenerateErrorMsg("Je kunt de raid countdown niet lager zetten " +
                                    "dan 6 uur!");
                            player.spigot().sendMessage(errorMsg.create());
                        }
                    }
                    updateMenuItems(cc_player.faction.factionCore);
            }

            POJO_Faction pojo_faction = FactionManager.FactionToPOJO(cc_player.faction);
            CrackCityRaids.instance.dbHandler.updateFaction(pojo_faction);
        }
    }

    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getInventory().equals(inventory)) {
            e.setCancelled(true);
        }
    }
}
