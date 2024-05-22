package net.maksy.grimoires.modules.shelves;

import net.maksy.grimoires.Grimoires;
import net.maksy.grimoires.configuration.ModuleInstance;

import java.util.ArrayList;
import java.util.List;

public class BookShelfModule implements ModuleInstance {

    private final List<ModuleInstance> modules = new ArrayList<>();
    @Override
    public void loadModule() {
        Grimoires.registerListener(new BookViewer());

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
}
