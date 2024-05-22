package net.maksy.grimoires.modules.book_management.publication.gui;

import net.kyori.adventure.text.Component;
import net.maksy.grimoires.Grimoires;
import net.maksy.grimoires.modules.book_management.publication.PublicationModule;
import net.maksy.grimoires.modules.book_management.storage.BookStorageModule;
import net.maksy.grimoires.modules.book_management.storage.Genre;
import net.maksy.grimoires.modules.book_management.storage.Grimoire;
import net.maksy.grimoires.utils.InventoryUT;
import net.maksy.grimoires.utils.ItemUT;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GenreGui implements Listener {

    private final PublicationEditor editor;

    public List<Genre> allGenres;
    public List<Genre> genres;
    private final Component title;
    private List<Inventory> inventories;

    private final HashMap<Inventory, HashMap<Integer, Genre>> slots = new HashMap<>();

    public GenreGui(PublicationEditor editor, List<Genre> initialGenres) {
        this.allGenres = BookStorageModule.getGenreCfg().getAllGenres();
        this.editor = editor;
        this.genres = new ArrayList<>(initialGenres);
        this.title = PublicationModule.getPublicationCfg().getPGenresGuiTitle();
        inventories = List.of(InventoryUT.createFilledInventory(null, title, 45, Material.GRAY_STAINED_GLASS_PANE));
        Grimoires.registerListener(this);
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void addGenre(Genre genre) {
        genres.add(genre);
    }

    public void removeGenre(Genre genre) {
        genres.remove(genre);
    }

    public void open(Player player) {
        initialize();
        open(player, 0);
    }

    public void open(Player player, int page) {
        player.openInventory(inventories.get(page) != null ? inventories.get(page) : inventories.get(page - 1));
    }

    private void initialize() {
        slots.clear();
        HashMap<Integer, Genre> invSlots = new HashMap<>();
        List<Inventory> inventories = new ArrayList<>();
        Inventory inv = null;

        for (int i = 0; i < genres.size(); i++) {
            int invDex = i % 27 + 9;
            if (invDex == 9) {
                if (inv != null)
                    inventories.add(inv);
                inv = InventoryUT.createFilledInventory(null, title, 45, Material.GRAY_STAINED_GLASS_PANE);
                inv.setItem(4, PublicationModule.getPublicationCfg().getGenresGuiAddIcon());
                inv.setItem(36, ItemUT.backItem);
                inv.setItem(39, ItemUT.previousPageItem);
                inv.setItem(41, ItemUT.nextPageItem);
                invSlots = new HashMap<>();
            }
            if (genres.get(i) == null)
                break;
            Genre entry = genres.get(i);
            invSlots.put(invDex, entry);
            slots.put(inv, invSlots);
            inv.setItem(invDex, PublicationModule.getPublicationCfg().getGenresGuiGenreIcon(entry));
        }

        if(inv != null) {
            inventories.add(inv);
            inventories.add(inv);
        } else {
            Inventory _inv = InventoryUT.createFilledInventory(null, title, 45, Material.GRAY_STAINED_GLASS_PANE);
            _inv.setItem(4, PublicationModule.getPublicationCfg().getGenresGuiAddIcon());
            _inv.setItem(36, ItemUT.backItem);
            _inv.setItem(39, ItemUT.previousPageItem);
            _inv.setItem(41, ItemUT.nextPageItem);
            inventories.add(_inv);
        }
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
            case 4 -> {}
            case 36 -> editor.open();
            case 39 -> open(player, (size + invdex - 1) % size);
            case 41 -> open(player, (invdex + 1) % size);
            default -> {
                if (!slots.isEmpty() && slots.get(inventories.get(invdex)).get(slot) != null) {
                    if(event.isRightClick()) {
                        Genre entry = slots.get(inventories.get(invdex)).get(slot);
                        removeGenre(entry);
                        open(player);
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
