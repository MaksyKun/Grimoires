package net.maksy.grimoires.configuration.translation;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.maksy.grimoires.utils.ChatUT;

@Getter
@SuppressWarnings("unused")
public class Replaceable {
    private final String key, value;
    private Component hoverEvent;
    private final ClickEvent clickEvent;

    public Replaceable(String key, String value) {
        this.key = key;
        this.value = value;
        this.hoverEvent = null;
        this.clickEvent = null;
    }

    public Replaceable(String key, String value, Component hoverEvent, ClickEvent clickEvent) {
        this.key = key;
        this.value = value;
        this.hoverEvent = hoverEvent;
        this.clickEvent = clickEvent;
    }

    public boolean hasHoverEvent() { return hoverEvent != null; }

    public void setHoverEvent(String text) {
        this.hoverEvent = ChatUT.hexComp(text);
    }

    public boolean hasClickEvent() { return clickEvent != null; }

}
