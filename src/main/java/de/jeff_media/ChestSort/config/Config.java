package de.jeff_media.ChestSort.config;

import de.jeff_media.ChestSort.ChestSortPlugin;

public class Config {
    public static final String HOTKEY_COOLDOWN = "hotkey-cooldown";
    public static final String DEBUG2 = "debug2";

    private final ChestSortPlugin main;

    public Config(ChestSortPlugin main) {
        this.main=main;

        main.getConfig().addDefault(HOTKEY_COOLDOWN,0.0);
        main.getConfig().addDefault(DEBUG2,false);
    }
}
