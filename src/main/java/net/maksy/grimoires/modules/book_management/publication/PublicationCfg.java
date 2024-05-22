package net.maksy.grimoires.modules.book_management.publication;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.maksy.grimoires.Grimoires;
import net.maksy.grimoires.configuration.YamlParser;
import net.maksy.grimoires.modules.book_management.storage.Genre;
import net.maksy.grimoires.modules.book_management.storage.Grimoire;
import net.maksy.grimoires.utils.ChatUT;
import net.maksy.grimoires.utils.ItemUT;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PublicationCfg {

    private final YamlParser config;

    public PublicationCfg() {
        this.config = YamlParser.loadOrExtract(Grimoires.getInstance(), "Features/Publication.yml");
    }

    /* Main publication Gui*/
    public Component getPublicationTitle() {
        return ChatUT.hexComp(config.getString("MainGui.Title", "Publication Editor"));
    }

    public ItemStack getPublicationAuthorsIcon(Grimoire grimoire) {
        Material material = Material.valueOf(config.getString("MainGui.Icons.Authors.Material", "FEATHER").toUpperCase());
        Component title = ChatUT.hexComp(config.getString("MainGui.Icons.Authors.Title", "&9Authors"));
        List<Component> lore = new ArrayList<>();
        for (String line : config.getStringList("MainGui.Icons.Authors.Lore"))
            lore.add(ChatUT.hexComp(line)
                    .replaceText(TextReplacementConfig.builder().match("%authors%").replacement(grimoire.getAuthorsComponent()).build())
            );
        return ItemUT.getItem(material, title, false, lore);
    }

    public ItemStack getPublicationGenresIcon(Grimoire grimoire) {
        Material material = Material.valueOf(config.getString("MainGui.Icons.Genres.Material", "BOOK").toUpperCase());
        Component title = ChatUT.hexComp(config.getString("MainGui.Icons.Genres.Title", "&9Genres"));
        List<Component> lore = new ArrayList<>();
        for (String line : config.getStringList("MainGui.Icons.Genres.Lore"))
            lore.add(ChatUT.hexComp(line)
                    .replaceText(TextReplacementConfig.builder().match("%genres%").replacement(grimoire.getGenresComponent()).build())
            );
        return ItemUT.getItem(material, title, false, lore);
    }

    public ItemStack getPublicationBookIcon(Grimoire grimoire) {
        Material material = Material.valueOf(config.getString("MainGui.Icons.Book.Material", "WRITTEN_BOOK").toUpperCase());
        Component title = ChatUT.hexComp(config.getString("MainGui.Icons.Book.Title", "&9Publication"));
        List<Component> lore = new ArrayList<>();
        for (String line : config.getStringList("MainGui.Icons.Book.Lore"))
            lore.add(ChatUT.hexComp(line)
                    .replaceText(TextReplacementConfig.builder().match("%title%").replacement(Component.text(grimoire.getTitle())).build())
                    .replaceText(TextReplacementConfig.builder().match("%authors%").replacement(grimoire.getAuthorsComponent()).build())
                    .replaceText(TextReplacementConfig.builder().match("%genres%").replacement(grimoire.getGenresComponent()).build())
            );
        return ItemUT.getItem(material, title, false, lore);
    }

    public ItemStack getPublicationPricingIcon(Grimoire grimoire) {
        Material material = Material.valueOf(config.getString("MainGui.Icons.Pricing.Material", "EMERALD").toUpperCase());
        Component title = ChatUT.hexComp(config.getString("MainGui.Icons.Pricing.Title", "&9Pricing"));
        List<Component> lore = new ArrayList<>();
        for (String line : config.getStringList("MainGui.Icons.Pricing.Lore"))
            lore.add(ChatUT.hexComp(line)
                    .replaceText(TextReplacementConfig.builder().match("%price%").replacement(Component.text(String.valueOf(grimoire.getPublicationPrice()))).build())
            );
        return ItemUT.getItem(material, title, false, lore);
    }

    /* Sub gui for authors */
    public Component getAuthorsGuiTitle() {
        return ChatUT.hexComp(config.getString("SubGui.Authors.Title", "Authors"));
    }

    public ItemStack getAuthorsGuiAddIcon() {
        Material material = Material.valueOf(config.getString("SubGui.Authors.Icons.Add.Material", "FEATHER").toUpperCase());
        Component title = ChatUT.hexComp(config.getString("SubGui.Author.Icons.Add.Title", "&9Add Author"));
        List<Component> lore = new ArrayList<>();
        for (String line : config.getStringList("SubGui.Authors.Icons.Add.Lore"))
            lore.add(ChatUT.hexComp(line));
        return ItemUT.getItem(material, title, false, lore);
    }

    public ItemStack getAuthorsGuiAuthorIcon(UUID uuid) {
        Component title = ChatUT.hexComp(config.getString("SubGui.Authors.Icons.Author.Title", "&9Author").replace("%name%", ChatUT.getPlayerName(uuid)));
        List<Component> lore = new ArrayList<>();
        for (String line : config.getStringList("SubGui.Authors.Icons.Author.Lore"))
            lore.add(ChatUT.hexComp(line.replace("%name%", ChatUT.getPlayerName(uuid))));
        return ItemUT.getSkull(uuid, title, lore);
    }
    /* Player Search Mechanic*/
    public PlayerSearchMechanic getPlayerSearchMechanic() {
        return new PlayerSearchMechanic(SearchType.valueOf(config.getString("PlayerSearch.Type", "NEARBY").toUpperCase()), config.getDouble("PlayerSearch.Distance", 20), config.getInt("PlayerSearch.CheckInterval", 5), config.getInt("PlayerSearch.Limit", 3));
    }

    /* Sub gui for genres */
    public Component getPGenresGuiTitle() {
        return ChatUT.hexComp(config.getString("SubGui.Genres.Title", "Genres"));
    }

    public ItemStack getGenresGuiAddIcon() {
        Material material = Material.valueOf(config.getString("SubGui.Genres.Icons.Add.Material", "WRITABLE_BOOK").toUpperCase());
        Component title = ChatUT.hexComp(config.getString("SubGui.Genres.Icons.Add.Title", "&9Add Genre"));
        List<Component> lore = new ArrayList<>();
        for (String line : config.getStringList("SubGui.Genres.Icons.Add.Lore"))
            lore.add(ChatUT.hexComp(line));
        return ItemUT.getItem(material, title, false, lore);
    }

    public ItemStack getGenresGuiGenreIcon(Genre genre) {
        Material material = Material.valueOf(config.getString("SubGui.Genres.Icons.Genre.Material", "BOOK").toUpperCase());
        Component title = ChatUT.hexComp(config.getString("SubGui.Genres.Icons.Genre.Title", "&9Genre"));
        List<Component> lore = new ArrayList<>();
        for (String line : config.getStringList("SubGui.Genres.Icons.Genre.Lore"))
            lore.add(ChatUT.hexComp(line)
                    .replaceText(TextReplacementConfig.builder().match("%genre%").replacement(Component.text(genre.getName())).build())
            );
        return ItemUT.getItem(material, title, false, lore);
    }

    /* Pricing mechanic */
    public PricingStages getPricingStages() {
        PricingStages pricing = new PricingStages(config.getBoolean("Pricing.Enabled", false));
        for (String key : config.getSection("Pricing.PageStages")) {
            try {
                pricing.addPrice(Integer.parseInt(key), config.getDouble("Pricing.PageStages." + key));
            } catch (NumberFormatException e) {
                Grimoires.getInstance().getLogger().warning("Invalid pricing stage: " + key);
            }
        }
        return pricing;
    }
}
