package net.maksy.grimoires.configuration;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.maksy.grimoires.modules.book_management.storage.Genre;
import net.maksy.grimoires.modules.book_management.storage.Grimoire;
import net.maksy.grimoires.Grimoires;
import net.maksy.grimoires.configuration.sql.DatabaseType;
import net.maksy.grimoires.modules.book_management.publication.PricingStages;
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
}
