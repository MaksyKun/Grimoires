package net.maksy.grimoires.modules.storage;

import net.kyori.adventure.text.Component;
import net.maksy.grimoires.Grimoires;
import net.maksy.grimoires.utils.InventoryUT;
import net.maksy.grimoires.utils.ItemUT;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.*;

public class GrimoireStorage implements Listener {

    public static Map<Genre, GrimoireStorage> CachedGenreStorage = new HashMap<>();
    public static Map<UUID, GrimoireStorage> CachedAuthorStorage = new HashMap<>();

    private final List<GrimoireStorage> folders = new ArrayList<>();
    private final List<Grimoire> books = new ArrayList<>();

    private UUID selectedUUID = null;
    private Genre selectedGenre = null;

    private final Component mainDisplay;
    private List<Inventory> inventories;
    private final HashMap<Inventory, HashMap<Integer, GrimoireStorage>> folderSlots = new HashMap<>();
    private final HashMap<Inventory, HashMap<Integer, Grimoire>> itemSlots = new HashMap<>();

    public GrimoireStorage() {
        this.mainDisplay = Grimoires.getConfiguration().getMainItemFolderTitle("");
        this.inventories = List.of(InventoryUT.createFilledInventory(null, mainDisplay, 45, Material.GRAY_STAINED_GLASS_PANE));
        Grimoires.registerListener(this);
        initialize();
    }

    public GrimoireStorage(UUID uuid) {
        this.selectedUUID = uuid;
        this.mainDisplay = Grimoires.getConfiguration().getMainItemFolderTitle("");
        this.inventories = List.of(InventoryUT.createFilledInventory(null, mainDisplay, 45, Material.GRAY_STAINED_GLASS_PANE));
        Grimoires.registerListener(this);
        initialize();
    }

    public GrimoireStorage(Genre genre) {
        this.selectedGenre = genre;
        this.mainDisplay = Grimoires.getConfiguration().getMainItemFolderTitle(genre.getName());
        this.inventories = List.of(InventoryUT.createFilledInventory(null, mainDisplay, 45, Material.GRAY_STAINED_GLASS_PANE));
        Grimoires.registerListener(this);
        initialize();
    }

    public GrimoireStorage(UUID uuid, Genre genre) {
        this.selectedUUID = uuid;
        this.selectedGenre = genre;
        this.mainDisplay = Grimoires.getConfiguration().getMainItemFolderTitle(genre.getName());
        this.inventories = List.of(InventoryUT.createFilledInventory(null, mainDisplay, 45, Material.GRAY_STAINED_GLASS_PANE));
        Grimoires.registerListener(this);
        initialize();
    }

    public void open(Player player) {
        initialize();
        open(player, 0);
    }

    public void open(Player player, int page) {
        player.openInventory(inventories.get(page) != null ? inventories.get(page) : inventories.get(page - 1));
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
            inv.setItem(invDex, Grimoires.getConfiguration().getGenreIcon(entry));
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
            inv.setItem(invDex, Grimoires.getConfiguration().getBookIcon(entry));
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
                if (!folderSlots.isEmpty() && folderSlots.get(inventories.get(invdex)).get(slot) != null) {
                    GrimoireStorage entry = folderSlots.get(inventories.get(invdex)).get(slot);
                    entry.open(player);
                }
                if (!itemSlots.isEmpty() && itemSlots.get(inventories.get(invdex)).get(slot) != null) {
                    Grimoire entry = itemSlots.get(inventories.get(invdex)).get(slot);
                    player.openBook(entry.getBook());
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        click(event);
    }
}
