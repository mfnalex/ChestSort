package de.jeff_media.chestsort.commands;

import de.jeff_media.chestsort.config.Messages;
import de.jeff_media.chestsort.handlers.Debugger;
import de.jeff_media.chestsort.data.PlayerSetting;
import de.jeff_media.chestsort.ChestSortPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

public class ChestSortCommand implements CommandExecutor {

	private final ChestSortPlugin plugin;
	final String noPermission;

	public ChestSortCommand(ChestSortPlugin plugin) {
		this.plugin = plugin;
		 noPermission = plugin.getCommand("sort").getPermissionMessage();
	}

	private void sendNoPermissionMessage(CommandSender sender) {
		if(noPermission != null && noPermission.length()>0) {
			sender.sendMessage(noPermission);
		}
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {

		// This command toggles automatic chest sorting for the player that runs the command
		if (!command.getName().equalsIgnoreCase("sort")) {
			return false;
		}

		if(!plugin.getConfig().getBoolean("allow-commands") && !sender.isOp()) {
			sendNoPermissionMessage(sender);
			return true;
		}
		
		// Reload command
		if(args.length>0 && args[0].equalsIgnoreCase("reload")) {
			if(!sender.hasPermission("chestsort.reload")) {
				sendNoPermissionMessage(sender);
				return true;
			}
			sender.sendMessage(ChatColor.GRAY + "Reloading ChestSort...");
			plugin.load(true);
			sender.sendMessage(ChatColor.GREEN + "ChestSort has been reloaded.");
			return true;
		}

		// Debug command
		if(args.length>0 && args[0].equalsIgnoreCase("debug")) {
			if(!sender.hasPermission("chestsort.debug")) {
				sendNoPermissionMessage(sender);
			}
			sender.sendMessage(ChatColor.RED+"ChestSort Debug mode enabled - I hope you know what you are doing!");
			plugin.setDebug(true);
			Debugger debugger = new Debugger(plugin);
			plugin.getServer().getPluginManager().registerEvents(debugger, plugin);
			plugin.debug("Debug mode activated through command by "+sender.getName());
			return true;
		}

		if(args.length>0 && args[0].equalsIgnoreCase("help")) {
			return false;
		}

			if (!(sender instanceof Player)) {
				
				if(args.length!=0) {
					if(args[0].equalsIgnoreCase("debug")) {
						plugin.setDebug(true);
						plugin.getLogger().info("ChestSort debug mode enabled.");
						return true;
					} 
				}
				
				sender.sendMessage(Messages.MSG_PLAYERSONLY);
				return true;
			}

			Player p = (Player) sender;
			
			// fix for Spigot's stupid /reload function
			plugin.registerPlayerIfNeeded(p);

			if(!plugin.getConfig().getBoolean("allow-automatic-sorting")) args=new String[] {"hotkeys"};
			
			// Settings GUI
			if(args.length>0) {
				if(args[0].equalsIgnoreCase("hotkey") || args[0].equalsIgnoreCase("hotkeys")) {
					
					if(!plugin.isHotkeyGUI()) {
						p.sendMessage(Messages.MSG_ERR_HOTKEYSDISABLED);
						return true;
					}
					
					plugin.getSettingsGUI().openGUI(p);
					
					return true;
				} 
				
			}
			// Settings GUI End
			
			PlayerSetting setting = plugin.getPerPlayerSettings().get(p.getUniqueId().toString());
			
			if(args.length>0
					 && !args[0].equalsIgnoreCase("toggle")
					 && !args[0].equalsIgnoreCase("on")
					 && !args[0].equalsIgnoreCase("off")) {
				p.sendMessage(String.format(Messages.MSG_INVALIDOPTIONS,"\""+args[0]+"\"","\"toggle\", \"on\", \"off\", \"hotkeys\""));
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
				p.sendMessage(Messages.MSG_ACTIVATED);
			} else {
				p.sendMessage(Messages.MSG_DEACTIVATED);
			}

			return true;
	}

}
