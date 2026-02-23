package net.maksy.grimoires.configuration;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public enum Permissions {
    Use_Grimoires("grimoires.use"),
    Admin_Edit("grimoires.admin.edit"),
    Admin_GiveEditor("grimoires.admin.give-editor");

    private final String permission;

    Permissions(String permission) {
        this.permission = permission;
    }

    public boolean hasPermission(Player player) {
        return player.hasPermission(permission);
    }

    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission(permission);
    }
}
