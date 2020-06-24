package de.jeff_media.ChestSort;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ChestSortAPI {
	
	final ChestSortPlugin plugin;
	
	ChestSortAPI(ChestSortPlugin plugin) {
		this.plugin = plugin;
	}
	
	// Public API method to sort any given inventory
	public void sortInventory(Inventory inv) {
		plugin.organizer.sortInventory(inv);
	}

	// Public API method to sort any given inventory inbetween startSlot and endSlot
	public void sortInventory(Inventory inv, int startSlot, int endSlot) {
		plugin.organizer.sortInventory(inv, startSlot, endSlot);
	}
	
	// Public API method to check if player has automatic chest sorting enabled
	public boolean sortingEnabled(Player p) {
		return plugin.isSortingEnabled(p);
	}

}
