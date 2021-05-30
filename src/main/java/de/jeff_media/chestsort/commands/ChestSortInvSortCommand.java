package de.jeff_media.chestsort.commands;

import de.jeff_media.chestsort.config.Messages;
import de.jeff_media.chestsort.handlers.ChestSortLogger;
import de.jeff_media.chestsort.data.ChestSortPlayerSetting;
import de.jeff_media.chestsort.ChestSortPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ChestSortInvSortCommand implements CommandExecutor {
	
	final ChestSortPlugin plugin;
	
	public ChestSortInvSortCommand(ChestSortPlugin plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {

		Player p = null;

		if(args.length>0 && args[0].equalsIgnoreCase("help")) {
			return false;
		}
		
		if (!(sender instanceof Player)) {

			if(args.length==0) {
				sender.sendMessage(Messages.MSG_PLAYERSONLY);
				return true;
			}
			// Console can sort player's inventories
			if(Bukkit.getPlayer(args[0]) == null) {
				sender.sendMessage("Could not find player "+args[0]);
				return true;
			}

			p = Bukkit.getPlayer(args[0]);

			if(args.length>1) {
				args = new String[] { args[1] };
			} else {
				args = new String[0];
			}

			//sender.sendMessage(Messages.MSG_PLAYERSONLY);
			//return true;
		}
		
		if(p == null) p = (Player) sender;
		
		int start = 9;
		int end = 35;
		
		ChestSortPlayerSetting setting = plugin.perPlayerSettings.get(p.getUniqueId().toString());

		if(!plugin.getConfig().getBoolean("allow-automatic-inventory-sorting")) {
			if(args.length==0 || args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("toggle") ) {
				args = new String[]{"inv"};
			}
		}
		
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
				p.sendMessage(Messages.MSG_INVACTIVATED);
				return true;
			} else if(args[0].equalsIgnoreCase("off")) {
				setting.disableInvSorting();
				p.sendMessage(Messages.MSG_INVDEACTIVATED);
				return true;
			} else if(args[0].equalsIgnoreCase("toggle")) {
				setting.toggleInvSorting();
				if(setting.invSortingEnabled) {
					p.sendMessage(Messages.MSG_INVACTIVATED);
				} else {
					p.sendMessage(Messages.MSG_INVDEACTIVATED);
				}
				return true;
			}
			else {
				p.sendMessage(String.format(Messages.MSG_INVALIDOPTIONS,"\""+args[0]+"\"","\"on\", \"off\", \"toggle\", \"inv\", \"hotbar\", \"all\""));
				return true;
			}
		}
		plugin.lgr.logSort(p, ChestSortLogger.SortCause.CMD_ISORT);
		plugin.organizer.sortInventory(p.getInventory(), start, end);
		p.sendMessage(Messages.MSG_PLAYERINVSORTED);
		
		return true;
		
	}

}
