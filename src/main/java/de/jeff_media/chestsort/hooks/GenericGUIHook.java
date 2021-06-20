package de.jeff_media.chestsort.hooks;

import de.jeff_media.chestsort.ChestSortPlugin;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;
import java.util.List;

public class GenericGUIHook {

    private final ChestSortPlugin main;
    //boolean enabled;

    private final List<String> guiClasses = Arrays.asList("me.droreo002.chestshopconfirmation.inventory.ConfirmationInventory");

    public GenericGUIHook(ChestSortPlugin main, boolean enabled) {
        this.main=main;
        //this.enabled=enabled;
    }

    public boolean isPluginGUI(Inventory inv) {
        if(inv.getHolder()!=null && (inv.getHolder().getClass().getName().toLowerCase().contains("gui")
            || inv.getHolder().getClass().getName().toLowerCase().contains("menu"))) {
            main.debug("Generic GUI detected by class name containing \"gui\" or \"menu\"");
            return true;
        }
        if(main.getConfig().getBoolean("prevent-sorting-null-inventories")) {
            if(inv.getHolder()==null) {
                return false;
            }
        }
        if(inv.getHolder() != null && guiClasses.contains(inv.getHolder().getClass().getName())) return true;
        return false;
    }

}
