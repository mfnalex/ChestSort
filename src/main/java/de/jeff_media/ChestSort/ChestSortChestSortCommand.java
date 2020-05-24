package de.jeff_media.ChestSort;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChestSortChestSortCommand implements CommandExecutor {

	ChestSortPlugin plugin;

	ChestSortChestSortCommand(ChestSortPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		// This command toggles automatic chest sorting for the player that runs the command
		if (!command.getName().equalsIgnoreCase("chestsort")) {
			return false;
		}

			if (!(sender instanceof Player)) {
				
				if(args.length!=0) {
					if(args[0].equalsIgnoreCase("debug")) {
						plugin.debug=true;
						plugin.getLogger().info("ChestSort debug mode enabled.");
						return true;
					} 
				}
				
				sender.sendMessage(plugin.messages.MSG_PLAYERSONLY);
				return true;
			}

			Player p = (Player) sender;
			
			// fix for Spigot's stupid /reload function
			plugin.registerPlayerIfNeeded(p);
			
			// Settings GUI
			if(args.length>0) {
				if(args[0].equalsIgnoreCase("hotkey") || args[0].equalsIgnoreCase("hotkeys")) {
					
//					if(plugin.hotkeyGUI==false) {
//						p.sendMessage(plugin.messages.MSG_ERR_HOTKEYSDISABLED);
//						return true;
//					}
					
					plugin.settingsGUI.openGUI(p);
					
					return true;
				} 
				
			}
			// Settings GUI End
			
			ChestSortPlayerSetting setting = plugin.perPlayerSettings.get(p.getUniqueId().toString());
			
			if(args.length>0
					 && !args[0].equalsIgnoreCase("toggle")
					 && !args[0].equalsIgnoreCase("on")
					 && !args[0].equalsIgnoreCase("off")) {
				p.sendMessage(String.format(plugin.messages.MSG_INVALIDOPTIONS,"\""+args[0]+"\"","\"toggle\", \"on\", \"off\", \"hotkeys\""));
				return true;
			}
			if(args.length==0 || args[0].equalsIgnoreCase("toggle")) {
				setting.toggleChestSorting();
			}
			else if(args[0].equalsIgnoreCase("on")) {
				setting.enableChestSorting();
			}
			else if(args[0].equalsIgnoreCase("off")) {
				setting.disableChestSorting();
			}
			setting.hasSeenMessage=true;

			if (setting.sortingEnabled) {
				p.sendMessage(plugin.messages.MSG_ACTIVATED);
			} else {
				p.sendMessage(plugin.messages.MSG_DEACTIVATED);
			}

			return true;
	}

}
