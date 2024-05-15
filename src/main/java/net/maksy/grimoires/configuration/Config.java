package net.maksy.grimoires.configuration;

import net.kyori.adventure.text.Component;
import net.maksy.grimoires.Grimoires;
import net.maksy.grimoires.configuration.sql.DatabaseType;
import net.maksy.grimoires.utils.ChatUT;
import org.bukkit.event.block.Action;

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
    public Component getMainItemFolderTitle(String folder) {
        return ChatUT.hexComp(config.getString("Storage.DisplayTitle", "Books").replace("%folder%", folder));
    }
}
