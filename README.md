# ChestSort
1.8 to 1.14 compatible Minecraft-/Spigot-Plugin to allow automatic chest sorting.

## About
Tired of sorting your chests? Let's spend less time on organizing, and more on playing!

![Screenshot ChestSort](https://static.jeff-media.de/i/chestsortbeforeafter.jpg "Screenshot ChestSort")

ChestSort will automatically sort every chest after you have closed it. Every player can enable or disable this feature if desired with the simple command `/chestsort`. By default, sorting is disabled. If a player uses a chest for the first time after logging in, they will be shown a text on how to enable automatic chest sorting. Players need the "chestsort.use" permission to use the plugin.

Sorting will work with chests and shulker boxes.

Tested Spigot versions: 1.8 to 1.14

## Download & more information
Please see the related topic at spigotmc.org for information regarding the commands, permissions and download links:

https://www.spigotmc.org/resources/1-13-chestsort.59773/

## Building .jar file
To build the .jar file, you will need maven. Also, the CrackShot library is in no public repository, you please create a lib directory and put the latest CrackShot.jar inside it. Now you can do `mvn install`

## Technical stuff
ChestSort takes an instance of org.bukkit.inventory.Inventory and copies the contents. The resulting array is sorted by rules defined in the config.yml. This takes far less than one millisecond for a whole chest. So there should be no problems even on big servers, where hundreds of players are using chests at the same time.
The plugin should cause no lag at all.
