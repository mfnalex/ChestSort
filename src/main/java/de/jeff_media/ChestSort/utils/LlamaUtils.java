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
			if(inv.getHolder() instanceof ChestedHorse) {
				System.out.println("This inventory belongs to a llama");
				return true;
			}
			System.out.println("This inventory does NOT belong to a llama");
			return false;
		}
		
}
