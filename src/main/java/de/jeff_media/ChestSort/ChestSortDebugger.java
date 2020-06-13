package de.jeff_media.ChestSort;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public class ChestSortDebugger implements @NotNull Listener {
	
	private ChestSortPlugin plugin;

	ChestSortDebugger(ChestSortPlugin plugin) {
		plugin.getLogger().warning("=======================================");
		plugin.getLogger().warning("    CHESTSORT DEBUG MODE ACTIVATED!");
		plugin.getLogger().warning("Only use this for development purposes!");
		plugin.getLogger().warning("=======================================");
		this.plugin=plugin;
	}
	
	@EventHandler
	public void onInventoryClickEvent(InventoryClickEvent e) {
		// Debug
		if(plugin.debug) {
			System.out.println(" ");
			System.out.println("InventoryClickEvent:");
			System.out.println("- Holder: " + e.getInventory().getHolder());
			//System.out.println("- Holder (Class): "+e.getInventory().getHolder().getClass().getName());
			System.out.println("- Slot: "+e.getRawSlot());
			System.out.println(" ");
		}
	}

}
