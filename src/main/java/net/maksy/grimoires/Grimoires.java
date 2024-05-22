package net.maksy.grimoires;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.maksy.grimoires.commands.GrimoireCommand;
import net.maksy.grimoires.configuration.Config;
import net.maksy.grimoires.configuration.ModuleInstance;
import net.maksy.grimoires.modules.book_management.BookManagementModule;
import net.maksy.grimoires.modules.book_management.storage.GenreCfg;
import net.maksy.grimoires.configuration.sql.SQLManager;
import net.maksy.grimoires.configuration.translation.TranslationConfig;
import net.maksy.grimoires.hooks.HookManager;
import net.maksy.grimoires.modules.shelves.BookShelfModule;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public final class Grimoires extends JavaPlugin implements ModuleInstance {

    @Getter
    private static JavaPlugin instance;

    private static Config config;

    @Getter
    private static HookManager hookManager;
    @Getter
    private static TranslationConfig translations;
    @Getter
    private static SQLManager sql;

    private final List<ModuleInstance> modules = new ArrayList<>();

    @Override
    public void onEnable() {
        instance = this;
        loadModule();
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void loadModule() {
        config = new Config();
        hookManager = new HookManager();
        translations = new TranslationConfig();
        sql = new SQLManager();

        /* Modules */
        registerModule(new BookShelfModule());
        registerModule(new BookManagementModule());

        loadSubModules();
        /* Commands */
        if(modules.size() > 0)
            registerCommand(new GrimoireCommand());
    }

    @Override
    public void unloadModule() {
        for (ModuleInstance module : modules)
            module.unloadModule();
        modules.clear();
    }

    @Override
    public void loadSubModules() {
        for(ModuleInstance module : modules)
            module.loadModule();
    }

    @Override
    public void registerModule(ModuleInstance module) {
        modules.add(module);
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
