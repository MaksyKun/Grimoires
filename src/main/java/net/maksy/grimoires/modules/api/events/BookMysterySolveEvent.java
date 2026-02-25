package net.maksy.grimoires.modules.api.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.maksy.grimoires.modules.book_management.storage.Grimoire;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Fired when a player successfully solves a mystery/decryption challenge in a Grimoire.
 */
@Getter
@RequiredArgsConstructor
public class BookMysterySolveEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final Grimoire grimoire;
    /** The decrypted word/key that was solved. */
    private final String solvedWord;
    private boolean cancelled = false;

    @Override
    public boolean isCancelled() { return cancelled; }

    @Override
    public void setCancelled(boolean cancel) { this.cancelled = cancel; }

    @Override
    public @NotNull HandlerList getHandlers() { return HANDLERS; }

    public static HandlerList getHandlerList() { return HANDLERS; }
}
