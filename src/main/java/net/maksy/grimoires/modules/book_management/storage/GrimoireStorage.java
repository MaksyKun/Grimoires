package net.maksy.grimoires.modules.book_management.storage;

import net.kyori.adventure.text.Component;
import net.maksy.grimoires.Grimoires;
import net.maksy.grimoires.modules.GuiSession;
import net.maksy.grimoires.modules.GuiSessionManager;
import net.maksy.grimoires.utils.InventoryUT;
import net.maksy.grimoires.utils.ItemUT;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.*;

public class GrimoireStorage implements Listener, GuiSession {
    private UUID selectedUUID = null;
    private Genre selectedGenre = null;

    private final Component mainDisplay;
    private List<Inventory> inventories;
    private final HashMap<Inventory, HashMap<Integer, GrimoireStorage>> folderSlots = new HashMap<>();
    private final HashMap<Inventory, HashMap<Integer, Grimoire>> itemSlots = new HashMap<>();

    private boolean registered = false;

    public GrimoireStorage() {
        this.mainDisplay = BookStorageModule.getBookStorageCfg().getTitle("");
        this.inventories = List.of(InventoryUT.createFilledInventory(null, mainDisplay, 45, Material.GRAY_STAINED_GLASS_PANE));
        initialize();
    }

    public GrimoireStorage(UUID uuid) {
        this.selectedUUID = uuid;
        this.mainDisplay = BookStorageModule.getBookStorageCfg().getTitle("");
        this.inventories = List.of(InventoryUT.createFilledInventory(null, mainDisplay, 45, Material.GRAY_STAINED_GLASS_PANE));
        initialize();
    }

    public GrimoireStorage(Genre genre) {
        this.selectedGenre = genre;
        this.mainDisplay = BookStorageModule.getBookStorageCfg().getTitle(genre.getName());
        this.inventories = List.of(InventoryUT.createFilledInventory(null, mainDisplay, 45, Material.GRAY_STAINED_GLASS_PANE));
        initialize();
    }

    public GrimoireStorage(UUID uuid, Genre genre) {
        this.selectedUUID = uuid;
        this.selectedGenre = genre;
        this.mainDisplay = BookStorageModule.getBookStorageCfg().getTitle(genre.getName());
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
            player.sendMessage(Component.text("This grimoire has no pages to display."));
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
        // Close any nested child sessions that were opened
        for (HashMap<Integer, GrimoireStorage> slots : folderSlots.values()) {
            for (GrimoireStorage child : slots.values()) {
                if (child.registered) child.close();
            }
        }
        inventories = Collections.emptyList();
        folderSlots.clear();
        itemSlots.clear();
    }

    public void initialize() {
        List<Genre> genres = this.selectedUUID == null ? GrimoireRegistry.getGenres() : GrimoireRegistry.getGenres(this.selectedUUID);
        if (this.selectedUUID != null && this.selectedGenre != null) {
            // Book view of specific author and genre
            initializeBooks(this.selectedUUID, this.selectedGenre);
        } else if (this.selectedUUID != null) {
            // Genre view of specific author
            initializeGenres(genres);
        } else if (this.selectedGenre != null) {
            // Book view of specific genre
            initializeBooks(null, this.selectedGenre);
        } else {
            // Genre view of all books
            initializeGenres(genres);
        }
    }

    private void initializeGenres(List<Genre> entries) {
        folderSlots.clear();
        HashMap<Integer, GrimoireStorage> invSlots = new HashMap<>();
        List<Inventory> inventories = new ArrayList<>();
        Inventory inv = null;

        for (int i = 0; i < entries.size(); i++) {
            int invDex = i % 27 + 9;
            if (invDex == 9) {
                if (inv != null)
                    inventories.add(inv);
                inv = InventoryUT.createFilledInventory(null, mainDisplay, 45, Material.GRAY_STAINED_GLASS_PANE);
                inv.setItem(36, ItemUT.backItem);
                inv.setItem(39, ItemUT.previousPageItem);
                inv.setItem(41, ItemUT.nextPageItem);
                invSlots = new HashMap<>();
            }
            if (entries.get(i) == null)
                break;
            Genre entry = entries.get(i);
            invSlots.put(invDex, new GrimoireStorage(selectedUUID, entry));
            folderSlots.put(inv, invSlots);
            inv.setItem(invDex, BookStorageModule.getBookStorageCfg().getGenreIcon(entry));
        }
        inventories.add(inv == null ? InventoryUT.createFilledInventory(null, mainDisplay, 45, Material.GRAY_STAINED_GLASS_PANE) : inv);
        this.inventories = inventories;
    }

    private void initializeBooks(UUID author, Genre genre) {
        List<Grimoire> entries = author == null ? GrimoireRegistry.getGrimoires(genre) : GrimoireRegistry.getGrimoires(author, genre);

        itemSlots.clear();
        HashMap<Integer, Grimoire> invSlots = new HashMap<>();
        List<Inventory> inventories = new ArrayList<>();
        Inventory inv = null;

        for (int i = 0; i < entries.size(); i++) {
            int invDex = i % 27 + 9;
            if (invDex == 9) {
                if (inv != null)
                    inventories.add(inv);
                inv = InventoryUT.createFilledInventory(null, mainDisplay, 45, Material.GRAY_STAINED_GLASS_PANE);
                inv.setItem(36, ItemUT.backItem);
                inv.setItem(39, ItemUT.previousPageItem);
                inv.setItem(41, ItemUT.nextPageItem);
                invSlots = new HashMap<>();
            }
            if (entries.get(i) == null)
                break;
            Grimoire entry = entries.get(i);
            invSlots.put(invDex, entry);
            itemSlots.put(inv, invSlots);
            inv.setItem(invDex, BookStorageModule.getBookStorageCfg().getBookIcon(entry));
        }
        inventories.add(inv == null ? InventoryUT.createFilledInventory(null, mainDisplay, 45, Material.GRAY_STAINED_GLASS_PANE) : inv);
        this.inventories = inventories;
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

            }
            case 39 -> open(player, (size + invdex - 1) % size);
            case 41 -> open(player, (invdex + 1) % size);
            default -> {
                if (!folderSlots.isEmpty()) {
                    HashMap<Integer, GrimoireStorage> fSlots = folderSlots.get(inventories.get(invdex));
                    if (fSlots != null && fSlots.get(slot) != null) {
                        GrimoireStorage entry = fSlots.get(slot);
                        entry.open(player);
                    }
                }
                if (!itemSlots.isEmpty()) {
                    HashMap<Integer, Grimoire> iSlots = itemSlots.get(inventories.get(invdex));
                    if (iSlots != null && iSlots.get(slot) != null) {
                        Grimoire entry = iSlots.get(slot);
                        player.openBook(entry.getBook(player));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        click(event);
    }
}
