package de.jeffclan.JeffChestSort;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class JeffChestSortOrganizer {
	
	/*
	 * Thoughts before implementing:
	 * We create a string from each item that can be sorted.
	 * We will omit certain parts of the name and put them behind the main name for sorting reasons.
	 * E.g. ACACIA_LOG -> LOG_ACACIA (so all LOGs are grouped)
	 * Diamond, Gold, Iron, Stone, Wood does NOT have to be sorted, because they are already alphabetically in the right order
	 */

	JeffChestSortPlugin plugin;

	static final String[] colors = { "white", "orange", "magenta", "light_blue", "light_gray", "yellow", "lime", "pink", "gray",
			"cyan", "purple", "blue", "brown", "green", "red", "black" };
	static final String[] woodNames = { "acacia", "birch", "jungle", "oak", "spruce", "dark_oak" };  

	ArrayList<JeffChestSortCategory> categories = new ArrayList<JeffChestSortCategory>();

	public JeffChestSortOrganizer(JeffChestSortPlugin plugin) {
		this.plugin = plugin;

	
		// Load Categories
		File categoriesFolder = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "categories" + File.separator);
		File[] listOfCategoryFiles = categoriesFolder.listFiles();

		for (File file : listOfCategoryFiles) {
			if (file.isFile()) {
				String categoryName = file.getName().replaceFirst(".txt", "");
				
				try {
					categories.add(new JeffChestSortCategory(categoryName,getArrayFromCategoryFile(file)));
					plugin.getLogger().info("Loaded category file "+file.getName());
				} catch (FileNotFoundException e) {
					plugin.getLogger().warning("Could not load category file: "+file.getName());
					//e.printStackTrace();
				}
			}
		}

	}
	
	String[] getArrayFromCategoryFile(File file) throws FileNotFoundException {
		Scanner sc = new Scanner(file);
		List<String> lines = new ArrayList<String>();
		while (sc.hasNextLine()) {
			//if(!sc.nextLine().startsWith("#")) {
		  lines.add(sc.nextLine());
			//}
		}

		String[] arr = lines.toArray(new String[0]);
		sc.close();
		return arr;
	}
	

	String[] getTypeAndColor(String typeName) {
		
		// [0] = TypeName
		// [1] = Color

		String myColor = "<none>";
		typeName = typeName.toLowerCase();

		for (String color : colors) {
			if (typeName.startsWith(color)) {
				typeName = typeName.replaceFirst(color + "_", "");
				myColor = color;
			}
		}
		
		for(String woodName : woodNames) {
			if(typeName.equals(woodName+"_wood")) {
				typeName = "log_wood";
				myColor = woodName;
			}
			else if(typeName.startsWith(woodName)) {
				typeName = typeName.replaceFirst(woodName+"_", "");
				myColor = woodName;
			}
			else if(typeName.equals("stripped_"+woodName+"_log")) {
				//typeName = typeName.replaceFirst("stripped_"+woodName+"_", "stripped_");
				typeName = "log_stripped";
				myColor = woodName;
			} else if(typeName.equals("stripped_"+woodName+"_wood")) {
				typeName = "log_wood_stripped";
				myColor = woodName;
			}
		}
		
		// Egg has to be put in front to group spawn eggs
		// E.g. cow_spawn_egg -> egg_cow_spawn
		if(typeName.endsWith("_egg")) {
			typeName = typeName.replaceFirst("_egg", "");
			typeName = "egg_" + typeName;
		}
		
		// Sort armor: helmet, chestplate, leggings, boots
		if(typeName.endsWith("helmet")) {
			typeName = typeName.replaceFirst("helmet", "1_helmet");
		} else if(typeName.endsWith("chestplate")) {
			typeName = typeName.replaceFirst("chestplate", "2_chestplate");
		} else if(typeName.endsWith("leggings")) {
			typeName = typeName.replaceFirst("leggings", "3_leggings");
		} else if(typeName.endsWith("boots")) {
			typeName = typeName.replaceFirst("boots", "4_boots");
		}
		
		// Group horse armor
		if(typeName.endsWith("horse_armor")) {
			typeName = typeName.replaceFirst("_horse_armor", "");
			typeName = "horse_armor_" + typeName;
		}

		// Wool (sort by color)
		/*
		 * if(typeName.endsWith("_wool")) {
		 * 
		 * typeName = typeName.replaceFirst("_wool", ""); return "wool_" + typeName; } }
		 */

		String[] typeAndColor = new String[2];
		typeAndColor[0] = typeName;
		typeAndColor[1] = myColor;

		return typeAndColor;
	}

	String getCategory(String typeName) {

		typeName = typeName.toLowerCase();

		for (JeffChestSortCategory cat : categories) {
			if (cat.matches(typeName)) {
				return cat.name;
			}
		}

		return "<none>";
	}

	String getSortableString(ItemStack item) {
		char blocksFirst;
		char itemsFirst;
		if (item.getType().isBlock()) {
			blocksFirst = '!';
			itemsFirst = '#';
		} else {
			blocksFirst = '#';
			itemsFirst = '!';
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
		if(plugin.debug) {
			System.out.println(" ");
			System.out.println(" ");
		}
		ItemStack[] items = inv.getContents();
		inv.clear();
		String[] itemList = new String[inv.getSize()];

		int i = 0;
		for (ItemStack item : items) {
			if (item != null) {
				itemList[i] = getSortableString(item);
				if (plugin.debug)
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
					if (item.hashCode() == Integer.parseInt(s.split(",")[s.split(",").length - 1])) {
						inv.addItem(item);
						item = null;
						s = null;
					}
				}
			}
		}
	}

}