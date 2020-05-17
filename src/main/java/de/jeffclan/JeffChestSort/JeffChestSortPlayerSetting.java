package de.jeffclan.JeffChestSort;

import org.bukkit.inventory.Inventory;

public class JeffChestSortPlayerSetting {
	
	// Represents the information regarding a player
	// That includes:
	// - Does this player has sorting enabled?
	// - Did this player see the message on how to use ChestSort (message-when-using-chest in config.yml)

	// Sorting enabled for this player?
	boolean sortingEnabled;
	
	// Inventory sorting enabled for this player?
	boolean invSortingEnabled;
	
	// Hotkey settings
	boolean middleClick, shiftClick, doubleClick, shiftRightClick;
	
	Inventory guiInventory = null;

	// Did we already show the message how to activate sorting?
	boolean hasSeenMessage = false;

	JeffChestSortPlayerSetting(boolean sortingEnabled, boolean invSortingEnabled, boolean middleClick, boolean shiftClick, boolean doubleClick, boolean shiftRightClick) {
		this.sortingEnabled = sortingEnabled;
		this.middleClick = middleClick;
		this.shiftClick = shiftClick;
		this.doubleClick = doubleClick;
		this.shiftRightClick = shiftRightClick;
		this.invSortingEnabled = invSortingEnabled;
	}

}
