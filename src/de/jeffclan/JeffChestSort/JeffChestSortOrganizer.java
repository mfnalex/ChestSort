package de.jeffclan.JeffChestSort;

import java.util.Arrays;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class JeffChestSortOrganizer {

    static String getSortableString(ItemStack item,String sortingMethod) {
        char blocksFirst;
        char itemsFirst;
        if(item.getType().isBlock()) {
        	blocksFirst='!';
        	itemsFirst='#';
        	//System.out.println(item.getType().name() + " is a block.");
        } else {
        	blocksFirst='#';
        	itemsFirst='!';
        	//System.out.println(item.getType().name() + " is an item.");
        }
        
        String typeName = item.getType().name();
        String hashCode = String.valueOf(item.hashCode());

        String sortableString = sortingMethod.replaceAll("\\{itemsFirst\\}", String.valueOf(itemsFirst));
        sortableString = sortableString.replaceAll("\\{blocksFirst\\}", String.valueOf(blocksFirst));
        sortableString = sortableString.replaceAll("\\{name\\}", "name:"+typeName);
        sortableString = sortableString + "," + hashCode;

        return sortableString;

    }

    static void sortInventory(Inventory inv,String sortingMethod) {
        ItemStack[] items = inv.getContents();
        inv.clear();
        String[] itemList = new String[inv.getSize()];

        int i = 0;
        for (ItemStack item : items) {
            if (item != null) {
                itemList[i] = getSortableString(item,sortingMethod);
                //System.out.println(itemList[i]);
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
            // System.out.println(s);
            for (ItemStack item : items) {
                if (item != null && s != null) {
                    if (item.hashCode() == Integer.parseInt(s.split(",")[s.split(",").length-1])) {
                        inv.addItem(item);
                        item = null;
                        s = null;
                    }
                }
            }
        }
    }

}