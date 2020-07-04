package de.jeff_media.ChestSort.test;

import de.jeff_media.ChestSort.ChestSortPlugin;
import org.bukkit.entity.Player;

public class APIDeprecation {

    public static void sortInventoryDeprecated(Player p, ChestSortPlugin plugin) {

        plugin.sortInventory(p.getInventory());

    }
}
