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
				sender.sendMessage(plugin.messages.MSG_PLAYERSONLY);
				return true;
			}

			Player p = (Player) sender;
			
			// fix for Spigot's stupid /reload function
			plugin.listener.registerPlayerIfNeeded(p);
			
			JeffChestSortPlayerSetting setting = plugin.PerPlayerSettings.get(p.getUniqueId().toString());
			setting.sortingEnabled = !setting.sortingEnabled;
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
			
			plugin.sortInventory(p.getInventory(), 9, 35);
			p.sendMessage(plugin.messages.MSG_PLAYERINVSORTED);
			
			return true;
			
		}

		return false;
	}

}
