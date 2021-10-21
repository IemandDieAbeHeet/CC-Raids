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
    private final Map<ItemStack, FactionCoreGUINavigation> navigationItemMap = new HashMap<>();
    private final List<Material> incrementMaterials = new ArrayList<Material>() {{
        add(Material.COAL);
        add(Material.IRON_INGOT);
        add(Material.GOLD_INGOT);
        add(Material.DIAMOND);
    }};

    private final List<Integer> increments = new ArrayList<Integer>() {{
        add(1);
        add(5);
        add(10);
        add(20);
    }};

    private final Map<FactionCoreGUINavigation, Integer> navigationInventoryPositionMap =
    new HashMap<FactionCoreGUINavigation, Integer>() {{
       put(FactionCoreGUINavigation.DECREASE_SIZE_X, 21);
       put(FactionCoreGUINavigation.INCREASE_SIZE_X, 23);
       put(FactionCoreGUINavigation.DECREASE_SIZE_Y, 31);
       put(FactionCoreGUINavigation.INCREASE_SIZE_Y, 13);
    }};

    private int currentIncrement = 0;

    private ItemStack changeIncrementItem;

    public FactionCoreGUI(FactionCore factionCore) {
        inventory = Bukkit.createInventory(null, 45, ("Faction: " + factionCore.factionName));

        initializeItems();
    }

    public void initializeItems() {
        ItemStack decreaseX = createGUIItem(Material.RED_CONCRETE, "Verklein X as",
                "Verklein de zone van je base in de X as");
        ItemStack increaseX = createGUIItem(Material.GREEN_CONCRETE, "Vergroot X as",
                "Vergroot de zone van je base in de X as");
        ItemStack decreaseY = createGUIItem(Material.RED_CONCRETE, "Verklein Y as",
                "Verklein de zone van je base in de Y as");
        ItemStack increaseY = createGUIItem(Material.GREEN_CONCRETE, "Vergroot Y as",
                "Vergroot de zone van je base in de Y as");
        changeIncrementItem = generateIncrementItem();
        inventory.setItem(21, decreaseX);
        inventory.setItem(23, increaseX);
        inventory.setItem(31, decreaseY);
        inventory.setItem(13, increaseY);
        inventory.setItem(22, changeIncrementItem);
        navigationItemMap.put(decreaseX, FactionCoreGUINavigation.DECREASE_SIZE_X);
        navigationItemMap.put(increaseX, FactionCoreGUINavigation.INCREASE_SIZE_X);
        navigationItemMap.put(decreaseY, FactionCoreGUINavigation.DECREASE_SIZE_Y);
        navigationItemMap.put(increaseY, FactionCoreGUINavigation.INCREASE_SIZE_Y);
        navigationItemMap.put(changeIncrementItem, FactionCoreGUINavigation.CHANGE_INCREMENT);
    }

    protected ItemStack generateIncrementItem() {
        return createGUIItem(incrementMaterials.get(currentIncrement),
                ("Toename/afname: " + increments.get(currentIncrement)),
                "Verander de toename/afname van de grootte per klik.");
    }

    protected ItemStack generateNavigationItem(FactionCoreGUINavigation navigation, int size) {
        switch (navigation) {
            case DECREASE_SIZE_X:
                return createGUIItem(Material.RED_CONCRETE, "Verklein X as (" + size + ")",
                        "Verklein de zone van je base in de X as");
            case INCREASE_SIZE_X:
                return createGUIItem(Material.GREEN_CONCRETE, "Vergroot X as (" + size + ")",
                        "Vergroot de zone van je base in de X as");
            case DECREASE_SIZE_Y:
                return createGUIItem(Material.RED_CONCRETE, "Verklein Y as (" + size + ")",
                        "Verklein de zone van je base in de Y as");
            case INCREASE_SIZE_Y:
                return  createGUIItem(Material.GREEN_CONCRETE, "Vergroot Y as (" + size + ")",
                        "Vergroot de zone van je base in de Y as");
        }
        return null;
    }

    protected void updateNavigationItems(CC_Player cc_player) {
        for(FactionCoreGUINavigation navigation : FactionCoreGUINavigation.values()) {
            ItemStack item;
            switch (navigation) {
                case DECREASE_SIZE_X:
                    item = createGUIItem(Material.RED_CONCRETE, "Verklein X as (" + cc_player.faction.xSize + ")",
                            "Verklein de zone van je base in de X as");
                case INCREASE_SIZE_X:
                    item = createGUIItem(Material.GREEN_CONCRETE, "Vergroot X as (" + cc_player.faction.xSize + ")",
                            "Vergroot de zone van je base in de X as");
                    inventory.setItem(navigationInventoryPositionMap.get(navigation), item);
                case DECREASE_SIZE_Y:
                    item = createGUIItem(Material.RED_CONCRETE, "Verklein Y as (" + cc_player.faction.ySize + ")",
                            "Verklein de zone van je base in de Y as");
                case INCREASE_SIZE_Y:
                    item = createGUIItem(Material.GREEN_CONCRETE, "Vergroot Y as (" + cc_player.faction.ySize + ")",
                            "Vergroot de zone van je base in de Y as");
            }
        }
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
        CC_Player cc_player = CrackCityRaids.instance.playerManager.getCCPlayer(player);

        FactionCoreGUINavigation navigation = navigationItemMap.get(clickedItem);
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
                        ItemStack newItem = generateNavigationItem(navigation, cc_player.faction.xSize);
                        navigationItemMap.remove(inventory.getItem(21));
                        navigationItemMap.put(newItem, navigation);
                        inventory.setItem(21, newItem);
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
                        ItemStack newItem = generateNavigationItem(navigation, cc_player.faction.ySize);
                        navigationItemMap.remove(inventory.getItem(31));
                        navigationItemMap.put(newItem, navigation);
                        inventory.setItem(31, newItem);
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
                        ItemStack newItem = generateNavigationItem(navigation, cc_player.faction.xSize);
                        navigationItemMap.remove(inventory.getItem(23));
                        navigationItemMap.put(newItem, navigation);
                        inventory.setItem(23, newItem);
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
                        ItemStack newItem = generateNavigationItem(navigation, cc_player.faction.ySize);
                        navigationItemMap.remove(inventory.getItem(13));
                        navigationItemMap.put(newItem, navigation);
                        inventory.setItem(13, newItem);
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
                    navigationItemMap.remove(changeIncrementItem);
                    navigationItemMap.put(newIncrementItem, FactionCoreGUINavigation.CHANGE_INCREMENT);
                    changeIncrementItem = newIncrementItem;
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
