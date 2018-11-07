# API Usage
You have to import ChestSort.jar into your BuildPath.

Then you can access it via the plugin manager:

```
JeffChestSortPlugin chestSort = (JeffChestSortPlugin) getServer().getPluginManager().getPlugin("ChestSort");
		
if(chestSort==null || !(chestSort instanceof JeffChestSortPlugin)) {
	getLogger().warning("ChestSort plugin not found.");
}
```

Now, you can sort any Inventory! Just like this:

```
Inventory inv = ...
chestSort.sortInventory(inv);
```
