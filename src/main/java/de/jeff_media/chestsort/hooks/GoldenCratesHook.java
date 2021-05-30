package de.jeff_media.chestsort.hooks;

import de.jeff_media.chestsort.ChestSortPlugin;
import org.bukkit.inventory.Inventory;

public class GoldenCratesHook {

   private final ChestSortPlugin main;

    public GoldenCratesHook(ChestSortPlugin main) {
        this.main=main;
    }


    public boolean isCrate(Inventory inv) {
        if(inv==null) return false;
        if(inv.getHolder()==null) return false;
        if(!main.getConfig().getBoolean("hook-goldencrates",true)) return false;
        return inv.getHolder().getClass().getName().contains("crate") || inv.getHolder().getClass().getName().contains("crates") || inv.getHolder().getClass().getName().contains("preview") || inv.getHolder().getClass().getName().contains("golden");
    }

}
