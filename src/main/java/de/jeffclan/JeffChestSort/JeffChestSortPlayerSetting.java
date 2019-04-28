package de.jeffclan.JeffChestSort;

public class JeffChestSortPlayerSetting {
	
	// Represents the information regarding a player
	// That includes:
	// - Does this player has sorting enabled?
	// - Did this player see the message on how to use ChestSort (message-when-using-chest in config.yml)

	// Sorting enabled for this player?
	boolean sortingEnabled;

	// Did we already show the message how to activate sorting?
	boolean hasSeenMessage = false;

	JeffChestSortPlayerSetting(boolean sortingEnabled) {
		this.sortingEnabled = sortingEnabled;
	}

}
