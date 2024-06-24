package net.maksy.grimoires.configuration;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.maksy.grimoires.Grimoires;
import net.maksy.grimoires.modules.book_management.storage.Grimoire;
import net.maksy.grimoires.utils.ChatUT;
import net.maksy.grimoires.utils.ItemUT;
import net.maksy.grimoires.utils.PersistentMetaData;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GrimoireDesignCfg {

    private final YamlParser config;

    public static boolean isCustomPagingEnabled;
    public static int maxPages;
    public static String previousPage;
    public static String previousPageHover;
    public static String nextPage;
    public static String nextPageHover;
    public static String deletePage;
    public static String deletePageHover;
    public static String addPage;
    public static String addPageHover;
    public static String pageLayout;
    public static String pageEditLayout;

    public GrimoireDesignCfg() {
        this.config = YamlParser.loadOrExtract(Grimoires.getInstance(), "GrimoireDesign.yml");
        isCustomPagingEnabled = config.getBoolean("Paging.CustomPaging", false);
        maxPages = config.getInt("Paging.MaxPages", 10);
        previousPage = config.getString("Paging.PageComponents.PreviousPage", "<--");
        previousPageHover = config.getString("Paging.PageComponentHover.PreviousPage", "&7Go to the previous page.");
        nextPage = config.getString("Paging.PageComponents.NextPage", "-->");
        nextPageHover = config.getString("Paging.PageComponentHover.NextPage", "&7Go to the next page.");
        addPage = config.getString("Paging.PageComponents.AddPage", "&2&a+");
        addPageHover = config.getString("Paging.PageComponentHover.AddPage", "&7Add a new page.");
        deletePage = config.getString("Paging.PageComponents.DeletePage", "&4&c-");
        deletePageHover = config.getString("Paging.PageComponentHover.DeletePage", "&7Delete the current page.");

        pageLayout = config.getString("Paging.PageLayouts.Default", " %previous%&0 [%page%/%max_page%] %next%");
        pageEditLayout = config.getString("Paging.PageLayouts.Edit", " %previous%&0 [%page%/%max_page%] %next% %delete% %add%");
    }

    public Component getGrimoireTitle(Grimoire grimoire) {
        return ChatUT.hexComp(config.getString("Grimoire.Title", grimoire.getTitle()).replace("%title%", grimoire.getTitle()));
    }

    public List<Component> getGrimoireLore(Grimoire grimoire) {
        List<Component> lore = new ArrayList<>();
        for (String line : config.getStringList("Grimoire.Lore")) {
            lore.add(ChatUT.hexComp(line
                    .replace("%title%", grimoire.getTitle())
                    .replace("%authors%", grimoire.getAuthors().toString())
                    .replace("%description%", grimoire.getDescription())
                    .replace("%genres%", grimoire.getGenres().toString())
            ));
        }
        return lore;
    }

    /* Paging Layout */
    public Component getPageLayout(Grimoire grimoire, int page) {
        Component prevPage = page - 1 > 0 ? ChatUT.hexComp(previousPage).hoverEvent(ChatUT.hexComp(previousPageHover)).clickEvent(ClickEvent.runCommand("/grimoire read " + grimoire.getId() + " " + (page - 1))) : Component.text("");
        Component nextPage = page + 1 < grimoire.getPages().size() ? ChatUT.hexComp(nextPageHover).hoverEvent(ChatUT.hexComp(nextPageHover)).clickEvent(ClickEvent.runCommand("/grimoire read " + grimoire.getId() + " " + (page + 1))) : Component.text("");
        return ChatUT.hexComp(pageLayout
                        .replace("%page%", String.valueOf(page))
                        .replace("%max_page%", String.valueOf(grimoire.getPages().size())))
                .replaceText(TextReplacementConfig.builder().match("%previous%").replacement(prevPage).build())
                .replaceText(TextReplacementConfig.builder().match("%next%").replacement(nextPage).build());
    }

    public Component getEditPageLayout(Grimoire grimoire, int page) {
        Component prevPage = page - 1 > 0 ? ChatUT.hexComp(previousPage).hoverEvent(ChatUT.hexComp(previousPageHover)).clickEvent(ClickEvent.runCommand("/grimoire edit " + grimoire.getId() + " " + (page - 1))) : Component.text("");
        Component nextPage = page + 1 < grimoire.getPages().size() ? ChatUT.hexComp(nextPageHover).hoverEvent(ChatUT.hexComp(nextPageHover)).clickEvent(ClickEvent.runCommand("/grimoire edit " + grimoire.getId() + " " + (page + 1))) : Component.text("");
        Component addPage = ChatUT.hexComp(nextPageHover).hoverEvent(ChatUT.hexComp(nextPageHover)).clickEvent(ClickEvent.runCommand("/grimoire add " + grimoire.getId() + " " + (page + 1)));
        Component deletePage = ChatUT.hexComp(nextPageHover).hoverEvent(ChatUT.hexComp(nextPageHover)).clickEvent(ClickEvent.runCommand("/grimoire delete " + grimoire.getId() + " " + page));

        return ChatUT.hexComp(pageEditLayout
                        .replace("%page%", String.valueOf(page))
                        .replace("%max_page%", String.valueOf(grimoire.getPages().size())))
                .replaceText(TextReplacementConfig.builder().match("%previous%").replacement(prevPage).build())
                .replaceText(TextReplacementConfig.builder().match("%next%").replacement(nextPage).build())
                .replaceText(TextReplacementConfig.builder().match("%add%").replacement(addPage).build())
                .replaceText(TextReplacementConfig.builder().match("%delete%").replacement(deletePage).build());
    }

    public ItemStack getGrimoireEditor(@Nullable Grimoire grimoire, int page) {
        Component title = ChatUT.hexComp(config.getString("Editor.Title", "&6Grimoire Editor"));
        List<Component> lore = new ArrayList<>();
        for (String line : config.getStringList("Editor.Lore")) {
            lore.add(ChatUT.hexComp(line
                    .replace("%title%", grimoire != null ? grimoire.getTitle() : "")
                    .replace("%pages%", grimoire != null ? String.valueOf(grimoire.getPages().size()) : "")
                    .replace("%page%", page > 0 ? String.valueOf(page) : "")
            ));
        }
        ItemStack item = ItemUT.getItem(Material.WRITABLE_BOOK, title, false, lore);
        ItemMeta meta = item.getItemMeta();
        UUID uuid = UUID.randomUUID();
        int id = grimoire != null ? grimoire.getId() : -1;
        PersistentMetaData.setNameSpace(meta, "editor", uuid.toString());
        PersistentMetaData.setNameSpace(meta, "editor", id);
        // TODO Save in sql

        item.setItemMeta(meta);
        return item;
    }
}
