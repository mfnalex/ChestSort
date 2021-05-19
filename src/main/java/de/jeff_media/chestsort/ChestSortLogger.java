package de.jeff_media.chestsort;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ChestSortLogger {

    ChestSortPlugin plugin;
    boolean log;
    Logger logger;

    ChestSortLogger(ChestSortPlugin plugin, boolean log) {
        if(!log) return;
        plugin.getLogger().info("=======================================");
        plugin.getLogger().info("     CHESTSORT LOGGER ACTIVATED!");
        plugin.getLogger().info("=======================================");
        this.plugin=plugin;
        this.log=log;
        logger = Logger.getLogger("ChestSortLogger");
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
        if(plugin.perPlayerSettings.containsKey(p.getUniqueId().toString())) {
            ChestSortPlayerSetting s = plugin.perPlayerSettings.get(p.getUniqueId().toString());
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

    void logSort(Player p, @Nullable SortCause cause) {
        if(!log) return;
        String settings = getPlayerSettings(p);
        if(cause==null) cause = SortCause.UNKNOWN;
        log(String.format("SORT: Player: %s, Cause: %s, Settings: {%s}",p.getName(),cause.name(),settings));
    }

    enum SortCause {
        UNKNOWN, INV_CLOSE, CONT_CLOSE, CONT_OPEN, EC_OPEN, H_MIDDLE, H_SHIFT, H_DOUBLE, H_SHIFTRIGHT, H_LEFT, H_RIGHT, CMD_ISORT
    }

    void logPlayerJoin(Player p) {
        if(!log) return;
        String settings = getPlayerSettings(p);
        log(String.format("JOIN: Player: %s, Settings: {%s}",p.getName(),settings));
    }
}
