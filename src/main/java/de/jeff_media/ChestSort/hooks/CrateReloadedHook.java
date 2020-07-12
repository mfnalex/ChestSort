package de.jeff_media.ChestSort.hooks;

import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class CrateReloadedHook {

    // CrateReloaded inventories seem to have a holder called cratereloaded.bo
    // Maybe this changes? We just check if the String starts with cratereloaded
    public static boolean isCrate(Inventory inv) {
        if(inv==null) return false;
        if(inv.getHolder()==null) return false;
        return inv.getHolder().getClass().getName().startsWith("cratereloaded");
    }

}
