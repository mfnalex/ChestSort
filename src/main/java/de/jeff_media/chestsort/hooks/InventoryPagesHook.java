package de.jeff_media.chestsort.hooks;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import de.jeff_media.chestsort.ChestSortPlugin;
import org.jetbrains.annotations.NotNull;

public class InventoryPagesHook {

	final ChestSortPlugin plugin;
	YamlConfiguration inventoryPagesConfig;
	
	int prevSlot, nextSlot;
	Material prevMat, nextMat, noPageMat;
	String prevName, nextName, noPageName;
	
	public InventoryPagesHook(ChestSortPlugin plugin) {
		this.plugin = plugin;
		
		if(!plugin.hookInventoryPages) {
			return;
		}
		
		File inventoryPagesConfigFile = new File(plugin.getDataFolder() + File.separator + ".." + File.separator + "InventoryPages" + File.separator + "config.yml");
		inventoryPagesConfig = YamlConfiguration.loadConfiguration(inventoryPagesConfigFile);
		
		plugin.getLogger().info("Succesfully hooked into InventoryPages");
		
		prevSlot = inventoryPagesConfig.getInt("items.prev.position")+9;
		nextSlot = inventoryPagesConfig.getInt("items.next.position")+9;
		
		prevMat = Material.valueOf(inventoryPagesConfig.getString("items.prev.id"));
		nextMat = Material.valueOf(inventoryPagesConfig.getString("items.next.id"));
		noPageMat = Material.valueOf(inventoryPagesConfig.getString("items.noPage.id"));
				
		prevName = ChatColor.translateAlternateColorCodes('&', inventoryPagesConfig.getString("items.prev.name"));
		nextName = ChatColor.translateAlternateColorCodes('&', inventoryPagesConfig.getString("items.next.name"));
		noPageName = ChatColor.translateAlternateColorCodes('&', inventoryPagesConfig.getString("items.noPage.name"));
		
		//plugin.getLogger().info("Prev Button: " + prevSlot + "," + prevMat.name() + "," + prevName);
		//plugin.getLogger().info("Next Button: " + nextSlot + "," + nextMat.name() + "," + nextName);
		
	}
	
	public boolean isButton(@NotNull ItemStack item, int slot, @NotNull Inventory inv) {
		
		if(!plugin.hookInventoryPages) {
			return false;
		}
		
		if(!(inv instanceof PlayerInventory)) {
			return false;
		}

		// When using &f as color, we manually have to add this to the string because it gets removed by InventoryPages
		//if(prevName.startsWith("§f")) prevName = prevName.substring(2);
		//if(nextName.startsWith("§f")) nextName = nextName.substring(2);
		//if(noPageName.startsWith("§f")) noPageName = noPageName.substring(2);
		
		if(slot == prevSlot ) { 
			if(item.getType() == prevMat && (ChatColor.stripColor(item.getItemMeta().getDisplayName()).equals(ChatColor.stripColor(prevName)))) {
				return true;
			} else return item.getType() == noPageMat && ChatColor.stripColor(item.getItemMeta().getDisplayName()).equals(ChatColor.stripColor(noPageName));
		}
		
		if(slot == nextSlot  ) { 
			if(item.getType() == nextMat && ChatColor.stripColor(item.getItemMeta().getDisplayName()).equals(ChatColor.stripColor(nextName))) {
				return true;
			} else return item.getType() == noPageMat && ChatColor.stripColor(item.getItemMeta().getDisplayName()).equals(ChatColor.stripColor(noPageName));
		}
		
		return false;
	}
	
}
