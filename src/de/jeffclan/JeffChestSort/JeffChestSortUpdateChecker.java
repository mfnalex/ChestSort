package de.jeffclan.JeffChestSort;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class JeffChestSortUpdateChecker {

	private JeffChestSortPlugin plugin;

	public JeffChestSortUpdateChecker(JeffChestSortPlugin plugin) {
		this.plugin = plugin;
	}

	String latestVersionLink = "https://api.jeff-media.de/chestsort/chestsort-latest-version.txt";
	String downloadLink = "https://www.spigotmc.org/resources/1-13-chestsort.59773/";
	private String currentVersion;
	private String latestVersion;

	public void sendUpdateMessage(Player p) {
		if (!currentVersion.equals(latestVersion)) {
			p.sendMessage(ChatColor.GRAY + "There is a new version of " + ChatColor.GOLD + "ChestSort" + ChatColor.GRAY
					+ " available. Please download at " + downloadLink);
		}
	}

	public void checkForUpdate() {

		try {

			HttpURLConnection httpcon = (HttpURLConnection) new URL(latestVersionLink).openConnection();
			httpcon.addRequestProperty("User-Agent", "Mozilla/4.76");

			BufferedReader reader = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));

			String inputLine = reader.readLine().trim();

			currentVersion = plugin.getDescription().getVersion().trim();
			latestVersion = inputLine;

			System.out.print("latest : " + inputLine);
			System.out.print("current: " + currentVersion);
			if (latestVersion.equals(currentVersion)) {
				plugin.getLogger().info("You are using the latest version of ChestSort.");
			} else {
				plugin.getLogger().warning("There is a new version of ChestSort available!");
				plugin.getLogger().warning("Please update to the newest version. Download:");
				plugin.getLogger().warning(downloadLink);
			}

			reader.close();
		} catch (Exception e) {
			plugin.getLogger().warning("Could not check for updates.");
		}

	}

}
