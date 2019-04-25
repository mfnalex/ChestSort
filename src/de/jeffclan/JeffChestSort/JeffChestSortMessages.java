package de.jeffclan.JeffChestSort;

import org.bukkit.ChatColor;

public class JeffChestSortMessages {

	JeffChestSortPlugin plugin;

	final String MSG_ACTIVATED, MSG_DEACTIVATED, MSG_COMMANDMESSAGE, MSG_COMMANDMESSAGE2, MSG_PLAYERSONLY;

	JeffChestSortMessages(JeffChestSortPlugin plugin) {
		this.plugin = plugin;

		MSG_ACTIVATED = ChatColor.translateAlternateColorCodes('&', plugin.getConfig()
				.getString("message-sorting-enabled", "&7Automatic chest sorting has been &aenabled&7.&r"));

		MSG_DEACTIVATED = ChatColor.translateAlternateColorCodes('&', plugin.getConfig()
				.getString("message-sorting-disabled", "&7Automatic chest sorting has been &cdisabled&7.&r"));

		MSG_COMMANDMESSAGE = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString(
				"message-when-using-chest", "&7Hint: Type &6/chestsort&7 to enable automatic chest sorting."));

		MSG_COMMANDMESSAGE2 = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString(
				"message-when-using-chest2", "&7Hint: Type &6/chestsort&7 to disable automatic chest sorting."));

		MSG_PLAYERSONLY = ChatColor.translateAlternateColorCodes('&', plugin.getConfig()
				.getString("message-error-players-only", "&cError: This command can only be run by players.&r"));
	}

}
