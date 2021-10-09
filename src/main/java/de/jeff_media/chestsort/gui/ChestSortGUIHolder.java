package de.jeff_media.chestsort.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class ChestSortGUIHolder implements InventoryHolder {

    private Inventory inv;

    public void setInventory(Inventory inv) {
        this.inv = inv;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inv;
    }
}
