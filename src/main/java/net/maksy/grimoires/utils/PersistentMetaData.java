package net.maksy.grimoires.utils;

import net.maksy.grimoires.Grimoires;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("all")
public class PersistentMetaData {

    private static final JavaPlugin plugin = Grimoires.getInstance();

    public static void setNameSpace(ItemMeta itemMeta, String key, String id) {
        itemMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, key), PersistentDataType.STRING, id);
    }

    public static void setNameSpace(ItemMeta itemMeta, String key, int id) {
        itemMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, key), PersistentDataType.INTEGER, id);
    }

    public static String getNameSpaceString(ItemMeta itemMeta, String key) {
        if (!hasNameSpaceString(itemMeta, key))
            return null;

        return itemMeta.getPersistentDataContainer().get(new NamespacedKey(plugin, key), PersistentDataType.STRING);
    }

    public static int getNameSpaceInt(ItemMeta itemMeta, String key) {
        return itemMeta.getPersistentDataContainer().get(new NamespacedKey(plugin, key), PersistentDataType.INTEGER);
    }

    public static boolean hasNameSpaceString(ItemMeta itemMeta, String key) {
        if (itemMeta == null)
            return false;
        return itemMeta.getPersistentDataContainer().get(new NamespacedKey(plugin, key), PersistentDataType.STRING) != null;
    }

    public static boolean hasNameSpaceInt(ItemMeta itemMeta, String key) {
        if (itemMeta == null) {
            return false;
        }
        if(itemMeta.getPersistentDataContainer().get(new NamespacedKey(plugin, key), PersistentDataType.INTEGER) == -1) {
            return false;
        }
        return itemMeta.getPersistentDataContainer().get(new NamespacedKey(plugin, key), PersistentDataType.INTEGER) > 0;
    }
}
