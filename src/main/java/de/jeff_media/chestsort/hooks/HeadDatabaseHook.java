package de.jeff_media.chestsort.hooks;

import de.jeff_media.chestsort.ChestSortPlugin;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class HeadDatabaseHook {

    private final ChestSortPlugin main;

    public HeadDatabaseHook(ChestSortPlugin main) {
        this.main=main;
    }

    public boolean isHeadDB(Inventory inv, InventoryHolder holder) {
        if(inv==null) return false;
        if(holder == null) return false;
        if(!main.getConfig().getBoolean("hook-headdatabase",true)) return false;
        return holder.getClass().getName().equals("me.arcaniax.hdb.object.HeadDatabaseHolder");
    }

}
