package net.maksy.grimoires.modules.book_management;

import net.maksy.grimoires.configuration.ModuleInstance;
import net.maksy.grimoires.modules.book_management.publication.PublicationModule;
import net.maksy.grimoires.modules.book_management.storage.BookStorageModule;
import net.maksy.grimoires.modules.book_management.storage.GrimoireRegistry;

import java.util.ArrayList;
import java.util.List;

public class BookManagementModule implements ModuleInstance {


    private final List<ModuleInstance> modules = new ArrayList<>();

    @Override
    public void loadModule() {

        /* Modules */
        registerModule(new PublicationModule());
        registerModule(new BookStorageModule());

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
