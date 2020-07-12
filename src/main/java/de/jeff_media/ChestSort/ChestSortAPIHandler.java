package de.jeff_media.ChestSort;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ChestSortAPIHandler implements de.jeff_media.ChestSortAPI.ChestSortAPI {
	
	final ChestSortPlugin plugin;
	
	ChestSortAPIHandler(ChestSortPlugin plugin) {
		this.plugin = plugin;
	}
	
	// Public API method to sort any given inventory
	@Override
	public void sortInventory(Inventory inv) {
		plugin.organizer.sortInventory(inv);
	}

	// Public API method to sort any given inventory inbetween startSlot and endSlot
	@Override
	public void sortInventory(Inventory inv, int startSlot, int endSlot) {
		plugin.organizer.sortInventory(inv, startSlot, endSlot);
	}
	
	// Public API method to check if player has automatic chest sorting enabled
	@Override
	public boolean sortingEnabled(Player p) {
		return plugin.isSortingEnabled(p);
	}

}
