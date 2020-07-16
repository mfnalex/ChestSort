package de.jeff_media.ChestSort.hooks;

import de.jeff_media.ChestSort.ChestSortPlugin;
import de.jeff_media.ChestSortAPI.ChestSortCheckForCustomGUIEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

public class GenericGUIHook {

    ChestSortPlugin main;
    boolean enabled;

    public GenericGUIHook(ChestSortPlugin main, boolean enabled) {
        this.main=main;
        this.enabled=enabled;
    }

    public boolean isPluginGUIByEvent(InventoryView view, InventoryType.SlotType type, int slot, ClickType click, InventoryAction action) {
        if(slot==-999) slot= 9;
        ChestSortCheckForCustomGUIEvent event = new ChestSortCheckForCustomGUIEvent(view, type, slot, click, action);
        main.getServer().getPluginManager().callEvent(event);
        return event.isCancelled();
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
