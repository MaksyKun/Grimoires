package net.maksy.grimoires.configuration;

public interface ModuleInstance {
    void loadModule();
    void unloadModule();
    void loadSubModules();
    void registerModule(ModuleInstance module);
}
