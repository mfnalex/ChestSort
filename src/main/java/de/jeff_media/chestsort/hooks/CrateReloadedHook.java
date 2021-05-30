package de.jeff_media.chestsort.hooks;

import de.jeff_media.chestsort.ChestSortPlugin;
import org.bukkit.inventory.Inventory;

public class CrateReloadedHook {

    private final ChestSortPlugin main;

    public CrateReloadedHook(ChestSortPlugin main) {
        this.main=main;
    }

    // CrateReloaded inventories seem to have a holder called cratereloaded.bo
    // Maybe this changes? We just check if the String starts with cratereloaded
    public boolean isCrate(Inventory inv) {
        if(inv==null) return false;
        if(inv.getHolder()==null) return false;
        if(!main.getConfig().getBoolean("hook-cratereloaded",true)) return false;
        return inv.getHolder().getClass().getName().startsWith("cratereloaded");
    }

}
