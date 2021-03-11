package de.jeff_media.ChestSort.hooks;

import de.jeff_media.ChestSort.ChestSortPlugin;
import org.bukkit.inventory.Inventory;

public class EnderContainersHook {

    private static final String CLASS_NAME = "fr.utarwyn.endercontainers.inventory.EnderChestInventory";

    private final ChestSortPlugin main;

    public EnderContainersHook(ChestSortPlugin main) {
        this.main = main;
    }

    public boolean isEnderchest(Inventory inv) {
        if (inv == null) return false;
        if (inv.getHolder() == null) return false;
        if (!main.getConfig().getBoolean("hook-endercontainers", true)) return false;
        return inv.getHolder().getClass().getName().equals(CLASS_NAME);
    }

}
