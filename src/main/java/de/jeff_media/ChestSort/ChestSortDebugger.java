package de.jeff_media.ChestSort;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public class ChestSortDebugger implements @NotNull Listener {
	
	private final ChestSortPlugin plugin;

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
			if(e.getInventory().getHolder()!=null) {
				System.out.println("- Holder class: "+e.getInventory().getHolder().getClass());
			}
			System.out.println("- Slot: "+e.getRawSlot());
			System.out.println("- Left-Click: "+e.isLeftClick());
			System.out.println("- Right-Click: "+e.isRightClick());
			System.out.println("- Shift-Click: "+e.isShiftClick());
			System.out.println(" ");
		}
	}

}
