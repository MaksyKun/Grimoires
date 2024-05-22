package net.maksy.grimoires.configuration.translation;


import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.maksy.grimoires.Grimoires;
import org.bukkit.command.CommandSender;

@Getter
@SuppressWarnings("unused")
public enum Translation {

    HoverPlaceholder("HoverPlaceholder"),

    Publication_BookPublished("Publication.BookPublished"),
    Publication_BookAlreadyPublished("Publication.BookAlreadyPublished"),
    Publication_BookUnpublished("Publication.BookUnpublished"),
    Publication_AuthorsLimitReached("Publication.AuthorsLimitReached"),

    Vault_ErrorPlayers("Vault.ErrorPlayers"),
    Vault_FreeFormat("Vault.FreeFormat"),
    Vault_MoneyFormat("Vault.MoneyFormat");



    private final String path;

    Translation(String path) {
        this.path = path;
    }

    public Component asComponent(Replaceable... replaceables) {
        return Grimoires.getTranslations().message(this, replaceables);
    }

    public void sendMessage(CommandSender sender, Replaceable... replaceables) {
        sender.sendMessage(Grimoires.getTranslations().message(this, replaceables));
    }

    public void sendActionBar(CommandSender sender, Replaceable... replaceables) {
        sender.sendActionBar(Grimoires.getTranslations().message(this, replaceables));
    }

    public void sendTitle(CommandSender sender, Component subtitle, Title.Times times) {
        Title tile = Title.title(Grimoires.getTranslations().message(this), subtitle, times);
        sender.showTitle(tile);
    }
}
