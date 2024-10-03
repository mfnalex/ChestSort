package de.jeff_media.chestsort.hooks;

import de.jeff_media.chestsort.ChestSortPlugin;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class EnderContainersHook {

    private static final String CLASS_NAME = "fr.utarwyn.endercontainers.inventory.EnderChestInventory";

    private final ChestSortPlugin main;

    public EnderContainersHook(ChestSortPlugin main) {
        this.main = main;
    }

    public boolean isEnderchest(Inventory inv, InventoryHolder holder) {
        if (inv == null) return false;
        if (holder == null) return false;
        if (!main.getConfig().getBoolean("hook-endercontainers", true)) return false;
        return holder.getClass().getName().equals(CLASS_NAME);
    }

}
