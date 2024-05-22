package net.maksy.grimoires.modules.book_management.publication;

import net.maksy.grimoires.modules.book_management.publication.gui.AuthorGui;
import net.maksy.grimoires.modules.book_management.publication.gui.PlayerSearchGui;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class PlayerSearchMechanic {

    private final SearchType searchType;
    private final double distance;
    private final int limit;
    private final int interval;

    private final Map<UUID, PlayerSearchGui> searches = new TreeMap<>();

    public PlayerSearchMechanic(SearchType searchType, double distance, int limit, int interval) {
        this.searchType = searchType;
        this.distance = distance;
        this.limit = limit;
        this.interval = interval;
    }

    public void updateInventories() {

    }
}
