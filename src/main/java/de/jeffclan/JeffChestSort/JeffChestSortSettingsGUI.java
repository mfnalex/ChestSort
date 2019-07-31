package de.jeffclan.JeffChestSort;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class JeffChestSortSettingsGUI {
	
	JeffChestSortPlugin plugin;
	
	public static int slotMiddleClick = 1 + 9;
	public static int slotShiftClick = 3 + 9;
	public static int slotDoubleClick = 5 + 9;
	public static int slotShiftRightClick = 7 + 9;
	
	enum Hotkey {
		MiddleClick, ShiftClick, DoubleClick, ShiftRightClick;
	}
	
	JeffChestSortSettingsGUI(JeffChestSortPlugin plugin) {
		this.plugin=plugin;
	}
	
	ItemStack getItem(boolean active, Hotkey hotkey) {
		ItemStack is = null;
		String suffix;
		Material green = Material.getMaterial("GREEN_WOOL");
		Material red = Material.getMaterial("RED_WOOL");
		
		if(green==null || red==null) {
			return null;
		}
		
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
		default:
			break;
		}
		
		is.setItemMeta(meta);
		
		return is;
	}
	
    void openGUI(Player player) {
        Inventory inventory = createGUI("ChestSort", player);
        
        JeffChestSortPlayerSetting setting = plugin.PerPlayerSettings.get(player.getUniqueId().toString());
        
        // Test if running 1.13 or later
        if(Material.getMaterial("GREEN_WOOL") == null) {
        	player.sendMessage(plugin.messages.MSG_ERR_HOTKEYSDISABLED);
        	return;
        }
        
        inventory.setItem(slotMiddleClick, getItem(setting.middleClick,Hotkey.MiddleClick));
        inventory.setItem(slotShiftClick, getItem(setting.shiftClick,Hotkey.ShiftClick));
        inventory.setItem(slotDoubleClick, getItem(setting.doubleClick,Hotkey.DoubleClick));
        inventory.setItem(slotShiftRightClick, getItem(setting.shiftRightClick,Hotkey.ShiftRightClick));
        
        setting.guiInventory = inventory;
        player.openInventory(inventory);
    }
    
    Inventory createGUI(String name, Player inventoryHolder) {
        Inventory inventory = Bukkit.createInventory(inventoryHolder, InventoryType.CHEST, name);
        return inventory;
    }
}
