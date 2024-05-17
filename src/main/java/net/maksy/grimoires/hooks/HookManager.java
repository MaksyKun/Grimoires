package net.maksy.grimoires.hooks;

import net.maksy.grimoires.Grimoires;
import net.maksy.grimoires.utils.ChatUT;

import java.util.LinkedList;
import java.util.List;

public class HookManager {

    private final List<HookType> hookedPlugins;

    public HookManager() {
        hookedPlugins = new LinkedList<>();
        Grimoires.consoleMessage(ChatUT.hexComp("&6[Grimoires] Dependency-HookUps"));
        for (HookType hook : HookType.values()) {
            if (Grimoires.getInstance().getServer().getPluginManager().getPlugin(hook.toString()) != null) {
                hookedPlugins.add(hook);
                switch (hook) {
                    case HeadDatabase:
                        HeadDatabaseHook.hook();
                        break;
                    case Vault:
                        VaultHook.hook();
                        break;
                }
                Grimoires.consoleMessage(ChatUT.hexComp("&6[Grimoires] &aHooked: &7" + hook));
            }
        }
    }

    public boolean isHooked(HookType... hookTypes) {
        for (HookType hook : hookTypes) {
            if (!hookedPlugins.contains(hook)) {
                //OpenMCFramework.getInstance().getLogger().warning("Missing hook: " + hook.toString());
                return false;
            }
        }
        return true;
    }
}
