package net.maksy.grimoires.modules.book_management.storage;

import net.maksy.grimoires.configuration.YamlParser;
import net.maksy.grimoires.Grimoires;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenreCfg {

    public static Map<String, Genre> Genres = new HashMap<>();

    private final YamlParser config;

    public GenreCfg() {
        this.config = YamlParser.loadOrExtract(Grimoires.getInstance(), "Features/Genres.yml");
        loadGenres();
    }

    public void loadGenres() {
        for(String genre : config.getSection("Genres")) {
            Genres.put(genre, new Genre(genre, config.getString("Genres." + genre + ".Name"), config.getStringList("Genres." + genre + ".Description")));
        }
    }

    public Genre getDefaultGenre() {
        String genre = config.getString("DefaultGenre");
        return Genres.get(genre);
    }

    public List<Genre> getAllGenres() {
        return new ArrayList<>(Genres.values());
    }
}
