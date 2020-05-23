package de.jeffclan.hooks;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import at.pcgamingfreaks.Minepacks.Bukkit.API.MinepacksPlugin;
import de.jeffclan.JeffChestSort.JeffChestSortPlugin;

public class MinepacksHook {
	
	JeffChestSortPlugin plugin;
	MinepacksPlugin minepacks = null;

	public MinepacksHook(JeffChestSortPlugin plugin) {
		this.plugin = plugin;
	    Plugin bukkitPlugin = Bukkit.getPluginManager().getPlugin("Minepacks");
	    if(plugin.hookMinepacks && bukkitPlugin instanceof MinepacksPlugin) {
	    	// Do something if Minepacks is not available
	        minepacks = (MinepacksPlugin) bukkitPlugin;
	    }
	}
	
	public boolean isMinepacksBackpack(Inventory inv) {
				
		if(minepacks == null) return false;
		
		if( inv.getHolder() == null) return false;
		
		if( inv.getHolder().getClass().getName().equalsIgnoreCase("at.pcgamingfreaks.MinepacksStandalone.Bukkit.Backpack")) {
			return true;
		}
		
		return false;
		
		//System.out.println(inv.getHolder().getClass().getName());
				
		/*if(inv.getHolder() instanceof Backpack) {
			return true;
		}
		
		if(minepacks.getBackpackCachedOnly(p).getInventory() == inv) {
			return true;
		}*/
	}

}
