package net.maksy.grimoires.modules.book_management.publication;

import net.maksy.grimoires.configuration.ModuleInstance;

import java.util.ArrayList;
import java.util.List;

public class PublicationModule implements ModuleInstance {

    private static PublicationCfg publicationCfg;

    private final List<ModuleInstance> modules = new ArrayList<>();

    @Override
    public void loadModule() {
        publicationCfg = new PublicationCfg();

        loadSubModules();
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

    public static PublicationCfg getPublicationCfg() {
        return publicationCfg;
    }
}
