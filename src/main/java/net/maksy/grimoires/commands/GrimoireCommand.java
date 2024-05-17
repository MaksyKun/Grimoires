package net.maksy.grimoires.commands;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.maksy.grimoires.configuration.translation.Replaceable;
import net.maksy.grimoires.configuration.translation.Translation;
import net.maksy.grimoires.Grimoire;
import net.maksy.grimoires.GrimoireRegistry;
import net.maksy.grimoires.GrimoireStorage;
import net.maksy.grimoires.Grimoires;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GrimoireCommand implements CommandExecutor, TabCompleter {

    public static final LegacyComponentSerializer Serializer = LegacyComponentSerializer.builder().build();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 0) {
            // TODO help message
            return true;
        }

        if(!(sender instanceof Player player)) {
            // TODO console message
            return true;
        }

        if(args.length == 1) {
            switch (args[0]) {
                case "savebook":
                    ItemStack item = player.getInventory().getItemInMainHand();
                    if (item.getItemMeta() instanceof BookMeta book) {
                        if(GrimoireRegistry.isGrimoireExistent(player.getUniqueId(), book.getTitle())) {
                            Translation.Publication_BookAlreadyPublished.sendMessage(player);
                            return true;
                        }
                        Grimoire grimoire = new Grimoire(-1, List.of(player.getUniqueId()), book.getTitle(), " ", List.of(), book.pages().stream().map(Serializer::serialize).toList(), System.currentTimeMillis());
                        Grimoires.sql().getBooksSQL().addBook(grimoire);
                        GrimoireRegistry.updateRegistry();
                        Translation.Publication_BookPublished.sendMessage(player, new Replaceable("%title%", book.getTitle()));
                    }
                    break;
                case "show":
                    new GrimoireStorage().open(player);
                    break;
                default:
                    // TODO help message
                    break;
            }
        } else if(args.length == 2 && args[0].equals("get")) {
            try {
                int id = Integer.parseInt(args[1]);
                Grimoire grimoire = Grimoires.sql().getBooksSQL().getBook(id);
                player.openBook(grimoire.getBook());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> entries = new ArrayList<>();
        if(args.length == 1) {
            if("savebook".startsWith(args[0])) entries.add("savebook");
            if("show".startsWith(args[0])) entries.add("show");
            if("get".startsWith(args[0])) entries.add("get");
        }
        return entries;
    }
}
