package de.jeffclan.JeffChestSort;

public class JeffChestSortPlayerSetting {

	// Sorting enabled for this player?
	boolean sortingEnabled;

	// Did we already show the message how to activate sorting?
	boolean hasSeenMessage = false;

	JeffChestSortPlayerSetting(boolean sortingEnabled) {
		this.sortingEnabled = sortingEnabled;
	}

}
