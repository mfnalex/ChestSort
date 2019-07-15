# ChestSort
1.8 to 1.14 compatible Minecraft-/Spigot-Plugin to allow automatic chest and inventory sorting.

## About
Tired of sorting your chests? Let's spend less time on organizing, and more on playing!

<p align="center"><img src="https://static.jeff-media.de/chestsort/chestsort-screen1.jpg" alt="Screenshot ChestSort" /></p>

<p align="center"><img src="https://static.jeff-media.de/chestsort/chestsort-screen2.jpg" alt="Screenshot ChestSort" /></p>

Tested Spigot versions: 1.8 to 1.14

## Download & more information
Please see the related topic at spigotmc.org for information regarding the commands, permissions and download links:

https://www.spigotmc.org/resources/1-13-chestsort.59773/

## Building .jar file
To build the .jar file, you will need maven. Also, the CrackShot library is in no public repository, so please create a directory called `lib` and put the latest CrackShot.jar file inside it. Now you can do `mvn install`

## Technical stuff
ChestSort takes an instance of org.bukkit.inventory.Inventory and copies the contents. The resulting array is sorted by rules defined in the config.yml. This takes far less than one millisecond for a whole chest. So there should be no problems even on big servers, where hundreds of players are using chests at the same time.
The plugin should cause no lag at all.
