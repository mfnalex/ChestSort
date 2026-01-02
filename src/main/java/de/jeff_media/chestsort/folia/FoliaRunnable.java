package de.jeff_media.chestsort.folia;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.scheduler.BukkitRunnable;

public class FoliaRunnable extends BukkitRunnable {

    private ScheduledTask foliaTask;

    @Override
    public synchronized void cancel() throws IllegalStateException {
        if(foliaTask != null) foliaTask.cancel();
    }

    @Override
    public void run() {
    }

    public void setScheduledTask(ScheduledTask task) {
        this.foliaTask = task;
    }
}
