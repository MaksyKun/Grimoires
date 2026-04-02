package net.maksy.grimoires.modules.book_management.store;

import net.kyori.adventure.text.Component;
import net.maksy.grimoires.Grimoires;
import net.maksy.grimoires.configuration.translation.Replaceable;
import net.maksy.grimoires.configuration.translation.Translation;
import net.maksy.grimoires.hooks.HookType;
import net.maksy.grimoires.hooks.VaultHook;
import net.maksy.grimoires.modules.GuiSession;
import net.maksy.grimoires.modules.GuiSessionManager;
import net.maksy.grimoires.modules.api.events.BookReboughtEvent;
import net.maksy.grimoires.modules.book_management.storage.BookStorageModule;
import net.maksy.grimoires.modules.book_management.storage.Grimoire;
import net.maksy.grimoires.modules.book_management.storage.GrimoireRegistry;
import net.maksy.grimoires.utils.InventoryUT;
import net.maksy.grimoires.utils.ItemUT;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * GUI shown in physical buyType mode that lets players re-purchase a replacement copy
 * of a book they already own but have lost. Ownership is NOT re-recorded.
 */
public class BookRebuyStorage implements Listener, GuiSession {

    private final Player player;
    private final BookStoreStorage parentStore;

    private final Component mainDisplay;
    private List<Inventory> inventories;
    private final HashMap<Inventory, HashMap<Integer, Grimoire>> itemSlots = new HashMap<>();

    private boolean registered = false;

    public BookRebuyStorage(Player player, BookStoreStorage parentStore) {
        this.player = player;
        this.parentStore = parentStore;
        this.mainDisplay = BookStorageModule.getBookStorageCfg().getRecoverTitle();
        this.inventories = List.of(InventoryUT.createFilledInventory(null, mainDisplay, 45, Material.GRAY_STAINED_GLASS_PANE));
        initialize();
    }

    private void initialize() {
        // Show only paid books the player already owns
        List<Integer> ownedIds = Grimoires.sql().playerBooks().getBooks(player.getUniqueId());
        List<Grimoire> entries = new ArrayList<>();
        for (int id : ownedIds) {
            Grimoire g = GrimoireRegistry.getGrimoire(id);
            if (g != null && !g.isFree()) {
                entries.add(g);
            }
        }

        itemSlots.clear();
        HashMap<Integer, Grimoire> invSlots = new HashMap<>();
        List<Inventory> inventories = new ArrayList<>();
        Inventory inv = null;

        for (int i = 0; i < entries.size(); i++) {
            int invDex = i % 27 + 9;
            if (invDex == 9) {
                if (inv != null) inventories.add(inv);
                inv = InventoryUT.createFilledInventory(null, mainDisplay, 45, Material.GRAY_STAINED_GLASS_PANE);
                inv.setItem(36, ItemUT.backItem);
                inv.setItem(39, ItemUT.previousPageItem);
                inv.setItem(41, ItemUT.nextPageItem);
                invSlots = new HashMap<>();
            }
            Grimoire entry = entries.get(i);
            invSlots.put(invDex, entry);
            itemSlots.put(inv, invSlots);
            inv.setItem(invDex, BookStorageModule.getBookStorageCfg().getRecoverBookIcon(entry));
        }
        inventories.add(inv == null ? InventoryUT.createFilledInventory(null, mainDisplay, 45, Material.GRAY_STAINED_GLASS_PANE) : inv);
        this.inventories = inventories;
    }

    public void open() {
        if (!registered) {
            Grimoires.registerListener(this);
            registered = true;
        }
        open(0);
    }

    private void open(int page) {
        if (inventories.isEmpty()) {
            player.sendMessage(Component.text("You have no books to recover."));
            return;
        }
        int safePage = Math.max(0, Math.min(page, inventories.size() - 1));
        Inventory inv = inventories.get(safePage);
        GuiSessionManager.get().track(inv, this);
        player.openInventory(inv);
    }

    @Override
    public void close() {
        HandlerList.unregisterAll(this);
        registered = false;
        GuiSessionManager.get().untrack(this);
        inventories = Collections.emptyList();
        itemSlots.clear();
    }

    public void click(InventoryClickEvent event) {
        int invdex = inventories.indexOf(event.getInventory());
        if (invdex < 0) return;
        event.setCancelled(true);
        int slot = event.getSlot();
        int size = inventories.size();
        switch (slot) {
            case 36 -> parentStore.open(player);
            case 39 -> open((size + invdex - 1) % size);
            case 41 -> open((invdex + 1) % size);
            default -> {
                HashMap<Integer, Grimoire> iSlots = itemSlots.get(inventories.get(invdex));
                if (iSlots != null && iSlots.get(slot) != null) {
                    handleRebuyClick(iSlots.get(slot));
                }
            }
        }
    }

    private void handleRebuyClick(Grimoire grimoire) {
        double price = grimoire.getSellPrice();

        // Fire the rebuy event before charging
        BookReboughtEvent rebuyEvent = new BookReboughtEvent(player, grimoire, price);
        Bukkit.getPluginManager().callEvent(rebuyEvent);
        if (rebuyEvent.isCancelled()) return;

        // Free books don't need a payment check
        if (price > 0) {
            if (!Grimoires.getHookManager().isHooked(HookType.Vault)) {
                Translation.Vault_ErrorPlayers.sendMessage(player);
                return;
            }
            Economy eco = VaultHook.getEconomy();
            if (eco == null) {
                Translation.Vault_ErrorPlayers.sendMessage(player);
                return;
            }
            if (!eco.has(player, price)) {
                Translation.Store_InsufficientFunds.sendMessage(player,
                        new Replaceable("%price%", String.valueOf(price)));
                return;
            }
            eco.withdrawPlayer(player, price);
        }

        Translation.Store_BookRetrieved.sendMessage(player,
                new Replaceable("%title%", grimoire.getTitle()),
                new Replaceable("%price%", String.valueOf(price)));

        // Deliver physical copy – ownership table is NOT updated
        Map<Integer, ItemStack> leftover = player.getInventory().addItem(grimoire.toItemStack());
        if (!leftover.isEmpty()) {
            Translation.Store_InventoryFull.sendMessage(player);
            for (ItemStack item : leftover.values()) {
                player.getWorld().dropItem(player.getLocation(), item);
            }
        }
        player.closeInventory();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        click(event);
    }
}
