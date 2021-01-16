package de.jeff_media.ChestSort.hooks;

import io.github.thebusybiscuit.slimefun4.implementation.items.backpacks.SlimefunBackpack;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import org.bukkit.inventory.ItemStack;

public class SlimeFunHook {

    public static boolean isSlimefunBackpack(ItemStack item) {
        return SlimefunItem.getByItem(item) instanceof SlimefunBackpack;
    }
}
