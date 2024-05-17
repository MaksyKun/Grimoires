package net.maksy.grimoires;

import net.maksy.grimoires.commands.GrimoireCommand;
import net.maksy.grimoires.configuration.Config;
import net.maksy.grimoires.configuration.GenreCfg;
import net.maksy.grimoires.configuration.sql.SQLManager;
import net.maksy.grimoires.configuration.translation.TranslationConfig;
import net.maksy.grimoires.utils.HeadDatabaseHook;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Grimoires extends JavaPlugin {

    private static JavaPlugin instance;

    private static Config config;
    private static TranslationConfig translations;
    private static GenreCfg genreCfg;
    private static SQLManager sql;

    @Override
    public void onEnable() {
        instance = this;
        if(getServer().getPluginManager().getPlugin("HeadDatabase") != null) {
            HeadDatabaseHook.hook();
        }
        config = new Config();
        translations = new TranslationConfig();
        genreCfg = new GenreCfg();
        sql = new SQLManager();
        GrimoireRegistry.updateRegistry();
        registerListener(new BookViewer());
        registerCommand(new GrimoireCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static void registerListener(Listener listener) {
        instance.getServer().getPluginManager().registerEvents(listener, instance);
    }

    public static void registerCommand(CommandExecutor command) {
        Objects.requireNonNull(instance.getCommand("grimoire")).setExecutor(command);
    }

    public static JavaPlugin getInstance() {
        return instance;
    }

    public static Config getConfigManager() {
        return config;
    }

    public static TranslationConfig getTranslations() {
        return translations;
    }

    public static GenreCfg getGenreCfg() {
        return genreCfg;
    }

    public static SQLManager sql() {
        return sql;
    }
}
