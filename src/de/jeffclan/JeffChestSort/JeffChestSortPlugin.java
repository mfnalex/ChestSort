package de.jeffclan.JeffChestSort;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import de.jeffclan.utils.Utils;

public class JeffChestSortPlugin extends JavaPlugin {

	Map<String, JeffChestSortPlayerSetting> PerPlayerSettings = new HashMap<String, JeffChestSortPlayerSetting>();
	JeffChestSortMessages messages;
	JeffChestSortOrganizer organizer;
	JeffChestSortUpdateChecker updateChecker;
	String sortingMethod;
	int currentConfigVersion = 5;
	boolean usingMatchingConfig = true;
	boolean debug = false;
	long updateCheckInterval = 86400; // in seconds. We check on startup and every 24 hours (if you never restart your
	// server)

	@Override
	public void onEnable() {
		
		if(debug) {
			System.out.println("======= ALL MATERIALS ======");
			for(Material mat : Material.values()) {
		
				System.out.println(mat.name().toLowerCase());
			}
			System.out.println("============================");
		}
		
		createConfig();
		saveDefaultCategories();
		messages = new JeffChestSortMessages(this);
		organizer = new JeffChestSortOrganizer(this);
		updateChecker = new JeffChestSortUpdateChecker(this);
		sortingMethod = getConfig().getString("sorting-method", "{itemsFirst},{name},{color}");
		getServer().getPluginManager().registerEvents(new JeffChestSortListener(this), this);
		JeffChestSortCommandExecutor commandExecutor = new JeffChestSortCommandExecutor(this);
		this.getCommand("chestsort").setExecutor(commandExecutor);

		



		getLogger().info("Current sorting method: " + sortingMethod);

		if (getConfig().getInt("config-version", 0) != currentConfigVersion) {
			getLogger().warning("========================================================");
			getLogger().warning("YOU ARE USING AN OLD CONFIG FILE!");
			getLogger().warning("This is not a problem, as ChestSort will just use the");
			getLogger().warning("default settings for unset values. However, if you want");
			getLogger().warning("to configure the new options, please go to");
			getLogger().warning("https://www.spigotmc.org/resources/1-13-chestsort.59773/");
			getLogger().warning("and replace your config.yml with the new one. You can");
			getLogger().warning("then insert your old changes into the new file.");
			getLogger().warning("========================================================");
			usingMatchingConfig = false;
		}

		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				if (getConfig().getBoolean("check-for-updates", true)) {
					updateChecker.checkForUpdate();
				}
			}
		}, 0L, updateCheckInterval * 20);
		
		
		
		@SuppressWarnings("unused")
		Metrics metrics = new Metrics(this);

		// metrics.addCustomChart(new Metrics.SimplePie("bukkit_version", () ->
		// getServer().getBukkitVersion()));
		metrics.addCustomChart(new Metrics.SimplePie("sorting_method", () -> sortingMethod));
		metrics.addCustomChart(new Metrics.SimplePie("config_version",
				() -> Integer.toString(getConfig().getInt("config-version", 0))));
		metrics.addCustomChart(new Metrics.SimplePie("check_for_updates",
				() -> Boolean.toString(getConfig().getBoolean("check-for-updates", true))));
		metrics.addCustomChart(new Metrics.SimplePie("show_message_when_using_chest",
				() -> Boolean.toString(getConfig().getBoolean("show-message-when-using-chest"))));
		metrics.addCustomChart(new Metrics.SimplePie("show_message_again_after_logout",
				() -> Boolean.toString(getConfig().getBoolean("show-message-again-after-logout"))));
		metrics.addCustomChart(new Metrics.SimplePie("sorting_enabled_by_default",
				() -> Boolean.toString(getConfig().getBoolean("sorting-enabled-by-default"))));
		metrics.addCustomChart(new Metrics.SimplePie("using_matching_config_version",
				() ->Boolean.toString(usingMatchingConfig)));

	}

	private void saveDefaultCategories() {
		String[] defaultCategories = { "900-valuables","910-tools","920-combat","930-brewing","940-food","950-redstone","960-wood","970-stone","980-plants","981-corals" };

		for (String category : defaultCategories) {

			if(debug) getLogger().info("Trying to save default category file: " + category);

			FileOutputStream fopDefault = null;
			File fileDefault;
			// String content = "This is the text content";

			try {
				InputStream in = getClass().getResourceAsStream("/de/jeffclan/utils/categories/" + category + ".default.txt");
				// Reader fr = new InputStreamReader(in, "utf-8");

				fileDefault = new File(getDataFolder().getAbsolutePath() + File.separator + "categories" + File.separator + category + ".txt");
				fopDefault = new FileOutputStream(fileDefault);

				// if file doesnt exists, then create it
				//if (!fileDefault.getAbsoluteFile().exists()) {
					fileDefault.createNewFile();
				//}

				// get the content in bytes
				byte[] contentInBytes = Utils.getBytes(in);

				fopDefault.write(contentInBytes);
				fopDefault.flush();
				fopDefault.close();

				// System.out.println("Done");

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (fopDefault != null) {
						fopDefault.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
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
		if (!playerDataFolder.exists()) {
			playerDataFolder.mkdir();
		}
		File categoriesFolder = new File(getDataFolder().getPath() + File.separator + "categories");
		if (!categoriesFolder.exists()) {
			categoriesFolder.mkdir();
		}

		getConfig().addDefault("sorting-enabled-by-default", false);
		getConfig().addDefault("show-message-when-using-chest", true);
		getConfig().addDefault("show-message-when-using-chest-and-sorting-is-enabled", false);
		getConfig().addDefault("show-message-again-after-logout", true);
		getConfig().addDefault("sorting-method", "{category},{itemsFirst},{name},{color}");
		getConfig().addDefault("check-for-updates", true);
	}

	void unregisterPlayer(Player p) {
		UUID uniqueId = p.getUniqueId();
		if (PerPlayerSettings.containsKey(uniqueId.toString())) {
			JeffChestSortPlayerSetting setting = PerPlayerSettings.get(p.getUniqueId().toString());
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
