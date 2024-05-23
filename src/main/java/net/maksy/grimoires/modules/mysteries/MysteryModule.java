package net.maksy.grimoires.modules.mysteries;

import lombok.Getter;
import net.maksy.grimoires.configuration.ModuleInstance;

public class MysteryModule implements ModuleInstance {

    private static MysteriesCfg mysteriesCfg;
    private static EncryptionAlgorithm encryptionAlgorithm;

    @Override
    public void loadModule() {
        mysteriesCfg = new MysteriesCfg();
        encryptionAlgorithm = mysteriesCfg.getEncryptionAlgorithm();
    }

    @Override
    public void unloadModule() {

    }

    @Override
    public void loadSubModules() {

    }

    @Override
    public void registerModule(ModuleInstance module) {

    }

    public static MysteriesCfg getMysteriesCfg() {
        return mysteriesCfg;
    }

    public static EncryptionAlgorithm getEncryptionAlgorithm() {
        return encryptionAlgorithm;
    }
}
