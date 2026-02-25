package net.maksy.grimoires.modules.book_management.storage;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.maksy.grimoires.Grimoires;
import net.maksy.grimoires.configuration.YamlParser;
import net.maksy.grimoires.utils.ChatUT;
import net.maksy.grimoires.utils.ItemUT;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BookStorageCfg {

    private final YamlParser config;

    public BookStorageCfg() {
        this.config = YamlParser.loadOrExtract(Grimoires.getInstance(), "Features/BookStorage.yml");
    }

    public String getStatus(boolean status) {
        return config.getString("Storage.Status." + (status ? "True" : "False"));
    }

    public String getDateTime(long time) {
        if (time == -1) return config.getString("Storage.Status.Unpublished");
        Instant instant = Instant.ofEpochMilli(time);
        return LocalDateTime.ofInstant(instant, ZoneId.of(config.getString("Storage.ZoneId", "UTC"))).format(DateTimeFormatter.ofPattern(config.getString("Storage.DateTimeFormat", "dd/MM/yyyy HH:mm:ss")));
    }

    public Component getTitle(String folder) {
        return ChatUT.hexComp(config.getString("Storage.DisplayTitle", "Books").replace("%name%", folder));
    }

    public ItemStack getGenreIcon(Genre genre) {
        Material material = Material.valueOf(config.getString("Storage.Icons.Folder.Material", "BOOK").toUpperCase());
        Component title = ChatUT.hexComp(config.getString("Storage.Icons.Folder.Title", "&9" + genre.getName()).replace("%genre%", genre.getName()));
        List<Component> lore = new ArrayList<>();

        for (String line : config.getStringList("Storage.Icons.Folder.Lore")) {
            lore.add(ChatUT.hexComp(line));
        }
        for (String line : genre.getDescription())
            lore.add(ChatUT.hexComp(line));
        return ItemUT.getItem(material, title, false, lore);
    }

    public ItemStack getBookIcon(Grimoire grimoire) {
        Material material = Material.valueOf(config.getString("Storage.Icons.Book.Material", "WRITTEN_BOOK").toUpperCase());
        Component title = ChatUT.hexComp(config.getString("Storage.Icons.Book.Title", "&9" + grimoire.getTitle()).replace("%title%", grimoire.getTitle()));
        List<Component> lore = new ArrayList<>();

        for (String line : config.getStringList("Storage.Icons.Book.Lore"))
            lore.add(ChatUT.hexComp(line)
                    .replaceText(TextReplacementConfig.builder().match("%description%").replacement(Component.text(grimoire.getDescription())).build())
                    .replaceText(TextReplacementConfig.builder().match("%authors%").replacement(grimoire.getAuthorsComponent()).build())
                    .replaceText(TextReplacementConfig.builder().match("%date%").replacement(getDateTime(grimoire.getPublishedOn())).build())
            );
        return ItemUT.getItem(material, title, false, lore);
    }

    /* Book Store */
    public String getBuyType() {
        return config.getString("Store.BuyType", "virtual").toLowerCase();
    }

    public boolean isPhysicalBookRestricted() {
        return config.getBoolean("Store.PhysicalBookRestricted", false);
    }

    public Component getStoreTitle(String folder) {
        return ChatUT.hexComp(config.getString("Store.DisplayTitle", "Book Store %name%").replace("%name%", folder));
    }

    public Component getRecoverTitle() {
        return ChatUT.hexComp(config.getString("Store.RecoverTitle", "Recover Lost Books"));
    }

    public ItemStack getStoreBookIcon(Grimoire grimoire) {
        boolean free = grimoire.isFree();
        String section = free ? "Store.Icons.FreeBook" : "Store.Icons.Book";
        String priceText = free ? "Free" : grimoire.getSellPrice() + "$";
        Material material = Material.valueOf(config.getString(section + ".Material", "WRITTEN_BOOK").toUpperCase());
        Component title = ChatUT.hexComp(config.getString(section + ".Title", "&9%title%").replace("%title%", grimoire.getTitle()));
        List<Component> lore = new ArrayList<>();
        for (String line : config.getStringList(section + ".Lore"))
            lore.add(ChatUT.hexComp(line)
                    .replaceText(TextReplacementConfig.builder().match("%title%").replacement(Component.text(grimoire.getTitle())).build())
                    .replaceText(TextReplacementConfig.builder().match("%authors%").replacement(grimoire.getAuthorsComponent()).build())
                    .replaceText(TextReplacementConfig.builder().match("%price%").replacement(Component.text(priceText)).build())
            );
        return ItemUT.getItem(material, title, false, lore);
    }

    public ItemStack getStoreOwnedBookIcon(Grimoire grimoire) {
        Material material = Material.valueOf(config.getString("Store.Icons.OwnedBook.Material", "WRITTEN_BOOK").toUpperCase());
        Component title = ChatUT.hexComp(config.getString("Store.Icons.OwnedBook.Title", "&9%title%").replace("%title%", grimoire.getTitle()));
        List<Component> lore = new ArrayList<>();
        for (String line : config.getStringList("Store.Icons.OwnedBook.Lore"))
            lore.add(ChatUT.hexComp(line)
                    .replaceText(TextReplacementConfig.builder().match("%title%").replacement(Component.text(grimoire.getTitle())).build())
                    .replaceText(TextReplacementConfig.builder().match("%authors%").replacement(grimoire.getAuthorsComponent()).build())
            );
        return ItemUT.getItem(material, title, true, lore);
    }

    public ItemStack getRecoverButtonIcon() {
        Material material = Material.valueOf(config.getString("Store.Icons.RecoverButton.Material", "WRITABLE_BOOK").toUpperCase());
        Component title = ChatUT.hexComp(config.getString("Store.Icons.RecoverButton.Title", "&6Recover Lost Books"));
        List<Component> lore = new ArrayList<>();
        for (String line : config.getStringList("Store.Icons.RecoverButton.Lore"))
            lore.add(ChatUT.hexComp(line));
        return ItemUT.getItem(material, title, false, lore);
    }

    public ItemStack getRecoverBookIcon(Grimoire grimoire) {
        String priceText = grimoire.isFree() ? "Free" : grimoire.getSellPrice() + "$";
        Material material = Material.valueOf(config.getString("Store.Icons.RecoverBook.Material", "WRITTEN_BOOK").toUpperCase());
        Component title = ChatUT.hexComp(config.getString("Store.Icons.RecoverBook.Title", "&9%title%").replace("%title%", grimoire.getTitle()));
        List<Component> lore = new ArrayList<>();
        for (String line : config.getStringList("Store.Icons.RecoverBook.Lore"))
            lore.add(ChatUT.hexComp(line)
                    .replaceText(TextReplacementConfig.builder().match("%title%").replacement(Component.text(grimoire.getTitle())).build())
                    .replaceText(TextReplacementConfig.builder().match("%authors%").replacement(grimoire.getAuthorsComponent()).build())
                    .replaceText(TextReplacementConfig.builder().match("%price%").replacement(Component.text(priceText)).build())
            );
        return ItemUT.getItem(material, title, false, lore);
    }

    /* Chiseled Bookshelf Gui */
    public boolean isChiseledBookshelfGuiEnabled() {
        return config.getBoolean("ChiseledBookShelfGui.Enabled", true);
    }

    public Component getChiseledBookshelfGuiTitle() {
        return ChatUT.hexComp(config.getString("ChiseledBookShelfGui.Title", "Books"));
    }

    public boolean requiresBookshelfSneaking() {
        return config.getBoolean("ChiseledBookShelfGui.SneakingRequired", true);
    }

    public Action getBookshelfAction() {
        return Action.valueOf(config.getString("ChiseledBookShelfGui.ActionRequired", "RIGHT_CLICK_BLOCK").toUpperCase());
    }
}
