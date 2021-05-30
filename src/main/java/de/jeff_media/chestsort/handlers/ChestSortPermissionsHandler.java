package de.jeff_media.chestsort.handlers;

import java.util.HashMap;
import java.util.UUID;

import de.jeff_media.chestsort.ChestSortPlugin;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

public class ChestSortPermissionsHandler {
	
	private final HashMap<UUID,PermissionAttachment> permissions;
	private final ChestSortPlugin plugin;
	
	public ChestSortPermissionsHandler(ChestSortPlugin plugin) {
		this.plugin = plugin;
		this.permissions = new HashMap<>();
	}
	
	public void addPermissions(Player p) {
		if(plugin.getConfig().getBoolean("use-permissions")) return;
		if(permissions.containsKey(p.getUniqueId())) return;
		PermissionAttachment attachment = p.addAttachment(plugin);
		attachment.setPermission("chestsort.use", true);
		attachment.setPermission("chestsort.use.inventory", true);
		permissions.put(p.getUniqueId(), attachment);
	}
	
	public void removePermissions(Player p) {
		if(plugin.getConfig().getBoolean("use-permissions")) return;
		if(!permissions.containsKey(p.getUniqueId())) return;
		PermissionAttachment attachment = permissions.get(p.getUniqueId());
		attachment.unsetPermission("chestsort.use");
		attachment.unsetPermission("chestsort.use.inventory");
		p.removeAttachment(attachment);
		permissions.remove(p.getUniqueId());
	}

}
