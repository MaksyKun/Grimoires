package net.maksy.grimoires.modules.shelves;

import net.maksy.grimoires.Grimoires;
import net.maksy.grimoires.configuration.translation.Translation;
import net.maksy.grimoires.modules.api.events.BookOpenShelfEvent;
import net.maksy.grimoires.modules.api.events.BookReadEvent;
import net.maksy.grimoires.modules.book_management.storage.BookStorageModule;
import net.maksy.grimoires.modules.book_management.storage.Grimoire;
import net.maksy.grimoires.modules.book_management.storage.GrimoireRegistry;
import net.maksy.grimoires.utils.PersistentMetaData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.ChiseledBookshelf;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class BookViewer implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!BookStorageModule.getBookStorageCfg().isChiseledBookshelfGuiEnabled()) return;
        ItemStack item = event.getItem();

        if (item == null || (item.getType() == Material.WRITTEN_BOOK || item.getType() == Material.WRITABLE_BOOK || item.getType() == Material.ENCHANTED_BOOK || item.getType() == Material.KNOWLEDGE_BOOK))
            return;
        Block block = event.getClickedBlock();
        if (block == null) return;
        if (event.getAction() == BookStorageModule.getBookStorageCfg().getBookshelfAction()) {
            if ((BookStorageModule.getBookStorageCfg().requiresBookshelfSneaking() && event.getPlayer().isSneaking())
                    || (!BookStorageModule.getBookStorageCfg().requiresBookshelfSneaking() && !event.getPlayer().isSneaking())) {
                if (block.getState() instanceof ChiseledBookshelf chiseledBookshelf) {
                    BookOpenShelfEvent shelfEvent = new BookOpenShelfEvent(event.getPlayer(), chiseledBookshelf);
                    Bukkit.getPluginManager().callEvent(shelfEvent);
                    if (shelfEvent.isCancelled()) return;
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

                Player player = event.getPlayer();

                // Physical book restriction: require the reader to have purchased the book
                if (!grimoire.isFree() && BookStorageModule.getBookStorageCfg().isPhysicalBookRestricted()) {
                    if (!Grimoires.sql().playerBooks().hasBook(player.getUniqueId(), grimoire.getId())) {
                        Translation.Store_NotAllowedToRead.sendMessage(player);
                        return;
                    }
                }

                // Fire read event – allow cancellation
                BookReadEvent readEvent = new BookReadEvent(player, grimoire);
                Bukkit.getPluginManager().callEvent(readEvent);
                if (readEvent.isCancelled()) return;

                player.openBook(grimoire.getBook(player));
            }
        }
    }
}
