package de.jeff_media.ChestSort.utils;

import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Donkey;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Mule;
import org.bukkit.inventory.Inventory;

public class LlamaUtils {

		public static int getLlamaChestSize(ChestedHorse llama) {
			if(llama==null) return -1;
			if(!llama.isCarryingChest()) return -1;
			if(llama instanceof Llama) {
				return ((Llama) llama).getStrength()*3;
			}
			if(llama instanceof Donkey || llama instanceof Mule) {
				return 15;
			}
			return -1;
		}
		
		public static boolean belongsToLlama(Inventory inv) {
			if(inv.getHolder() != null && inv.getHolder().getClass().getName().endsWith("CraftLlama")) {
				return true;
			}
			return false;
		}
		
}
