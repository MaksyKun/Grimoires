package net.maksy.grimoires.modules.storage.publication;

import it.unimi.dsi.fastutil.Pair;
import net.maksy.grimoires.Grimoires;
import net.maksy.grimoires.configuration.translation.Translation;
import net.maksy.grimoires.hooks.HookType;
import net.maksy.grimoires.hooks.VaultHook;
import net.maksy.grimoires.utils.ChatUT;
import org.bukkit.entity.Player;

import java.util.*;

public class PricingStages {

    private final List<Pair<Integer, Double>> Pricing = new ArrayList<>();
    private boolean enabled;

    public PricingStages(boolean enabled) {
        this.enabled = enabled;
        if(enabled)
            Grimoires.consoleMessage(ChatUT.hexComp("&6[Grimoires] &aPricing is enabled."));
    }

    public void addPrice(int pages, double price) {
        Pricing.add(Pair.of(pages, price));
    }

    public double getPrice(int pages) {
        if(!enabled)
            return 0;

        for (Pair<Integer, Double> pair : Pricing) {
            if (pages <= pair.left()) {
                return pair.right();
            }
        }
        return 0;
    }

    public boolean transact(Player player, int pages) {
        if(!enabled)
            return true;
        double price = getPrice(pages);
        if (price > 0) {
            if (!Grimoires.getHookManager().isHooked(HookType.Vault)) {
                Grimoires.consoleMessage(ChatUT.hexComp("Vault is not hooked, cannot process transaction."));
                Translation.Vault_ErrorPlayers.sendMessage(player);
                return false;
            }
            Objects.requireNonNull(VaultHook.getEconomy()).withdrawPlayer(player, price);
            return true;
        }
        return true;
    }
}
