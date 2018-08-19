package de.jeffclan.JeffChestSort;

import java.util.Arrays;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class JeffChestSortOrganizer {

    static String getSortableString(ItemStack item,String sortingMethod) {
        int typeIsBlock = item.getType().isBlock() ? 1 : 0;
        int typeIsItem = item.getType().isItem() ? 1 : 0;
        String typeName = item.getType().name();
        String hashCode = String.valueOf(item.hashCode());
        short maxDurability = item.getType().getMaxDurability();

        String sortableString = sortingMethod.replaceAll("\\{typeIsBlock\\}", "typeIsBlock:"+String.valueOf(typeIsBlock));
        sortableString = sortableString.replaceAll("\\{typeIsItem\\}", "typeIsItem:"+typeIsItem);
        sortableString = sortableString.replaceAll("\\{typeName\\}", "typeName:"+typeName);
        sortableString = sortableString.replaceAll("\\{typeMaxDurability\\}", "maxDurability:"+String.format("%06d", maxDurability));
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
                System.out.println(itemList[i]);
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