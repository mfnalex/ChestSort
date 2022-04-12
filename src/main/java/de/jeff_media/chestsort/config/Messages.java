package de.jeff_media.chestsort.config;

import de.jeff_media.chestsort.ChestSortPlugin;
import de.jeff_media.jefflib.TextUtils;
import org.bukkit.ChatColor;

public class Messages {
	public static String MSG_GUI_LEFTCLICKOUTSIDE, MSG_CONTAINER_SORTED;

    // Messages can be customized in the config.yml
	// To avoid problems with missing messages in the config, the default messages
	// are
	// hardcoded.
	// When creating pull requests that feature a message to the player, please
	// stick to this scheme

	public static String MSG_ACTIVATED, MSG_DEACTIVATED, MSG_INVACTIVATED, MSG_INVDEACTIVATED, MSG_COMMANDMESSAGE, MSG_COMMANDMESSAGE2, MSG_PLAYERSONLY,
			MSG_PLAYERINVSORTED, MSG_INVALIDOPTIONS;

	public static String MSG_GUI_ENABLED, MSG_GUI_DISABLED;

	public static String MSG_GUI_MIDDLECLICK, MSG_GUI_SHIFTCLICK, MSG_GUI_DOUBLECLICK, MSG_GUI_SHIFTRIGHTCLICK, MSG_GUI_LEFTCLICK, MSG_GUI_RIGHTCLICK;

	public static String MSG_ERR_HOTKEYSDISABLED;

	public Messages() {

		ChestSortPlugin plugin = ChestSortPlugin.getInstance();

		MSG_CONTAINER_SORTED = TextUtils.format( plugin.getConfig()
				.getString("message-container-sorted","&aContainer sorted!"));

		MSG_ACTIVATED = TextUtils.format( plugin.getConfig()
				.getString("message-sorting-enabled", "&7Automatic chest sorting has been &aenabled&7.&r"));

		MSG_DEACTIVATED = TextUtils.format( plugin.getConfig()
				.getString("message-sorting-disabled", "&7Automatic chest sorting has been &cdisabled&7.&r"));
		
		MSG_INVACTIVATED = TextUtils.format( plugin.getConfig()
				.getString("message-inv-sorting-enabled", "&7Automatic inventory sorting has been &aenabled&7.&r"));

		MSG_INVDEACTIVATED = TextUtils.format( plugin.getConfig()
				.getString("message-inv-sorting-disabled", "&7Automatic inventory sorting has been &cdisabled&7.&r"));

		MSG_COMMANDMESSAGE = TextUtils.format( plugin.getConfig().getString(
				"message-when-using-chest", "&7Hint: Type &6/chestsort&7 to enable automatic chest sorting."));

		MSG_COMMANDMESSAGE2 = TextUtils.format( plugin.getConfig().getString(
				"message-when-using-chest2", "&7Hint: Type &6/chestsort&7 to disable automatic chest sorting."));

		MSG_PLAYERSONLY = TextUtils.format( plugin.getConfig()
				.getString("message-error-players-only", "&cError: This command can only be run by players.&r"));

		MSG_PLAYERINVSORTED = TextUtils.format(
				plugin.getConfig().getString("message-player-inventory-sorted", "&7Your inventory has been sorted."));

		MSG_INVALIDOPTIONS = TextUtils.format( plugin.getConfig()
				.getString("message-error-invalid-options", "&cError: Unknown option %s. Valid options are %s."));
		
		MSG_GUI_ENABLED = TextUtils.format( plugin.getConfig()
				.getString("message-gui-enabled","&aEnabled"));
		
		MSG_GUI_DISABLED = TextUtils.format( plugin.getConfig()
				.getString("message-gui-disabled","&cDisabled"));
		
		MSG_GUI_MIDDLECLICK = TextUtils.format( plugin.getConfig()
				.getString("message-gui-middle-click","Middle-Click"));
		
		MSG_GUI_SHIFTCLICK = TextUtils.format( plugin.getConfig()
				.getString("message-gui-shift-click","Shift + Click"));
		
		MSG_GUI_DOUBLECLICK = TextUtils.format( plugin.getConfig()
				.getString("message-gui-double-click","Double-Click"));

		MSG_GUI_LEFTCLICKOUTSIDE = TextUtils.format( plugin.getConfig()
				.getString("message-gui-left-click-outside", "Left-Click"));
		
		MSG_GUI_SHIFTRIGHTCLICK = TextUtils.format( plugin.getConfig()
				.getString("message-gui-shift-right-click","Shift + Right-Click"));
		
		MSG_GUI_LEFTCLICK = TextUtils.format( plugin.getConfig().getString("message-gui-left-click","Fill Chest (Left-Click/Double-Left-Click)"));
		
		MSG_GUI_RIGHTCLICK = TextUtils.format( plugin.getConfig().getString("message-gui-right-click","Unload Chest (Right-Click/Double-Right-Click)"));
		
		MSG_ERR_HOTKEYSDISABLED = ChatColor.RED + "[ChestSort] Hotkeys have been disabled by the admin.";
	}

}
