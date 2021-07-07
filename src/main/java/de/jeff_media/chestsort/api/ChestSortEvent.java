package de.jeff_media.chestsort.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This event is called whenever ChestSort attempts to sort an inventory. Can be cancelled to prevent ChestSort from manipulating this inventory.
 */
public class ChestSortEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    final Inventory inv;
    // For each ItemStack, a map of "{placeholder}", "sortString" pairs.
    Map<ItemStack, Map<String, String>> invSortableMaps;
    boolean cancelled = false;
    Location loc;
    HumanEntity p;

    public List<ItemStack> getUnmovableItemStacks() {
        return unmovableItemStacks;
    }

    public List<Integer> getUnmovableSlots() {
        return unmovableSlots;
    }

    List<ItemStack> unmovableItemStacks;
    List<Integer> unmovableSlots;

    public ChestSortEvent(Inventory inv) {
        this.inv = inv;
        this.unmovableItemStacks = new ArrayList<>();
        this.unmovableSlots = new ArrayList<>();
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * Returns the location associated with this event. Might be null
     * @return Location associated with this event, or null if no location has been set
     */
    @Nullable
    public Location getLocation() {
        return loc;
    }

    /**
     * Sets the location associated with this event
     * @param loc
     */
    public void setLocation(Location loc) { this.loc=loc; }

    /**
     * Returns the inventory associated with this event
     * @return Inventory to be sorted
     */
    public Inventory getInventory() {
        return inv;
    }

    public Map<ItemStack, Map<String, String>> getSortableMaps() {
        return invSortableMaps;
    }

    public void setSortableMaps(Map<ItemStack, Map<String, String>> sortableMap) {
        invSortableMaps = sortableMap;
    }

    /**
     * Returns the player associated with this event. Might be null
     * @return Player associated with this event, or null if no player has been set
     */
    @Nullable
    public HumanEntity getPlayer() {
        return p;
    }

    /**
     * Sets the player associated with this event
     * @param p Player associated with this event, can be null
     */
    public void setPlayer(@Nullable HumanEntity p) { this.p=p; }

    /**
     * Prevents ChestSort from sorting/moving this specific slot
     * @param slot
     */
    public void setUnmovable(int slot) {
        unmovableSlots.add(slot);
    }

    /**
     * Prevents ChestSort from sorting/moving matching ItemStacks
     * @param itemStack
     */
    public void setUnmovable(ItemStack itemStack) {
        unmovableItemStacks.add(itemStack);
    }

    /**
     * Removes a slot number from the list of unmovable slots
     * @param slot
     */
    public void removeUnmovable(int slot) {
        unmovableSlots.remove(slot);
    }

    /**
     * Removes an ItemStack from the list of unmovable ItemStacks
     * @param itemStack
     */
    public void removeUnmovable(ItemStack itemStack) {
        unmovableItemStacks.remove(itemStack);
    }

    /**
     * Checks whether a slot number is set as unmovable
     * @param slot
     * @return true if the slot number has been set unmovable, otherwise false
     */
    public boolean isUnmovable(int slot) {
        return unmovableSlots.contains(slot);
    }

    /**
     * Checks whether an ItemStack is set as unmovable
     * @param itemStack
     * @return true if the ItemStack has been set unmovable, otherwise false
     */
    public boolean isUnmovable(ItemStack itemStack) {
        return unmovableItemStacks.contains(itemStack);
    }

    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * Checks whether this event is cancelled. If true, the Inventory will not be sorted
     * @return true when the event has been cancelled, otherwise false
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }


}
