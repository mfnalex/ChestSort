package de.jeffclan.hooks;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import at.pcgamingfreaks.Minepacks.Bukkit.API.Backpack;
import at.pcgamingfreaks.Minepacks.Bukkit.API.MinepacksPlugin;
import de.jeffclan.JeffChestSort.JeffChestSortPlugin;

public class MinepacksHook {
	
	JeffChestSortPlugin plugin;
	MinepacksPlugin minepacks = null;

	public MinepacksHook(JeffChestSortPlugin plugin) {
		this.plugin = plugin;
	    Plugin bukkitPlugin = Bukkit.getPluginManager().getPlugin("Minepacks");
	    if(plugin.hookMinepacks && bukkitPlugin instanceof MinepacksPlugin) {
	        minepacks = (MinepacksPlugin) bukkitPlugin;
			plugin.getLogger().info("Succesfully hooked into Minepacks");
	    }
	}
	
	public boolean isMinepacksBackpack(ItemStack item) {
		if(minepacks == null) return false;
		
		if(minepacks.isBackpackItem(item)) return true;
		
		return false;
	}
	
	public boolean isMinepacksBackpack(Inventory inv) {
				
		if(minepacks == null) return false;
		
		if( inv.getHolder() == null) return false;
				
		if(inv.getHolder() instanceof Backpack) {
			return true;
		}
		
		return false;
		
	}

}
