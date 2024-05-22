package net.maksy.grimoires.modules.book_management.publication.gui;

import net.maksy.grimoires.Grimoires;
import net.maksy.grimoires.modules.book_management.publication.PublicationModule;
import net.maksy.grimoires.modules.book_management.storage.Grimoire;
import net.maksy.grimoires.utils.ItemUT;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class PublicationEditor implements Listener {

    private final Inventory inventory;
    private final Player player;
    private final Grimoire grimoire;

    private final AuthorGui authors;
    private final GenreGui genres;

    public PublicationEditor(Player player, Grimoire grimoire) {
        this.player = player;
        this.grimoire = grimoire;
        this.authors = new AuthorGui(this, grimoire.getAuthors());
        this.genres = new GenreGui(this, grimoire.getGenres());
        this.inventory = Bukkit.createInventory(player, 9, PublicationModule.getPublicationCfg().getPublicationTitle());
        Grimoires.registerListener(this);
    }

    public Player getPlayer() {
        return player;
    }

    public Grimoire getGrimoire() {
        return grimoire;
    }

    private void initialize() {
        inventory.setItem(0, ItemUT.fillerItem);
        inventory.setItem(1, PublicationModule.getPublicationCfg().getPublicationBookIcon(grimoire));
        inventory.setItem(2, ItemUT.fillerItem);
        inventory.setItem(3, PublicationModule.getPublicationCfg().getPublicationPricingIcon(grimoire));
        inventory.setItem(4, ItemUT.fillerItem);
        inventory.setItem(5, PublicationModule.getPublicationCfg().getPublicationAuthorsIcon(grimoire));
        inventory.setItem(6, PublicationModule.getPublicationCfg().getPublicationGenresIcon(grimoire));
        inventory.setItem(7, ItemUT.fillerItem);
        inventory.setItem(8, ItemUT.fillerItem);
    }

    public void open() {
        initialize();
        player.openInventory(inventory);
    }

    public void click(InventoryClickEvent event) {
        if(event.getInventory() != inventory) return;
        event.setCancelled(true);
        int slot = event.getSlot();
        switch (slot) {
            case 3 -> {
                grimoire.setAuthors(authors.getAuthors());
                grimoire.setGenres(genres.getGenres());
            }
            case 5 -> authors.open(player);
            case 6 -> genres.open(player);
            case 8 -> player.closeInventory();
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        click(event);
    }
}
