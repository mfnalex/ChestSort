package de.jeffclan.JeffChestSort;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

public class JeffChestSortPlugin extends JavaPlugin {

	boolean debug = false;
	Map<String, JeffChestSortPlayerSetting> PerPlayerSettings = new HashMap<String, JeffChestSortPlayerSetting>();
	JeffChestSortMessages msg;

	@Override
	public void onEnable() {
		createConfig();
		msg = new JeffChestSortMessages(this);
		getServer().getPluginManager().registerEvents(new JeffChestSortListener(this), this);
		JeffChestSortCommandExecutor commandExecutor = new JeffChestSortCommandExecutor(this);
		this.getCommand("chestsort").setExecutor(commandExecutor);
		
	    try {
	        Metrics metrics = new Metrics(this);
	        metrics.start();
	    } catch (IOException e) {
	        getLogger().warning("Error while trying to start Metrics. This is not important.");
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
	}
	
	String getSortableString(ItemStack item) {
		return item.getType().name()
				/*+ ","
				+ (10000-item.getDurability())*/
				+ ","
				+ String.valueOf(item.hashCode());
	}

}
