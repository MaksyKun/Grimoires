package net.maksy.grimoires.modules.book_management.storage;

import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.maksy.grimoires.modules.mysteries.MysteryModule;
import net.maksy.grimoires.utils.ChatUT;
import org.bukkit.Bukkit;
import org.intellij.lang.annotations.RegExp;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Grimoire implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /* Common variables in use */
    private final int id;
    private List<UUID> authors;
    private String title;
    private String description;
    private List<Genre> genres;
    private List<String> pages;
    private long publishedOn;


    /*  Variables for Mystery Features
        requires them to be enabled */
    private boolean exactOrder;
    private List<String> encryptionKeys;
    private List<String> commands;

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
        if(!MysteryModule.getEncryptionAlgorithm().enabled() || encryptionKeys.isEmpty()) {
            book.pages(pages.stream().map(ChatUT::hexComp).toArray(Component[]::new));
        } else {
            AtomicInteger amount = new AtomicInteger();
            book.pages(pages.stream().map(page -> {
                Component component = ChatUT.hexComp(page);
                if(amount.get() >= encryptionKeys.size()) return component;
                if(page.contains(MysteryModule.getMysteriesCfg().getEncryptionAlgorithm().indicator())) {
                    // Get the words right after the indicator and replace those by a component that highlights the word
                    // Also adds a click event to decrypt the word later
                    String indicator = MysteryModule.getMysteriesCfg().getEncryptionAlgorithm().indicator();
                    String[] words = page.split(" ");
                    for(@RegExp String word : words) {
                        if(word.startsWith(indicator)) {
                            TextReplacementConfig replacement = TextReplacementConfig.builder().match(word).replacement(ChatUT.hexComp(MysteryModule.getEncryptionAlgorithm().design().replace("%text%", word.replace(indicator, ""))).clickEvent(ClickEvent.runCommand("/grimoires decrypt " + id + " " + word.replace(indicator, "")))).build();
                            component = component.replaceText(replacement);
                            amount.getAndIncrement();
                            return component;
                        }
                    }
                }
                return component;
            }).toArray(Component[]::new));
        }
        return book.build();
    }

    public Component getAuthorsComponent() {
        return ChatUT.hexComp(authors.stream().map(ChatUT::getPlayerName).reduce((a, b) -> a + ", " + b).orElse(""));
    }

    public Component getGenresComponent() {
        return ChatUT.hexComp(genres.stream().map(Genre::getName).reduce((a, b) -> a + ", " + b).orElse(""));
    }

    public double getPublicationPrice() {
        return GrimoireRegistry.pricing().getPrice(this.pages.size());
    }

    public boolean isExactOrder() {
        return exactOrder;
    }

    public void setExactOrder(boolean exactOrder) {
        this.exactOrder = exactOrder;
    }

    public List<String> getEncryptionKeys() {
        return encryptionKeys;
    }

    public void setEncryptionKeys(List<String> encryptionKeys) {
        this.encryptionKeys = encryptionKeys;
    }

    public List<String> getCommands() {
        return commands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }
}
