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
	JeffChestSortOrganizer organizer;
	String sortingMethod;
	int currentConfigVersion = 4;
	boolean debug = false;

	@Override
	public void onEnable() {
		createConfig();
		messages = new JeffChestSortMessages(this);
		organizer = new JeffChestSortOrganizer(this);
		sortingMethod = getConfig().getString("sorting-method","{itemsFirst},{name}");
		getServer().getPluginManager().registerEvents(new JeffChestSortListener(this), this);
		JeffChestSortCommandExecutor commandExecutor = new JeffChestSortCommandExecutor(this);
		this.getCommand("chestsort").setExecutor(commandExecutor);
		
		@SuppressWarnings("unused")
		Metrics metrics = new Metrics(this);
		
		getLogger().info("Current sorting method: "+sortingMethod);
		
		if(getConfig().getInt("config-version",0) != currentConfigVersion) {		
			getLogger().warning("========================================================");
			getLogger().warning("YOU ARE USING AN OLD CONFIG FILE!");
			getLogger().warning("This is not a problem, as ChestSort will just use the");
			getLogger().warning("default settings for unset values. However, if you want");
			getLogger().warning("to configure the new options, please go to");
			getLogger().warning("https://www.spigotmc.org/resources/1-13-chestsort.59773/");
			getLogger().warning("and replace your config.yml with the new one. You can");
			getLogger().warning("then insert your old changes into the new file.");
			getLogger().warning("========================================================");
		}
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
		getConfig().addDefault("sorting-method", "{itemsFirst},{name},{color}");
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
