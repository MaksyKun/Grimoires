package net.maksy.grimoires.commands;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.maksy.grimoires.configuration.GrimoireDesignCfg;
import net.maksy.grimoires.configuration.Permissions;
import net.maksy.grimoires.configuration.translation.Replaceable;
import net.maksy.grimoires.configuration.translation.Translation;
import net.maksy.grimoires.modules.book_management.BookManagementModule;
import net.maksy.grimoires.modules.book_management.publication.gui.PublicationEditor;
import net.maksy.grimoires.modules.book_management.storage.Grimoire;
import net.maksy.grimoires.modules.book_management.storage.GrimoireRegistry;
import net.maksy.grimoires.modules.book_management.storage.GrimoireStorage;
import net.maksy.grimoires.modules.book_management.store.BookStoreStorage;
import net.maksy.grimoires.modules.mysteries.DecryptionProcess;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GrimoireCommand implements CommandExecutor, TabCompleter {

    public static final LegacyComponentSerializer Serializer = LegacyComponentSerializer.builder().build();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            Translation.Command_Usage.sendMessage(sender);
            return true;
        }

        String subcommand = args[0].toLowerCase();

        switch (subcommand) {
            case "show" -> {
                if (!(sender instanceof Player player)) {
                    Translation.Command_PlayerOnly.sendMessage(sender);
                    return true;
                }
                if (!Permissions.Use_Grimoires.hasPermission(player)) {
                    Translation.Command_NoPermission.sendMessage(sender);
                    return true;
                }
                new GrimoireStorage().open(player);
            }
            case "store" -> {
                if (!(sender instanceof Player player)) {
                    Translation.Command_PlayerOnly.sendMessage(sender);
                    return true;
                }
                if (!Permissions.Use_Grimoires.hasPermission(player)) {
                    Translation.Command_NoPermission.sendMessage(sender);
                    return true;
                }
                new BookStoreStorage().open(player);
            }
            case "give-editor" -> {
                if (!(sender instanceof Player player)) {
                    Translation.Command_PlayerOnly.sendMessage(sender);
                    return true;
                }
                if (!Permissions.Admin_GiveEditor.hasPermission(player)) {
                    Translation.Command_NoPermission.sendMessage(sender);
                    return true;
                }
                if (!GrimoireDesignCfg.isCustomPagingEnabled) {
                    Translation.Command_CustomPagingDisabled.sendMessage(sender);
                    return true;
                }
                player.getInventory().addItem(BookManagementModule.getDesignCfg().getGrimoireEditor(null, 0));
            }
            case "read", "edit", "add", "delete" -> {
                if (!(sender instanceof Player player)) {
                    Translation.Command_PlayerOnly.sendMessage(sender);
                    return true;
                }
                Permissions perm = subcommand.equals("read") ? Permissions.Use_Grimoires : Permissions.Admin_Edit;
                if (!perm.hasPermission(player)) {
                    Translation.Command_NoPermission.sendMessage(sender);
                    return true;
                }
                if (!subcommand.equals("read") && !GrimoireDesignCfg.isCustomPagingEnabled) {
                    Translation.Command_CustomPagingDisabled.sendMessage(sender);
                    return true;
                }
                boolean needsPage = !subcommand.equals("add");
                int requiredArgs = needsPage ? 3 : 2;
                if (args.length != requiredArgs) {
                    Translation.Command_Usage.sendMessage(sender);
                    return true;
                }
                int id;
                try {
                    id = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    Translation.Command_Usage.sendMessage(sender);
                    return true;
                }
                Grimoire grimoire = GrimoireRegistry.getGrimoire(id);
                if (grimoire == null) {
                    Translation.Command_GrimoireNotFound.sendMessage(sender, new Replaceable("%id%", String.valueOf(id)));
                    return true;
                }
                if (subcommand.equals("add")) {
                    grimoire.addPage(player);
                } else {
                    int page;
                    try {
                        page = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        Translation.Command_Usage.sendMessage(sender);
                        return true;
                    }
                    if (page < 0 || page >= grimoire.getPages().size()) {
                        Translation.Command_InvalidPage.sendMessage(sender, new Replaceable("%page%", String.valueOf(page)));
                        return true;
                    }
                    switch (subcommand) {
                        case "read" -> grimoire.openPage(player, page);
                        case "edit" -> grimoire.editPage(player, page);
                        case "delete" -> grimoire.deletePage(player, page);
                    }
                }
            }
            case "publish" -> {
                if (!(sender instanceof Player player)) {
                    Translation.Command_PlayerOnly.sendMessage(sender);
                    return true;
                }
                if (!Permissions.Use_Grimoires.hasPermission(player)) {
                    Translation.Command_NoPermission.sendMessage(sender);
                    return true;
                }
                if (!(player.getInventory().getItemInMainHand().getType() == Material.WRITTEN_BOOK
                        && player.getInventory().getItemInMainHand().getItemMeta() instanceof BookMeta bookMeta)) {
                    Translation.Command_NotHoldingBook.sendMessage(sender);
                    return true;
                }
                String title = bookMeta.getTitle() != null ? bookMeta.getTitle() : "";
                if (GrimoireRegistry.isGrimoireExistent(player.getUniqueId(), title)) {
                    Translation.Publication_BookAlreadyPublished.sendMessage(sender);
                    return true;
                }
                List<String> pages = new ArrayList<>();
                for (net.kyori.adventure.text.Component page : bookMeta.pages()) {
                    pages.add(Serializer.serialize(page));
                }
                Grimoire grimoire = new Grimoire(0, new ArrayList<>(List.of(player.getUniqueId())),
                        title, "", new ArrayList<>(), pages, System.currentTimeMillis());
                new PublicationEditor(player, grimoire).open();
            }
            case "get" -> {
                if (!Permissions.Admin_Edit.hasPermission(sender)) {
                    Translation.Command_NoPermission.sendMessage(sender);
                    return true;
                }
                if (args.length != 3) {
                    Translation.Command_Usage.sendMessage(sender);
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    Translation.Command_PlayerNotFound.sendMessage(sender, new Replaceable("%player%", args[1]));
                    return true;
                }
                int getId;
                try {
                    getId = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    Translation.Command_Usage.sendMessage(sender);
                    return true;
                }
                Grimoire getGrimoire = GrimoireRegistry.getGrimoire(getId);
                if (getGrimoire == null) {
                    Translation.Command_GrimoireNotFound.sendMessage(sender, new Replaceable("%id%", String.valueOf(getId)));
                    return true;
                }
                target.getInventory().addItem(getGrimoire.toItemStack());
                Translation.Command_BookGiven.sendMessage(sender,
                        new Replaceable("%title%", getGrimoire.getTitle()),
                        new Replaceable("%player%", target.getName()));
            }
            case "decrypt" -> {
                if (!(sender instanceof Player player)) {
                    Translation.Command_PlayerOnly.sendMessage(sender);
                    return true;
                }
                if (!Permissions.Use_Grimoires.hasPermission(player)) {
                    Translation.Command_NoPermission.sendMessage(sender);
                    return true;
                }
                if (args.length != 4) {
                    Translation.Command_Usage.sendMessage(sender);
                    return true;
                }
                int decryptId;
                try {
                    decryptId = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    Translation.Command_Usage.sendMessage(sender);
                    return true;
                }
                Grimoire decryptGrimoire = GrimoireRegistry.getGrimoire(decryptId);
                if (decryptGrimoire == null) {
                    Translation.Command_GrimoireNotFound.sendMessage(sender, new Replaceable("%id%", String.valueOf(decryptId)));
                    return true;
                }
                List<String> keys = decryptGrimoire.getEncryptionKeys();
                if (keys == null || keys.isEmpty()) {
                    return true;
                }
                DecryptionProcess process = DecryptionProcess.get(player, decryptGrimoire);
                if (process == null) {
                    return true;
                }
                process.decrypt(args[3]);
            }
            default -> Translation.Command_Usage.sendMessage(sender);
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> entries = new ArrayList<>();
        if (args.length == 1) {
            if ("give-editor".startsWith(args[0])) entries.add("give-editor");
            if ("publish".startsWith(args[0])) entries.add("publish");
            if ("show".startsWith(args[0])) entries.add("show");
            if ("store".startsWith(args[0])) entries.add("store");
            if ("get".startsWith(args[0])) entries.add("get");
        }
        return entries;
    }
}

