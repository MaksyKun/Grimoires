package net.maksy.grimoires.modules.book_management.storage;

import lombok.Setter;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.maksy.grimoires.modules.book_management.publication.PublicationModule;
import net.maksy.grimoires.modules.mysteries.DecryptionProcess;
import net.maksy.grimoires.modules.mysteries.MysteryModule;
import net.maksy.grimoires.utils.ChatUT;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
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
    private int id;
    private List<UUID> authors;
    private String title;
    private String description;
    private List<Genre> genres;
    private List<String> pages;
    private long publishedOn;


    /*  Variables for Mystery Features
        requires them to be enabled */
    @Setter
    private boolean exactOrder;
    @Setter
    private List<String> encryptionKeys;
    @Setter
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

    public void setId(int id) {
        this.id = id;
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

    public List<String> getEncryptionKeys() {
        return encryptionKeys;
    }

    public List<String> getCommands() {
        return commands;
    }

    public Book getBook(Player player) {
        Book.Builder book = Book.builder();
        book.title(ChatUT.hexComp(title));
        String authorsString = authors.stream().map(uuid -> Bukkit.getOfflinePlayer(uuid).getName()).reduce((a, b) -> a + ", " + b).orElse("");
        book.author(ChatUT.hexComp(authorsString));
        if(!MysteryModule.getEncryptionAlgorithm().enabled() || encryptionKeys.isEmpty()) {
            book.pages(pages.stream().map(ChatUT::hexComp).toArray(Component[]::new));
        } else {
            AtomicInteger amount = new AtomicInteger();
            String indicator = MysteryModule.getMysteriesCfg().getEncryptionAlgorithm().indicator();
            book.pages(pages.stream().map(page -> {
                Component component = ChatUT.hexComp(page);
                if(amount.get() >= encryptionKeys.size()) return component;
                if(page.contains(MysteryModule.getMysteriesCfg().getEncryptionAlgorithm().indicator())) {
                    // Get the words right after the indicator and replace those by a component that highlights the word
                    // Also adds a click event to decrypt the word later
                    String[] words = page.split(" ");
                    for(@RegExp String word : words) {
                        if(word.startsWith(indicator)) {
                            String solvedColor = "";
                            boolean isDecrypted = false;
                            if(player != null) {
                                DecryptionProcess process = DecryptionProcess.get(player, this);
                                isDecrypted = process.isDecrypted(word.replace(indicator, ""), amount.get());
                                if(isDecrypted) {
                                    solvedColor = MysteryModule.getMysteriesCfg().getEncryptionAlgorithm().solvedColor();
                                }
                            }
                            Component _component = ChatUT.hexComp(solvedColor + MysteryModule.getEncryptionAlgorithm().design().replace("%word%", word.replace(indicator, "")));
                            if(!isDecrypted) {
                                _component = _component.clickEvent(ClickEvent.runCommand("/grimoire decrypt " + id + " " + pages.indexOf(page) + " " + word.replace(indicator, "")));
                            }
                            TextReplacementConfig replacement = TextReplacementConfig.builder().match(word).replacement(_component).build();
                            component = component.replaceText(replacement);
                            amount.getAndIncrement();
                        }
                    }
                }
                return component;
            }).toArray(Component[]::new));
        }
        return book.build();
    }

    public ItemStack toItemStack() {
        return PublicationModule.getPublicationCfg().getGrimoireItemstack(this);
    }

    public void openPage(Player player, int page) {
        Book.Builder book = Book.builder();
        Component _page = ChatUT.hexComp(pages.get(page));
    }

    public void editPage(Player player, int page) {
        Book.Builder book = Book.builder();
        Component _page = ChatUT.hexComp(pages.get(page));
    }

    public void deletePage(Player player, int page) {
        pages.remove(page);
        editPage(player, page - 1);
    }

    public void addPage(Player player) {
        pages.add("");
        editPage(player, pages.size());
    }
}
