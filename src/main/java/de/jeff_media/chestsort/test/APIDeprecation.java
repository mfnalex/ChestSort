package de.jeff_media.chestsort.test;

import de.jeff_media.chestsort.ChestSortPlugin;
import org.bukkit.entity.Player;

public class APIDeprecation {

    public static void sortInventoryDeprecated(Player p, ChestSortPlugin plugin) {

        plugin.sortInventory(p.getInventory());

    }
}
