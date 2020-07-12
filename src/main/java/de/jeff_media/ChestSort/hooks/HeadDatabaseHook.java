package de.jeff_media.ChestSort.hooks;

import org.bukkit.inventory.Inventory;

public class HeadDatabaseHook {

    public static boolean isHeadDB(Inventory inv) {
        if(inv==null) return false;
        if(inv.getHolder() == null) return false;
        return inv.getHolder().getClass().getName().equals("me.arcaniax.hdb.object.HeadDatabaseHolder");
    }

}
