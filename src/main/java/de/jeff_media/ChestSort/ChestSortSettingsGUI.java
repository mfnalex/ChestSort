package de.jeff_media.ChestSort;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ChestSortSettingsGUI implements Listener {
	
	final ChestSortPlugin plugin;
	
	public static final int slotMiddleClick = 1;
	public static final int slotShiftClick = 3 ;
	public static final int slotDoubleClick = 5 ;
	public static final int slotShiftRightClick = 7 ;
	public static final int slotLeftClickFromOutside = 4 + 9;
	public static final int slotLeftClick = 2+18;
	public static final int slotRightClick = 6+18;
	
	final static Material red = Material.REDSTONE_BLOCK;
	final static Material green = Material.EMERALD_BLOCK;
	
	enum Hotkey {
		MiddleClick, ShiftClick, DoubleClick, ShiftRightClick, LeftClick, RightClick, LeftClickOutside
	}
	
	ChestSortSettingsGUI(ChestSortPlugin plugin) {
		this.plugin=plugin;
	}
	
	ItemStack getItem(boolean active, Hotkey hotkey) {
		ItemStack is;
		String suffix;
		
		if(active) {
			is = new ItemStack(green);
			suffix = plugin.messages.MSG_GUI_ENABLED;
		}
		else {
			is = new ItemStack(red);
			suffix = plugin.messages.MSG_GUI_DISABLED;
		}
		
		ItemMeta meta = is.getItemMeta();
				
		switch(hotkey) {
		case MiddleClick:
			meta.setDisplayName(ChatColor.RESET + plugin.messages.MSG_GUI_MIDDLECLICK + ": " + suffix);
			break;
		case ShiftClick:
			meta.setDisplayName(ChatColor.RESET + plugin.messages.MSG_GUI_SHIFTCLICK + ": " + suffix);
			break;
		case DoubleClick:
			meta.setDisplayName(ChatColor.RESET + plugin.messages.MSG_GUI_DOUBLECLICK + ": " + suffix);
			break;
		case ShiftRightClick:
			meta.setDisplayName(ChatColor.RESET + plugin.messages.MSG_GUI_SHIFTRIGHTCLICK + ": " + suffix);
			break;
		case LeftClickOutside:
			meta.setDisplayName(ChatColor.RESET + plugin.messages.MSG_GUI_LEFTCLICKOUTSIDE + ": " + suffix);
			break;
		case LeftClick:
			meta.setDisplayName(ChatColor.RESET + plugin.messages.MSG_GUI_LEFTCLICK + ": "+ suffix);
			break;
		case RightClick:
			meta.setDisplayName(ChatColor.RESET + plugin.messages.MSG_GUI_RIGHTCLICK + ": "+ suffix);
			break;
		default:
			break;
		}
		
		is.setItemMeta(meta);
		
		return is;
	}
	
    void openGUI(Player player) {
        Inventory inventory = createGUI("ChestSort", player);
        
        ChestSortPlayerSetting setting = plugin.perPlayerSettings.get(player.getUniqueId().toString());

        if(plugin.getConfig().getBoolean("allow-sorting-hotkeys")) {
			inventory.setItem(slotMiddleClick, getItem(setting.middleClick, Hotkey.MiddleClick));
			inventory.setItem(slotShiftClick, getItem(setting.shiftClick, Hotkey.ShiftClick));
			inventory.setItem(slotDoubleClick, getItem(setting.doubleClick, Hotkey.DoubleClick));
			inventory.setItem(slotShiftRightClick, getItem(setting.shiftRightClick, Hotkey.ShiftRightClick));
		}
        if(plugin.getConfig().getBoolean("allow-left-click-to-sort")) {
        	inventory.setItem(slotLeftClickFromOutside, getItem(setting.leftClickOutside, Hotkey.LeftClickOutside));
		}
        if(plugin.getConfig().getBoolean("allow-additional-hotkeys")) {
        	inventory.setItem(slotLeftClick, getItem(setting.leftClick,Hotkey.LeftClick));
			inventory.setItem(slotRightClick, getItem(setting.rightClick,Hotkey.RightClick));
		}
        
        setting.guiInventory = inventory;
        player.openInventory(inventory);
    }
    
    Inventory createGUI(String name, Player inventoryHolder) {
		return Bukkit.createInventory(inventoryHolder, InventoryType.CHEST, name);
    }
    
    @EventHandler
	void onGUIInteract(InventoryClickEvent event) {
		if(!plugin.hotkeyGUI) {
			return;
		}
		if(!(event.getWhoClicked() instanceof Player)) {
			return;
		}
		Player p = (Player) event.getWhoClicked();
		plugin.listener.plugin.registerPlayerIfNeeded(p);
		ChestSortPlayerSetting setting = plugin.perPlayerSettings.get(p.getUniqueId().toString());
		
		if(setting.guiInventory==null) {
			return;
		}
		
		if(event.getClickedInventory()==null) {
			return;
		}
		if(!event.getClickedInventory().equals(setting.guiInventory)) {
			return;
		}
		
		// We only get this far if the player has clicked inside his GUI inventory
		event.setCancelled(true);
		if(event.getClick() != ClickType.LEFT) {
			return;
		}

		if(event.getCurrentItem()==null || event.getCurrentItem().getType()==Material.AIR) {
			return;
		}


		if(event.getSlot() == ChestSortSettingsGUI.slotMiddleClick) {
			setting.toggleMiddleClick();
			plugin.settingsGUI.openGUI(p);
		}
		else if(event.getSlot() == ChestSortSettingsGUI.slotShiftClick) {
			setting.toggleShiftClick();
			plugin.settingsGUI.openGUI(p);
		} else 	if(event.getSlot() == ChestSortSettingsGUI.slotDoubleClick) {
			setting.toggleDoubleClick();
			plugin.settingsGUI.openGUI(p);
		} else if(event.getSlot() == ChestSortSettingsGUI.slotLeftClickFromOutside) {
			setting.toggleLeftClickOutside();
			plugin.settingsGUI.openGUI(p);
		} else if(event.getSlot() == ChestSortSettingsGUI.slotShiftRightClick) {
			setting.toggleShiftRightClick();
			plugin.settingsGUI.openGUI(p);
		} else if(event.getSlot() == ChestSortSettingsGUI.slotLeftClick) {
			setting.toggleLeftClick();
			plugin.settingsGUI.openGUI(p);
		} else if(event.getSlot() == ChestSortSettingsGUI.slotRightClick) {
			setting.toggleRightClick();
			plugin.settingsGUI.openGUI(p);
		}
		
	}
}
