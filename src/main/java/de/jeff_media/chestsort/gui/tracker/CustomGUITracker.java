package de.jeff_media.chestsort.gui.tracker;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

public class CustomGUITracker {

    private static final Map<InventoryView, CustomGUIType> guis = new ConcurrentHashMap<>();

    public static InventoryView open(Player player, Inventory inventory, CustomGUIType type) {
        InventoryView view = player.openInventory(inventory);
        guis.put(view, type);
        return view;
    }

    public static CustomGUIType getType(InventoryView view) {
        return guis.get(view);
    }

    public static void close(InventoryView view) {
        guis.remove(view);
        // view.close();
    }
}
