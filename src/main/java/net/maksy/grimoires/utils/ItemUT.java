package net.maksy.grimoires.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.maksy.grimoires.Grimoires;
import net.maksy.grimoires.hooks.HeadDatabaseHook;
import net.maksy.grimoires.hooks.HookType;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemUT {

    public static final ItemStack backItem = getHead("8790", ChatUT.hexComp("&7Zurück"), null);
    public static final ItemStack nextPageItem = getHead("513", ChatUT.hexComp("&7Nächste Seite"), null);
    public static final ItemStack previousPageItem = getHead("516", ChatUT.hexComp("&7Vorherige Seite"), null);
    public static final ItemStack fillerItem = getItem(Material.GRAY_STAINED_GLASS_PANE, Component.text(" "), false, null);

    public static ItemStack getItem(final Material material, final Component name, final boolean glowing, final List<Component> lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();
        if (name != null) {
            meta.displayName(name.decoration(TextDecoration.ITALIC, false));
        }
        if (lore != null) {
            if (!lore.isEmpty()) {
                meta.lore(lore);
            }
        }
        if (glowing) {
            meta.addEnchant(Enchantment.DURABILITY, 1, false);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getHead(String headID, Component name, List<Component> lore) {
        ItemStack item;
        if(Grimoires.getHookManager().isHooked(HookType.HeadDatabase)) {
            item = new ItemStack(Material.PLAYER_HEAD, 1);
        } else {
            item = HeadDatabaseHook.HeadDatabaseAPI.getItemHead(headID);
        }
        ItemMeta meta = item.getItemMeta();
        if (name != null) {
            meta.displayName(name.decoration(TextDecoration.ITALIC, false));
        }
        if (lore != null) {
            if (!lore.isEmpty()) {
                meta.lore(lore);
            }
        }
        item.setItemMeta(meta);
        return item;
    }
}
