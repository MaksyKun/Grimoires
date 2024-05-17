package net.maksy.grimoires;

import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.maksy.grimoires.utils.ChatUT;
import org.bukkit.Bukkit;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class Grimoire implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final int id;
    private List<UUID> authors;
    private String title;
    private String description;
    private List<Genre> genres;
    private List<String> pages;

    private long publishedOn;

    public Grimoire(int id, List<UUID> authors, String title, String description, List<Genre> genres, List<String> pages, long publishedOn) {
        this.id = id;
        this.authors = authors;
        this.title = title;
        this.description = description;
        this.genres = genres;
        this.pages = pages;
        this.publishedOn = publishedOn;
    }

    public int getId() {
        return id;
    }

    public List<UUID> getAuthors() {
        return authors;
    }

    public void setAuthors(List<UUID> authors) {
        this.authors = authors;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public List<String> getPages() {
        return pages;
    }

    public void setPages(List<String> pages) {
        this.pages = pages;
    }

    public long getPublishedOn() {
        return publishedOn;
    }

    public void setPublishedOn(long publishedOn) {
        this.publishedOn = publishedOn;
    }

    public Book getBook() {
        Book.Builder book = Book.builder();
        book.title(ChatUT.hexComp(title));
        String authorsString = authors.stream().map(uuid -> Bukkit.getOfflinePlayer(uuid).getName()).reduce((a, b) -> a + ", " + b).orElse("");
        book.author(ChatUT.hexComp(authorsString));
        book.pages(pages.stream().map(ChatUT::hexComp).toArray(Component[]::new));
        return book.build();
    }

    public Component getAuthorsComponent() {
        return ChatUT.hexComp(authors.stream().map(uuid -> Bukkit.getOfflinePlayer(uuid).getName()).reduce((a, b) -> a + ", " + b).orElse(""));
    }
}
