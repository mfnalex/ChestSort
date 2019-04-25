package de.jeffclan.JeffChestSort;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class JeffChestSortUpdateChecker {

	private JeffChestSortPlugin plugin;

	JeffChestSortUpdateChecker(JeffChestSortPlugin plugin) {
		this.plugin = plugin;
	}

	String latestVersionLink = "https://api.jeff-media.de/chestsort/chestsort-latest-version.txt";
	String downloadLink = "https://www.spigotmc.org/resources/1-13-chestsort.59773/";
	private String currentVersion = "undefined";
	private String latestVersion = "undefined";

	void sendUpdateMessage(Player p) {
		if(!latestVersion.equals("undefined")) {
		if (!currentVersion.equals(latestVersion)) {
			p.sendMessage(ChatColor.GRAY + "There is a new version of " + ChatColor.GOLD + "ChestSort" + ChatColor.GRAY
					+ " available.");
					p.sendMessage(ChatColor.GRAY + "Please download at " + downloadLink);
		}
		}
	}

	void checkForUpdate() {
		
		plugin.getLogger().info("Checking for available updates...");

		try {

			HttpURLConnection httpcon = (HttpURLConnection) new URL(latestVersionLink).openConnection();
			httpcon.addRequestProperty("User-Agent", "ChestSort/"+plugin.getDescription().getVersion());

			BufferedReader reader = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));

			String inputLine = reader.readLine().trim();

			latestVersion = inputLine;
			currentVersion = plugin.getDescription().getVersion().trim();

			
			if (latestVersion.equals(currentVersion)) {
				plugin.getLogger().info("You are using the latest version of ChestSort.");
			} else {
				plugin.getLogger().warning("========================================================");
				plugin.getLogger().warning("There is a new version of ChestSort available!");
				plugin.getLogger().warning("Latest : " + inputLine);
				plugin.getLogger().warning("Current: " + currentVersion);
				plugin.getLogger().warning("Please update to the newest version. Download:");
				plugin.getLogger().warning(downloadLink);
				plugin.getLogger().warning("========================================================");
			}

			reader.close();
		} catch (Exception e) {
			plugin.getLogger().warning("Could not check for updates.");
		}

	}

}
