package de.jeff_media.ChestSort;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

public class ChestSortPermissionsHandler {
	
	HashMap<UUID,PermissionAttachment> permissions;
	ChestSortPlugin plugin;
	
	ChestSortPermissionsHandler(ChestSortPlugin plugin) {
		this.plugin = plugin;
		this.permissions = new HashMap<UUID,PermissionAttachment>();
	}
	
	void addPermissions(Player p) {
		if(plugin.usePermissions) return;
		if(permissions.containsKey(p.getUniqueId())) return;
		PermissionAttachment attachment = p.addAttachment(plugin);
		attachment.setPermission("chestsort.use", true);
		attachment.setPermission("chestsort.use.inventory", true);
		permissions.put(p.getUniqueId(), attachment);
	}
	
	void removePermissions(Player p) {
		if(plugin.usePermissions) return;
		if(!permissions.containsKey(p.getUniqueId())) return;
		PermissionAttachment attachment = permissions.get(p.getUniqueId());
		attachment.unsetPermission("chestsort.use");
		attachment.unsetPermission("chestsort.use.inventory");
		p.removeAttachment(attachment);
		permissions.remove(p.getUniqueId());
	}

}
