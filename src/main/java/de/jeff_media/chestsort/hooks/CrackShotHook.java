package de.jeff_media.chestsort.hooks;

import org.bukkit.inventory.ItemStack;

import de.jeff_media.chestsort.ChestSortPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CrackShotHook {

    final ChestSortPlugin plugin;
    Class<?> csUtilityClass = null;
    Object crackShotUtility = null;
    Method getWeaponTitle = null;

    public CrackShotHook(ChestSortPlugin plugin) {
        this.plugin=plugin;

        if(plugin.isHookCrackShot()) {
            try {
                csUtilityClass = Class.forName("com.shampaggon.crackshot.CSUtility");
                crackShotUtility = csUtilityClass.getConstructor().newInstance();
                getWeaponTitle = csUtilityClass.getMethod("getWeaponTitle", ItemStack.class);
                plugin.getLogger().info("Successfully hooked into CrackShot");
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ignored) {

            }

        }
    }

    // Will return when not a weapon
    public String getCrackShotWeaponName(ItemStack item) {
        if(getWeaponTitle == null || !plugin.isHookCrackShot()) {
            return null;
        }

        // Will be null if not a weapon
        try {
            return (String) getWeaponTitle.invoke(crackShotUtility,item);
        } catch (InvocationTargetException | IllegalAccessException ignored) {
            return null;
        }
    }

}