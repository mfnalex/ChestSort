package de.jeff_media.chestsort.hooks;

import de.jeff_media.chestsort.ChestSortPlugin;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class GoldenCratesHook {

   private final ChestSortPlugin main;

    public GoldenCratesHook(ChestSortPlugin main) {
        this.main=main;
    }


    public boolean isCrate(Inventory inv, InventoryHolder holder) {
        if(inv==null) return false;
        if(holder ==null) return false;
        if(!main.getConfig().getBoolean("hook-goldencrates",true)) return false;
        return holder.getClass().getName().contains("crate") || holder.getClass().getName().contains("crates") || holder.getClass().getName().contains("preview") || holder.getClass().getName().contains("golden");
    }

}
