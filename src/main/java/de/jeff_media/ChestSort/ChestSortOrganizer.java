package de.jeff_media.ChestSort;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;

import de.jeff_media.ChestSort.hooks.CrackShotHook;
import de.jeff_media.ChestSort.hooks.InventoryPagesHook;
import de.jeff_media.ChestSort.utils.CategoryLinePair;
import de.jeff_media.ChestSort.utils.TypeMatchPositionPair;

public class ChestSortOrganizer {

	// This is the heart of ChestSort!
	// All of the sorting stuff happens here.

	/*
	 * Thoughts before implementing: We create a string from each item that can be
	 * sorted. We will omit certain parts of the name and put them behind the main
	 * name for sorting reasons. E.g. ACACIA_LOG -> LOG_ACACIA (so all LOGs are
	 * grouped) Diamond, Gold, Iron, Stone, Wood does NOT have to be sorted, because
	 * they are already alphabetically in the right order
	 */

	ChestSortPlugin plugin;
	CrackShotHook crackShotHook;
	InventoryPagesHook inventoryPagesHook;
	
	private static final int maxInventorySize=54;
	private static final int playerInvStartSlot=9; // Inclusive
	private static final int playerInvEndSlot=35; // Inclusive

	// All available colors in the game. We will strip this from the item names and
	// keep the color in a separate variable
	static final String[] colors = { "white", "orange", "magenta", "light_blue", "light_gray", "yellow", "lime", "pink",
			"gray", "cyan", "purple", "blue", "brown", "green", "red", "black" };

	// The same applies for wood. We strip the wood name from the item name and keep
	// it in the above mentioned color variable
	static final String[] woodNames = { "acacia", "birch", "jungle", "oak", "spruce", "dark_oak" };

	private static final String emptyPlaceholderString = "~";

	// We store a list of all Category objects
	ArrayList<ChestSortCategory> categories = new ArrayList<ChestSortCategory>();
	ArrayList<String> stickyCategoryNames = new ArrayList<String>();

	ChestSortOrganizer(ChestSortPlugin plugin) {
		this.plugin = plugin;

		// Load Categories
		File categoriesFolder = new File(
				plugin.getDataFolder().getAbsolutePath() + File.separator + "categories" + File.separator);
		File[] listOfCategoryFiles = categoriesFolder.listFiles(new FilenameFilter() {
			public boolean accept(File directory, String fileName) {
				if (!fileName.endsWith(".txt")) {
					return false;
				}
				if (fileName.matches("(?i)^\\d\\d\\d.*\\.txt$")) // Category between 900 and 999-... are default
																	// categories
				{
					return true;
				}
				return false;
			}
		});
		for (File file : listOfCategoryFiles) {
			if (file.isFile()) {
				// Category name is the filename without .txt
				String categoryName = file.getName().replaceFirst(".txt", "");

				if (plugin.debug) {
					plugin.getLogger().info("Loading category file " + file.getName());
				}
				try {
					ChestSortCategory category = new ChestSortCategory(categoryName, loadCategoryFile(file));
					categories.add(category);
					if (plugin.debug) {
						plugin.getLogger().info("Loaded category file " + file.getName() + " ("
								+ category.typeMatches.length + " items)");
					}
				} catch (FileNotFoundException e) {
					plugin.getLogger().warning("Could not load category file: " + file.getName());
					e.printStackTrace();
				}
			}
		}
		
		// Make categories sticky
		for(String catName : stickyCategoryNames) {
			for(ChestSortCategory cat : categories) {
				if(catName.equalsIgnoreCase(cat.name)) {
					cat.setSticky();
				}
			}
		}
		
		crackShotHook = new CrackShotHook(plugin);
		inventoryPagesHook = new InventoryPagesHook(plugin);

	}

	// Returns an array with all typematches listed in the category file
	TypeMatchPositionPair[] loadCategoryFile(File file) throws FileNotFoundException {
		// This is called "sticky" in the category files. When it is enabled, it has the
		// same effect as when you set {keepCategoryOrder} in your sorting-method, but
		// you can set it per category
		boolean appendLineNumber = false;
		Scanner sc = new Scanner(file);
		List<TypeMatchPositionPair> lines = new ArrayList<TypeMatchPositionPair>();
		short currentLineNumber = 1;
		while (sc.hasNextLine()) {
			String currentLine = sc.nextLine();
			currentLine = currentLine.trim().replaceAll(" ", "");

			if (currentLine.contains("#")) {
				// the first #
				String[] split = currentLine.split("#");
				if (split.length > 0) {
					currentLine = split[0];
				} else {
					currentLine = null;
				}
			}
			if ("".equals(currentLine)) {
				currentLine = null;
			}
			if (currentLine != null) {
				if (currentLine.toLowerCase().startsWith("sticky=")) {

					if (currentLine.toLowerCase().endsWith("=true")) {
						appendLineNumber = true;
						makeCategoryStickyByFileName(file.getName());
						if (plugin.debug)
							plugin.getLogger().info("Sticky set to true in " + file.getName());
					}
				} else {
					if (currentLine != null) {
						lines.add(new TypeMatchPositionPair(currentLine, currentLineNumber, appendLineNumber));
						if (plugin.debug)
							plugin.getLogger().info("Added typeMatch to category file: " + currentLine);
					}
				}
			}
			currentLineNumber++;
		}
		TypeMatchPositionPair[] result = lines.toArray(new TypeMatchPositionPair[0]);
		sc.close();
		return result;
	}

	private void makeCategoryStickyByFileName(String name) {
		String catName = name.replaceAll("\\.txt$", "");
		
		stickyCategoryNames.add(catName);
		
		
	}

	// Convert the item name to what I call a "sortable item name".
	// Sorry, the method name is a bit misleading.
	// The array's [0] value contains the item name with a few fixes, see below
	// The array's [1] value contains the color or wood name of the item, or
	// "<none>"
	String[] getTypeAndColor(String typeName) {

		// [0] = Sortable Item name
		// [1] = Color/Wood

		String myColor = (plugin.debug) ? "~color~" : emptyPlaceholderString;

		// Only work with lowercase
		typeName = typeName.toLowerCase();

		// When a color occurs at the beginning (e.g. "white_wool"), we omit the color
		// so that the color will not
		// determine the beginning letters of the sortable item name
		for (String color : colors) {
			if (typeName.startsWith(color)) {
				typeName = typeName.replaceFirst(color + "_", "");
				myColor = color;
			}
		}
		// Same for wood, but the wood name can also be in the middle of the item name,
		// e.g. "stripped_oak_log"
		
		for (String woodName : woodNames) {
			if (typeName.equals(woodName + "_wood")) {
				typeName = "log_wood";
				myColor = woodName;
			} else if (typeName.startsWith(woodName)) {
				typeName = typeName.replaceFirst(woodName + "_", "");
				myColor = woodName;
			} else if (typeName.equals("stripped_" + woodName + "_log")) {
				// typeName = typeName.replaceFirst("stripped_"+woodName+"_", "stripped_");
				typeName = "log_stripped";
				myColor = woodName;
			} else if (typeName.equals("stripped_" + woodName + "_wood")) {
				typeName = "log_wood_stripped";
				myColor = woodName;
			}
		}
		// when typeName is exactly "log", change to "log_a" so it gets sorted before the stripped variants
		if(typeName.equals("log")) {
			typeName = "log_a";
		}

		// "egg" has to be put in front to group spawn eggs
		// e.g. cow_spawn_egg -> egg_cow_spawn
		if (typeName.endsWith("_egg")) {
			typeName = typeName.replaceFirst("_egg", "");
			typeName = "egg_" + typeName;
		}

		// polished_andesite -> andesite_polished
		if (typeName.startsWith("polished_")) {
			typeName = typeName.replaceFirst("polished_", "");
			typeName = typeName + "_polished";
		}

		// Group wet and dry sponges
		if (typeName.equalsIgnoreCase("wet_sponge")) {
			typeName = "sponge_wet";
		}

		// Group pumpkins and jack-o-lanterns / carved pumpkins
		if (typeName.equalsIgnoreCase("carved_pumpkin")) {
			typeName = "pumpkin_carved";
		}

		// Sort armor: helmet, chestplate, leggings, boots
		// We add a number to keep the armor in the "right" order
		if (typeName.endsWith("helmet")) {
			typeName = typeName.replaceFirst("helmet", "1_helmet");
		} else if (typeName.endsWith("chestplate")) {
			typeName = typeName.replaceFirst("chestplate", "2_chestplate");
		} else if (typeName.endsWith("leggings")) {
			typeName = typeName.replaceFirst("leggings", "3_leggings");
		} else if (typeName.endsWith("boots")) {
			typeName = typeName.replaceFirst("boots", "4_boots");
		}

		// Group horse armor
		if (typeName.endsWith("horse_armor")) {
			typeName = typeName.replaceFirst("_horse_armor", "");
			typeName = "horse_armor_" + typeName;
		}

		String[] typeAndColor = new String[2];
		typeAndColor[0] = typeName;
		typeAndColor[1] = myColor;

		return typeAndColor;
	}

	// This method takes a sortable item name and checks all categories for a match
	// If none, matches, return "<none>" (it will be put behind all categorized
	// items when sorting by category)
	CategoryLinePair getCategoryLinePair(String typeName) {
		typeName = typeName.toLowerCase();
		for (ChestSortCategory cat : categories) {
			short matchingLineNumber = cat.matches(typeName);
			if (matchingLineNumber != 0) {
				return new CategoryLinePair(cat.name, matchingLineNumber);
			}
		}
		return new CategoryLinePair((plugin.debug) ? "~category~" : emptyPlaceholderString, (short) 0);
	}

	// This puts together the sortable item name, the category, the color, and
	// whether the item is a block or a "regular item"
	String getSortableString(ItemStack item) {
		char blocksFirst;
		char itemsFirst;
		if (item.getType().isBlock()) {
			blocksFirst = '!'; // ! is before # in ASCII
			itemsFirst = '#';
		} else {
			blocksFirst = '#';
			itemsFirst = '!';
		}

		String[] typeAndColor = getTypeAndColor(item.getType().name());
		String typeName = typeAndColor[0];
		String color = typeAndColor[1];
		String potionEffect = ",";
		
		// Potions
		if (item.getItemMeta() != null) {
			ItemMeta meta = item.getItemMeta();
			if (meta instanceof PotionMeta) {
				PotionMeta potionMeta = (PotionMeta) meta;
				// Only continue if Method "getBasePotionData" exists
				Class<? extends PotionMeta> potionMetaClass = potionMeta.getClass();
				try {
					if (potionMetaClass.getDeclaredMethod("getBasePotionData", null) != null) {
						if (potionMeta.getBasePotionData() != null) {
							PotionData pdata = potionMeta.getBasePotionData();
							if (pdata != null && pdata.getType() != null && pdata.getType().getEffectType() != null) {
								potionEffect = "|" + pdata.getType().getEffectType().getName();
							}
						}
					}
				} catch (NoSuchMethodException | SecurityException e) {		}

				// potionEffects = potionEffects.substring(0, potionEffects.length()-1);
			}
		}

		String hookChangedName = item.getType().name();
		
		// CrackShot Support Start
		if(plugin.hookCrackShot) {
			if(crackShotHook.getCrackShotWeaponName(item)!=null) {
				typeName = plugin.getConfig().getString("hook-crackshot-prefix") + "_" + crackShotHook.getCrackShotWeaponName(item);
				color="";
				hookChangedName = typeName;
			}
		}
		// CrackShot Support End
		
		CategoryLinePair categoryLinePair = getCategoryLinePair(hookChangedName);
		String categoryName = categoryLinePair.getCategoryName();
		String categorySticky = categoryName;
		String lineNumber = getCategoryLinePair(hookChangedName).getFormattedPosition();
		if(stickyCategoryNames.contains(categoryName)) {
			categorySticky = categoryName+"~"+lineNumber;
		}
		
		String customName = (plugin.debug) ? "~customName~" : emptyPlaceholderString;
		if (item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName() != null) {
			customName = item.getItemMeta().getDisplayName();
		}
		String lore = (plugin.debug) ? "~lore~" : emptyPlaceholderString;
		if (item.getItemMeta().hasLore() && item.getItemMeta().getLore() != null
				&& item.getItemMeta().getLore().size() != 0) {
			String[] loreArray = item.getItemMeta().getLore().toArray(new String[0]);
			lore = String.join(",", loreArray);
		}
		
		// Put enchanted items before unenchanted ones
		typeName = typeName + String.format("%05d", 10000-getNumberOfEnchantments(item));

		// Generate the strings that finally are used for sorting.
		// They are generated according to the config.yml's sorting-method option
		String sortableString = plugin.sortingMethod.replaceAll(",", "|");
		sortableString = sortableString.replace("{itemsFirst}", String.valueOf(itemsFirst));
		sortableString = sortableString.replace("{blocksFirst}", String.valueOf(blocksFirst));
		sortableString = sortableString.replace("{name}", typeName+potionEffect);
		sortableString = sortableString.replace("{color}", color);
		sortableString = sortableString.replace("{category}", categorySticky);
		sortableString = sortableString.replace("{keepCategoryOrder}", lineNumber);
		sortableString = sortableString.replace("{customName}", customName);
		sortableString = sortableString.replace("{lore}", lore);

		return sortableString;

	}

	// Sort a complete inventory
	void sortInventory(Inventory inv) {
		sortInventory(inv, 0, inv.getSize() - 1);
	}

	// Sort an inventory only between startSlot and endSlot
	void sortInventory(Inventory inv, int startSlot, int endSlot) {
		
		if(inv.getLocation() != null) {
			ChestSortEvent chestSortEvent = new ChestSortEvent(inv.getLocation());
			Bukkit.getPluginManager().callEvent(chestSortEvent);
			if (chestSortEvent.isCancelled()) {
			    return;
			}
		}

		if (plugin.debug) {
			System.out.println(" ");
			System.out.println(" ");
		}
		
		ArrayList<Integer> unsortableSlots = new ArrayList<Integer>();

		// We copy the complete inventory into an array
		ItemStack[] items = inv.getContents();

		// Get rid of all stuff before startSlot...
		for (int i = 0; i < startSlot; i++) {
			items[i] = null;
		}
		// ... and after endSlot
		for (int i = endSlot + 1; i < inv.getSize(); i++) {
			items[i] = null;
		}
		// Get rid of all stuff that contains more than maxStackSize
		// We do not need this as ChestSort will keep the "overstacked" stacks intact
		/*for(int i = 0; i<endSlot; i++) {
			if(inv.getItem(i) != null && inv.getItem(i).getAmount() > inv.getItem(i).getMaxStackSize()) {
				//System.out.println("Debug: "+inv.getItem(i).getMaxStackSize());
				//items[i] = null;
				//unsortableSlots.add(i);
			}
		}*/
		// Do not move Minepacks' backpacks
		if(plugin.hookMinepacks) {
			for(int i = startSlot; i<= endSlot; i++) {
				if(plugin.listener.minepacksHook.isMinepacksBackpack(items[i])) {
					items[i] = null;
					unsortableSlots.add(i);
				}
			}
		}
		// If InventoryPages is installed: get rid of the buttons
		if(plugin.hookInventoryPages) {
			for(int i = startSlot; i<= endSlot; i++) {
				if(inventoryPagesHook.isButton(items[i], i,inv)) {
					//System.out.println("Inventory Pages Button found at slot " + i);
					items[i] = null;
					unsortableSlots.add(i);
				}
			}
		}
		// Do not move ItemStacks with more than 64 items
		for(int i = startSlot; i<= endSlot; i++) {
			if(isOversizedStack(items[i])) {
				items[i] = null;
				unsortableSlots.add(i);
			}
		}

		// Remove the stuff from the original inventory
		for (int i = startSlot; i <= endSlot; i++) {
			if(!unsortableSlots.contains(i))
			{
				inv.clear(i);
			}
		}

		// We don't want to have stacks of null, so we create a new ArrayList and put in
		// everything != null
		ArrayList<ItemStack> nonNullItemsList = new ArrayList<ItemStack>();
		for (ItemStack item : items) {
			if (item != null) {
				nonNullItemsList.add(item);
			}
		}

		// We no longer need the original array that includes all the null-stacks
		items = null;

		// We need the new list as array. So why did'nt we take an array from the
		// beginning?
		// Because I did not bother to count the number of non-null items beforehand.
		// TODO: Feel free to make a Pull request if you want to save your server a few
		// nanoseconds :)
		ItemStack[] nonNullItems = nonNullItemsList.toArray(new ItemStack[nonNullItemsList.size()]);

		// Sort the array with ItemStacks according to each ItemStacks' sortable String
		Arrays.sort(nonNullItems, new Comparator<ItemStack>() {
			@Override
			public int compare(ItemStack s1, ItemStack s2) {
				return (getSortableString(s1).compareTo(getSortableString(s2)));
			}
		});

		// Now, we put everything back in a temporary inventory to combine ItemStacks
		// even when using strict slot sorting
		// Thanks to SnackMix for this idea!
		// Without doing this, it would not be possible to sort an inventory with a
		// startSlot other than 0,
		// because Spigot's add(ItemStack...) method will always to store the ItemStack
		// in the first possible slot

		// Create the temporary inventory with a null holder. 54 slots is enough for
		// every inventory
		Inventory tempInventory = Bukkit.createInventory(null, maxInventorySize); // cannot be bigger than 54 as of 1.14

		for (ItemStack item : nonNullItems) {
			if (plugin.debug)
				System.out.println(getSortableString(item));
			// Add the item to the temporary inventory
			tempInventory.addItem(item);
		}

		// Now, we iterate through all slots between startSlot and endSlot in the
		// original inventory
		// and set those to whatever the temporary inventory contains
		// Since we already deleted all those slots, there is no chance for item
		// duplication
		int currentSlot = startSlot;
		for (ItemStack item : tempInventory.getContents()) {
			// Ignore null ItemStacks. TODO: Actually, we could skip the for-loop here
			// because
			// our temporary inventory was already sorted. Feel free to make a pull request
			// to
			// save your server half a nanosecond :)
			if (item != null)
			{

				while(unsortableSlots.contains(currentSlot) && currentSlot < endSlot) {
					currentSlot++;
				}
				inv.setItem(currentSlot, item);
			}
			currentSlot++;
		}
		

	}
	
	static int getNumberOfEnchantments(ItemStack is) {
		
		int totalEnchants = 0;
		
		//if(!is.getItemMeta().hasEnchants()) return 0;
		if(is.getItemMeta() instanceof EnchantmentStorageMeta) {
			EnchantmentStorageMeta storageMeta = (EnchantmentStorageMeta) is.getItemMeta();
			Map<Enchantment,Integer> storedEnchants = storageMeta.getStoredEnchants();
			for(int level : storedEnchants.values()) {
				totalEnchants += level;
			}
		}
		
		Map<Enchantment,Integer> enchants= is.getItemMeta().getEnchants();
			
		for(int level : enchants.values()) {
			totalEnchants += level;
		}
		return totalEnchants;
	}
	
	public void updateInventoryView(InventoryClickEvent event) {
		for(HumanEntity viewer : event.getViewers()) {
			if(viewer instanceof Player) {
				Player playerViewer = (Player) viewer;
				playerViewer.updateInventory();
			}
		}
	}
	
	public void updateInventoryView(Inventory inventory) {
		for(HumanEntity viewer : inventory.getViewers()) {
			if(viewer instanceof Player) {
				Player playerViewer = (Player) viewer;
				playerViewer.updateInventory();
			}
		}
	}
	
	public boolean isOversizedStack(ItemStack item) {
		if(item!=null && item.getAmount()>64) return true;
		return false;
	}
	
	public void stuffInventoryIntoAnother(Inventory source, Inventory destination,Inventory origSource) {
		
		Material placeholderMaterial = Material.DIRT;
		ItemStack[] hotbarStuff = new ItemStack[9];
		boolean destinationIsPlayerInventory = true;
		if(destination.getHolder()==null || !(destination.getHolder() instanceof Player) || destination.getType() != InventoryType.PLAYER) {
			destinationIsPlayerInventory = false;
		}
		if(destinationIsPlayerInventory) {
		for(int i = 0; i<9;i++) {
			hotbarStuff[i] = destination.getItem(i);
			destination.setItem(i, new ItemStack(placeholderMaterial,64));
		}
		}
		
		ArrayList<ItemStack> leftovers = new ArrayList<ItemStack>();
		
		for(int i = 0;i<source.getSize();i++) {
			
			ItemStack current = source.getItem(i);
			if(current == null) continue;
			if(isOversizedStack(current)) continue;
			source.clear(i);
			HashMap<Integer,ItemStack> currentLeftovers = destination.addItem(current);
			
			
			for(ItemStack currentLeftover : currentLeftovers.values()) {
				leftovers.add(currentLeftover);
			}
		}
		
		origSource.addItem(leftovers.toArray(new ItemStack[leftovers.size()]));
		if(destinationIsPlayerInventory) { for(int i=0;i<9;i++) {
			destination.setItem(i, hotbarStuff[i]);
		}
		}
		updateInventoryView(destination);
		updateInventoryView(source);
		
	}

	public void stuffPlayerInventoryIntoAnother(PlayerInventory source,
			Inventory destination) {
		boolean destinationIsShulkerBox = destination.getType().name().equalsIgnoreCase("SHULKER_BOX");
		Inventory temp = Bukkit.createInventory(null, maxInventorySize);
		for(int i = playerInvStartSlot;i<=playerInvEndSlot;i++) {
			if(source.getItem(i)==null) continue;
			
			if(plugin.hookMinepacks && plugin.listener.minepacksHook.isMinepacksBackpack(destination)
					&& plugin.listener.minepacksHook.isMinepacksBackpack(source.getItem(i))) continue;
			
			if(plugin.hookInventoryPages
					&& plugin.organizer.inventoryPagesHook.isButton(source.getItem(i), i, source)) continue;
			
			if(destinationIsShulkerBox && source.getItem(i).getType().name().endsWith("SHULKER_BOX")) continue;
			
			if(isOversizedStack(source.getItem(i))) continue;
			
			temp.addItem(source.getItem(i));
			source.clear(i);
		}
		stuffInventoryIntoAnother(temp,destination,source);
	}

}