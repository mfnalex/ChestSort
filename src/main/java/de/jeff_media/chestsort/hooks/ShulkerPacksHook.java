package de.jeff_media.chestsort.hooks;

import de.jeff_media.chestsort.ChestSortPlugin;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

public class ShulkerPacksHook {

    private static Method checkIfOpenMethod;
    private static Boolean installed = null;

    public static boolean isOpenShulkerView(InventoryHolder topHolder) {
        return topHolder != null && topHolder.getClass().getName().toLowerCase(Locale.ROOT).contains("shulkerpacks");
    }

    public static boolean isOpenShulkerPack(ItemStack item) {
        if(installed == null) {
            if (Bukkit.getPluginManager().getPlugin("ShulkerPacks") == null) {
                installed = false;
                return false;
            }

            try {
                checkIfOpenMethod = Class.forName("me.darkolythe.shulkerpacks.ShulkerListener").getDeclaredMethod("checkIfOpen",ItemStack.class);
                //System.out.println("Found ShulkerPacks method: " + checkIfOpenMethod);
                if(checkIfOpenMethod != null) checkIfOpenMethod.setAccessible(true);
                installed = checkIfOpenMethod != null;
            } catch (Throwable t) {
                installed = false;
                ChestSortPlugin.getInstance().getLogger().severe("Error while hooking into ShulkerPacks. Try updating ShulkerPacks to the newest version.");
                //t.printStackTrace();
                return false;
            }
        }


        if(installed && checkIfOpenMethod != null) {
            try {
                return (boolean) checkIfOpenMethod.invoke(null, item);
            } catch (IllegalAccessException | InvocationTargetException ignored) {
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean containsOpenShulkerPack(Inventory inventory) {
        for(ItemStack item : inventory.getContents()) {
            if(item == null) continue;
            if(!item.getType().name().endsWith("SHULKER_BOX")) continue;
            if(isOpenShulkerPack(item)) return true;
        }
        return false;
    }
}
