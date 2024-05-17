package net.maksy.grimoires.configuration;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.maksy.grimoires.modules.storage.Genre;
import net.maksy.grimoires.modules.storage.Grimoire;
import net.maksy.grimoires.Grimoires;
import net.maksy.grimoires.configuration.sql.DatabaseType;
import net.maksy.grimoires.modules.storage.publication.PricingStages;
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

public class Config {

    private final YamlParser config;

    public Config() {
        this.config = YamlParser.loadOrExtract(Grimoires.getInstance(), "Config.yml");
    }

    /* SQL */
    public DatabaseType getDatabaseType() {
        return DatabaseType.valueOf(config.getString("SQL.Type", "Locale").toUpperCase());
    }

    public String getSQLHost() {
        return config.getString("SQL.Host");
    }

    public String getSQLDatabase() {
        return config.getString("SQL.Database");
    }

    public String getSQLUsername() {
        return config.getString("SQL.Username");
    }

    public String getSQLPassword() {
        return config.getString("SQL.Password");
    }

    public int getSQLPort() {
        return config.getInt("SQL.Port");
    }

    /* Chiseled Bookshelf Gui*/
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
        return Action.valueOf(config.getString("ChiseledBookShelfGui.Action", "RIGHT_CLICK_BLOCK").toUpperCase());
    }

    /* Storage */

    public String getStatus(boolean status) {
        return config.getString("Storage.Status." + (status ? "True" : "False"));
    }

    public String getDateTime(long time) {
        if (time == -1) return config.getString("Storage.Status.Unpublished");
        Instant instant = Instant.ofEpochMilli(time);
        return LocalDateTime.ofInstant(instant, ZoneId.of(config.getString("Storage.ZoneId", "UTC"))).format(DateTimeFormatter.ofPattern(config.getString("Storage.DateTimeFormat", "dd/MM/yyyy HH:mm:ss")));
    }

    public Component getMainItemFolderTitle(String folder) {
        return ChatUT.hexComp(config.getString("Storage.DisplayTitle", "Books").replace("%name%", folder));
    }

    public ItemStack getGenreIcon(Genre genre) {
        Material material = Material.valueOf(config.getString("Storage.Icons.Folder.Material", "BOOK").toUpperCase());
        Component title = ChatUT.hexComp(config.getString("Storage.Icons.Folder.Title", "&9" + genre.getName()).replace("%genre%", genre.getName()));
        List<Component> lore = new ArrayList<>();

        for (String line : config.getStringList("Storage.Icons.Folder.Lore")) {
            lore.add(ChatUT.hexComp(line));
        }
        for(String line : genre.getDescription())
            lore.add(ChatUT.hexComp(line));
        return ItemUT.getItem(material, title, false, lore);
    }

    public ItemStack getBookIcon(Grimoire grimoire) {
        Material material = Material.valueOf(config.getString("Storage.Icons.Book.Material", "WRITTEN_BOOK").toUpperCase());
        Component title = ChatUT.hexComp(config.getString("Storage.Icons.Book.Title", "&9" + grimoire.getTitle()).replace("%title%", grimoire.getTitle()));
        List<Component> lore = new ArrayList<>();
        Component loreComp = Component.text("");

        for (String line : config.getStringList("Storage.Icons.Book.Lore"))
            lore.add(ChatUT.hexComp(line)
                    .replaceText(TextReplacementConfig.builder().match("%description%").replacement(Component.text(grimoire.getDescription())).build())
                    .replaceText(TextReplacementConfig.builder().match("%authors%").replacement(grimoire.getAuthorsComponent()).build())
                    .replaceText(TextReplacementConfig.builder().match("%date%").replacement(getDateTime(grimoire.getPublishedOn())).build())
            );
        return ItemUT.getItem(material, title, false, lore);
    }

    public PricingStages getPricingStages() {
        PricingStages pricing = new PricingStages(config.getBoolean("Storage.Pricing.Enabled", false));
        for(String key : config.getSection("Storage.Pricing.PageStages")) {
            try {
                pricing.addPrice(Integer.parseInt(key), config.getDouble("Storage.Pricing.PageStages." + key));
            } catch (NumberFormatException e) {
                Grimoires.getInstance().getLogger().warning("Invalid pricing stage: " + key);
            }
        }
        return pricing;
    }
}
