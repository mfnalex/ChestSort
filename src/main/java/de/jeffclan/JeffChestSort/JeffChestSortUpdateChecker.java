package de.jeffclan.JeffChestSort;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

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
	final static String latestVersionLink = "https://api.jeff-media.de/chestsort/chestsort-latest-version.txt";
	final static String downloadLink = "https://chestsort.de/download";
	final static String changelogLink = "https://chestsort.de/changelog";
	final static String donateLink = "https://chestsort.de/donate";
	
	private String currentVersion = "undefined";
	private String latestVersion = "undefined";
	
	private TextComponent createLink(String text, String link, net.md_5.bungee.api.ChatColor color) {
		// Hover text
		ComponentBuilder hoverCB = new ComponentBuilder(
                text+" Link: ").bold(true)
                .append(link).bold(false);
		
		TextComponent tc = new TextComponent(text);
		tc.setBold(true);
		tc.setColor(color);
		tc.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,link));
		tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,hoverCB.create()));
		return tc;
	}
	
	private void sendLinks(Player p) {
		  TextComponent text = new TextComponent("");
		
		  TextComponent download = createLink("Download",downloadLink,net.md_5.bungee.api.ChatColor.GOLD); 
		  TextComponent donate = createLink("Donate",donateLink,net.md_5.bungee.api.ChatColor.GOLD);
		  TextComponent changelog = createLink("Changelog",changelogLink,net.md_5.bungee.api.ChatColor.GOLD);
		  
		  TextComponent placeholder = new TextComponent(" | ");
		  placeholder.setColor(net.md_5.bungee.api.ChatColor.GRAY);
		  
		  text.addExtra(download);
		  text.addExtra(placeholder);
		  text.addExtra(donate);
		  text.addExtra(placeholder);
		  text.addExtra(changelog);
	        
	      p.spigot().sendMessage(text);
	}

	void sendUpdateMessage(Player p) {
		if (!latestVersion.equals("undefined")) {
			if (!currentVersion.equals(latestVersion)) {		
				p.sendMessage(ChatColor.GRAY + "There is a new version of " + ChatColor.GOLD + "ChestSort"
						+ ChatColor.GRAY + " available.");
				sendLinks(p);
				p.sendMessage(ChatColor.DARK_GRAY + "Your version: "+currentVersion + " | Latest version: "+ latestVersion);
				p.sendMessage("");
			}
		}
	}

	void checkForUpdate() {
		Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
			@Override
			public void run() {

				plugin.getLogger().info("Checking for available updates...");
				try {
					String userAgent = "ChestSort/"+plugin.getDescription().getVersion()+" (MC "+plugin.mcVersion+", "+plugin.getServer().getOnlinePlayers().size()+"/"+plugin.getServer().getOfflinePlayers().length+")";
					HttpURLConnection httpcon = (HttpURLConnection) new URL(latestVersionLink).openConnection();
					httpcon.addRequestProperty("User-Agent", userAgent);
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
