package de.jeffclan.JeffChestSort;

import java.util.Arrays;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class JeffChestSortOrganizer {
	
	static String getSortableString(ItemStack item) {
		return item.getType().name()
				+ ","
				+ String.valueOf(item.hashCode());
	}
	
	static void sortInventory(Inventory inv) {
		ItemStack[] items = inv.getContents();
		inv.clear();
		String[] itemList = new String[inv.getSize()];

		int i = 0;
		for (ItemStack item : items) {
			if (item != null) {
				itemList[i] = getSortableString(item);
				i++;
			}
		}

		// count all items that are not null
		int count = 0;
		for (String s : itemList) {
			if (s != null) {
				count++;
			}
		}

		// create new array with just the size we need
		String[] shortenedArray = new String[count];

		// fill new array with items
		for (int j = 0; j < count; j++) {
			shortenedArray[j] = itemList[j];
		}

		// sort array alphabetically
		Arrays.sort(shortenedArray);

		// put everything back in the inventory
		for (String s : shortenedArray) {
			for (ItemStack item : items) {
				if (item != null && s != null) {
					if (item.hashCode() == Integer.parseInt(s.split(",")[1])) {
						inv.addItem(item);
						item = null;
						s = null;
					}
				}
			}
		}
	}

}
