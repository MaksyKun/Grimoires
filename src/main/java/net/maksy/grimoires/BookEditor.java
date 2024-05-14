package net.maksy.grimoires;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class BookEditor {

    public static Map<UUID, Grimoire> BookEditor = new TreeMap<>();

    public static Grimoire getGrimoire(UUID uuid) {
        return BookEditor.get(uuid);
    }

    public static void addGrimoire(UUID uuid, Grimoire grimoire) {
        BookEditor.put(uuid, grimoire);
    }

    public static void removeGrimoire(UUID uuid) {
        BookEditor.remove(uuid);
    }
}
