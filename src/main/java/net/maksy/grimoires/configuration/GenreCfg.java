package net.maksy.grimoires.configuration;

import net.maksy.grimoires.Genre;
import net.maksy.grimoires.Grimoires;

import java.util.HashMap;
import java.util.Map;

public class GenreCfg {

    public static Map<String, Genre> Genres = new HashMap<>();

    private final YamlParser config;

    public GenreCfg() {
        this.config = YamlParser.loadOrExtract(Grimoires.getInstance(), "Genres.yml");
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
}
