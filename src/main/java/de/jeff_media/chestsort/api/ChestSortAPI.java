package de.jeff_media.chestsort.api;

import de.jeff_media.chestsort.ChestSortPlugin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ChestSortAPI {

    public static void sortInventory(Inventory inventory) {
        ChestSortPlugin.getInstance().getOrganizer().sortInventory(inventory);
    }

    public static void sortInventory(Inventory inventory, int startSlot, int endSlot) {
        ChestSortPlugin.getInstance().getOrganizer().sortInventory(inventory, startSlot, endSlot);
    }

    public static boolean hasSortingEnabled(Player player) {
        return ChestSortPlugin.getInstance().isSortingEnabled(player);
    }

    public static void setSortable(Inventory inv) {
        ChestSortPlugin.getInstance().getOrganizer().setSortable(inv);
    }

    public static void setUnsortable(Inventory inv) {
        ChestSortPlugin.getInstance().getOrganizer().setUnsortable(inv);
    }

}
