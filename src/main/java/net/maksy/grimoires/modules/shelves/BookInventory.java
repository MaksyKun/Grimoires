package net.maksy.grimoires.modules.shelves;

import net.maksy.grimoires.Grimoires;
import net.maksy.grimoires.modules.GuiSession;
import net.maksy.grimoires.modules.GuiSessionManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.ChiseledBookshelf;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class BookInventory implements Listener, GuiSession {

    private Inventory inventory;
    private final ChiseledBookshelf chiseledBookshelf;
    private boolean registered = false;

    public BookInventory(ChiseledBookshelf chiseledBookshelf) {
        this.inventory = Bukkit.createInventory(null, 9, Grimoires.getConfiguration().getChiseledBookshelfGuiTitle());
        this.chiseledBookshelf = chiseledBookshelf;
        initialize();
    }

    private void initialize() {
        inventory.setItem(0, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        for (int i = 1; i <= 6; i++) {
            inventory.setItem(i, chiseledBookshelf.getInventory().getItem(i - 1));
        }
        inventory.setItem(7, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        inventory.setItem(8, new ItemStack(Material.BARRIER));
    }

    public void click(InventoryClickEvent event) {
        if (inventory == null || !event.getInventory().equals(inventory)) return;
        event.setCancelled(true);
        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        if (item.getType() == Material.BARRIER) {
            event.getWhoClicked().closeInventory();
        }
        if (item.getItemMeta() instanceof BookMeta meta) {
            if (!meta.hasPages()) return;
            event.getWhoClicked().openBook(meta);
        }
    }

    public void open(HumanEntity player) {
        if (!registered) {
            Grimoires.registerListener(this);
            registered = true;
        }
        GuiSessionManager.get().track(inventory, this);
        player.openInventory(inventory);
    }

    @Override
    public void close() {
        HandlerList.unregisterAll(this);
        registered = false;
        GuiSessionManager.get().untrack(this);
        inventory = null;
    }

    @EventHandler
    public void onInventory(InventoryClickEvent event) {
        click(event);
    }
}
