# Changelog

## 7.6-pre1
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

Info: The "allow-shortcut" option in the config.yml has been renamed to "allow-hotkeys".

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
