# API Usage

### Note: The API documentation is currently outdated, I will update them in the upcoming days - please look at the source code instead, it's really easy to understand. Sorry for the trouble. If you need assistance on using the API, feel free to contact me on Discord at https://discord.jeff-media.de

If you want to use ChestSort's advanced sorting features for your own plugin, or if ChestSort causes trouble with your own plugin, you can use the ChestSort API. It provides
- the methods `sortInventory(Inventory inv)` and `sortInventory(Inventory inv, int startSlot, int endSlot)`to sort any given inventory, following the rules you have specified in your ChestSort's plugin.yml and the corresponding category files
- a cancellable event called `ChestSortEvent` that is fired whenever ChestSort is about to sort an inventory
- a custom InventoryHolder called `Sortable` that you can use when creating inventories to tell ChestSort that this inventory should be sortable

and more.

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
		<version>3.0.0</version> <!-- The API version is independent of the ChestSort version -->
        	<scope>compile</scope>
	</dependency>
</dependencies>
```

If you use the `Sortable`class or the `ISortable` interface, you must also shade the ChestSortAPI into your plugin:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-shade-plugin</artifactId>
            <version>3.1.0</version>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>shade</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
``` 

## Accessing the API
Then you can access the API via the plugin manager:

```java
ChestSort chestSort = (ChestSort) getServer().getPluginManager().getPlugin("ChestSort");
if(chestSort==null) {
	getLogger().severe("Error: ChestSort is not installed.");
	return;
}
	
ChestSortAPI chestSortAPI = chestSort.getAPI();
```

### Sorting inventories

Now, you can sort any Inventory! Just like this:

```java
chestSortAPI.sortInventory(Inventory inventory);
```

To sort only specific slots, you can pass slot numbers where to start and end sorting. ChestSort will not modify the inventory outside the given slot range.

```java
chestSortAPI.sortInventory(Inventory inventory, int startSlot, int endSlot);
```

You can also check if a player has automatic sorting enabled or disabled:

```java
boolean sortingEnabled = chestSortAPI.sortingEnabled(Player player);
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

### Making custom inventories sortable

If you create a new Inventory inside your plugin, you can use the `Sortable` class to tell ChestSort that your custom inventory should be sortable.

```java
Inventory inv = Bukkit.createInventory(new Sortable(), 9, "Example");
```

You can also store another InventoryHolder in the Inventory if needed:

```java
Sortable holder = new Sortable(player)
```

You can also instead use your own custom inventory holder that either `implements ISortable` or `extends Sortable`.

## Example Plugin

Here is a complete example plugin that shows to add and use the ChestSort API: [LINK](https://github.com/JEFF-Media-GbR/ChestSortAPIExample) (OUTDATED: The example plugin still uses API version 2.0.0)

## Javadocs & Source code
- [ChestSortAPI Javadocs](https://repo.jeff-media.de/javadocs/ChestSortAPI). (OUTDATED: The JavaDocs are still on version 2.0.0, I will update it in the next days, sorry for the trouble - contact me on Discord at https://discord.jeffm-media.de if you need assistance)
- [ChestSortAPI source code](https://github.com/JEFF-Media-GbR/Spigot-ChestSortAPI).
