package net.maksy.grimoires.modules.shelves;

import net.maksy.grimoires.Grimoires;
import net.maksy.grimoires.modules.book_management.storage.Grimoire;
import net.maksy.grimoires.modules.book_management.storage.GrimoireRegistry;
import net.maksy.grimoires.utils.ChatUT;
import net.maksy.grimoires.utils.PersistentMetaData;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.ChiseledBookshelf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class BookViewer implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!Grimoires.getConfiguration().isChiseledBookshelfGuiEnabled()) return;
        ItemStack item = event.getItem();

        if (item == null || (item.getType() == Material.WRITTEN_BOOK || item.getType() == Material.WRITABLE_BOOK || item.getType() == Material.WRITTEN_BOOK || item.getType() == Material.ENCHANTED_BOOK || item.getType() == Material.KNOWLEDGE_BOOK))
            return;
        Block block = event.getClickedBlock();
        if (block == null) return;
        if (event.getAction() == Grimoires.getConfiguration().getBookshelfAction()) {
            if ((Grimoires.getConfiguration().requiresBookshelfSneaking() && event.getPlayer().isSneaking())
                    || (!Grimoires.getConfiguration().requiresBookshelfSneaking() && !event.getPlayer().isSneaking())) {
                if (block.getState() instanceof ChiseledBookshelf chiseledBookshelf) {
                    event.setCancelled(true);
                    new BookInventory(chiseledBookshelf).open(event.getPlayer());
                }
            }
        }
    }

    @EventHandler
    public void onRead(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null || item.getType() == Material.AIR) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (PersistentMetaData.getNameSpaceString(item.getItemMeta(), "grimoire") != null) {
                event.setCancelled(true);
                String id = PersistentMetaData.getNameSpaceString(item.getItemMeta(), "grimoire");
                Grimoire grimoire = GrimoireRegistry.getGrimoire(Integer.parseInt(id));
                if (grimoire == null) return;
                event.getPlayer().openBook(grimoire.getBook(event.getPlayer()));
            }
        }
    }
}
