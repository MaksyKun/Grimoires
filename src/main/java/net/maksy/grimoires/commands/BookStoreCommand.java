package net.maksy.grimoires.commands;

import net.maksy.grimoires.configuration.Permissions;
import net.maksy.grimoires.configuration.translation.Translation;
import net.maksy.grimoires.modules.book_management.store.BookStoreStorage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BookStoreCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            Translation.Command_PlayerOnly.sendMessage(sender);
            return true;
        }
        if (!Permissions.Use_Grimoires.hasPermission(player)) {
            Translation.Command_NoPermission.sendMessage(sender);
            return true;
        }
        new BookStoreStorage().open(player);
        return true;
    }
}
