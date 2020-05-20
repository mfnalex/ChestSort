# ChestSort
1.8.x to 1.15.x compatible Minecraft-/Spigot-Plugin to allow automatic chest and inventory sorting.

## Download & more information
Please see the related topic at spigotmc.org for information regarding the commands, permissions and download links:

https://www.spigotmc.org/resources/1-13-chestsort.59773/

## Building .jar file
To build the .jar file, you will need maven. Also, the CrackShot library is in no public repository, so please create a directory called `lib` and put the latest CrackShot.jar file [(available here)](https://www.spigotmc.org/resources/crackshot-guns.48301/) inside it. Now you can do `mvn install`

## Technical stuff
ChestSort takes an instance of org.bukkit.inventory.Inventory and copies the contents. The resulting array is sorted by rules defined in the config.yml. This takes far less than one millisecond for a whole chest. So there should be no problems even on big servers, where hundreds of players are using chests at the same time.
The plugin should cause no lag at all.

## Screenshots
<p align="center"><img src="https://www.spigotmc.org/attachments/chestsort-screen2-jpg.382332/" alt="Screenshot ChestSort" /></p>

<p align="center"><img src="https://proxy.spigotmc.org/389aa74fb5a6a5d49051e321525599ab729e0965?url=http%3A%2F%2Fapi.jeff-media.de%2Fchestsort%2Fspigotmc%2Fimg%2Fchestsort7.6.gifg" alt="Screenshot ChestSort" /></p>
