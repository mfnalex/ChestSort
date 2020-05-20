package de.jeffclan.utils;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventoryHelper {
	
	private static final int maxInventorySize=54;
	private static final int playerInvStartSlot=9; // Inclusive
	private static final int playerInvEndSlot=35; // Inclusive
	
	public static void updateInventoryView(InventoryClickEvent event) {
		for(HumanEntity viewer : event.getViewers()) {
			if(viewer instanceof Player) {
				Player playerViewer = (Player) viewer;
				playerViewer.updateInventory();
			}
		}
	}
	
	public static void updateInventoryView(Inventory inventory) {
		for(HumanEntity viewer : inventory.getViewers()) {
			if(viewer instanceof Player) {
				Player playerViewer = (Player) viewer;
				playerViewer.updateInventory();
			}
		}
	}
	
	public static void stuffInventoryIntoAnother(Inventory source, Inventory destination) {
		
		ArrayList<ItemStack> leftovers = new ArrayList<ItemStack>();
		
		for(int i = 0;i<source.getSize();i++) {
			
			ItemStack current = source.getItem(i);
			
			if(current == null) continue;
			
			source.clear(i);
			HashMap<Integer,ItemStack> currentLeftovers = destination.addItem(current);
			
			for(ItemStack currentLeftover : currentLeftovers.values()) {
				leftovers.add(currentLeftover);
			}
		}
		
		source.addItem(leftovers.toArray(new ItemStack[leftovers.size()]));
		updateInventoryView(destination);
		updateInventoryView(source);
		
	}

	public static void stuffPlayerInventoryIntoAnother(PlayerInventory source,
			Inventory destination) {
		Inventory temp = Bukkit.createInventory(null, maxInventorySize);
		for(int i = playerInvStartSlot;i<=playerInvEndSlot;i++) {
			if(source.getItem(i)==null) continue;
			temp.addItem(source.getItem(i));
			source.clear(i);
		}
		stuffInventoryIntoAnother(temp,destination);
	}
	

}
