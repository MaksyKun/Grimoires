package net.maksy.grimoires.modules.book_management.storage;

import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class Genre implements Serializable, Comparable<Genre> {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private List<String> description;

    public Genre(String id, String name, List<String> description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getDescription() {
        return description;
    }

    @Override
    public int compareTo(@NotNull Genre o) {
        //Compare alphabetically
        if (this.name.compareTo(o.name) < 0) {
            return -1;
        } else if (this.name.compareTo(o.name) > 0) {
            return 1;
        }
        return 0;
    }
}
