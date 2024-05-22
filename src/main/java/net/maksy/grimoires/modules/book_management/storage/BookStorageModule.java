package net.maksy.grimoires.modules.book_management.storage;

import net.maksy.grimoires.configuration.ModuleInstance;

import java.util.ArrayList;
import java.util.List;

public class BookStorageModule implements ModuleInstance {

    private static GenreCfg genreCfg;
    private static BookStorageCfg bookStorageCfg;

    private final List<ModuleInstance> modules = new ArrayList<>();

    @Override
    public void loadModule() {
        genreCfg = new GenreCfg();
        bookStorageCfg = new BookStorageCfg();
        GrimoireRegistry.updateRegistry();
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

    public static GenreCfg getGenreCfg() {
        return genreCfg;
    }

    public static BookStorageCfg getBookStorageCfg() {
        return bookStorageCfg;
    }
}
