package net.maksy.grimoires.modules;

import net.maksy.grimoires.Grimoires;
import net.maksy.grimoires.utils.ChatUT;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Central manager that maps open inventories to their owning {@link GuiSession} and
 * calls {@link GuiSession#close()} when the player truly leaves the GUI (as opposed
 * to navigating between pages of the same multi-page session).
 *
 * <p>The manager is a permanent, singleton {@link Listener} registered once during
 * plugin start-up via {@link #get()}. GUI classes call {@link #track(Inventory, GuiSession)}
 * each time they open an inventory page, and the manager automatically invokes
 * {@link GuiSession#close()} via a one-tick delayed task once no inventory of that
 * session remains open.</p>
 *
 * <p>The one-tick delay allows {@code player.openInventory()} calls (page navigation)
 * to register the new page <em>before</em> the close-check fires, preventing false
 * session-close events during in-session navigation.</p>
 */
public class GuiSessionManager implements Listener {

    private static GuiSessionManager instance;

    /** Primary map: inventory object → owning session. */
    private final Map<Inventory, GuiSession> inventoryToSession = new HashMap<>();

    /** Reverse map: session → all inventories it currently owns. */
    private final Map<GuiSession, Set<Inventory>> sessionToInventories = new HashMap<>();

    private GuiSessionManager() {}

    /** Returns (and lazily initialises) the singleton manager, registering its listener. */
    public static GuiSessionManager get() {
        if (instance == null) {
            instance = new GuiSessionManager();
            Grimoires.registerListener(instance);
        }
        return instance;
    }

    /**
     * Associates {@code inventory} with {@code session} so that
     * {@link InventoryCloseEvent} can locate the owning session.
     * Safe to call multiple times for the same session (multi-page GUIs).
     */
    public void track(Inventory inventory, GuiSession session) {
        inventoryToSession.put(inventory, session);
        sessionToInventories.computeIfAbsent(session, k -> new HashSet<>()).add(inventory);
        if (Grimoires.getConfiguration().isDebugEnabled()) {
            Grimoires.consoleMessage(ChatUT.hexComp(
                    "[Grimoires Debug] Registered session " + session.getClass().getSimpleName()
                            + "@" + Integer.toHexString(System.identityHashCode(session))));
        }
    }

    /**
     * Removes all inventory mappings for the given session.
     * Called by {@link GuiSession#close()} implementations so the session is fully
     * de-registered from this manager.
     */
    public void untrack(GuiSession session) {
        Set<Inventory> invs = sessionToInventories.remove(session);
        if (invs != null) {
            for (Inventory inv : invs) inventoryToSession.remove(inv);
        }
        if (Grimoires.getConfiguration().isDebugEnabled()) {
            Grimoires.consoleMessage(ChatUT.hexComp(
                    "[Grimoires Debug] Unregistered session " + session.getClass().getSimpleName()
                            + "@" + Integer.toHexString(System.identityHashCode(session))));
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        GuiSession session = inventoryToSession.get(event.getInventory());
        if (session == null) return;

        HumanEntity player = event.getPlayer();
        Bukkit.getScheduler().runTask(Grimoires.getInstance(), () -> {
            // Session may have already been closed (e.g. by parent session's close()).
            if (!sessionToInventories.containsKey(session)) return;

            Inventory openInv = player.getOpenInventory().getTopInventory();
            if (openInv == null) {
                session.close();
                return;
            }
            // If the player's new top inventory still belongs to the same session
            // they are navigating between pages – do not close the session.
            if (inventoryToSession.getOrDefault(openInv, null) == session) return;

            session.close();
        });
    }
}
