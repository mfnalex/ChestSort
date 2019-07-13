package de.jeffclan.JeffChestSort;

import java.util.UUID;
import java.io.File;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.EnderChest;
import org.bukkit.block.ShulkerBox;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class JeffChestSortListener implements Listener {

	JeffChestSortPlugin plugin;

	JeffChestSortListener(JeffChestSortPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {

		// DEBUG
		// To enable debug mode, put debug: true into your config.yml
		// Checking for my username because I always forget to comment this out before
		// releases
		// if (event.getPlayer().getName().equalsIgnoreCase("mfnalex")) {
		// plugin.debug = true;
		// }

		// OPs will get an update notice if a new update is available
		if (event.getPlayer().isOp()) {
			plugin.updateChecker.sendUpdateMessage(event.getPlayer());
		}

		// Put player into our perPlayerSettings map
		registerPlayerIfNeeded(event.getPlayer());

	}

	// Put player into our perPlayerSettings map
	void registerPlayerIfNeeded(Player p) {
		// Players are stored by their UUID, so that name changes don't break player's
		// settings
		UUID uniqueId = p.getUniqueId();

		// Add player to map only if they aren't registered already
		if (!plugin.PerPlayerSettings.containsKey(uniqueId.toString())) {

			// Player settings are stored in a file named after the player's UUID
			File playerFile = new File(plugin.getDataFolder() + File.separator + "playerdata",
					p.getUniqueId().toString() + ".yml");
			YamlConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);

			boolean activeForThisPlayer = false;

			if (!playerFile.exists()) {
				// If the player settings file does not exist for this player, set it to the
				// default value
				activeForThisPlayer = plugin.getConfig().getBoolean("sorting-enabled-by-default");
			} else {
				// If the file exists, check if the player has sorting enabled
				activeForThisPlayer = playerConfig.getBoolean("sortingEnabled");
			}

			JeffChestSortPlayerSetting newSettings = new JeffChestSortPlayerSetting(activeForThisPlayer);

			// when "show-message-again-after-logout" is enabled, we don't care if the
			// player already saw the message
			if (!plugin.getConfig().getBoolean("show-message-again-after-logout")) {
				newSettings.hasSeenMessage = playerConfig.getBoolean("hasSeenMessage");
			}

			// Finally add the PlayerSetting object to the map
			plugin.PerPlayerSettings.put(uniqueId.toString(), newSettings);

		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		plugin.unregisterPlayer(event.getPlayer());
	}

	@EventHandler
	public void onInventoryEvent(InventoryEvent event) {
		plugin.getLogger().info("InventoryEvent");
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
	public void onChestClose(InventoryOpenEvent event) {

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
		// Possible Fix for https://github.com/JEFF-Media-GbR/Spigot-ChestSort/issues/13
		if (inventory.getHolder() == null) {
			return false;
		}

		// System.out.println(inventory.getHolder().getClass().getName());

		// Only continue if the inventory belongs to a chest, double chest, shulkerbox
		// or barrel
		// NOTE: We use .getClass().toString() for new items instead of directly
		// comparing the ENUM, because we
		// want to keep compatability between different minecraft versions (e.g. there
		// is no BARREL prior 1.14 and no shulker box prior 1.11)
		// WARNING: The names are inconsistent! A chest will return
		// org.bukkit.craftbukkit.v1_14_R1.block.CraftChest
		// in Spigot 1.14 while a double chest returns org.bukkit.block.DoubleChest
		if (!(inventory.getHolder() instanceof Chest) && !(inventory.getHolder() instanceof DoubleChest)
				&& !(inventory.getHolder().getClass().toString().endsWith(".CraftShulkerBox"))
				&& !(inventory.getHolder().getClass().toString().endsWith(".CraftBarrel"))) {
			return false;
		}
		return true;
	}

	private boolean isReadyToSort(Player p) {
		if (!p.hasPermission("chestsort.use")) {
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
		registerPlayerIfNeeded(p);

		// Get the current player's settings
		// We do not immediately cancel when sorting is disabled because we might want
		// to show the hint message
		JeffChestSortPlayerSetting setting = plugin.PerPlayerSettings.get(p.getUniqueId().toString());

		// Show "how to enable ChestSort" message when ALL of the following criteria are
		// met:
		// - Player has sorting disabled
		// - Player has not seen the message yet (whether or not this resets after a
		// logout
		// is defined by the config setting "show-message-again-after-logout")
		// - "show-message-when-using-chest" is set to true in the config.yml
		if (!plugin.sortingEnabled(p)) {
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
		
		if(!plugin.getConfig().getBoolean("allow-shortcut")) {
			return;
		}
		
		if(!(event.getWhoClicked() instanceof Player)) {
			return;
		}
		
		Player p = (Player) event.getWhoClicked();
		
		if(!p.hasPermission("chestsort.use")) {
			return;
		}
		
		InventoryHolder holder = event.getInventory().getHolder();
		
		boolean sort = false;
		
		switch(event.getClick()) {
		case MIDDLE:
			if(plugin.getConfig().getBoolean("shortcuts.middle-click")) {
				sort=true;
			}
			break;
		case DOUBLE_CLICK:
			if(plugin.getConfig().getBoolean("shortcuts.double-click")) {			
				// We need getCursor() instead of getCurrentItem(), because after picking up the item, it is gone into the cursor
				if(event.getCursor().getType() == Material.AIR) {
					sort=true;
				}
			}
			break;
		case SHIFT_LEFT: 
			if(plugin.getConfig().getBoolean("shortcuts.shift-click")) {				
				if(event.getCurrentItem().getType() == Material.AIR) {
					sort=true;
				}
			}
			break;
		default:
			break;
		}
		
		if(!sort) return;
		
		if(belongsToChestLikeBlock(event.getInventory())) {
			plugin.organizer.sortInventory(event.getInventory());
			updateInventoryView(event);
			return;
		} else if(holder instanceof Player) {
			if(event.getSlotType() == SlotType.QUICKBAR) {
				plugin.organizer.sortInventory(p.getInventory(),0,8);
				updateInventoryView(event);
				return;
			}
			else if(event.getSlotType() == SlotType.CONTAINER) {
				plugin.organizer.sortInventory(p.getInventory(),9,35);
				updateInventoryView(event);
				return;
			}
			return;
		}
	}
	
	void updateInventoryView(InventoryClickEvent event) {
		for(HumanEntity viewer : event.getViewers()) {
			if(viewer instanceof Player) {
				Player playerViewer = (Player) viewer;
				playerViewer.updateInventory();
			}
		}
	}

}
