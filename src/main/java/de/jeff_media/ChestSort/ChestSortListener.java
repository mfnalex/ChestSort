package de.jeff_media.ChestSort;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import de.jeff_media.ChestSort.hooks.MinepacksHook;

public class ChestSortListener implements Listener {

	ChestSortPlugin plugin;
	MinepacksHook minepacksHook;

	ChestSortListener(ChestSortPlugin plugin) {
		this.plugin = plugin;
		this.minepacksHook = new MinepacksHook(plugin);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		
		plugin.permissionsHandler.addPermissions(event.getPlayer());

		// DEBUG
		// To enable debug mode, put debug: true into your config.yml

		// OPs will get an update notice if a new update is available
		if (event.getPlayer().isOp()) {
			plugin.updateChecker.sendUpdateMessage(event.getPlayer());
		}

		// Put player into our perPlayerSettings map
		plugin.registerPlayerIfNeeded(event.getPlayer());

	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		plugin.permissionsHandler.removePermissions(event.getPlayer());
		plugin.unregisterPlayer(event.getPlayer());
	}


	@EventHandler
	public void onBackPackClose(InventoryCloseEvent event) {
		if(plugin.getConfig().getString("sort-time").equalsIgnoreCase("close")
				 || plugin.getConfig().getString("sort-time").equalsIgnoreCase("both"))
		onBackPackUse(event.getInventory(),(Player)event.getPlayer());
	}
	
	@EventHandler
	public void onBackPackOpen(InventoryOpenEvent event) {
		if(plugin.getConfig().getString("sort-time").equalsIgnoreCase("open")
				 || plugin.getConfig().getString("sort-time").equalsIgnoreCase("both"))
		onBackPackUse(event.getInventory(),(Player)event.getPlayer());
	}
	
	void onBackPackUse(Inventory inv, Player p) {
		if(!minepacksHook.isMinepacksBackpack(inv)) return;
		if( !p.hasPermission("chestsort.use")) return;
		plugin.registerPlayerIfNeeded(p);
		ChestSortPlayerSetting setting = plugin.perPlayerSettings.get(p.getUniqueId().toString());
		if(!setting.sortingEnabled) return;
		plugin.organizer.sortInventory(inv);
	}
	
	@EventHandler
	public void onPlayerInventoryClose(InventoryCloseEvent event) {
		if(event.getInventory()==null) return;
		if(event.getInventory().getHolder()==null) return;
		if(event.getInventory().getType() == null) return;
		if(event.getInventory().getType() != InventoryType.CRAFTING) return; // Weird! Returns CRAFTING instead of PLAYER
		if(!(event.getInventory().getHolder() instanceof Player)) return;

		Player p = (Player) event.getInventory().getHolder();
		
		if( !p.hasPermission("chestsort.use.inventory")) return;
		plugin.registerPlayerIfNeeded(p);
		
		ChestSortPlayerSetting setting = plugin.perPlayerSettings.get(p.getUniqueId().toString());
		if(!setting.invSortingEnabled) return;
		
		plugin.organizer.sortInventory(p.getInventory(),9,35);
		
	}

	// This event fires when someone closes an inventory
	// We check if the closed inventory belongs to a chest, shulkerbox or barrel,
	// and then call the Organizer to sort the inventory (if the player has
	// the chestsort.use permission and has /chestsort enabled)
	@EventHandler
	public void onChestClose(InventoryCloseEvent event) {

		if (!(plugin.getConfig().getString("sort-time").equalsIgnoreCase("close")
				|| plugin.getConfig().getString("sort-time").equalsIgnoreCase("both"))) {
			return;
		}

		// event.getPlayer returns HumanEntity, so it could also be an NPC or something
		if (!(event.getPlayer() instanceof Player)) {
			return;
		}
		Player p = (Player) event.getPlayer();
		Inventory inventory = event.getInventory();

		if (!belongsToChestLikeBlock(inventory)) {
			return;
		}

		if (isReadyToSort(p)) {

			// Finally call the Organizer to sort the inventory
			plugin.organizer.sortInventory(event.getInventory());
		}

	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onChestOpen(InventoryOpenEvent event) {

		if (!(plugin.getConfig().getString("sort-time").equalsIgnoreCase("open")
				|| plugin.getConfig().getString("sort-time").equalsIgnoreCase("both"))) {
			return;
		}

		if (event.isCancelled()) {
			return;
		}

		// event.getPlayer returns HumanEntity, so it could also be an NPC or something
		if (!(event.getPlayer() instanceof Player)) {
			return;
		}
		Player p = (Player) event.getPlayer();
		Inventory inventory = event.getInventory();

		if (!belongsToChestLikeBlock(inventory)) {
			return;
		}

		if (isReadyToSort(p)) {

			// Finally call the Organizer to sort the inventory
			plugin.organizer.sortInventory(event.getInventory());
		}

	}

	private boolean belongsToChestLikeBlock(Inventory inventory) {

		// Check by InventoryType
		if (inventory.getType() == InventoryType.ENDER_CHEST || inventory.getType().name().equalsIgnoreCase("SHULKER_BOX")) {
			return true;
		}

		// Possible Fix for https://github.com/JEFF-Media-GbR/Spigot-ChestSort/issues/13
		if (inventory.getHolder() == null) {
			return false;
		}

		// Check by InventoryHolder
		// Only continue if the inventory belongs to one of the following:
		// - a chest,
		// - double chest,
		// - shulkerbox (MC 1.11) (obsolete, is checked above by InventoryType
		// - barrel (MC 1.14)
		// - Minecart with Chest (MC 1.0)
		// NOTE: Hoppers are NOT included because it may break item sorters like those: https://minecraft.gamepedia.com/Tutorials/Hopper#Item_sorter
		// NOTE: We use .getClass().toString() for new items instead of directly
		// comparing the ENUM, because we
		// want to keep compatability between different minecraft versions (e.g. there
		// is no BARREL prior 1.14 and no shulker box prior 1.11)
		// WARNING: The names are inconsistent! A chest will return
		// org.bukkit.craftbukkit.v1_14_R1.block.CraftChest
		// in Spigot 1.14 while a double chest returns org.bukkit.block.DoubleChest
		if (!(inventory.getHolder() instanceof Chest) && !(inventory.getHolder() instanceof DoubleChest)
				&& !(inventory.getHolder().getClass().toString().endsWith(".CraftMinecartChest"))
				&& !(inventory.getHolder().getClass().toString().endsWith(".CraftShulkerBox")) //Obsolete, is checked above by InventoryType
				&& !(inventory.getHolder().getClass().toString().endsWith(".CraftBarrel"))) {
			return false;
		}
		return true;
	}

	private boolean isReadyToSort(Player p) {
		if ( !p.hasPermission("chestsort.use")) {
			return false;
		}

		// checking in lower case for lazy admins
		if (plugin.disabledWorlds.contains(p.getWorld().getName().toLowerCase())) {
			return false;
		}

		// Don't sort automatically when player is spectator or in adventure mode
		// TODO: Make this configurable in config.yml
		if (p.getGameMode() == GameMode.SPECTATOR || p.getGameMode() == GameMode.ADVENTURE) {
			return false;
		}

		// Fixes exception when using Spigot's stupid /reload command
		plugin.registerPlayerIfNeeded(p);

		// Get the current player's settings
		// We do not immediately cancel when sorting is disabled because we might want
		// to show the hint message
		ChestSortPlayerSetting setting = plugin.perPlayerSettings.get(p.getUniqueId().toString());

		// Show "how to enable ChestSort" message when ALL of the following criteria are
		// met:
		// - Player has sorting disabled
		// - Player has not seen the message yet (whether or not this resets after a
		// logout
		// is defined by the config setting "show-message-again-after-logout")
		// - "show-message-when-using-chest" is set to true in the config.yml
		if (!plugin.isSortingEnabled(p)) {
			if (!setting.hasSeenMessage) {
				setting.hasSeenMessage = true;
				if (plugin.getConfig().getBoolean("show-message-when-using-chest")) {
					p.sendMessage(plugin.messages.MSG_COMMANDMESSAGE);
				}
			}
			return false;
		}
		// Show "how to disable ChestSort" message when ALL of the following criteria
		// are met:
		// - Player has sorting enabled
		// - Player has not seen the message yet (whether or not this resets after a
		// logout
		// is defined by the config setting "show-message-again-after-logout")
		// - "show-message-when-using-chest-and-sorting-is-enabled" is set to true in
		// the config.yml
		else {
			if (!setting.hasSeenMessage) {
				setting.hasSeenMessage = true;
				if (plugin.getConfig().getBoolean("show-message-when-using-chest-and-sorting-is-enabled")) {
					p.sendMessage(plugin.messages.MSG_COMMANDMESSAGE2);
				}
			}
		}
		return true;
	}

	@EventHandler
	public void onEnderChestOpen(InventoryOpenEvent event) {

		if (!(event.getPlayer() instanceof Player)) {
			return;
		}

		Player p = (Player) event.getPlayer();

		// Check if this is an EnderChest (is there a smarter way?)
		if (!event.getInventory().equals(p.getEnderChest())) {
			return;
		}

		if (isReadyToSort(p)) {

			// Finally call the Organizer to sort the inventory
			plugin.organizer.sortInventory(event.getInventory());
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onInventoryClickEvent(InventoryClickEvent event) {
		
		if(!(event.getWhoClicked() instanceof Player)) {
			return;
		}
		
		Player p = (Player) event.getWhoClicked();
		
		plugin.registerPlayerIfNeeded(p);
				
		if(!plugin.getConfig().getBoolean("allow-hotkeys")) {
			return;
		}
		
		// DEBUG START
//		p.sendMessage("=====================");
//		p.sendMessage("Click type: " + event.getClick().name());
//		p.sendMessage("Right click: " + event.isRightClick());
//		p.sendMessage("Shift click: " + event.isShiftClick());
//		p.sendMessage("=====================");
		// DEBUG END
				
		if( !p.hasPermission("chestsort.use") && !p.hasPermission("chestsort.use.inventory")) {
			return;
		}
		
		//InventoryHolder holder = event.getInventory().getHolder();
		if(event.getClickedInventory() == null) {
			return;
		}
		// Possible fix for #57
		if(event.getClickedInventory().getHolder() != null
				&& event.getClickedInventory().getHolder() == p
				&& event.getClickedInventory() != p.getInventory()) return;
		// End Possible fix for #57
		InventoryHolder holder = event.getClickedInventory().getHolder();
		
		boolean sort = false;
		
		ChestSortPlayerSetting setting = plugin.perPlayerSettings.get(p.getUniqueId().toString());
		
		// Do not sort the GUI inventory
		if(event.getClickedInventory() == setting.guiInventory) {
			return;
		}
		// Prevent player from putting items into GUI inventory
		if(event.getInventory() == setting.guiInventory) {
			event.setCancelled(true);
			return;
		}
		switch(event.getClick()) {
		case MIDDLE:
			//if(plugin.getConfig().getBoolean("hotkeys.middle-click")) {
			if(setting.middleClick) {
				sort=true;
			}
			break;
		case DOUBLE_CLICK:
			//if(plugin.getConfig().getBoolean("hotkeys.double-click")) {
			if(setting.doubleClick) {
				// We need getCursor() instead of getCurrentItem(), because after picking up the item, it is gone into the cursor
				if(event.getCursor() == null || (event.getCursor() != null && event.getCursor().getType() == Material.AIR)) {
					sort=true;
				}
			}
			break;
		case SHIFT_LEFT: 
			//if(plugin.getConfig().getBoolean("hotkeys.shift-click")) {
			if(setting.shiftClick) {
				if(event.getCurrentItem() == null || (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.AIR) ){
					sort=true;
				}
			}
			break;
		case SHIFT_RIGHT:
			//if(plugin.getConfig().getBoolean("hotkeys.shift-right-click")) {
			if(setting.shiftRightClick) {
				if(event.getCurrentItem() == null || ( event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.AIR)) {
					sort=true;
				}
			}
			break;
		default:
			break;
		}
		
		if(!sort) {
			return;
		}
		if(belongsToChestLikeBlock(event.getClickedInventory()) || minepacksHook.isMinepacksBackpack(event.getClickedInventory())) {
			
			if( !p.hasPermission("chestsort.use")) {
				return;
			}
			
			
			plugin.organizer.sortInventory(event.getClickedInventory());
			plugin.organizer.updateInventoryView(event);
			return;
		} else if(holder instanceof Player) {
			if( !p.hasPermission("chestsort.use.inventory")) {
				return;
			}
			
			if(event.getSlotType() == SlotType.QUICKBAR) {
				plugin.organizer.sortInventory(p.getInventory(),0,8);
				plugin.organizer.updateInventoryView(event);
				return;
			}
			else if(event.getSlotType() == SlotType.CONTAINER) {
				plugin.organizer.sortInventory(p.getInventory(),9,35);
				plugin.organizer.updateInventoryView(event);
				return;
			}
			return;
		}
	}
	
	@EventHandler
	public void onAdditionalHotkeys(InventoryClickEvent e) {
		
		if(e.getClickedInventory() != null && e.getClickedInventory().getLocation()!=null) {
			ChestSortEvent chestSortEvent = new ChestSortEvent(e.getClickedInventory().getLocation());
			Bukkit.getPluginManager().callEvent(chestSortEvent);
			if (chestSortEvent.isCancelled()) {
			    return;
			}
		}
		
		if(!plugin.getConfig().getBoolean("allow-hotkeys")) {
			return;
		}
		if(!(e.getWhoClicked() instanceof Player)) {
			return;
		}
		Player p = (Player) e.getWhoClicked();
		// Only continue if clicked outside of the chest
		if(e.getClickedInventory()!=null) {
			return;
		}
		// Possible fix for #57
		if(e.getInventory().getHolder()==null) return;
		if(e.getInventory().getHolder() == p && e.getInventory() != p.getInventory()) return;
		// End Possible fix for #57
		if(e.getInventory().getType() != InventoryType.CHEST
				&& e.getInventory().getType() != InventoryType.DISPENSER
				&& e.getInventory().getType() != InventoryType.DROPPER
				&& e.getInventory().getType() != InventoryType.ENDER_CHEST
				&& !e.getInventory().getType().name().equalsIgnoreCase("SHULKER_BOX")
				&& (e.getInventory().getHolder() == null || !e.getInventory().getHolder().getClass().toString().endsWith(".CraftBarrel"))) {
			return;
		}
		
		if( !p.hasPermission("chestsort.use")) return;
		
		plugin.registerPlayerIfNeeded(p);
		ChestSortPlayerSetting setting = plugin.perPlayerSettings.get(p.getUniqueId().toString());
		
		if(e.isLeftClick() && setting.leftClick) {
			plugin.organizer.stuffPlayerInventoryIntoAnother(p.getInventory(), e.getInventory());
			plugin.organizer.sortInventory(e.getInventory());
			plugin.organizer.updateInventoryView(e.getInventory());
		} else if(e.isRightClick() && setting.rightClick) {
			plugin.organizer.stuffInventoryIntoAnother(e.getInventory(), p.getInventory(),e.getInventory());
		}
	}
	


}
