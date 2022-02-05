package de.jeff_media.chestsort.api;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class ChestSortPostSortEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final ChestSortEvent event;

    public ChestSortPostSortEvent(ChestSortEvent event) {
        this.event = event;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public ChestSortEvent getChestSortEvent() {
        return event;
    }
}
