package de.jeff_media.chestsort.hooks;

import de.jeff_media.chestsort.ChestSortPlugin;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class PlayerVaultsHook {

    private final ChestSortPlugin main;

    public PlayerVaultsHook(ChestSortPlugin main) {
        this.main=main;
    }

    public boolean isPlayerVault(Inventory inv, InventoryHolder holder) {
        if(inv==null) return false;
        if(holder ==null) return false;
        if(!main.getConfig().getBoolean("hook-playervaults",true)) return false;
        return holder.getClass().getName().equals("com.drtshock.playervaults.vaultmanagement.VaultHolder")
                || holder.getClass().getName().equals("com.github.dig.endervaults.bukkit.vault.BukkitInventoryHolder");
    }
}
