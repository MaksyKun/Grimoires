package net.maksy.grimoires.utils;

import net.maksy.grimoires.Grimoires;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class NamespaceUT {

    public static NamespacedKey getNamespacedKey(ItemStack item) {
        if (item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(Grimoires.getInstance(), "customitem"), PersistentDataType.STRING)) {
            String keyString = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Grimoires.getInstance(), "customitem"), PersistentDataType.STRING);
            return new NamespacedKey(Grimoires.getInstance(), keyString);
        } else {
            return item.getType().getKey();
        }
    }

    public static String getNamespacedKeyString(ItemStack item) {
        if (item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(Grimoires.getInstance(), "customitem"), PersistentDataType.STRING)) {
            return item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Grimoires.getInstance(), "customitem"), PersistentDataType.STRING);
        } else {
            return item.getType().toString().toLowerCase();
        }
    }
}
