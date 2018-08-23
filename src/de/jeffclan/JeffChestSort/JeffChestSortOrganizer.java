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

	JeffChestSortPlugin plugin;

	String[] colors = { "white", "orange", "magenta", "light_blue", "light_gray", "yellow", "lime", "pink", "gray",
			"cyan", "purple", "blue", "brown", "green", "red", "black" };
	String[] woodNames = { "acacia", "birch", "jungle", "oak", "spruce", "dark_oak" };  

	ArrayList<JeffChestSortCategory> categories = new ArrayList<JeffChestSortCategory>();

	String[] woodBlocks = { "_log", "_wood", "_planks", "acacia_", "oak_", "birch_", "jungle_", "dark_oak_",
			"spruce_" };
	String[] redstone = { "dispenser","note_block","sticky_piston","piston","tnt","lever","_pressure_plate","redstone", "_button","tripwire","trapped_chest","daylight_detector","hopper","dropper","observer","iron_trapdoor","iron_door","repeater","comparator","powered_rail","detector_rail","rail","activator_rail","minecart" };
	String[] combat = { "turtle_helmet","bow","arrow","_sword","_helmet","_chestplate","_leggings","_boots","spectral_arrow","tipped_arrow","shield","totem_of_undying","trident"};

	String[] plants = { "_sapling","_leaves","grass","fern","dead_bush","seagrass","sea_pickle","dandelion","poppy","blue_orchid","allium","azure_bluet","red_tulip","orange_tulip","white_tulip","pink_tulip","oxeye_daisy","brown_mushroom","red_mushroom","chorus_plant","chorus_flower","cactus","brown_mushroom_block","red_mushroom_block","mushroom_stem","vine","lily_pad","sunflower","lilac","rose_bush","peony","tall_grass","large_fern","_coral","flower_pot"};
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
			if(!sc.nextLine().startsWith("#")) {
		  lines.add(sc.nextLine());
			}
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
			if(typeName.startsWith(woodName)) {
				typeName = typeName.replaceFirst(woodName+"_", "");
				myColor = woodName;
			}
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
			if (plugin.debug)
				System.out.println(item.getType().name() + " is a block.");
		} else {
			blocksFirst = '#';
			itemsFirst = '!';
			if (plugin.debug)
				System.out.println(item.getType().name() + " is an item.");
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