# API Usage

If you want to use ChestSort's advanced sorting features for your own plugin, or if ChestSort causes trouble with your own plugin, you can use the ChestSort API. It provides methods to sort any given inventory, following the rules you have specified in your ChestSort's plugin.yml and the corresponding category files, and a cancellable ChestSortEvent event that is fired whenever ChestSort is about to sort an inventory.

## Maven repository
You can use maven to add ChestSort as a dependency to your Spigot-/Bukkit-Plugin:

```
<repositories>
	<repository>
		<id>jeff-media-repo</id>
		<url>https://repo.jeff-media.de/maven2</url>
	</repository>
</repositories>
<dependencies>
	<dependency>
		<groupId>de.jeff_media</groupId>
		<artifactId>ChestSort</artifactId>
		<version>8.12.0</version> <!-- Check www.chestsort.de for latest version -->
        	<scope>provided</scope>
	</dependency>
</dependencies>
```

## Accessing the API
Then you can access it via the plugin manager:

```
ChestSortPlugin chestSort = (ChestSortPlugin) getServer().getPluginManager().getPlugin("ChestSort");
if(chestSort==null || !(chestSort instanceof ChestSortPlugin)) {
	getLogger().severe("Error: ChestSort is not installed.");
	return;
}
	
ChestSortAPI chestSortAPI = chestSort.getAPI();
```

### Sorting inventories

Now, you can sort any Inventory! Just like this:

```
chestSortAPI.sortInventory(Inventory inventory);
```

To sort only specific slots, you can pass slot numbers where to start and end sorting. ChestSort will not modify the inventory outside the given slot range.

```
chestSortAPI.sortInventory(Inventory inventory, int startSlot, int endSlot);
```

You can also check if a player has automatic sorting enabled or disabled:

```
boolean sortingEnabled = chestSortAPI.sortingEnabled(Player player);
```

### Custom ChestSort event

If you want to prevent ChestSort from sorting a certain inventory, you can listen to the ChestSortEvent event.

```
@EventHandler
public void onChestSortEvent(ChestSortEvent event) {
	if(event.getInventory() == whatever) {
		event.setCancelled(true);
	}
}
```

## Example Plugin

Here is a complete example plugin that shows to add and use the ChestSort API: [LINK](https://github.com/JEFF-Media-GbR/ChestSortAPIExample)
