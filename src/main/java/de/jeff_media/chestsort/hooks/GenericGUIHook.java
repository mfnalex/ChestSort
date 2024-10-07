package de.jeff_media.chestsort.hooks;

import de.jeff_media.chestsort.ChestSortPlugin;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;
import java.util.List;
import org.bukkit.inventory.InventoryHolder;

public class GenericGUIHook {

    private final ChestSortPlugin main;
    //boolean enabled;

    private final List<String> guiClasses = Arrays.asList("me.droreo002.chestshopconfirmation.inventory.ConfirmationInventory");

    public GenericGUIHook(ChestSortPlugin main, boolean enabled) {
        this.main=main;
        //this.enabled=enabled;
    }

    public boolean isPluginGUI(Inventory inv, InventoryHolder holder) {
        if(holder !=null && (holder.getClass().getName().toLowerCase().contains("gui")
                || holder.getClass().getName().endsWith(".FastInv")
            || holder.getClass().getName().toLowerCase().contains("menu"))) {
            main.debug("Generic GUI detected by class name containing \"gui\" or \"menu\"");
            return true;
        }
        if(main.getConfig().getBoolean("prevent-sorting-null-inventories")) {
            if(holder ==null) {
                return false;
            }
        }
        if(holder != null && guiClasses.contains(holder.getClass().getName())) return true;
        return false;
    }

}
