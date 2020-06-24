package de.jeff_media.ChestSort;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

public class ChestSortPermissionsHandler {
	
	final HashMap<UUID,PermissionAttachment> permissions;
	final ChestSortPlugin plugin;
	
	ChestSortPermissionsHandler(ChestSortPlugin plugin) {
		this.plugin = plugin;
		this.permissions = new HashMap<>();
	}
	
	void addPermissions(Player p) {
		if(plugin.getConfig().getBoolean("use-permissions")) return;
		if(permissions.containsKey(p.getUniqueId())) return;
		PermissionAttachment attachment = p.addAttachment(plugin);
		attachment.setPermission("chestsort.use", true);
		attachment.setPermission("chestsort.use.inventory", true);
		permissions.put(p.getUniqueId(), attachment);
	}
	
	void removePermissions(Player p) {
		if(plugin.getConfig().getBoolean("use-permissions")) return;
		if(!permissions.containsKey(p.getUniqueId())) return;
		PermissionAttachment attachment = permissions.get(p.getUniqueId());
		attachment.unsetPermission("chestsort.use");
		attachment.unsetPermission("chestsort.use.inventory");
		p.removeAttachment(attachment);
		permissions.remove(p.getUniqueId());
	}

}
