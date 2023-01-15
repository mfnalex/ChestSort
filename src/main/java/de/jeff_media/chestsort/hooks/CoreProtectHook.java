package de.jeff_media.chestsort.hooks;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

public class CoreProtectHook {

    public static void logContainerTransaction(String user, Location location) {
        CoreProtectAPI api = getCoreProtect();
        if (api == null) {
            return;
        }
        api.logContainerTransaction(user, location);
    }

    private static CoreProtectAPI getCoreProtect() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("CoreProtect");

        // Check that CoreProtect is loaded
        if (!(plugin instanceof CoreProtect)) {
            return null;
        }

        // Check that the API is enabled
        CoreProtectAPI api = ((CoreProtect) plugin).getAPI();
        if (!api.isEnabled()) {
            return null;
        }

        // Check that a compatible version of the API is loaded
        if (api.APIVersion() < 9) {
            return null;
        }
        return api;
    }

}
