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
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] arg3) {

		if (arg1.getName().equalsIgnoreCase("chestsort")) {

			//			if(arg3.length>0) {
			//				if(arg3[0].equalsIgnoreCase("reload")) {
			//					if(sender.hasPermission("chestsort.reload")) {
			//						/* Reload Configuration */
			//						plugin.getServer().getPluginManager().disablePlugin(plugin);
			//						plugin.getServer().getPluginManager().enablePlugin(plugin);
			//						sender.sendMessage(ChatColor.GOLD + "ChestSort " + ChatColor.GRAY + plugin.getDescription().getVersion() + " has been reloaded.");
			//						return true;
			//					}
			//				}
			//			}

			if (!(sender instanceof Player)) {
				sender.sendMessage(plugin.messages.MSG_PLAYERSONLY);
				return true;
			}

			Player p = (Player) sender;
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

		}

		return false;
	}

}
