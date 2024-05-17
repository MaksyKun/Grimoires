// 
// Decompiled by Procyon v0.5.36
// 

package net.maksy.grimoires.hooks;

import net.maksy.grimoires.Grimoires;
import net.maksy.grimoires.utils.ChatUT;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

@SuppressWarnings("all")
public class VaultHook
{
    private static net.milkbowl.vault.economy.Economy provider;

    public static boolean hook() {
        if (Grimoires.getInstance().getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = Grimoires.getInstance().getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        provider = rsp.getProvider();
        return provider != null;
    }

    public static Economy getEconomy() {
        if(provider == null) {
            Grimoires.consoleMessage(ChatUT.hexComp("&cVault is not hooked in Grimoires!"));
            return null;
        }
        return provider;
    }
}
