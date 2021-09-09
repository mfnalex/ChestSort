package de.jeff_media.chestsort.hooks;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

public class SlimeFunHook {

    public static boolean isSlimefunBackpack(ItemStack item) {

        if(Bukkit.getPluginManager().getPlugin("Slimefun") == null) {
            return false;
        }

        // New Slimefun API
        try {
            final Class<?> slimefunItemClass = Class.forName("io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem");
            final Class<?> slimefunBackpackClass = Class.forName("io.github.thebusybiscuit.slimefun4.implementation.items.backpacks.SlimefunBackpack");
            final Method getByItemMethod = slimefunItemClass.getMethod("getByItem", ItemStack.class);
            final Object result = getByItemMethod.invoke(null, item);
            if(result == null) return false;
            return slimefunBackpackClass.isInstance(result);
        } catch (Throwable ignored) {

        }

        // Old Slimefun API
        try {
            final Class<?> slimefunItemClass = Class.forName("me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem");
            final Class<?> slimefunBackpackClass = Class.forName("io.github.thebusybiscuit.slimefun4.implementation.items.backpacks.SlimefunBackpack");
            final Method getByItemMethod = slimefunItemClass.getMethod("getByItem", ItemStack.class);
            final Object result = getByItemMethod.invoke(null, item);
            if(result == null) return false;
            return slimefunBackpackClass.isInstance(result);
        } catch (Throwable ignored) {

        }

        return false;
    }
}