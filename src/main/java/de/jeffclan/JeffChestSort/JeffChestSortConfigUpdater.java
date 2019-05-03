package de.jeffclan.JeffChestSort;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import de.jeffclan.utils.Utils;

public class JeffChestSortConfigUpdater {

	JeffChestSortPlugin plugin;

	public JeffChestSortConfigUpdater(JeffChestSortPlugin jeffChestSortPlugin) {
		this.plugin = jeffChestSortPlugin;
	}

	// Admins hate config updates. Just relax and let ChestSort update to the newest
	// config version
	// Don't worry! Your changes will be kept

	void updateConfig() {

		if(plugin.debug) plugin.getLogger().info("rename config.yml -> config.old.yml");
		Utils.renameFileInPluginDir(plugin, "config.yml", "config.old.yml");
		if(plugin.debug) plugin.getLogger().info("saving new config.yml");
		plugin.saveDefaultConfig();

		File oldConfigFile = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "config.old.yml");
		FileConfiguration oldConfig = new YamlConfiguration();

		try {
			oldConfig.load(oldConfigFile);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}

		Map<String, Object> oldValues = oldConfig.getValues(false);

		// Read default config to keep comments
		ArrayList<String> linesInDefaultConfig = new ArrayList<String>();
		try {

			Scanner scanner = new Scanner(
					new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "config.yml"));
			while (scanner.hasNextLine()) {
				linesInDefaultConfig.add(scanner.nextLine() + "");
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		ArrayList<String> newLines = new ArrayList<String>();
		for (String line : linesInDefaultConfig) {
			String newline = line;
			if (!line.startsWith("config-version:")) { // dont replace config-version
				for (String node : oldValues.keySet()) {
					if (line.startsWith(node + ":")) {

						String quotes = "";

						if (node.equalsIgnoreCase("sorting-method")) // needs single quotes
							quotes = "'";
						if (node.startsWith("message-")) // needs double quotes
							quotes = "\"";

						newline = node + ": " + quotes + oldValues.get(node).toString() + quotes;
						if(plugin.debug) plugin.getLogger().info("Updating config node " + newline);
						break;
					}
				}
			}
			newLines.add(newline);
		}

		FileWriter fw;
		String[] linesArray = newLines.toArray(new String[linesInDefaultConfig.size()]);
		try {
			fw = new FileWriter(plugin.getDataFolder().getAbsolutePath() + File.separator + "config.yml");
			for (int i = 0; i < linesArray.length; i++) {
				fw.write(linesArray[i] + "\n");
			}
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Utils.renameFileInPluginDir(plugin, "config.yml.default", "config.yml");

	}

}
