package net.maksy.grimoires.modules.mysteries;

import lombok.Getter;
import net.maksy.grimoires.Grimoires;
import net.maksy.grimoires.configuration.ModuleInstance;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;

public class MysteryModule implements ModuleInstance, Listener {

    @Getter
    private static MysteriesCfg mysteriesCfg;
    @Getter
    private static EncryptionAlgorithm encryptionAlgorithm;

    private final List<ModuleInstance> modules = new ArrayList<>();

    @Override
    public void loadModule() {
        mysteriesCfg = new MysteriesCfg();
        encryptionAlgorithm = mysteriesCfg.getEncryptionAlgorithm();
        Grimoires.registerListener(this);
    }

    @Override
    public void unloadModule() {
        for (ModuleInstance module : modules)
            module.unloadModule();
        modules.clear();
        DecryptionProcess.clearAll();
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

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        DecryptionProcess.remove(event.getPlayer().getUniqueId());
    }
}
