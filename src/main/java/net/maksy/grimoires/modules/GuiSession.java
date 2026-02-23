package net.maksy.grimoires.modules;

/**
 * Lifecycle contract for GUI instances that register Bukkit event listeners.
 * <p>
 * Implementors must:
 * <ul>
 *   <li>Register their listener (and track their inventory) inside {@code open()}.</li>
 *   <li>Implement {@code close()} to call {@code HandlerList.unregisterAll(this)},
 *       notify {@link GuiSessionManager}, and release held references.</li>
 * </ul>
 * {@link GuiSessionManager} is responsible for invoking {@code close()} when the
 * player closes the last inventory that belongs to this session.
 */
public interface GuiSession {
    /**
     * Unregisters all event handlers belonging to this session, removes it from
     * {@link GuiSessionManager}, and releases any inventory / player references.
     * After this call the instance must be considered disposed; if the same
     * logical GUI is needed again a fresh instance (or re-{@code open()}) must be used.
     */
    void close();
}
