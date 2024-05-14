package net.maksy.grimoires.configuration;

import net.kyori.adventure.text.Component;
import net.maksy.grimoires.Grimoires;
import net.maksy.grimoires.configuration.sql.DatabaseType;
import net.maksy.grimoires.utils.ChatUT;

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

    /* Storage */
    public Component getMainItemFolderTitle(String folder) {
        return ChatUT.hexComp(config.getString("Storage.DisplayTitle", "Books").replace("%folder%", folder));
    }
}
