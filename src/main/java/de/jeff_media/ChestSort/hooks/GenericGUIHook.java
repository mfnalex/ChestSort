package de.jeff_media.ChestSort.hooks;

import de.jeff_media.ChestSort.ChestSortPlugin;
import org.bukkit.inventory.Inventory;

public class GenericGUIHook {

    ChestSortPlugin main;
    boolean enabled;

    public GenericGUIHook(ChestSortPlugin main, boolean enabled) {
        this.main=main;
        this.enabled=enabled;
    }

    public boolean isPluginGUI(Inventory inv) {
        if(!enabled) return false;
        if(inv.getHolder()!=null && inv.getHolder().getClass().getName().contains("GUI")) {
            main.debug("Generic GUI detected by class name containing \"GUI\"");
            return true;
        }
        return false;
    }

}
