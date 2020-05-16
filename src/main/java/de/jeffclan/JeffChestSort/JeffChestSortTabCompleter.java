package de.jeffclan.JeffChestSort;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class JeffChestSortTabCompleter {
	
	String[] options = { "" };
	
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> list = new ArrayList<String>();
		if(!command.getName().equalsIgnoreCase("chestsort")) {
			return list;
		}
		
		return list;
	}

}
