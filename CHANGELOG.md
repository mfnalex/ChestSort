# Changelog

## 8.17.7-SNAPSHOT
- Fixed InventoryPages hook when using colored item names

## 8.17.6
- Fixed exception and hotbar being filled up with barrier blocks when using the addition right-click-hotkey on MC 1.8

## 8.17.5
- Fixed "/invsort hotbar" and "/invsort all" only sorting the normal inventory when "allow-automatic-inventory-sorting" is set to false

## 8.17.4
- Improved GUI detection
- Added Polish translation (thanks to PLKaratusPL)

## 8.17.3
- Improved GUI detection, specifically for AdvancedAchievements

## 8.17.2
- Added possibility to sort a player's inventory from console using /invsort <player> [toggle|on|off|hotbar|inv|all]
- Added placeholders (see new config.yml)
- Added "use permissions" metric

## 8.17.1
- Improved sorting of colored blocks
  - White, light gray, gray and black comes first
  - Remaining colors are sorted in a rainbow like order

## 8.17.0
- Added option to disable automatic sorting and/or automatic inventory sorting. Hotkeys will still work if enabled. When running /chestsort while automatic sorting is disabled, it will display the hotkeys gui instead.

## 8.16.1
- Allow middle-click hotkey in creative mode when clicked slot is empty

## 8.16.0
- Added generic hook for 3rd party plugin GUIs
- Added config option to disable additional hotkeys (left-click and right-click outside of inventory)
for all players, while still allowing them to use the normal hotkeys
  - Because of this, the option "allow-hotkeys" has been renamed to "allow-sorting-hotkeys". Don't worry, the config updater will take care of this change.

## 8.15.1
- Fixed dirt in containers being affected by the right-click hotkey even though the player had no dirt in his inventory

## 8.15.0
- Enabled left-click and right-click hotkey for 3rd party plugins implementing the ISortable interface from ChestSort's API (You only need this update if you use plugins depending on the ChestSortAPI)

## 8.14.2
- Made CrateReloaded hook and HeadDatabase hook toggleable
- Fixed wrong version number in plugin.yml
- Updated Chinese translation

## 8.14.1
- Prevent players from using left-click and right-click hotkeys on inventories created by CrateReloaded or HeadDatabase

## 8.14.0
- Updated Russian and Turkish translation
- Separated ChestSort plugin and API. You can view the new API documentation [here](https://github.com/JEFF-Media-GbR/Spigot-ChestSort/blob/master/HOW_TO_USE_API.md).

## 8.13.0
- Updated Chinese (Traditional) and Spanish translation
- Fixed TabComplete not working for /sort and /chestsort
- Added option to log ChestSort actions in a log file (default: false)
- Print console warnings when 3rd party plugins use deprecated ChestSort API methods
- Small code cleanup
- Updated API to 1.16.1

## 8.12.2
- Fixes NullPointerException when using EssentialsX' /ec command in Minecraft 1.12

## 8.12.0
- Changed name of command /chestsort to /sort. You can still use /chestsort though.
- Fixed weird config updater problem on systems that don't properly support UTF-8 (like Windows)
- Improved help messages and added /sort help and /invsort help commands
- Huge code cleanup
- Improved performance by caching Reflection checks in the Minepacks hook

## 8.11.0
- Adjustet Left-Click / Right-Click hotkeys:
  - Left-Click outside of inventories will put matching items from your inventory into the chest
  - Double-Left-Click will put all items into the chest
  - Right-Click outside of inventories will put matching items from the chest into your inventory
  - Double-Right-Click will put all items into your inventory 
- Using new universal Update checker (https://github.com/JEFF-Media-GbR/Spigot-UpdateChecker)

Note: The last two messages in the config.yml have changed, so please retranslate them. I am also always happy to integrate your translations into the default config.yml if you send them to me.

## 8.10.5
- Added reload command (/chestsort reload) with permission chestsort.reload
- ChestSort checks if Minepacks version is recent enough and, if not, disable the Minepacks hook.
- Backpacks from Minepacks will no longer be moved into chests with the left-/right-click hotkeys
- Middle-Click hotkey is now disabled in Creative mode unless the clicked slot is empty, to allow players to duplicate items just like in vanilla

## 8.10.4
- Fixed exception when sorting inventories in 1.8 because Inventory.getLocation() did not exist yet

## 8.10.3
- Fixed exception when using the left-/right-click hotkey while using PaperMC instead of Spigot
 
## 8.10.2
- Fixed exception on versions prior to 1.11

## 8.10.1
- Minor bugfixes

## 8.10
- Made llama, donkey and mule chests sortable via hotkeys and automatic chest sorting
- Left-Click/Right-Click-Hotkey only works with empty hand now
- Removed "checking for updates" message

## 8.9
- Prevent BossShopPro's GUI from being sorted
- Added custom event to let 3rd party plugins cancel sorting an inventory, see updated API doc for more information
- Published ChestSort in public maven repository repo.jeff-media.de/maven2, see updated API doc for more information

## 8.8.2
- Fixes exception when sorting inventories containing potions in ancient Minecraft versions like 1.8

## 8.8.1
- Changed config-version, because I forgot that in 8.8. You can now use the new config option and translations.

## 8.8
- Improved Minepacks hook and prevents backpacks from being put into itself
- Prevent ItemStacks > 64 slots from being sorted at all
- Update Checker interval is now configurable (default: every 4 hours)
- Updated French, Hungarian and Japanese translation
- Changed class names and moved API to its own class - can be accessed via ChestSortPlugin#getAPI()

## 8.7
- When using Minepacks, the backpack item in the inventory will not be moved
- Added use-permissions option. If you do not use a permissions plugin, you can set this to false to allow every player to use ChestSort

## 8.6
- Added support for Minepacks

## 8.5
- Prevent Right-Click-Hotkey from putting items in the hotbar
- Fixed InventoryPages support: when using &f at the beginning of button names, it was not detected by ChestSort as button
- Fixed possible problems with Spigot versions prior to 1.11
- Updated French, Chinese and Chinese (Traditional) translation

## 8.4
- Fixes InventoryPages support for the new hotkeys

## 8.3
- This should fix a problem where the player was able to sort inventories belonging to 3rd party plugins' GUIs using hotkeys

## 8.2
- Possibly fixes exception when using hotkeys in Inventories with a null-holder

## 8.1
- Sort chest's inventory after using the new Fill-Chest-Hotkey
- Prevent the new hotkeys from being used when allow-hotkeys is set to false
- Reformatted config.yml a bit (don't worry, you have automatic config updates)
- Update-Checker now includes Spigot version in User-Agent string

## 8.0
- Added two new hotkeys (disabled by default): Left-Click outside of a chest's (or barrel, ...) inventory will load all your stuff except hotbar into the chest, Right-Click outside of the chest's inventory will unload the chest into your inventory. The hotkeys can be enabled using /chestsort hotkeys and need the chestsort.use permission
- Added debug and dump option to config (you will probably not need those)
- Player configs will be only be saved if they have changed
- Note: This version includes two new messages, so please send me your new translations :)

## 7.7-pre1
- Moved /invsort command to separate class
- Moved registerPlayerIfNeeded from Listener to main class
- Save player configs only if they have changed

## 7.6.1
- Changed description for hotkeys in config.yml
- Updated French translation
- Updated Chinese (Traditional) translation
- Updated bStats to version 1.7

## 7.6
- Added automatic inventory sorting (disabled by default). Can be activated by using /invsort on
- Added options "toggle", "on", "off" to /chestsort. When no option is specified, "toggle" is assumed
- Improved the messages sent by the update checker, including links for download, donation and changelog
- Updated bStats to version 1.6
- Removed some unnecessary imports

## 7.5
- Added support for most Shulkerbox/Backpack plugins. Some of those (e.g. [Shulker Backpacks](https://www.spigotmc.org/resources/shulker-backpacks-1-13-1-15.67466/)) are fully compatible, while others (e.g. [BetterShulkerBoxes](https://www.spigotmc.org/resources/bsb-better-shulker-boxes.58837/)) will only work with hotkeys, not automatic sorting.
- Added Hungarian translation
- Fixed French translation

## 7.4
- Added TabComplete support for /chestsort and /invsort commands
- Fixed a bug that allowed players to put items into the GUI (/chestsort hotkeys)
- Added Korean translation
- Minor stuff

## 7.3
- Potions will now be grouped by effect type
- Reverting to 1.13 API to support all MC versions from 1.8.x to 1.15.x
- Added honey bottle to category files
- Added sorting support for Enderchests and Minecarts with Chest
- Minor stuff

## 7.2
Updated API to 1.15.2
- Compiled against 1.15.2 APi
- Updated languages: Chinese, French
- Minor stuff

## 7.1
- Removed weapons from armor-and-arrows category
- Logs will now get sorted before their stripped variants
- Bow will now get sorted properly into the weapons category
- Enchanted items will be put before their unenchanted equivalents

## 7.0
- Added hotkey GUI

## 6.4.5
- Fixed NPE when clicking outside of inventories

## 6.4.4
Bugs fixed:
- When using hotkeys, you can now sort your player inventory while accessing chests. Previously, the chest was sorted even when clicking into the lower half of the inventory
- Fixed Shift-Rightlick hotkey not working
- Player inventory sorting via hotkey now requires the chestsort.use.inventory
permission instead of the regular chestsort.use permission

Fully compatible with PaperSpigot

## 6.4.2
- Fixed: Shift-Right-Click hotkey was broken since 6.4.1
- Fixed exception when using special characters inside item names/lore while also using {customName} or {lore} in sorting-method

## 6.4.1
- Possible fix for Exception when using hotkeys with PaperSpigot

## 6.4
- Added support for plugin "InventoryPages"

## 6.3
- Added support for plugin "CrackShot" to group and sort custom weapons and guns

## 6.2
- New hotkey: Shift + rightclick on any EMPTY slot
- Improved config updater: Config updater now keeps disabled-worlds option (and everything else) intact

## 6.1
There are now three types of shortcuts, that can be disabled/enabled individually in the config.yml file.
The hotkeys are:
- Middle click (click with mouse wheel on ANY inventory slot)
- Shift click (click with shift + left click on any EMPTY inventory slot)
- Double click (double left click on any EMPTY inventory slot)

All hotkeys are enabled by default.

Info: The "allow-shortcut" option in the config.yml has been renamed to "allow-sorting-hotkeys".

## 6.0
Added middle-click shortcut

You can now sort any inventory by middle-clicking any slot within that inventory. This is useful if you do not want to enable automatic sorting, but only want to sort a chest or your own inventory from time to time. This also means that you do not have to use /invsort or /isort anymore - just middle click into your player inventory

## 5.1
- Fixes exceptions when using SPigot versions prior to 1.11

## 5.0.3
- New translation: Dutch
- Fixed translation: Chinese

## 5.0.2
- Fixed exception when using default Russian translation

## 5.0.1
- {keepCategoryOrder} now works
- sticky category files now work
- UpdateChecker showing correct version again

## 5.0
- The config file will update itself automatically when a new version is released. Your old changes will be kept, so you always see the new config options without having to redo all your changes.
- Categories can be set to sticky in the category file. That means that the items in this category will be ordered exactly as in the file. Without setting sticky, the items in one category will only be grouped together and sorted according to the other variables in your sorting-method.
- New placeholders for your sorting-method including custom item name (e.g. when renamed with an anvil) and lore. You can also use {keepCategoryOrder} behind {category}. This will have the same effect as when you set all categories to sticky.

## 4.2
- Added sort-time option. Available options: open, close, both

## 4.1.2
- Fixed compatability for Spigot and Paper from 1.8 to 1.14 (again...)

## 4.1.1
- Reverting to 1.13.2 API to fix exception when using certain paper und Spigot versions

## 4.1
- Sorting EnderChests is now possible! Your loot is finally safe and tidy.
- More options for /invsort: /invsort aka /isort now understands three options:
/invsort inv (default) will sort only the regular inventory, excluding hotbar
/invsort hotbar will only sort the hotbar
/invsort all will sort both
- Changed order of default category files. The valuables and tools category have switched places, so that tools is now the first category.
- Update Spigot-API to 1.14. ChestSort already worked with 1.14, but now it uses the new API natively.

## 4.0
- Player Inventory sorting! Players can sort their inventory using /invsort or /isort. This requires the chestsort.use.inventory permission
- Support for all major Minecraft versions! This version has been tested with 1.8, 1.11, 1.13.2 and 1.14, so it should work with every version between 1.8 and 1.14

## 3.7.1
- Added Russian translation
- Fixes exception when opening inventories without associated InventoryHolder

## 3.7
- Added sorting support for barrels
- Improved async update checker

## 3.6
- Fixed missing plugin.yml
- Added API function: boolean sortingEnabled = chestSort.sortingEnabled(Player player);

## 3.5
- Supports all Spigot versions from 1.11 to 1.14

## 3.4
- Added disabled-worlds option

## 3.3
- Fixed API function: chestSort.sortInventory(Inventory inventory, int startSlot, int endSlot) now works correctly

## 3.2
- Fixed bug when sorting custom player heads
- Revamped sorting algorithm, it is now even faster!

## 3.1
- Added API function: chestSort.sortInventory(Inventory inv, int startSlot, int endSlot)

## 3.0
- Added ChestSort API

## 2.0.3
- Added Chinese (Traditional) and Japanese translation

## 2.0.2
- Updated Spigot API to 1.13.2
- Players in adventure mode will no longer be able to sort chests

## 2.0.1
- Updated Spigot API to 1.13.1
- Added re-initialization of the PerPlayerSettings object to avoid problems with /reload
- Added ChestSort version to user agent for auto update
- Added show-message-when-using-chest-and-sorting-is-enabled to Metrics

## 2.0.0
- ChestSort now supports categories for sorting! There are pregenerated category files that you can adjust. Wildcards are supported!
- The default config now uses this sorting method: {category},{itemsFirst},{name},{color}
- Sorting has been improved for many things, like corals, stripped_log and many more
- Automatic update checker integrated. On Server startup and every 24 hours there will be a check if a new version is available. If yes, it is displayed in the console and to OPs on logging in. You can toggle this in the config.yml (check every 24 hours, check only on startup or don't check at all)
- Bugfixes and improvements
- Your old config file will be renamed, so that you can use the new features and start off with a freshly generated 2.0.0 config
- Some debug infos on server startup. You can disable this by setting verbose: false in your config.yml
- ChestSort now survives /reload (which is still UNSUPPORTED!)

## 1.7.4
- Added Spanish translation

## 1.7.3
- Fixed "using old config" message being shown when using freshly generated config

## 1.7.2
- Fixed debug mode being enabled by default

## 1.7.1
- Added color sorting for all blocks

## 1.6
- You can now customize the way ChestSort will sort your stuff. By default, everything is sorted alphabetically, but items will be put before blocks. See the new config.yml
- Bugfix: Players in spectator mode can no longer sort chest

## 1.5.8
- Added Chinese language support, thanks qsefthuopq for translating!
- The message on how to use /chestsort when accessing a chest for the first time is not shown anymore when a player already entered the command before

## 1.5.7
- Bugfix: Corrected default config file to use English as default. Since 1.5.6, it was accidentally set to Italian.

## 1.5.6
- Added Italien translation

## 1.5
- Added permissions

## 1.4
- Added bStats
