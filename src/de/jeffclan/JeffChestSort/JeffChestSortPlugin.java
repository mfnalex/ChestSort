package de.jeffclan.JeffChestSort;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class JeffChestSortPlugin extends JavaPlugin {

	Map<String, JeffChestSortPlayerSetting> PerPlayerSettings = new HashMap<String, JeffChestSortPlayerSetting>();
	JeffChestSortMessages messages;

	@Override
	public void onEnable() {
		createConfig();
		messages = new JeffChestSortMessages(this);
		getServer().getPluginManager().registerEvents(new JeffChestSortListener(this), this);
		JeffChestSortCommandExecutor commandExecutor = new JeffChestSortCommandExecutor(this);
		this.getCommand("chestsort").setExecutor(commandExecutor);
		
		@SuppressWarnings("unused")
		Metrics metrics = new Metrics(this);
		
	}
	
	@Override
	public void onDisable() {
		for (Player p : getServer().getOnlinePlayers()) {
			unregisterPlayer(p);
		}
	}

	public boolean sortingEnabled(Player p) {
		return PerPlayerSettings.get(p.getUniqueId().toString()).sortingEnabled;
	}

	void createConfig() {
		this.saveDefaultConfig();
		File playerDataFolder = new File(getDataFolder().getPath() + File.separator + "playerdata");
		if (!playerDataFolder.exists())
			playerDataFolder.mkdir();
		
		getConfig().addDefault("sorting-enabled-by-default", false);
		getConfig().addDefault("show-message-when-using-chest", true);
		getConfig().addDefault("show-message-when-using-chest-and-sorting-is-enabled", false);
		getConfig().addDefault("show-message-again-after-logout", true);
		getConfig().addDefault("message-when-using-chest","&7Hint: Type &6/chestsort&7 to enable automatic chest sorting.");
		getConfig().addDefault("message-when-using-chest2","&7Hint: Type &6/chestsort&7 to disable automatic chest sorting.");
		getConfig().addDefault("message-sorting-disabled","&7Automatic chest sorting has been &cdisabled&7.&r");
		getConfig().addDefault("message-sorting-enabled","&7Automatic chest sorting has been &aenabled&7.&r");
		getConfig().addDefault("message-error-players-only","&cError: This command can only be run by players.&r");
	}
	
	void unregisterPlayer(Player p) {
		UUID uniqueId = p.getUniqueId();
		if (PerPlayerSettings.containsKey(uniqueId.toString())) {
			JeffChestSortPlayerSetting setting = PerPlayerSettings
					.get(p.getUniqueId().toString());
			File playerFile = new File(getDataFolder() + File.separator + "playerdata",
					p.getUniqueId().toString() + ".yml");
			YamlConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
			playerConfig.set("sortingEnabled", setting.sortingEnabled);
			playerConfig.set("hasSeenMessage", setting.hasSeenMessage);
			try {
				playerConfig.save(playerFile);
			} catch (IOException e) {
				e.printStackTrace();
			}

			PerPlayerSettings.remove(uniqueId.toString());
		}
	}

}
