package de.jeffclan.JeffChestSort;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class JeffChestSortPlugin extends JavaPlugin {

	Map<String, JeffChestSortPlayerSetting> PerPlayerSettings = new HashMap<String, JeffChestSortPlayerSetting>();
	JeffChestSortMessages msg;

	@Override
	public void onEnable() {
		createConfig();
		msg = new JeffChestSortMessages(this);
		getServer().getPluginManager().registerEvents(new JeffChestSortListener(this), this);
		JeffChestSortCommandExecutor commandExecutor = new JeffChestSortCommandExecutor(this);
		this.getCommand("chestsort").setExecutor(commandExecutor);
		
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
	}
	
	String getSortableString(ItemStack item) {
		return item.getType().name()
				+ ","
				+ String.valueOf(item.hashCode());
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			PerPlayerSettings.remove(uniqueId.toString());
		}
	}

}
