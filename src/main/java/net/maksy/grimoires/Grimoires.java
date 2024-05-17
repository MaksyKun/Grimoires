package net.maksy.grimoires;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.maksy.grimoires.commands.GrimoireCommand;
import net.maksy.grimoires.configuration.Config;
import net.maksy.grimoires.modules.storage.GenreCfg;
import net.maksy.grimoires.configuration.sql.SQLManager;
import net.maksy.grimoires.configuration.translation.TranslationConfig;
import net.maksy.grimoires.hooks.HookManager;
import net.maksy.grimoires.modules.shelves.BookViewer;
import net.maksy.grimoires.modules.storage.GrimoireRegistry;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

@Getter
public final class Grimoires extends JavaPlugin {

    @Getter
    private static JavaPlugin instance;

    private static Config config;
    @Getter
    private static HookManager hookManager;
    @Getter
    private static TranslationConfig translations;
    @Getter
    private static GenreCfg genreCfg;
    private static SQLManager sql;

    @Override
    public void onEnable() {
        instance = this;

        config = new Config();
        hookManager = new HookManager();
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

    public static void consoleMessage(Component message) {
        instance.getServer().getConsoleSender().sendMessage(message);
    }

    public static Config getConfiguration() {
        return config;
    }

    public static SQLManager sql() {
        return sql;
    }
}
