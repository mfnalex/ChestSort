package de.jeff_media.ChestSort;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

public class ChestSortEvent extends Event implements Cancellable {
	
	boolean cancelled = false;
	Location loc;
	final Inventory inv;
	Player p;

	  private static final HandlerList HANDLERS = new HandlerList();
	  
	  public ChestSortEvent(Inventory inv) {
		  this.inv = inv;
	  }
	  
	  @Nullable
	  public Location getLocation() {
		  return loc;
	  }
	  
	  public Inventory getInventory() {
		  return inv;
	  }
	  
	  @Nullable
	  public Player getPlayer() {
		  return p;
	  }

	    public HandlerList getHandlers() {
	        return HANDLERS;
	    }

	    public static HandlerList getHandlerList() {
	        return HANDLERS;
	    }

		@Override
		public boolean isCancelled() {
			return cancelled;
		}

		@Override
		public void setCancelled(boolean cancel) {
			cancelled = cancel;
		}


}