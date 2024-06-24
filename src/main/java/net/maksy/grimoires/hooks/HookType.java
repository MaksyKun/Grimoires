package net.maksy.grimoires.hooks;

public enum HookType {
    HeadDatabase("HeadDatabase"),
    ProtocolLib("ProtocolLib"),
    Vault("Vault");

    private final String pluginName;

    HookType(String pluginName) {
        this.pluginName = pluginName;
    }

    @Override
    public String toString() { return pluginName; }
}
