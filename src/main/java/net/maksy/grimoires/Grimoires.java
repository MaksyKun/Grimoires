package net.maksy.grimoires;

import net.maksy.grimoires.configuration.Config;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Grimoires extends JavaPlugin {

    private static JavaPlugin instance;

    private static Config config;

    @Override
    public void onEnable() {
        instance = this;
        config = new Config();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static void registerListener(Listener listener) {
        instance.getServer().getPluginManager().registerEvents(listener, instance);
    }

    public static JavaPlugin getInstance() {
        return instance;
    }

    public static Config getConfigManager() {
        return config;
    }
}
