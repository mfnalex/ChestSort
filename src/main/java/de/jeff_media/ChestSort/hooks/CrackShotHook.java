package de.jeff_media.ChestSort.hooks;

import org.bukkit.inventory.ItemStack;

import com.shampaggon.crackshot.CSUtility;

import de.jeff_media.ChestSort.ChestSortPlugin;

public class CrackShotHook {
	
	final ChestSortPlugin plugin;
	CSUtility crackShotUtility = null;

	public CrackShotHook(ChestSortPlugin plugin) {
		this.plugin=plugin;
		
		if(plugin.hookCrackShot) {
			crackShotUtility = new CSUtility();
			plugin.getLogger().info("Succesfully hooked into CrackShot");
		}
	}
	
	// Will return when not a weapon
	public String getCrackShotWeaponName(ItemStack item) {
		if(crackShotUtility == null || !plugin.hookCrackShot) {
			return null;
		}
		
		// Will be null if not a weapon
		return crackShotUtility.getWeaponTitle(item);
	}
	
}
