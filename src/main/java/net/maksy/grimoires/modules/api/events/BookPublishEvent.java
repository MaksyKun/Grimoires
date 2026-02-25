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
 * Fired when a player publishes a new Grimoire.
 * Cancelling this event prevents the book from being saved and charges the player.
 */
@Getter
@RequiredArgsConstructor
public class BookPublishEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final Grimoire grimoire;
    private boolean cancelled = false;

    @Override
    public boolean isCancelled() { return cancelled; }

    @Override
    public void setCancelled(boolean cancel) { this.cancelled = cancel; }

    @Override
    public @NotNull HandlerList getHandlers() { return HANDLERS; }

    public static HandlerList getHandlerList() { return HANDLERS; }
}
