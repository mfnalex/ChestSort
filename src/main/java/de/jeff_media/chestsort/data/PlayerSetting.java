package de.jeff_media.chestsort.data;

import de.jeff_media.chestsort.ChestSortPlugin;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public class PlayerSetting {
	
	// Represents the information regarding a player
	// That includes:
	// - Does this player has sorting enabled?
	// - Did this player see the message on how to use ChestSort (message-when-using-chest in config.yml)

	// Sorting enabled for this player?
	public boolean sortingEnabled;
	
	// Inventory sorting enabled for this player?
	public boolean invSortingEnabled;
	
	// Hotkey settings
	public boolean middleClick;
	public boolean shiftClick;
	public boolean doubleClick;
	public boolean shiftRightClick;
	public boolean leftClick;
	public boolean rightClick;
	public boolean leftClickOutside;
	
	public Inventory guiInventory = null;

	// Did we already show the message how to activate sorting?
	public boolean hasSeenMessage = false;
	
	// Do we have to save these settings?
	public boolean changed;

	 DoubleClickType currentDoubleClick = DoubleClickType.NONE;

	public enum DoubleClickType {
		NONE, RIGHT_CLICK, LEFT_CLICK
	}

	public PlayerSetting(boolean sortingEnabled, boolean invSortingEnabled, boolean middleClick, boolean shiftClick, boolean doubleClick, boolean shiftRightClick, boolean leftClick, boolean rightClick, boolean leftCLickOutside, boolean changed) {
		this.sortingEnabled = sortingEnabled;
		this.middleClick = middleClick;
		this.shiftClick = shiftClick;
		this.doubleClick = doubleClick;
		this.shiftRightClick = shiftRightClick;
		this.invSortingEnabled = invSortingEnabled;
		this.leftClick = leftClick;
		this.rightClick = rightClick;
		this.leftClickOutside = leftCLickOutside;
		this.changed = changed;
	}

	public DoubleClickType getCurrentDoubleClick(ChestSortPlugin plugin, DoubleClickType click) {
		if(click == DoubleClickType.NONE) return DoubleClickType.NONE;
		if(currentDoubleClick == click) {
			currentDoubleClick = DoubleClickType.NONE;
			return click;
		}
		if(currentDoubleClick != click) {
			currentDoubleClick = click;
			Bukkit.getScheduler().runTaskLater(plugin, () -> currentDoubleClick = DoubleClickType.NONE, 10);
			return DoubleClickType.NONE;
		}
		return DoubleClickType.NONE;
	}
	
	public void toggleMiddleClick() {
		middleClick = !middleClick;
		changed = true;
	}
	public void toggleShiftClick() {
		shiftClick = !shiftClick;
		changed = true;
	}
	public void toggleDoubleClick() {
		doubleClick = !doubleClick;
		changed = true;
	}
	public void toggleShiftRightClick() {
		shiftRightClick = !shiftRightClick;
		changed = true;
	}
	public void toggleLeftClickOutside() {
		leftClickOutside = !leftClickOutside;
		changed = true;
	}
	public void toggleLeftClick() {
		leftClick = !leftClick;
		changed = true;
	}
	public void toggleRightClick() {
		rightClick = !rightClick;
		changed = true;
	}
	public void enableChestSorting() {
		sortingEnabled = true;
		changed = true;
	}
	public void disableChestSorting() {
		sortingEnabled = false;
		changed = true;
	}
	public void toggleChestSorting() {
		sortingEnabled = !sortingEnabled;
		changed = true;
	}
	public void enableInvSorting() {
		invSortingEnabled = true;
		changed = true;
	}
	public void disableInvSorting() {
		invSortingEnabled = false;
		changed = true;
	}
	public void toggleInvSorting() {
		invSortingEnabled = !invSortingEnabled;
		changed = true;
	}

}
