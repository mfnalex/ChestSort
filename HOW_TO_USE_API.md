# API Usage

If you want to use ChestSort's advanced sorting features for your own plugin, or if ChestSort causes trouble with your own plugin, you can use the ChestSortAPI. It provides
- the methods `sortInventory(Inventory inv)` and `sortInventory(Inventory inv, int startSlot, int endSlot)`to sort any given inventory, following the rules you have specified in your ChestSort's config.yml and the corresponding category files
- a cancellable event called `ChestSortEvent` that is fired whenever ChestSort is about to sort an inventory, that can also be used to change the sorting results
- the methods `setSortable(Inventory inv)` and `setUnsortable(Inventory inv)` to tell ChestSort whether your custom inventories should be sortable by ChestSort

and more, see the Javadocs at the bottom of this page. There's also a link to an example plugin.

## Maven repository
You can use maven to add ChestSort as a dependency to your Spigot-/Bukkit-Plugin:

```xml
<repositories>
	<repository>
		<id>jeff-media-repo</id>
		<url>https://repo.jeff-media.de/maven2</url>
	</repository>
</repositories>
<dependencies>
	<dependency>
		<groupId>de.jeff_media</groupId>
		<artifactId>ChestSortAPI</artifactId>
		<version>11.0.0-SNAPSHOT</version>
        	<scope>compile</scope>
	</dependency>
</dependencies>
```

You must also add ChestSort to the `depend` or `softdepend` section of your `plugin.yml`.

Note: it is no longer required **nor allowed** to shade the ChestSortAPI into your plugin.

## Accessing the API
### As `depend`
If you depend on ChestSort, you can easily access the API methods directly:

```java
import de.jeff_media.chestsort.api.ChestSortAPI;
...
ChestSortAPI.sortInventory(player.getInventory());
```

### As `softdepend`
If you only softdepend on ChestSort, you have to check whether ChestSort is installed. To avoid exceptions, do not import the ChestSortAPI class
in classes that you instantiate regardless of whether ChestSort is installed, but use qualified method calls instead:

```java
if(Bukkit.getPluginManager().getPlugin("ChestSort") != null) {
    de.jeff_media.chestsort.api.ChestSortAPI.sortInventory(player.getInventory());    
}
```

Your Listener has to import the ChestSortEvent class, so only register it when ChestSort is installed:
```java
if(Bukkit.getPluginManager().getPlugin("ChestSort") != null) {
    Bukkit.getPluginManager().registerEvents(new MyListener(), yourPlugin);    
}
```

### Sorting inventories

Now, you can sort any Inventory! Just like this:

```java
ChestSortAPI.sortInventory(Inventory inventory);
```

To sort only specific slots, you can pass slot numbers where to start and end sorting. ChestSort will not modify the inventory outside the given slot range.

```java
ChestSortAPI.sortInventory(Inventory inventory, int startSlot, int endSlot);
```

You can also check if a player has automatic sorting enabled or disabled:

```java
boolean sortingEnabled = ChestSortAPI.hasSortingEnabled(Player player);
```

### Custom ChestSort event

If you want to prevent ChestSort from sorting a certain inventory, you can listen to the ChestSortEvent event.

```java
@EventHandler
public void onChestSortEvent(ChestSortEvent event) {
	if(event.getInventory() == whatever) {
		event.setCancelled(true);
	}
}
```

You can also exempt certain slots / ItemStacks from being sorted using the following methods:

```java
    public void setUnmovable(int slot)

    public void setUnmovable(ItemStack itemStack)

    public void removeUnmovable(int slot)

    public void removeUnmovable(ItemStack itemStack)

    public boolean isUnmovable(int slot)

    public boolean isUnmovable(ItemStack itemStack)
```

For example, to avoid the first item in the player's hotbar from being sorted:

```java
@EventHandler
public void onChestSortEvent(ChestSortEvent event) {
	event.setUnmovable(0);
}
```

### Making custom inventories sortable / unsortable

If you create a new Inventory inside your plugin, you can tell ChestSort whether that inventory should be sortable by ChestSort:

```java
// Make it sortable
ChestSortAPI.setSortable(myInventory);

// or make it not sortable
ChestSortAPI.setUnsortable(myInventory);
```

## Example Plugin


## Javadocs, Source code & Discord
There are more methods you can use, just have a look at the Javadocs.
- [ChestSortAPI Javadocs](https://repo.jeff-media.de/javadocs/ChestSortAPI).
- [ChestSortAPI source code](https://github.com/JEFF-Media-GbR/Spigot-ChestSortAPI).

Here is a complete example plugin that shows to add and use the ChestSort API: [LINK](https://github.com/JEFF-Media-GbR/ChestSortAPIExample)

If you need help using the API, feel free to join my Discord server:

[![Join my Discord](https://api.jeff-media.de/img/discord1.png)](https://discord.jeff-media.de)