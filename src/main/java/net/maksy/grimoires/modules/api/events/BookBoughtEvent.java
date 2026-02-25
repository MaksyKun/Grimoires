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
 * Fired when a player purchases a Grimoire from the book store.
 * Cancelling this event prevents the purchase from completing (money is not charged and book is not given).
 */
@Getter
@RequiredArgsConstructor
public class BookBoughtEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final Grimoire grimoire;
    /** The price paid for this book. */
    private final double price;
    private boolean cancelled = false;

    @Override
    public boolean isCancelled() { return cancelled; }

    @Override
    public void setCancelled(boolean cancel) { this.cancelled = cancel; }

    @Override
    public @NotNull HandlerList getHandlers() { return HANDLERS; }

    public static HandlerList getHandlerList() { return HANDLERS; }
}
