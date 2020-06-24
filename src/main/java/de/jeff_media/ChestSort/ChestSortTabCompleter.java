package de.jeff_media.ChestSort;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class ChestSortTabCompleter implements TabCompleter {
	
	static final String[] chestsortOptions = { "toggle","on","off","hotkeys" };
	static final String[] invsortOptions = { "toggle","on","off","all", "hotbar", "inv" };
	
	private List<String> getMatchingOptions(String entered, String[] options) {
		List<String> list = new ArrayList<>();
		
		for(String option : options) {
			if(option.toLowerCase().startsWith(entered.toLowerCase())) {
				list.add(option);
			}
		}
		
		return list;
	}
	
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		
		String entered = "";
		if(args.length>0) {
			entered = args[args.length-1];
		}
		if(command.getName().equalsIgnoreCase("chestsort")) {
			return getMatchingOptions(entered,chestsortOptions);
		}
		if(command.getName().equalsIgnoreCase("invsort")) {
			return getMatchingOptions(entered,invsortOptions);
		}
		return new ArrayList<>();
	}

}
