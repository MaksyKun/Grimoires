package net.maksy.grimoires.hooks;

import me.arcaniax.hdb.api.HeadDatabaseAPI;

public class HeadDatabaseHook {

    public static HeadDatabaseAPI HeadDatabaseAPI;

    public static void hook() {
        HeadDatabaseAPI = new HeadDatabaseAPI();
    }
}
