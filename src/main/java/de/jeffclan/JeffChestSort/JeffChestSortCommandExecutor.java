package de.jeffclan.JeffChestSort;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JeffChestSortCommandExecutor implements CommandExecutor {

	JeffChestSortPlugin plugin;

	JeffChestSortCommandExecutor(JeffChestSortPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		// This command toggles automatic chest sorting for the player that runs the command
		if (command.getName().equalsIgnoreCase("chestsort")) {

			if (!(sender instanceof Player)) {
				
				if(args.length!=0) {
					if(args[0].equalsIgnoreCase("debug")) {
						plugin.debug=true;
						sender.sendMessage("ChestSort debug mode enabled.");
						return true;
					} else if(args[0].equalsIgnoreCase("reload")) {
						// TODO: EXPERIMENTAL
						plugin.onDisable();
						plugin.onEnable();
					
					}
				}
				
				sender.sendMessage(plugin.messages.MSG_PLAYERSONLY);
				return true;
			}

			Player p = (Player) sender;
			
			// fix for Spigot's stupid /reload function
			plugin.listener.registerPlayerIfNeeded(p);
			
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
			
			JeffChestSortPlayerSetting setting = plugin.PerPlayerSettings.get(p.getUniqueId().toString());
			
			if(args.length>0
					 && !args[0].equalsIgnoreCase("toggle")
					 && !args[0].equalsIgnoreCase("on")
					 && !args[0].equalsIgnoreCase("off")) {
				p.sendMessage(String.format(plugin.messages.MSG_INVALIDOPTIONS,"\""+args[0]+"\"","\"toggle\", \"on\", \"off\", \"hotkeys\""));
				return true;
			}
			if(args.length==0 || args[0].equalsIgnoreCase("toggle")) {
				setting.sortingEnabled = !setting.sortingEnabled;
			}
			else if(args[0].equalsIgnoreCase("on")) {
				setting.sortingEnabled = true;
			}
			else if(args[0].equalsIgnoreCase("off")) {
				setting.sortingEnabled = false;
			}
			setting.hasSeenMessage=true;

			if (setting.sortingEnabled) {
				p.sendMessage(plugin.messages.MSG_ACTIVATED);
			} else {
				p.sendMessage(plugin.messages.MSG_DEACTIVATED);
			}

			return true;

		} else if(command.getName().equalsIgnoreCase("invsort")) {
			// This command sorts the player's inventory
			
			if (!(sender instanceof Player)) {
				sender.sendMessage(plugin.messages.MSG_PLAYERSONLY);
				return true;
			}
			
			Player p = (Player) sender;
			
			int start = 9;
			int end = 35;
			
			JeffChestSortPlayerSetting setting = plugin.PerPlayerSettings.get(p.getUniqueId().toString());
			
			if(args.length>0) {
				if(args[0].equalsIgnoreCase("all")) {
					start=0;
					end=35;
				} else if(args[0].equalsIgnoreCase("hotbar")) {
					start=0;
					end=8;
				} else if(args[0].equalsIgnoreCase("inv")) {
					start=9;
					end=35;
				} else if(args[0].equalsIgnoreCase("on")) {
					setting.invSortingEnabled = true;
					p.sendMessage(plugin.messages.MSG_INVACTIVATED);
					return true;
				} else if(args[0].equalsIgnoreCase("off")) {
					setting.invSortingEnabled = false;
					p.sendMessage(plugin.messages.MSG_INVDEACTIVATED);
					return true;
				} else if(args[0].equalsIgnoreCase("toggle")) {
					setting.invSortingEnabled = !setting.invSortingEnabled;
					if(setting.invSortingEnabled) {
						p.sendMessage(plugin.messages.MSG_INVACTIVATED);
					} else {
						p.sendMessage(plugin.messages.MSG_INVDEACTIVATED);
					}
					return true;
				}
				else {
					p.sendMessage(String.format(plugin.messages.MSG_INVALIDOPTIONS,"\""+args[0]+"\"","\"on\", \"off\", \"toggle\", \"inv\", \"hotbar\", \"all\""));
					return true;
				}
			}
			
			plugin.sortInventory(p.getInventory(), start, end);
			p.sendMessage(plugin.messages.MSG_PLAYERINVSORTED);
			
			return true;
			
		}

		return false;
	}

}
