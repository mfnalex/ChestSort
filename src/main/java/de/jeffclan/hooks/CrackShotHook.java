package de.jeffclan.hooks;

import org.bukkit.inventory.ItemStack;

import com.shampaggon.crackshot.CSUtility;

import de.jeffclan.JeffChestSort.JeffChestSortPlugin;

public class CrackShotHook {
	
	JeffChestSortPlugin plugin;
	CSUtility crackShotUtility = null;

	public CrackShotHook(JeffChestSortPlugin plugin) {
		this.plugin=plugin;
		
		if(plugin.hookCrackShot) {
			crackShotUtility = new CSUtility();
			plugin.getLogger().info("Succesfully hooked into CrackShot");
		}
	}
	
	// Will return when not a weapon
	public String getCrackShotWeaponName(ItemStack item) {
		if(crackShotUtility == null || plugin.hookCrackShot==false) {
			return null;
		}
		
		// Will be null if not a weapon
		return crackShotUtility.getWeaponTitle(item);
	}
	
}
