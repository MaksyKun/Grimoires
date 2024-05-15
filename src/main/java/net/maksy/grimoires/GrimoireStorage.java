package net.maksy.grimoires;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;

import java.util.*;

public class GrimoireStorage implements Listener {

    public static Map<String, GrimoireStorage> CachedGenreStorage = new HashMap<>();
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
        this.inventories = new ArrayList<>();
        this.mainDisplay = Grimoires.getConfigManager().getMainItemFolderTitle("");
        Grimoires.registerListener(this);
        initialize();
    }

    public GrimoireStorage(UUID uuid) {
        this.selectedUUID = uuid;
        this.inventories = new ArrayList<>();
        this.mainDisplay = Grimoires.getConfigManager().getMainItemFolderTitle(Bukkit.getOfflinePlayer(uuid).getName());
        Grimoires.registerListener(this);
        initialize();
    }

    public GrimoireStorage(Genre genre) {
        this.selectedGenre = genre;
        this.inventories = new ArrayList<>();
        this.mainDisplay = Grimoires.getConfigManager().getMainItemFolderTitle(genre.getName());
        Grimoires.registerListener(this);
        initialize();
    }

    public GrimoireStorage(UUID uuid, Genre genre) {
        this.selectedUUID = uuid;
        this.selectedGenre = genre;
        this.inventories = new ArrayList<>();
        this.mainDisplay = Grimoires.getConfigManager().getMainItemFolderTitle(Bukkit.getOfflinePlayer(uuid).getName() + " - " + genre.getName());
        Grimoires.registerListener(this);
        initialize();
    }

    public void initialize() {
        List<Grimoire> books = Grimoires.sql().getBooksSQL().getBooks(null, null);
        if (this.selectedUUID != null && this.selectedGenre != null) {

        } else if (this.selectedUUID != null) {

        } else if (this.selectedGenre != null) {

        } else {

        }
    }

    private void initializeGenres(List<Genre> genres) {

    }

    private void initializeAuthors(List<UUID> authors) {

    }

    private void initializeBooks(UUID author, Genre genre) {

    }
}
