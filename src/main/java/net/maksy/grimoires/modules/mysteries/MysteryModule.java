package net.maksy.grimoires.modules.mysteries;

import lombok.Getter;
import net.maksy.grimoires.configuration.ModuleInstance;

import java.util.ArrayList;
import java.util.List;

public class MysteryModule implements ModuleInstance {

    @Getter
    private static MysteriesCfg mysteriesCfg;
    @Getter
    private static EncryptionAlgorithm encryptionAlgorithm;

    private final List<ModuleInstance> modules = new ArrayList<>();

    @Override
    public void loadModule() {
        mysteriesCfg = new MysteriesCfg();
        encryptionAlgorithm = mysteriesCfg.getEncryptionAlgorithm();
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
}
