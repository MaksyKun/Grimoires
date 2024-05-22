package net.maksy.grimoires.configuration;

import org.bukkit.entity.Player;

public enum Permissions {
    Use_Grimoires("grimoires.use");

    private final String permission;

    Permissions(String permission) {
        this.permission = permission;
    }

    public boolean hasPermission(Player player) {
        return player.hasPermission(permission);
    }
}
