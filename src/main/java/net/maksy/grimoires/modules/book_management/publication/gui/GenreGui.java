package net.maksy.grimoires.modules.book_management.publication.gui;

import net.kyori.adventure.text.Component;
import net.maksy.grimoires.Grimoires;
import net.maksy.grimoires.modules.GuiSession;
import net.maksy.grimoires.modules.GuiSessionManager;
import net.maksy.grimoires.modules.book_management.publication.PublicationModule;
import net.maksy.grimoires.modules.book_management.storage.BookStorageModule;
import net.maksy.grimoires.modules.book_management.storage.Genre;
import net.maksy.grimoires.modules.book_management.storage.Grimoire;
import net.maksy.grimoires.utils.InventoryUT;
import net.maksy.grimoires.utils.ItemUT;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class GenreGui implements Listener, GuiSession {

    private final PublicationEditor editor;

    public List<Genre> allGenres;
    public List<Genre> genres;
    private final Component title;
    private List<Inventory> inventories;

    private final HashMap<Inventory, HashMap<Integer, Genre>> slots = new HashMap<>();

    private boolean registered = false;

    public GenreGui(PublicationEditor editor, List<Genre> initialGenres) {
        this.allGenres = BookStorageModule.getGenreCfg().getAllGenres();
        this.editor = editor;
        this.genres = new ArrayList<>(initialGenres);
        this.title = PublicationModule.getPublicationCfg().getPGenresGuiTitle();
        inventories = List.of(InventoryUT.createFilledInventory(null, title, 45, Material.GRAY_STAINED_GLASS_PANE));
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
        if (!registered) {
            Grimoires.registerListener(this);
            registered = true;
        }
        initialize();
        open(player, 0);
    }

    public void open(Player player, int page) {
        Inventory inv = inventories.get(page) != null ? inventories.get(page) : inventories.get(page - 1);
        // Track under the parent editor's session so GuiSessionManager treats navigation
        // between the editor and this sub-GUI as part of the same session.
        GuiSessionManager.get().track(inv, editor);
        player.openInventory(inv);
    }

    @Override
    public void close() {
        HandlerList.unregisterAll(this);
        registered = false;
        // Do NOT call GuiSessionManager.untrack(this) – inventories are tracked under the
        // editor's session and will be cleaned up when the editor's session closes.
        inventories = Collections.emptyList();
        slots.clear();
    }

    private void initialize() {
        slots.clear();
        HashMap<Integer, Genre> invSlots = new HashMap<>();
        List<Inventory> inventories = new ArrayList<>();
        Inventory inv = null;

        for (int i = 0; i < allGenres.size(); i++) {
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
            if (allGenres.get(i) == null)
                break;
            Genre entry = allGenres.get(i);
            invSlots.put(invDex, entry);
            slots.put(inv, invSlots);
            inv.setItem(invDex, PublicationModule.getPublicationCfg().getGenresGuiGenreIcon(entry, genres.contains(entry)));
        }

        if (inv != null) {
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
            case 4 -> {
            }
            case 36 -> editor.open();
            case 39 -> open(player, (size + invdex - 1) % size);
            case 41 -> open(player, (invdex + 1) % size);
            default -> {
                if (!slots.isEmpty() && slots.get(inventories.get(invdex)).get(slot) != null) {
                    Genre entry = slots.get(inventories.get(invdex)).get(slot);
                    if (genres.contains(entry)) {
                        removeGenre(entry);
                    } else {
                        addGenre(entry);
                    }
                    editor.getGrimoire().setGenres(genres);
                    open(player);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        click(event);
    }
}
