package de.jeff_media.ChestSort;

import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ChestSortEvent extends Event implements Cancellable {
	
	boolean cancelled = false;
	Location loc;

	  private static final HandlerList HANDLERS = new HandlerList();
	  
	  public ChestSortEvent(Location loc) {
		  this.loc=loc;
	  }
	  
	  public Location getLocation() {
		  return loc;
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