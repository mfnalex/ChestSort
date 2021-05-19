package de.jeff_media.chestsort.hooks;

import de.jeff_media.chestsort.ChestSortPlugin;
import org.bukkit.inventory.Inventory;

public class HeadDatabaseHook {

    ChestSortPlugin main;

    public HeadDatabaseHook(ChestSortPlugin main) {
        this.main=main;
    }

    public boolean isHeadDB(Inventory inv) {
        if(inv==null) return false;
        if(inv.getHolder() == null) return false;
        if(!main.getConfig().getBoolean("hook-headdatabase",true)) return false;
        return inv.getHolder().getClass().getName().equals("me.arcaniax.hdb.object.HeadDatabaseHolder");
    }

}
