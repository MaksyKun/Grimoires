package net.maksy.grimoires.commands;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.maksy.grimoires.configuration.GrimoireDesignCfg;
import net.maksy.grimoires.configuration.Permissions;
import net.maksy.grimoires.configuration.translation.Replaceable;
import net.maksy.grimoires.configuration.translation.Translation;
import net.maksy.grimoires.modules.book_management.BookManagementModule;
import net.maksy.grimoires.modules.book_management.storage.Grimoire;
import net.maksy.grimoires.modules.book_management.storage.GrimoireRegistry;
import net.maksy.grimoires.modules.book_management.storage.GrimoireStorage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
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
            default -> Translation.Command_Usage.sendMessage(sender);
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> entries = new ArrayList<>();
        if (args.length == 1) {
            if ("read".startsWith(args[0])) entries.add("read");
            if ("edit".startsWith(args[0])) entries.add("edit");
            if ("add".startsWith(args[0])) entries.add("add");
            if ("delete".startsWith(args[0])) entries.add("delete");
            if ("give-editor".startsWith(args[0])) entries.add("give-editor");
            if ("publish".startsWith(args[0])) entries.add("publish");
            if ("show".startsWith(args[0])) entries.add("show");
            if ("get".startsWith(args[0])) entries.add("get");
        }
        return entries;
    }
}

