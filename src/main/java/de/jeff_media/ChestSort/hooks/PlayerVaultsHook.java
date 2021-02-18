package de.jeff_media.ChestSort.hooks;

import de.jeff_media.ChestSort.ChestSortPlugin;
import org.bukkit.inventory.Inventory;

public class PlayerVaultsHook {

    private final ChestSortPlugin main;

    public PlayerVaultsHook(ChestSortPlugin main) {
        this.main=main;
    }

    public boolean isPlayerVault(Inventory inv) {
        if(inv==null) return false;
        if(inv.getHolder()==null) return false;
        if(!main.getConfig().getBoolean("hook-playervaultsx",true)) return false;
        return inv.getHolder().getClass().getName().equals("com.drtshock.playervaults.vaultmanagement.VaultHolder");
    }
}
