package de.jeff_media.chestsort.hooks;

import de.jeff_media.chestsort.ChestSortPlugin;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import us.lynuxcraft.deadsilenceiv.advancedchests.AdvancedChestsAPI;
import us.lynuxcraft.deadsilenceiv.advancedchests.chest.AdvancedChest;
import us.lynuxcraft.deadsilenceiv.advancedchests.chest.gui.page.ChestPage;
import us.lynuxcraft.deadsilenceiv.advancedchests.utils.inventory.InteractiveInventory;

public class AdvancedChestsHook {

    final ChestSortPlugin plugin;

    public AdvancedChestsHook(ChestSortPlugin plugin) {
        this.plugin = plugin;
        if(plugin.isHookAdvancedChests()){
            double version = Double.parseDouble(plugin.getServer().getPluginManager()
                    .getPlugin("AdvancedChests")
                    .getDescription().getVersion());
            if(version >= 20.3) {
                plugin.getLogger().info("Successfully hooked into AdvancedChests");
            }else plugin.setHookAdvancedChests(false);
        }
    }

    public boolean isAnAdvancedChest(Inventory inventory){
        return plugin.isHookAdvancedChests()
                && inventory != null
                && AdvancedChestsAPI.getInventoryManager().getAdvancedChest(inventory) != null;
    }

    public boolean handleAChestSortingIfPresent(Inventory inventory){
        if(!plugin.isHookAdvancedChests())return false;
        InteractiveInventory interactiveInventory = AdvancedChestsAPI.getInventoryManager().getInteractiveByBukkit(inventory);
        if(interactiveInventory != null) {
            if (interactiveInventory instanceof ChestPage) {
                plugin.getOrganizer().sortInventory(inventory, 0, inventory.getSize() - 10);
            }
            return true;
        }else {
            return false;
        }
    }

    public boolean handleAChestSortingIfPresent(Location location){
        if(!plugin.isHookAdvancedChests())return false;
        AdvancedChest chest = AdvancedChestsAPI.getChestManager().getAdvancedChest(location);
        if(chest != null){
            for (ChestPage page : chest.getPages()) {
                Inventory inventory = page.getBukkitInventory();
                plugin.getOrganizer().sortInventory(inventory,0,inventory.getSize()-10);
            }
            return true;
        }
        return false;
    }
}
