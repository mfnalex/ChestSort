package de.jeffclan.JeffChestSort;

import org.bukkit.ChatColor;

public class JeffChestSortMessages {
	
	JeffChestSortPlugin plugin;
	
	final String MSG_ACTIVATED, MSG_DEACTIVATED, MSG_COMMANDMESSAGE, MSG_PLAYERSONLY;
	
	JeffChestSortMessages(JeffChestSortPlugin plugin) {
		this.plugin=plugin;
		
		
		MSG_ACTIVATED=ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("message-sorting-enabled"));
		MSG_DEACTIVATED=ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("message-sorting-disabled"));
		MSG_COMMANDMESSAGE=ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("message-when-using-chest"));
		MSG_PLAYERSONLY=ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("message-error-players-only"));
	}
	
	
}
