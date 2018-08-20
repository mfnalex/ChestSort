package de.jeffclan.JeffChestSort;

import java.util.Arrays;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class JeffChestSortOrganizer {
	
	JeffChestSortPlugin plugin;
	
	
	String[] colors = {"white","orange","magenta","light_blue","light_gray","yellow","lime","pink","gray","cyan","purple","blue","brown","green","red","black"};
	String[] tools = {"pickaxe","axe","shovel","hoe","flint_and_steel"};
	String[] loot = {"rotten_flesh","string","spider_eye"};
	
	
			
	
	public JeffChestSortOrganizer(JeffChestSortPlugin plugin) {
		this.plugin = plugin;
	}
	
	String[] getTypeAndColor(String typeName) {
		
		String myColor = "<none>";
		typeName = typeName.toLowerCase();
		
		
		for(String color : colors) {
			if(typeName.startsWith(color)) {
				typeName = typeName.replaceFirst(color + "_", "");
				myColor = color;
			}
		}
		
		// Wool (sort by color)
		/*if(typeName.endsWith("_wool")) {
			
			typeName = typeName.replaceFirst("_wool", "");
			return "wool_" + typeName;
			}
		}*/
		
		String[] typeAndColor = new String[2];
		typeAndColor[0] = typeName;
		typeAndColor[1] = myColor;
		
		return typeAndColor;
	}
	
	String getCategory(String typeName) {
		
		typeName = typeName.toLowerCase();
		
		for (String pattern : tools) {
			if(typeName.contains(pattern)) {
				return "tools";
			}
		}
		for(String pattern : loot) {
			if(typeName.contains(pattern)) {
				return "loot";
			}
		}
		return "<none>";
	}

    String getSortableString(ItemStack item) {
        char blocksFirst;
        char itemsFirst;
        if(item.getType().isBlock()) {
        	blocksFirst='!';
        	itemsFirst='#';
        	if(plugin.debug) System.out.println(item.getType().name() + " is a block.");
        } else {
        	blocksFirst='#';
        	itemsFirst='!';
        	if(plugin.debug) System.out.println(item.getType().name() + " is an item.");
        }
        
        String[] typeAndColor = getTypeAndColor(item.getType().name());
        String typeName = typeAndColor[0];
        String color = typeAndColor[1];
        String category = getCategory(item.getType().name());
        
        String hashCode = String.valueOf(item.hashCode());

        String sortableString = plugin.sortingMethod.replaceAll("\\{itemsFirst\\}", String.valueOf(itemsFirst));
        sortableString = sortableString.replaceAll("\\{blocksFirst\\}", String.valueOf(blocksFirst));
        sortableString = sortableString.replaceAll("\\{name\\}", typeName);
        sortableString = sortableString.replaceAll("\\{color\\}", color);
        sortableString = sortableString.replaceAll("\\{category\\}", category);
        sortableString = sortableString + "," + hashCode;

        return sortableString;

    }

    void sortInventory(Inventory inv) {
        ItemStack[] items = inv.getContents();
        inv.clear();
        String[] itemList = new String[inv.getSize()];

        int i = 0;
        for (ItemStack item : items) {
            if (item != null) {
                itemList[i] = getSortableString(item);
                if(plugin.debug) System.out.println(itemList[i]);
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