package net.maksy.grimoires.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryUT {

    public static Inventory createFilledInventory(Player player, Component title, int size, Material fillItem) {
        Inventory inv = Bukkit.createInventory(player, size, title);
        ItemStack filler = new ItemStack(fillItem);
        for (int i = 0; i < size; i++) {
            inv.setItem(i, filler);
        }
        return inv;
    }
}
