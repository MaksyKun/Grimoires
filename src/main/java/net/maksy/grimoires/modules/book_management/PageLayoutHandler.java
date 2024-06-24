package net.maksy.grimoires.modules.book_management;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.maksy.grimoires.Grimoires;
import net.maksy.grimoires.utils.ChatUT;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;

import java.util.List;

public class PageLayoutHandler implements Listener {
    private final LegacyComponentSerializer serializer = LegacyComponentSerializer.builder().build();

    @EventHandler
    public void onPageEdit(PlayerEditBookEvent event) {
        List<Component> pages = event.getNewBookMeta().pages();
        for (int i = 0; i < pages.size(); i++) {
            String pageText = serializer.serialize(pages.get(i));
            String[] lines = pageText.split("\n");
            if (lines.length >= 14) {  // Checking if the page has 14 or more lines
                lines[13] = "";  // Clear the 14th line (last row)
                StringBuilder newPageText = new StringBuilder();
                for (String line : lines) {
                    newPageText.append(line).append("\n");
                }
                pages.set(i, Component.text(newPageText.toString()));
            }
        }
        event.getNewBookMeta().pages(pages);
        Grimoires.consoleMessage(ChatUT.hexComp("Edit restricted to 13 lines per page."));
    }
}
