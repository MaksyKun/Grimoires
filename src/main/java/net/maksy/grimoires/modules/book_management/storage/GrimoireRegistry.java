package net.maksy.grimoires.modules.book_management.storage;

import net.maksy.grimoires.Grimoires;
import net.maksy.grimoires.modules.book_management.publication.PricingStages;
import net.maksy.grimoires.modules.book_management.publication.PublicationModule;

import java.util.*;

public class GrimoireRegistry {

    private static final Map<Genre, List<Grimoire>> Registry = new TreeMap<>();
    private static PricingStages pricingStages;

    public static void updateRegistry() {
        Registry.clear();
        pricingStages = PublicationModule.getPublicationCfg().getPricingStages();

        List<Grimoire> entries = Grimoires.sql().getBooksSQL().getBooks(null, null);
        Registry.putIfAbsent(BookStorageModule.getGenreCfg().getDefaultGenre(), new ArrayList<>());
        for (Grimoire grimoire : entries) {
            if(grimoire.getGenres().isEmpty()) {
                Registry.get(BookStorageModule.getGenreCfg().getDefaultGenre()).add(grimoire);
            } else {
                for (Genre genre : grimoire.getGenres()) {
                    Registry.putIfAbsent(genre, new ArrayList<>());
                    if (!Registry.get(genre).contains(grimoire))
                        Registry.get(genre).add(grimoire);
                }
            }
        }
    }

    public static List<Grimoire> getGrimoires(Genre genre) {
        return Registry.get(genre);
    }

    public static List<Grimoire> getGrimoires(UUID author) {
        List<Grimoire> grimoires = new ArrayList<>();
        for (List<Grimoire> list : Registry.values()) {
            for (Grimoire grimoire : list) {
                if (grimoire.getAuthors().contains(author)) {
                    grimoires.add(grimoire);
                }
            }
        }
        return grimoires;
    }

    public static List<Grimoire> getGrimoires(UUID author, Genre genre) {
        List<Grimoire> grimoires = new ArrayList<>();
        for (List<Grimoire> list : Registry.values()) {
            for (Grimoire grimoire : list) {
                if (grimoire.getAuthors().contains(author) && grimoire.getGenres().contains(genre)) {
                    grimoires.add(grimoire);
                }
            }
        }
        return grimoires;
    }

    public static List<Genre> getGenres(UUID author) {
        HashSet<Genre> genres = new HashSet<>();
        for (List<Grimoire> list : Registry.values()) {
            for (Grimoire grimoire : list) {
                if (grimoire.getAuthors().contains(author)) {
                    genres.addAll(grimoire.getGenres());
                }
            }
        }
        return genres.stream().toList();
    }

    public static List<Genre> getGenres() {
        return new ArrayList<>(Registry.keySet());
    }

    public static List<Grimoire> getGrimoires() {
        List<Grimoire> grimoires = new ArrayList<>();
        for (List<Grimoire> list : Registry.values()) {
            grimoires.addAll(list);
        }
        return grimoires;
    }

    public static boolean isGrimoireExistent(UUID uuid, String title) {
        for(Grimoire grimoire : getGrimoires(uuid)) {
            if(grimoire.getTitle().equalsIgnoreCase(title)) {
                return true;
            }
        }
        return false;
    }

    public static PricingStages pricing() {
        return pricingStages;
    }
}
