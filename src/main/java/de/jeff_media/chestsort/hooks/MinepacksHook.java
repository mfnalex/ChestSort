package de.jeff_media.chestsort.hooks;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import at.pcgamingfreaks.Minepacks.Bukkit.API.Backpack;
import at.pcgamingfreaks.Minepacks.Bukkit.API.MinepacksPlugin;
import de.jeff_media.chestsort.ChestSortPlugin;

public class MinepacksHook {

    final ChestSortPlugin plugin;
    MinepacksPlugin minepacks = null;
    boolean skipReflection = false;

    public MinepacksHook(ChestSortPlugin plugin) {
        this.plugin = plugin;
        Plugin bukkitPlugin = Bukkit.getPluginManager().getPlugin("Minepacks");
        if (plugin.isHookMinepacks() && bukkitPlugin instanceof MinepacksPlugin) {
            minepacks = (MinepacksPlugin) bukkitPlugin;
            plugin.getLogger().info("Successfully hooked into Minepacks");
        }
    }

    public boolean isMinepacksBackpack(ItemStack item) {
        if (minepacks == null) {
            return false;
        }
        if (skipReflection && minepacks.isBackpackItem(item)) {
            return true;
        }

        try {
            minepacks.getClass().getMethod("isBackpackItem", ItemStack.class);
            skipReflection = true;
            if (minepacks.isBackpackItem(item)) {
                return true;
            }
        } catch (NoSuchMethodException | SecurityException e) {
            plugin.getLogger().warning(
                "You are using a version of Minepacks that is too old and does not implement every API method needed by ChestSort. Minepacks hook will be disabled.");
            minepacks = null;
            plugin.setHookMinepacks(false);
        }

        return false;
    }

    public boolean isMinepacksBackpack(Inventory inv, InventoryHolder holder) {
        if (minepacks == null) {
            return false;
        }
        if (holder == null) {
            return false;
        }
        return holder instanceof Backpack;

    }

}
