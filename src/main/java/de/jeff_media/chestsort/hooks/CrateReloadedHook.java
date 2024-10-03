package de.jeff_media.chestsort.hooks;

import de.jeff_media.chestsort.ChestSortPlugin;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;

import java.lang.reflect.Method;
import java.util.Locale;
import org.bukkit.inventory.InventoryHolder;

/**
 * Who doesn't make their API available in a public maven repository
 * deserves to be touched using Reflection only.
 */
public class CrateReloadedHook {

    private static final ChestSortPlugin main = ChestSortPlugin.getInstance();
    private static Object blockCrateRegistrarObject;
    private static Method isCrateMethod;

    static {
        try {
            Class<?> crateApiClazz = Class.forName("com.hazebyte.crate.api.CrateAPI");
            Method getBlockCrateRegistrarMethod = crateApiClazz.getMethod("getBlockCrateRegistrar");
            Class<?> blockCrateRegistrarClazz = Class.forName("com.hazebyte.crate.api.crate.BlockCrateRegistrar");
            blockCrateRegistrarObject = getBlockCrateRegistrarMethod.invoke(null);
            isCrateMethod = blockCrateRegistrarClazz.getMethod("hasCrates", Location.class);
        } catch (Throwable ignored) {
            isCrateMethod = null;
        }
    }

    public static boolean isCrate(final Block block) {
        try {
            if(isCrateMethod != null) {
                return (boolean) isCrateMethod.invoke(blockCrateRegistrarObject, block.getLocation());
            }
        } catch (Throwable ignored) { }
        return false;
    }

    public static boolean isCrate(final Inventory inv, InventoryHolder holder) {
        if(inv==null) return false;
        if(holder ==null) return false;
        if(!main.getConfig().getBoolean("hook-cratereloaded",true)) return false;
        return holder.getClass().getName().toLowerCase(Locale.ROOT).contains("cratereloaded");
    }

}
