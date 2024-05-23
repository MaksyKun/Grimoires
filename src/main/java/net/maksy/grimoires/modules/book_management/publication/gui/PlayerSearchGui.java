package net.maksy.grimoires.modules.book_management.publication.gui;

import net.kyori.adventure.text.Component;
import net.maksy.grimoires.Grimoires;
import net.maksy.grimoires.configuration.translation.Replaceable;
import net.maksy.grimoires.configuration.translation.Translation;
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
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.*;

public class PlayerSearchGui implements Listener {

    private final AuthorGui authorGui;

    private final Component title;
    private List<Inventory> inventories;

    private final HashMap<Inventory, HashMap<Integer, UUID>> slots = new HashMap<>();

    public PlayerSearchGui(AuthorGui authorGui) {
        this.authorGui = authorGui;
        this.title = PublicationModule.getPublicationCfg().getAuthorsGuiTitle();
        Grimoires.registerListener(this);
    }

    public void open(Player player) {
        initialize();
        open(player, 0);
    }

    public void open(Player player, int page) {
        player.openInventory(inventories.get(page) != null ? inventories.get(page) : inventories.get(page - 1));
    }

    public void initialize() {
        slots.clear();
        HashMap<Integer, UUID> invSlots = new HashMap<>();
        List<Inventory> inventories = new ArrayList<>();
        Inventory inv = null;

        Player player = authorGui.getEditor().getPlayer();
        List<UUID> players = new ArrayList<>();
        SearchType searchType = PublicationModule.getPlayerSearchMechanic().searchType();
        if (searchType == SearchType.NEARBY) {
            List<Entity> entities = player.getNearbyEntities(PublicationModule.getPlayerSearchMechanic().distance(), PublicationModule.getPlayerSearchMechanic().distance(), PublicationModule.getPlayerSearchMechanic().distance());
            for (Entity entity : entities) {
                if (entity instanceof Player)
                    players.add(entity.getUniqueId());
            }
        } else {
            players.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).toList());
            if (searchType == SearchType.ALL)
                players.addAll(Arrays.stream(Bukkit.getOfflinePlayers()).toList().stream().map(OfflinePlayer::getUniqueId).toList());
        }

        players.removeAll(authorGui.getAuthors());

        for (int i = 0; i < players.size(); i++) {
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
            if (players.get(i) == null)
                break;
            UUID entry = players.get(i);
            invSlots.put(invDex, entry);
            slots.put(inv, invSlots);
            inv.setItem(invDex, PublicationModule.getPublicationCfg().getAuthorsGuiAuthorIcon(entry));
        }

        if (inv != null) {
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

        if(PublicationModule.getPlayerSearchMechanic().searchType() != SearchType.ALL) {
            Bukkit.getScheduler().runTaskLater(Grimoires.getInstance(), () -> {
                for (Inventory _inv : inventories)
                    if (_inv.getViewers().contains(player)) {
                        initialize();
                        break;
                    }
            }, PublicationModule.getPlayerSearchMechanic().interval());
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
            case 4 -> {
            }
            case 36 -> authorGui.open(player);
            case 39 -> open(player, (size + invdex - 1) % size);
            case 41 -> open(player, (invdex + 1) % size);
            default -> {
                if (!slots.isEmpty() && slots.get(inventories.get(invdex)).get(slot) != null) {
                    if(authorGui.getAuthors().size() >= PublicationModule.getPlayerSearchMechanic().limit()) {
                        Translation.Publication_AuthorsLimitReached.sendMessage(player, new Replaceable("%limit%", String.valueOf(PublicationModule.getPlayerSearchMechanic().limit())));
                        return;
                    }
                    UUID entry = slots.get(inventories.get(invdex)).get(slot);
                    authorGui.addAuthor(entry);
                    authorGui.getEditor().getGrimoire().setAuthors(authorGui.getAuthors());
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
