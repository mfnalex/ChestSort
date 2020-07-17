package de.jeff_media.ChestSort;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ChestSortInvSortCommand implements CommandExecutor {
	
	final ChestSortPlugin plugin;
	
	ChestSortInvSortCommand(ChestSortPlugin plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {

		// This command toggles automatic chest sorting for the player that runs the command
		if (!command.getName().equalsIgnoreCase("invsort")) {
			return false;
		}

		if(args.length>0 && args[0].equalsIgnoreCase("help")) {
			return false;
		}
		
		if (!(sender instanceof Player)) {
			sender.sendMessage(plugin.messages.MSG_PLAYERSONLY);
			return true;
		}
		
		Player p = (Player) sender;
		
		int start = 9;
		int end = 35;
		
		ChestSortPlayerSetting setting = plugin.perPlayerSettings.get(p.getUniqueId().toString());

		if(!plugin.getConfig().getBoolean("allow-automatic-inventory-sorting")) args=new String[]{"inv"};
		
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
				setting.enableInvSorting();
				p.sendMessage(plugin.messages.MSG_INVACTIVATED);
				return true;
			} else if(args[0].equalsIgnoreCase("off")) {
				setting.disableInvSorting();
				p.sendMessage(plugin.messages.MSG_INVDEACTIVATED);
				return true;
			} else if(args[0].equalsIgnoreCase("toggle")) {
				setting.toggleInvSorting();
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
		plugin.lgr.logSort(p, ChestSortLogger.SortCause.CMD_ISORT);
		plugin.organizer.sortInventory(p.getInventory(), start, end);
		p.sendMessage(plugin.messages.MSG_PLAYERINVSORTED);
		
		return true;
		
	}

}
