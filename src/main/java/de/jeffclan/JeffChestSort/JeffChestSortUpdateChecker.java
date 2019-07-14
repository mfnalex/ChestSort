package de.jeffclan.JeffChestSort;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class JeffChestSortUpdateChecker {

	// This checks for updates. A txt file is downloaded. If the txt file contains a
	// string that is unequal to the currently used plugin's version, a message is
	// printed in the console.
	// The listener will also ensure that OPs will be notified on join. When the
	// update checker could not complete the request, e.g. when the JEFF
	// Media GbR API server is offline, or if you have no internet connection, a
	// warning will be printed in the console.

	private JeffChestSortPlugin plugin;

	JeffChestSortUpdateChecker(JeffChestSortPlugin plugin) {
		this.plugin = plugin;
	}

	// This text file always contains a string with the latest version, e.g. 3.7.1
	String latestVersionLink = "https://api.jeff-media.de/chestsort/chestsort-latest-version.txt";

	String downloadLink = "https://www.chestsort.de";
	private String currentVersion = "undefined";
	private String latestVersion = "undefined";

	void sendUpdateMessage(Player p) {
		if (!latestVersion.equals("undefined")) {
			if (!currentVersion.equals(latestVersion)) {
				p.sendMessage(ChatColor.GRAY + "There is a new version of " + ChatColor.GOLD + "ChestSort"
						+ ChatColor.GRAY + " available.");
				p.sendMessage(ChatColor.GRAY + "Please download at " + downloadLink);
			}
		}
	}

	void checkForUpdate() {
		Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
			@Override
			public void run() {

				plugin.getLogger().info("Checking for available updates...");
				try {
					HttpURLConnection httpcon = (HttpURLConnection) new URL(latestVersionLink).openConnection();
					httpcon.addRequestProperty("User-Agent", "ChestSort/" + plugin.getDescription().getVersion());
					BufferedReader reader = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
					String inputLine = reader.readLine().trim();
					latestVersion = inputLine;
					currentVersion = plugin.getDescription().getVersion().trim();
					
					if (latestVersion.equals(currentVersion)) {
						plugin.getLogger().info("You are using the latest version of ChestSort.");
					} else {
						plugin.getLogger().warning("=================================================");
						plugin.getLogger().warning("There is a new version of ChestSort available!");
						plugin.getLogger().warning("Latest : " + inputLine);
						plugin.getLogger().warning("Current: " + currentVersion);
						plugin.getLogger().warning("Please update to the newest version. Download:");
						plugin.getLogger().warning(downloadLink);
						plugin.getLogger().warning("=================================================");
					}

					reader.close();
				} catch (Exception e) {
					plugin.getLogger().warning("Could not check for updates.");
				}

			}
		});

	}

}
