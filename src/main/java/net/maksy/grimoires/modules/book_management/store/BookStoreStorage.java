package net.maksy.grimoires.modules.book_management.store;

import net.kyori.adventure.text.Component;
import net.maksy.grimoires.Grimoires;
import net.maksy.grimoires.configuration.translation.Replaceable;
import net.maksy.grimoires.configuration.translation.Translation;
import net.maksy.grimoires.hooks.HookType;
import net.maksy.grimoires.hooks.VaultHook;
import net.maksy.grimoires.modules.GuiSession;
import net.maksy.grimoires.modules.GuiSessionManager;
import net.maksy.grimoires.modules.book_management.storage.BookStorageModule;
import net.maksy.grimoires.modules.book_management.storage.Genre;
import net.maksy.grimoires.modules.book_management.storage.Grimoire;
import net.maksy.grimoires.modules.book_management.storage.GrimoireRegistry;
import net.maksy.grimoires.utils.InventoryUT;
import net.maksy.grimoires.utils.ItemUT;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class BookStoreStorage implements Listener, GuiSession {

    private Genre selectedGenre = null;

    private final Component mainDisplay;
    private List<Inventory> inventories;
    private final HashMap<Inventory, HashMap<Integer, BookStoreStorage>> folderSlots = new HashMap<>();
    private final HashMap<Inventory, HashMap<Integer, Grimoire>> itemSlots = new HashMap<>();

    private BookStoreStorage parentStorage = null;
    private boolean registered = false;

    public BookStoreStorage() {
        this.mainDisplay = BookStorageModule.getBookStorageCfg().getStoreTitle("");
        this.inventories = List.of(InventoryUT.createFilledInventory(null, mainDisplay, 45, Material.GRAY_STAINED_GLASS_PANE));
        initialize();
    }

    public BookStoreStorage(Genre genre) {
        this.selectedGenre = genre;
        this.mainDisplay = BookStorageModule.getBookStorageCfg().getStoreTitle(genre.getName());
        this.inventories = List.of(InventoryUT.createFilledInventory(null, mainDisplay, 45, Material.GRAY_STAINED_GLASS_PANE));
        initialize();
    }

    public void open(Player player) {
        if (!registered) {
            Grimoires.registerListener(this);
            registered = true;
        }
        initialize();
        open(player, 0);
    }

    public void open(Player player, int page) {
        if (inventories.isEmpty()) {
            player.sendMessage(Component.text("This store has no books to display."));
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
        folderSlots.clear();
        itemSlots.clear();
    }

    private void initialize() {
        if (selectedGenre == null) {
            initializeGenres();
        } else {
            initializeBooks(selectedGenre);
        }
    }

    private void initializeGenres() {
        List<Genre> allGenres = GrimoireRegistry.getGenres();
        List<Genre> entries = allGenres.stream()
                .filter(g -> !GrimoireRegistry.getGrimoires(g).isEmpty())
                .collect(Collectors.toList());

        folderSlots.clear();
        HashMap<Integer, BookStoreStorage> invSlots = new HashMap<>();
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
            if (entries.get(i) == null) break;
            Genre entry = entries.get(i);
            invSlots.put(invDex, new BookStoreStorage(entry));
            invSlots.get(invDex).parentStorage = this;
            folderSlots.put(inv, invSlots);
            inv.setItem(invDex, BookStorageModule.getBookStorageCfg().getGenreIcon(entry));
        }
        inventories.add(inv == null ? InventoryUT.createFilledInventory(null, mainDisplay, 45, Material.GRAY_STAINED_GLASS_PANE) : inv);
        this.inventories = inventories;
    }

    private void initializeBooks(Genre genre) {
        List<Grimoire> entries = GrimoireRegistry.getGrimoires(genre);

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
            if (entries.get(i) == null) break;
            Grimoire entry = entries.get(i);
            invSlots.put(invDex, entry);
            itemSlots.put(inv, invSlots);
            // Use generic (non-player-specific) icon at build time
            inv.setItem(invDex, BookStorageModule.getBookStorageCfg().getStoreBookIcon(entry));
        }
        inventories.add(inv == null ? InventoryUT.createFilledInventory(null, mainDisplay, 45, Material.GRAY_STAINED_GLASS_PANE) : inv);
        this.inventories = inventories;
    }

    /** Refresh book icons for the given player to reflect ownership state. */
    private void refreshBookIcons(Player player, Inventory inv) {
        HashMap<Integer, Grimoire> iSlots = itemSlots.get(inv);
        if (iSlots == null) return;
        for (Map.Entry<Integer, Grimoire> e : iSlots.entrySet()) {
            Grimoire g = e.getValue();
            ItemStack icon;
            if (g.isFree()) {
                icon = BookStorageModule.getBookStorageCfg().getStoreBookIcon(g);
            } else if (Grimoires.sql().playerBooks().hasBook(player.getUniqueId(), g.getId())) {
                icon = BookStorageModule.getBookStorageCfg().getStoreOwnedBookIcon(g);
            } else {
                icon = BookStorageModule.getBookStorageCfg().getStoreBookIcon(g);
            }
            inv.setItem(e.getKey(), icon);
        }
    }

    public void click(InventoryClickEvent event) {
        int invdex = inventories.indexOf(event.getInventory());
        if (invdex < 0) return;
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();
        int size = inventories.size();
        switch (slot) {
            case 36 -> {
                if (parentStorage != null) {
                    parentStorage.open(player);
                } else {
                    player.closeInventory();
                }
            }
            case 39 -> open(player, (size + invdex - 1) % size);
            case 41 -> open(player, (invdex + 1) % size);
            default -> {
                if (!folderSlots.isEmpty()) {
                    HashMap<Integer, BookStoreStorage> fSlots = folderSlots.get(inventories.get(invdex));
                    if (fSlots != null && fSlots.get(slot) != null) {
                        BookStoreStorage entry = fSlots.get(slot);
                        entry.open(player);
                        return;
                    }
                }
                if (!itemSlots.isEmpty()) {
                    HashMap<Integer, Grimoire> iSlots = itemSlots.get(inventories.get(invdex));
                    if (iSlots != null && iSlots.get(slot) != null) {
                        handleBookClick(player, iSlots.get(slot), inventories.get(invdex));
                    }
                }
            }
        }
    }

    private void handleBookClick(Player player, Grimoire grimoire, Inventory inv) {
        // Free books are readable by everyone
        if (grimoire.isFree()) {
            player.openBook(grimoire.getBook(player));
            return;
        }
        // Already owns the book
        if (Grimoires.sql().playerBooks().hasBook(player.getUniqueId(), grimoire.getId())) {
            openOrGiveBook(player, grimoire);
            return;
        }
        // Purchase required
        if (!Grimoires.getHookManager().isHooked(HookType.Vault)) {
            Translation.Vault_ErrorPlayers.sendMessage(player);
            return;
        }
        Economy eco = VaultHook.getEconomy();
        if (eco == null) {
            Translation.Vault_ErrorPlayers.sendMessage(player);
            return;
        }
        double price = grimoire.getSellPrice();
        if (!eco.has(player, price)) {
            Translation.Store_InsufficientFunds.sendMessage(player,
                    new Replaceable("%price%", String.valueOf(price)));
            return;
        }
        eco.withdrawPlayer(player, price);
        Grimoires.sql().playerBooks().addBook(player.getUniqueId(), grimoire.getId());
        Translation.Store_BookBought.sendMessage(player,
                new Replaceable("%title%", grimoire.getTitle()),
                new Replaceable("%price%", String.valueOf(price)));
        refreshBookIcons(player, inv);
        openOrGiveBook(player, grimoire);
    }

    private void openOrGiveBook(Player player, Grimoire grimoire) {
        String buyType = BookStorageModule.getBookStorageCfg().getBuyType();
        if ("physical".equalsIgnoreCase(buyType)) {
            Map<Integer, ItemStack> leftover = player.getInventory().addItem(grimoire.toItemStack());
            if (!leftover.isEmpty()) {
                Translation.Store_InventoryFull.sendMessage(player);
                for (ItemStack item : leftover.values()) {
                    player.getWorld().dropItem(player.getLocation(), item);
                }
            }
            player.closeInventory();
        } else {
            player.openBook(grimoire.getBook(player));
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        click(event);
    }
}
