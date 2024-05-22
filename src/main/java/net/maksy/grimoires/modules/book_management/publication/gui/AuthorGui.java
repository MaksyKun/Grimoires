package net.maksy.grimoires.modules.book_management.publication.gui;

import net.kyori.adventure.text.Component;
import net.maksy.grimoires.Grimoires;
import net.maksy.grimoires.modules.book_management.publication.PublicationModule;
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
import java.util.UUID;

public class AuthorGui implements Listener {

    private final PublicationEditor editor;

    public List<UUID> authors;
    private final Component title;
    private List<Inventory> inventories;

    private final HashMap<Inventory, HashMap<Integer, UUID>> slots = new HashMap<>();

    public AuthorGui(PublicationEditor editor, List<UUID> initialAuthors) {
        this.editor = editor;
        this.authors = new ArrayList<>(initialAuthors);
        this.title = PublicationModule.getPublicationCfg().getAuthorsGuiTitle();
        inventories = List.of(InventoryUT.createFilledInventory(null, title, 45, Material.GRAY_STAINED_GLASS_PANE));
        Grimoires.registerListener(this);
    }

    public List<UUID> getAuthors() {
        return authors;
    }

    public void addAuthor(UUID author) {
        if(authors.contains(author)) return;
        authors.add(author);
    }

    public void removeAuthor(UUID author) {
        if(author.equals(editor.getPlayer().getUniqueId())) return;
        authors.remove(author);
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
        HashMap<Integer, UUID> invSlots = new HashMap<>();
        List<Inventory> inventories = new ArrayList<>();
        Inventory inv = null;

        for (int i = 0; i < authors.size(); i++) {
            int invDex = i % 27 + 9;
            if (invDex == 9) {
                if (inv != null)
                    inventories.add(inv);
                inv = InventoryUT.createFilledInventory(null, title, 45, Material.GRAY_STAINED_GLASS_PANE);
                inv.setItem(4, PublicationModule.getPublicationCfg().getAuthorsGuiAddIcon());
                inv.setItem(36, ItemUT.backItem);
                inv.setItem(39, ItemUT.previousPageItem);
                inv.setItem(41, ItemUT.nextPageItem);
                invSlots = new HashMap<>();
            }
            if (authors.get(i) == null)
                break;
            UUID entry = authors.get(i);
            invSlots.put(invDex, entry);
            slots.put(inv, invSlots);
            inv.setItem(invDex, PublicationModule.getPublicationCfg().getAuthorsGuiAuthorIcon(entry));
        }

        if(inv != null) {
            inventories.add(inv);
            inventories.add(inv);
        } else {
            Inventory _inv = InventoryUT.createFilledInventory(null, title, 45, Material.GRAY_STAINED_GLASS_PANE);
            _inv.setItem(4, PublicationModule.getPublicationCfg().getAuthorsGuiAddIcon());
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
                        UUID entry = slots.get(inventories.get(invdex)).get(slot);
                        removeAuthor(entry);
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
