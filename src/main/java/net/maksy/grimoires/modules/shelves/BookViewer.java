package net.maksy.grimoires.modules.shelves;

import net.maksy.grimoires.Grimoires;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.ChiseledBookshelf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class BookViewer implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!Grimoires.getConfiguration().isChiseledBookshelfGuiEnabled()) return;
        ItemStack item = event.getItem();
        if (item != null && (item.getType() == Material.WRITTEN_BOOK || item.getType() == Material.WRITABLE_BOOK || item.getType() == Material.WRITTEN_BOOK || item.getType() == Material.ENCHANTED_BOOK || item.getType() == Material.KNOWLEDGE_BOOK))
            return;
        Block block = event.getClickedBlock();
        if (block == null) return;
        if (event.getAction() == Grimoires.getConfiguration().getBookshelfAction()) {
            if((Grimoires.getConfiguration().requiresBookshelfSneaking() && event.getPlayer().isSneaking())
                    || (!Grimoires.getConfiguration().requiresBookshelfSneaking() && !event.getPlayer().isSneaking())) {
                if (block.getState() instanceof ChiseledBookshelf chiseledBookshelf) {
                    event.setCancelled(true);
                    new BookInventory(chiseledBookshelf).open(event.getPlayer());
                }
            }
        }
    }
}
