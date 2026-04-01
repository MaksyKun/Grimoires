package net.maksy.grimoires.modules.book_management.publication.gui;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.maksy.grimoires.Grimoires;
import net.maksy.grimoires.configuration.translation.Replaceable;
import net.maksy.grimoires.configuration.translation.Translation;
import net.maksy.grimoires.modules.GuiSession;
import net.maksy.grimoires.modules.GuiSessionManager;
import net.maksy.grimoires.modules.book_management.publication.PublicationModule;
import net.maksy.grimoires.modules.book_management.publication.SearchType;
import net.maksy.grimoires.utils.InventoryUT;
import net.maksy.grimoires.utils.ItemUT;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.*;

public class AuthorGui implements Listener, GuiSession {

    @Getter
    private final PublicationEditor editor;

    @Getter
    public List<UUID> authors;
    private final Component title;
    private List<Inventory> inventories;

    private final HashMap<Inventory, HashMap<Integer, UUID>> slots = new HashMap<>();

    private boolean registered = false;

    public AuthorGui(PublicationEditor editor, List<UUID> initialAuthors) {
        this.editor = editor;
        this.authors = new ArrayList<>(initialAuthors);
        this.title = PublicationModule.getPublicationCfg().getAuthorsGuiTitle();
        inventories = List.of(InventoryUT.createFilledInventory(null, title, 45, Material.GRAY_STAINED_GLASS_PANE));
    }

    public void addAuthor(UUID author) {
        if (authors.contains(author)) return;
        authors.add(author);
    }

    public void removeAuthor(UUID author) {
        if (author.equals(editor.getPlayer().getUniqueId())) return;
        authors.remove(author);
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
        // between the editor and this sub-GUI as part of the same session.  Without this,
        // opening the sub-GUI would cause the editor's session to be detected as "left",
        // triggering editor.close() → authors.close() → listener unregistered mid-use.
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
        HashMap<Integer, UUID> invSlots = new HashMap<>();
        List<Inventory> inventories = new ArrayList<>();
        Inventory inv = null;

        // Build list of all available players using the configured search mechanic
        Player player = editor.getPlayer();
        List<UUID> players = new ArrayList<>();
        SearchType searchType = PublicationModule.getPlayerSearchMechanic().searchType();
        if (searchType == SearchType.NEARBY) {
            double dist = PublicationModule.getPlayerSearchMechanic().distance();
            for (Entity entity : player.getNearbyEntities(dist, dist, dist)) {
                if (entity instanceof Player) players.add(entity.getUniqueId());
            }
        } else {
            players.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).toList());
            if (searchType == SearchType.ALL) {
                players.addAll(Arrays.stream(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getUniqueId).toList());
            }
        }

        for (int i = 0; i < players.size(); i++) {
            int invDex = i % 27 + 9;
            if (invDex == 9) {
                if (inv != null)
                    inventories.add(inv);
                inv = InventoryUT.createFilledInventory(null, title, 45, Material.GRAY_STAINED_GLASS_PANE);
                inv.setItem(36, ItemUT.backItem);
                inv.setItem(39, ItemUT.previousPageItem);
                inv.setItem(41, ItemUT.nextPageItem);
                invSlots = new HashMap<>();
            }
            if (players.get(i) == null)
                break;
            UUID entry = players.get(i);
            invSlots.put(invDex, entry);
            slots.put(inv, invSlots);
            // Show selected state (glow) if this player is already an author
            inv.setItem(invDex, PublicationModule.getPublicationCfg().getAuthorsGuiAuthorIcon(entry, authors.contains(entry)));
        }

        if (inv != null) {
            inventories.add(inv);
        } else {
            Inventory _inv = InventoryUT.createFilledInventory(null, title, 45, Material.GRAY_STAINED_GLASS_PANE);
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
            case 36 -> editor.open();
            case 39 -> open(player, (size + invdex - 1) % size);
            case 41 -> open(player, (invdex + 1) % size);
            default -> {
                HashMap<Integer, UUID> iSlots = slots.get(inventories.get(invdex));
                if (iSlots != null && iSlots.get(slot) != null) {
                    UUID entry = iSlots.get(slot);
                    if (authors.contains(entry)) {
                        removeAuthor(entry);
                    } else {
                        if (authors.size() >= PublicationModule.getPlayerSearchMechanic().limit()) {
                            Translation.Publication_AuthorsLimitReached.sendMessage(player,
                                    new Replaceable("%limit%", String.valueOf(PublicationModule.getPlayerSearchMechanic().limit())));
                            return;
                        }
                        addAuthor(entry);
                    }
                    editor.getGrimoire().setAuthors(authors);
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
