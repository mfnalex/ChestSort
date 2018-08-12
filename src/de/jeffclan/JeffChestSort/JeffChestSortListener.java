package de.jeffclan.JeffChestSort;

import java.util.Arrays;
import java.util.UUID;
import java.io.File;
import java.io.IOException;

import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.ShulkerBox;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class JeffChestSortListener implements Listener {

		
		JeffChestSortPlugin plugin;
		
		JeffChestSortListener(JeffChestSortPlugin plugin){
			this.plugin = plugin;
		}
		
		@EventHandler
		public void onPlayerJoin(PlayerJoinEvent event) {
			UUID uniqueId=event.getPlayer().getUniqueId();
			if(!plugin.PerPlayerSettings.containsKey(uniqueId.toString())) {
				
				File playerFile = new File(plugin.getDataFolder() + File.separator + "playerdata" ,event.getPlayer().getUniqueId().toString() + ".yml");
				YamlConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
				
				boolean activeForThisPlayer;
				
				if(!playerFile.exists()) {
					activeForThisPlayer = plugin.getConfig().getBoolean("sorting-enabled-by-default");
				} else {
					activeForThisPlayer = playerConfig.getBoolean("sortingEnabled");
				}
				
				JeffChestSortPlayerSetting newSettings = new JeffChestSortPlayerSetting(activeForThisPlayer);
				if (!plugin.getConfig().getBoolean("show-message-again-after-logout")) {
					newSettings.hasSeenMessage = playerConfig.getBoolean("hasSeenMessage");
				}
				plugin.PerPlayerSettings.put(uniqueId.toString(),newSettings);
				
				
			}
		}
		
		@EventHandler
		public void onPlayerQuit(PlayerQuitEvent event) {
			UUID uniqueId=event.getPlayer().getUniqueId();
			if(plugin.PerPlayerSettings.containsKey(uniqueId.toString())) {
				JeffChestSortPlayerSetting setting = plugin.PerPlayerSettings.get(event.getPlayer().getUniqueId().toString());
				File playerFile = new File(plugin.getDataFolder() + File.separator + "playerdata" ,event.getPlayer().getUniqueId().toString() + ".yml");
				YamlConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
				playerConfig.set("sortingEnabled", setting.sortingEnabled);
				playerConfig.set("hasSeenMessage", setting.hasSeenMessage);
				try {
					playerConfig.save(playerFile);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				plugin.PerPlayerSettings.remove(uniqueId.toString());
			}
		}
		
		@EventHandler
		public void onInventoryClose(InventoryCloseEvent event)
		{

			if(!(event.getPlayer() instanceof Player)) {
				return;
			}
			
			Player p = (Player) event.getPlayer();
			JeffChestSortPlayerSetting setting = plugin.PerPlayerSettings.get(p.getUniqueId().toString());
			
			if(!(event.getInventory().getHolder() instanceof Chest)
					&& !(event.getInventory().getHolder() instanceof DoubleChest)
					&& !(event.getInventory().getHolder() instanceof ShulkerBox))
			{
				return;
			}
			
			if(!plugin.sortingEnabled(p)) {
				if(!setting.hasSeenMessage) {
					setting.hasSeenMessage = true;
					if(plugin.getConfig().getBoolean("show-message-when-using-chest")) {
						//p.sendMessage(JeffChestSortMessages.MSG_COMMANDMESSAGE);
						p.sendMessage(plugin.msg.MSG_COMMANDMESSAGE);
					}
				}
				return;
			}
			
			Inventory inv = event.getInventory();
			ItemStack[] items = inv.getContents();
			event.getInventory().clear();
			String[] itemList = new String[inv.getSize()];
			
			int i=0;
			for(ItemStack item : items)
			{
				if(item!=null)
				{
					itemList[i] = item.getType().name() + "," + String.valueOf(item.hashCode());
					i++;
				}
			}
			
			// count all items that are not null
			int count = 0;
			for(String s: itemList)
			{
				if(s!=null)
				{
					count++;
				}
			}
			
			// create new array with just the size we need
			String[] shortenedArray = new String[count];
			
			// fill new array with items
			for(int j=0; j < count; j++)
			{
				shortenedArray[j] = itemList[j];			
			}
			
			// sort array alphabetically
			Arrays.sort(shortenedArray);
			
			// put everything back in the inventory
			for(String s : shortenedArray)
			{
				//System.out.println(s);
				for(ItemStack item : items) {
					if(item!=null && s != null) {
						if(item.hashCode() == Integer.parseInt(s.split(",")[1])) {
							inv.addItem(item);
							item = null;
							s = null;
						}
					}
				}		
			}
		}
	}
