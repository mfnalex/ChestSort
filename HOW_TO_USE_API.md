# API Usage

If you want to use ChestSort's advanced sorting features for your own plugin, or if ChestSort causes trouble with your own plugin, you can use the ChestSort API. It provides methods to sort any given inventory, following the rules you have specified in your ChestSort's plugin.yml and the corresponding category files, and a cancellable ChestSortEvent event that is fired whenever ChestSort is about to sort an inventory.

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
		<version>8.13.1</version> <!-- Check www.chestsort.de for latest version -->
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

### Making custom inventories sortable

If you create a new Inventory inside your plugin, you can use the `Sortable` class to tell ChestSort that your custom inventory should be sortable.

```java
Sortable holder = new Sortable();
Inventory inv = Bukkit.createInventory(holder, 9, "Example");
```

## Example Plugin

Here is a complete example plugin that shows to add and use the ChestSort API: [LINK](https://github.com/JEFF-Media-GbR/ChestSortAPIExample)
