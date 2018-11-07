# API Usage

If you want to use ChestSort's advanced sorting features for your own plugin, you can use the ChestSort API. It provides a method to sort any given inventory, following the rules you have specified in your ChestSort's plugin.yml and the corresponding category files.

To use ChestSort's sorting features in your Spigot/Bukkit plugin, you have to import ChestSort.jar into your BuildPath.

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
