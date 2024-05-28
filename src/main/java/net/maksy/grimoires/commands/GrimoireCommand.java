package net.maksy.grimoires.commands;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.maksy.grimoires.configuration.translation.Translation;
import net.maksy.grimoires.modules.book_management.storage.Grimoire;
import net.maksy.grimoires.modules.book_management.storage.GrimoireRegistry;
import net.maksy.grimoires.modules.book_management.storage.GrimoireStorage;
import net.maksy.grimoires.Grimoires;
import net.maksy.grimoires.modules.book_management.publication.gui.PublicationEditor;
import net.maksy.grimoires.modules.mysteries.DecryptionProcess;
import net.maksy.grimoires.utils.ChatUT;
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
                case "publish" -> {
                    ItemStack item = player.getInventory().getItemInMainHand();
                    if (item.getItemMeta() instanceof BookMeta book) {
                        if (GrimoireRegistry.isGrimoireExistent(player.getUniqueId(), book.getTitle())) {
                            Translation.Publication_BookAlreadyPublished.sendMessage(player);
                            return true;
                        }
                        Grimoire grimoire = new Grimoire(-1, List.of(player.getUniqueId()), book.getTitle(), " ", List.of(), book.pages().stream().map(Serializer::serialize).toList(), System.currentTimeMillis());
                        new PublicationEditor(player, grimoire).open();
                    }
                }
                case "show" -> new GrimoireStorage().open(player);
                default -> {
                }
                // TODO help message
            }
        } else if(args.length == 2 && args[0].equals("get")) {
            try {
                int id = Integer.parseInt(args[1]);
                Grimoire grimoire = Grimoires.sql().books().getBook(id);
                player.openBook(grimoire.getBook(player));
                player.getInventory().addItem(grimoire.toItemStack());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else if(args.length == 3 && args[0].equals("decrypt")) {
            int id = Integer.parseInt(args[1]);
            Grimoire grimoire = GrimoireRegistry.getGrimoire(id);
            if(grimoire == null) {
                Grimoires.consoleMessage(ChatUT.hexComp("Grimoire not found"));
                return true;
            }

            String key = args[2];
            Grimoires.consoleMessage(ChatUT.hexComp("Decryption key: " + key));
            if(!DecryptionProcess.Decryptions.containsKey(player.getUniqueId()) || DecryptionProcess.Decryptions.get(player.getUniqueId()).getGrimoire().getId() != id) {
                DecryptionProcess.Decryptions.put(player.getUniqueId(), Grimoires.sql().mysteries().getProcess(player, grimoire));
                ChatUT.hexComp("Decryption process created");
            }
            Grimoires.consoleMessage(ChatUT.hexComp("Decryption process started"));
            DecryptionProcess.Decryptions.get(player.getUniqueId()).decrypt(key);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> entries = new ArrayList<>();
        if(args.length == 1) {
            if("publish".startsWith(args[0])) entries.add("publish");
            if("show".startsWith(args[0])) entries.add("show");
            if("get".startsWith(args[0])) entries.add("get");
        }
        return entries;
    }
}
