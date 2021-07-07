package de.jeff_media.chestsort.api;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Public class that can be used as InventoryHolder to tell ChestSort that the associated inventory is sortable.
 */
public class Sortable implements ISortable {
    private Inventory inv;
    private InventoryHolder h = null;

    public Sortable() {

    }

    public Sortable(InventoryHolder h) {
        this.h=h;
    }

    public void setHolder(@NotNull InventoryHolder player) {
        this.h=h;
    }

    public void removeHolder() {
        this.h=null;
    }

    @Nullable
    public InventoryHolder getHolder() {
        return h;
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }

    public void setInventory(Inventory inv) {
        this.inv=inv;
    }
}
