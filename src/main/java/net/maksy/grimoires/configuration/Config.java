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
import java.util.UUID;

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

    /* Publication */
    public Component getPublicationEditorTitle() {
        return ChatUT.hexComp(config.getString("Publication.Title", "Publication Editor"));
    }

    public Component getPublicationAuthorsTitle() {
        return ChatUT.hexComp(config.getString("Publication.AuthorsGui.Title", "Authors"));
    }

    public Component getPublicationGenresTitle() {
        return ChatUT.hexComp(config.getString("Publication.GenresGui.Title", "Genres"));
    }

    public ItemStack getPublicationAuthorIcon(UUID uuid) {
        Component title = ChatUT.hexComp(config.getString("Publication.AuthorsGui.Icons.Author.Title", "&9Author").replace("%name%", ChatUT.getPlayerName(uuid)));
        List<Component> lore = new ArrayList<>();
        for (String line : config.getStringList("Publication.AuthorsGui.Icons.Author.Lore"))
            lore.add(ChatUT.hexComp(line.replace("%name%", ChatUT.getPlayerName(uuid))));
        return ItemUT.getSkull(uuid, title, lore);
    }

    public ItemStack getPublicationAuthorAddIcon() {
        Material material = Material.valueOf(config.getString("Publication.AuthorsGui.Icons.Add.Material", "FEATHER").toUpperCase());
        Component title = ChatUT.hexComp(config.getString("Publication.AuthorsGui.Icons.Add.Title", "&9Add Author"));
        List<Component> lore = new ArrayList<>();
        for (String line : config.getStringList("Publication.AuthorsGui.Icons.Add.Lore"))
            lore.add(ChatUT.hexComp(line));
        return ItemUT.getItem(material, title, false, lore);
    }

    public ItemStack getPublicationGenreIcon(Genre genre) {
        Material material = Material.valueOf(config.getString("Publication.Icons.Genre.Material", "BOOK").toUpperCase());
        Component title = ChatUT.hexComp(config.getString("Publication.Icons.Genre.Title", "&9Genre"));
        List<Component> lore = new ArrayList<>();
        for (String line : config.getStringList("Publication.Icons.Genre.Lore"))
            lore.add(ChatUT.hexComp(line)
                    .replaceText(TextReplacementConfig.builder().match("%genre%").replacement(Component.text(genre.getName())).build())
            );
        return ItemUT.getItem(material, title, false, lore);
    }

    public ItemStack getPublicationGenreAddIcon() {
        Material material = Material.valueOf(config.getString("Publication.GenresGui.Icons.Add.Material", "WRITABLE_BOOK").toUpperCase());
        Component title = ChatUT.hexComp(config.getString("Publication.GenresGui.Icons.Add.Title", "&9Add Genre"));
        List<Component> lore = new ArrayList<>();
        for (String line : config.getStringList("Publication.GenresGui.Icons.Add.Lore"))
            lore.add(ChatUT.hexComp(line));
        return ItemUT.getItem(material, title, false, lore);
    }

    public ItemStack getPublicationGenreGuiGenreIcon(Genre genre) {
        Material material = Material.valueOf(config.getString("Publication.GenreGui.Icons.Genre.Material", "BOOK").toUpperCase());
        Component title = ChatUT.hexComp(config.getString("Publication.GenreGui.Icons.Genre.Title", "&9Genre").replace("%genre%", genre.getName()));
        List<Component> lore = new ArrayList<>();
        for (String line : config.getStringList("Publication.GenreGui.Icons.Genre.Lore"))
            lore.add(ChatUT.hexComp(line)
                    .replaceText(TextReplacementConfig.builder().match("%genre%").replacement(Component.text(genre.getName())).build())
            );
        return ItemUT.getItem(material, title, false, lore);
    }

    public ItemStack getPublicationBookIcon(Grimoire grimoire) {
        Material material = Material.valueOf(config.getString("Publication.Icons.Book.Material", "WRITTEN_BOOK").toUpperCase());
        Component title = ChatUT.hexComp(config.getString("Publication.Icons.Book.Title", "&9Publication"));
        List<Component> lore = new ArrayList<>();
        for (String line : config.getStringList("Publication.Icons.Book.Lore"))
            lore.add(ChatUT.hexComp(line)
                    .replaceText(TextReplacementConfig.builder().match("%title%").replacement(Component.text(grimoire.getTitle())).build())
                    .replaceText(TextReplacementConfig.builder().match("%authors%").replacement(grimoire.getAuthorsComponent()).build())
                    .replaceText(TextReplacementConfig.builder().match("%genres%").replacement(grimoire.getGenresComponent()).build())
            );
        return ItemUT.getItem(material, title, false, lore);
    }

    public ItemStack getPublicationPricingIcon(Grimoire grimoire) {
        Material material = Material.valueOf(config.getString("Publication.Icons.Pricing.Material", "EMERALD").toUpperCase());
        Component title = ChatUT.hexComp(config.getString("Publication.Icons.Pricing.Title", "&9Pricing"));
        List<Component> lore = new ArrayList<>();
        for (String line : config.getStringList("Publication.Icons.Pricing.Lore"))
            lore.add(ChatUT.hexComp(line)
                    .replaceText(TextReplacementConfig.builder().match("%price%").replacement(Component.text(String.valueOf(grimoire.getPublicationPrice()))).build())
            );
        return ItemUT.getItem(material, title, false, lore);
    }

    public ItemStack getPublicationAuthorsIcon(Grimoire grimoire) {
        Material material = Material.valueOf(config.getString("Publication.Icons.Authors.Material", "FEATHER").toUpperCase());
        Component title = ChatUT.hexComp(config.getString("Publication.Icons.Authors.Title", "&9Authors"));
        List<Component> lore = new ArrayList<>();
        for (String line : config.getStringList("Publication.Icons.Authors.Lore"))
            lore.add(ChatUT.hexComp(line)
                    .replaceText(TextReplacementConfig.builder().match("%authors%").replacement(grimoire.getAuthorsComponent()).build())
            );
        return ItemUT.getItem(material, title, false, lore);
    }

    public ItemStack getPublicationGenresIcon(Grimoire grimoire) {
        Material material = Material.valueOf(config.getString("Publication.Icons.Genres.Material", "BOOK").toUpperCase());
        Component title = ChatUT.hexComp(config.getString("Publication.Icons.Genres.Title", "&9Genres"));
        List<Component> lore = new ArrayList<>();
        for (String line : config.getStringList("Publication.Icons.Genres.Lore"))
            lore.add(ChatUT.hexComp(line)
                    .replaceText(TextReplacementConfig.builder().match("%genres%").replacement(grimoire.getGenresComponent()).build())
            );
        return ItemUT.getItem(material, title, false, lore);
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

    public PricingStages getPricingStages() {
        PricingStages pricing = new PricingStages(config.getBoolean("Storage.Pricing.Enabled", false));
        for (String key : config.getSection("Storage.Pricing.PageStages")) {
            try {
                pricing.addPrice(Integer.parseInt(key), config.getDouble("Storage.Pricing.PageStages." + key));
            } catch (NumberFormatException e) {
                Grimoires.getInstance().getLogger().warning("Invalid pricing stage: " + key);
            }
        }
        return pricing;
    }
}
