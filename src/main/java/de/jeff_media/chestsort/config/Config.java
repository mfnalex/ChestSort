package de.jeff_media.chestsort.config;

import de.jeff_media.chestsort.ChestSortPlugin;

public class Config {
    public static final String CONFIG_VERSION = "config-version";
    public static final String CONFIG_PLUGIN_VERSION = "plugin-version";
    public static final String HOTKEY_COOLDOWN = "hotkey-cooldown";
    public static final String DISABLED_WORLDS = "disabled-worlds";
    public static final String DEBUG2 = "debug2";

    private final ChestSortPlugin main;

    public Config(ChestSortPlugin main) {
        this.main=main;

        main.getConfig().addDefault(HOTKEY_COOLDOWN,0.0);
        main.getConfig().addDefault(DEBUG2,false);
    }
}
