package net.maksy.grimoires;

import net.maksy.grimoires.commands.GrimoireCommand;
import net.maksy.grimoires.configuration.Config;
import net.maksy.grimoires.configuration.sql.SQLManager;
import net.maksy.grimoires.viewer.BookViewer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Grimoires extends JavaPlugin {

    private static JavaPlugin instance;

    private static Config config;
    private static SQLManager sql;

    @Override
    public void onEnable() {
        instance = this;
        config = new Config();
        sql = new SQLManager();
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

    public static SQLManager sql() {
        return sql;
    }
}
