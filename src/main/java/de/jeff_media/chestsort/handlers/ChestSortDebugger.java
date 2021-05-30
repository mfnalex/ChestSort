package de.jeff_media.chestsort.handlers;

import de.jeff_media.chestsort.ChestSortPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public class ChestSortDebugger implements @NotNull Listener {

    private final ChestSortPlugin plugin;

    public ChestSortDebugger(ChestSortPlugin plugin) {
        plugin.getLogger().warning("=======================================");
        plugin.getLogger().warning("    CHESTSORT DEBUG MODE ACTIVATED!");
        plugin.getLogger().warning("Only use this for development purposes!");
        plugin.getLogger().warning("=======================================");
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClickEvent(InventoryClickEvent e) {
    	if(plugin.debug) {
			// Debug
			plugin.debug(" ");
			plugin.debug("InventoryClickEvent:");
			plugin.debug("- Holder: " + e.getInventory().getHolder());
			if (e.getInventory().getHolder() != null) {
				plugin.debug("- Holder class: " + e.getInventory().getHolder().getClass());
			}
			plugin.debug("- Slot: " + e.getRawSlot());
			plugin.debug("- Left-Click: " + e.isLeftClick());
			plugin.debug("- Right-Click: " + e.isRightClick());
			plugin.debug("- Shift-Click: " + e.isShiftClick());
			plugin.debug(" ");
		}

    }

}
