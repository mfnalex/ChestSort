package de.jeff_media.chestsort.handlers;

import de.jeff_media.chestsort.ChestSortPlugin;
import de.jeff_media.chestsort.data.PlayerSetting;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

public class Logger {

    ChestSortPlugin plugin;
    boolean log;
    java.util.logging.Logger logger;

    public Logger(ChestSortPlugin plugin, boolean log) {
        if(!log) return;
        plugin.getLogger().info("=======================================");
        plugin.getLogger().info("     CHESTSORT LOGGER ACTIVATED!");
        plugin.getLogger().info("=======================================");
        this.plugin=plugin;
        this.log=log;
        logger = java.util.logging.Logger.getLogger("ChestSortLogger");
        logger.setUseParentHandlers(false);
        FileHandler fh;
        try {
            fh = new FileHandler(plugin.getDataFolder()+ File.separator+"ChestSort.log");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getPlayerSettings(Player p) {
        if(plugin.getPerPlayerSettings().containsKey(p.getUniqueId().toString())) {
            PlayerSetting s = plugin.getPerPlayerSettings().get(p.getUniqueId().toString());
            return String.format("sorting: %s, invsorting: %s, middle-click: %s, shift-click: %s, double-click: %s, shift-right-click: %s, left-click: %s, right-click: %s, seen-msg: %s",
                    s.sortingEnabled, s.invSortingEnabled, s.middleClick, s.shiftClick, s.doubleClick, s.shiftRightClick, s.leftClick, s.rightClick, s.hasSeenMessage);
        } else {
            return "null";
        }
    }

    private void log(String s) {
        if(!log) return;
        logger.info(s);
    }

    public void logSort(Player p, SortCause cause) {
        if(!log) return;
        String settings = getPlayerSettings(p);
        if(cause==null) cause = SortCause.UNKNOWN;
        log(String.format("SORT: Player: %s, Cause: %s, Settings: {%s}",p.getName(),cause.name(),settings));
    }

    public enum SortCause {
        UNKNOWN, INV_CLOSE, CONT_CLOSE, CONT_OPEN, EC_OPEN, H_MIDDLE, H_SHIFT, H_DOUBLE, H_SHIFTRIGHT, H_LEFT, H_RIGHT, CMD_ISORT
    }

    public void logPlayerJoin(Player p) {
        if(!log) return;
        String settings = getPlayerSettings(p);
        log(String.format("JOIN: Player: %s, Settings: {%s}",p.getName(),settings));
    }
}
