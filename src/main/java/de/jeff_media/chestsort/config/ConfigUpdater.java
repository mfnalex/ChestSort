package de.jeff_media.chestsort.config;

import de.jeff_media.chestsort.ChestSortPlugin;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Updates the config file. When a new config file is shipped with AngelChest, it will save the new
 * file and replace all default values with the values that were set in the old config file.
 */
public final class ConfigUpdater {

    // Lines STARTING WITH these names will be treated as String lists
    private static final String[] LINES_CONTAINING_STRING_LISTS = {Config.DISABLED_WORLDS + ":"};
    // Lines STARTING WITH these names will never get the old value applied
    private static final String[] LINES_IGNORED = {"config-version:", "plugin-version:"};
    // Lines STARTING WITH these names will get no quotes although they would match one of the lists below
    private static final String[] CONFLICTING_NODES_NEEDING_NO_QUOTES = {};
    // Lines STARTING WITH these names will get their values wrapped in double quotes
    private static final String[] NODES_NEEDING_DOUBLE_QUOTES = {"message-","sorting-method"};
    // Lines STARTING WITH these names will get their values wrapped in single quotes
    private static final String[] NODES_NEEDING_SINGLE_QUOTES = {};

    private static void backupCurrentConfig(final ChestSortPlugin main) {
        final File oldFile = new File(getFilePath(main, "config.yml"));
        final File newFile = new File(getFilePath(main, "config-backup-" + main.getConfig().getString(Config.CONFIG_PLUGIN_VERSION) + ".yml"));
        if (newFile.exists()) newFile.delete();
        if (oldFile.getAbsoluteFile().renameTo(newFile.getAbsoluteFile())) {
            if(main.isDebug()) main.debug("Could not rename " + oldFile.getAbsolutePath() + " to " + newFile.getAbsolutePath());
        }
    }

    /**
     * For debugging the config updater only
     */
    private static void debug(final Logger logger, final String message) {
        //noinspection ConstantConditions
        if (false) {
            logger.warning(message);
        }
    }

    private static String getFilePath(final Plugin main, final String fileName) {
        return main.getDataFolder() + File.separator + fileName;
    }

    private static List<String> getNewConfigAsArrayList(final Plugin main) {
        final List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(getFilePath(main, "config.yml")), StandardCharsets.UTF_8);
            return lines;
        } catch (final IOException ioException) {
            ioException.printStackTrace();
        }
        return null;
    }

    /**
     * Returns the config version of the currently installed AngelChest default config
     *
     * @return default config version
     */
    private static long getNewConfigVersion() {
        final InputStream in = ChestSortPlugin.getInstance().getClass().getResourceAsStream("/config-version.txt");
        final BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        try {
            return Long.parseLong(reader.readLine());
        } catch (final IOException ioException) {
            ioException.printStackTrace();
            return 0;
        }

    }

    /**
     * Returns a String representing the correct quotes to use for this key's value
     *
     * @param line line/key to get the quotes for
     * @return double quote, single quote or empty string, according to the key name
     */
    private static String getQuotes(final String line) {
        for(final String test : CONFLICTING_NODES_NEEDING_NO_QUOTES) {
            if(line.startsWith(test)) {
                return "";
            }
        }
        for (final String test : NODES_NEEDING_DOUBLE_QUOTES) {
            if (line.startsWith(test)) {
                return "\"";
            }
        }
        for (final String test : NODES_NEEDING_SINGLE_QUOTES) {
            if (line.startsWith(test)) {
                return "'";
            }
        }
        return "";
    }

    private static boolean lineContainsIgnoredNode(final String line) {
        for (final String test : LINES_IGNORED) {
            if (line.startsWith(test)) {
                return true;
            }
        }
        return false;
    }

    private static boolean lineIsStringList(final String line) {
        for (final String test : LINES_CONTAINING_STRING_LISTS) {
            if (line.startsWith(test)) {
                return true;
            }
        }
        return false;
    }

    private static void saveArrayListToConfig(final Plugin main, final List<String> lines) {
        try {
            final BufferedWriter fw = Files.newBufferedWriter(new File(getFilePath(main, "config.yml")).toPath(), StandardCharsets.UTF_8);
            for (final String line : lines) {
                fw.write(line + System.lineSeparator());
            }
            fw.close();
        } catch (final IOException ioException) {
            ioException.printStackTrace();
        }
    }

    /**
     * Attempts to update the config
     */
    public static void updateConfig() {
        final ChestSortPlugin main = ChestSortPlugin.getInstance();
        final Logger logger = main.getLogger();
        debug(logger, "Newest config version  = " + getNewConfigVersion());
        debug(logger, "Current config version = " + main.getConfig().getLong(Config.CONFIG_VERSION));
        if (main.getConfig().getLong(Config.CONFIG_VERSION) >= getNewConfigVersion()) {
            debug(logger, "The config currently used has an equal or newer version than the one shipped with this release.");
            return;
        }

        logger.info("===========================================");
        logger.info("You are using an outdated config file.");
        logger.info("Your config file will now be updated to the");
        logger.info("newest version. You changes will be kept.");
        logger.info("===========================================");

        // hotkeys has been renamed to sorting-hotkeys
        if(main.getConfig().isSet("hotkeys.middle-click")) {
            main.getConfig().set("sorting-hotkeys.middle-click", main.getConfig().getBoolean("hotkeys.middle-click"));
            main.getConfig().set("sorting-hotkeys.shift-click", main.getConfig().getBoolean("hotkeys.shift-click"));
            main.getConfig().set("sorting-hotkeys.double-click", main.getConfig().getBoolean("hotkeys.double-click"));
            main.getConfig().set("sorting-hotkeys.shift-right-click", main.getConfig().getBoolean("hotkeys.shift-right-click"));
        }

        // allow-hotkeys has been renamed to allow-sorting-hotkeys
        if(main.getConfig().isSet("allow-hotkeys")) {
            main.getConfig().set("allow-sorting-hotkeys",main.getConfig().getBoolean("allow-hotkeys"));
        }

        backupCurrentConfig(main);
        main.saveDefaultConfig();

        final Set<String> oldConfigNodes = main.getConfig().getKeys(false);
        final ArrayList<String> newConfig = new ArrayList<>();

        // Iterate through ALL lines from the new default config
        for (final String defaultLine : getNewConfigAsArrayList(main)) {

            String updatedLine = defaultLine;

            /*if (Daddy.allows(Features.GENERIC)) {
                if (updatedLine.startsWith("# PREMIUM FEATURE: ONLY AVAILABLE IN AngelChestPlus!")) {
                    updatedLine = null;
                }
            } else*/

            //noinspection StatementWithEmptyBody
            if (defaultLine.startsWith("sorting-hotkeys:") || defaultLine.startsWith("additional-hotkeys:")) {
                // dont replace hotkeys root part
            } else if (defaultLine.startsWith("  middle-click:")) {
                updatedLine = "  middle-click: " + main.getConfig().getBoolean("sorting-hotkeys.middle-click");
            } else if (defaultLine.startsWith("  shift-click:")) {
                updatedLine = "  shift-click: " + main.getConfig().getBoolean("sorting-hotkeys.shift-click");
            } else if (defaultLine.startsWith("  double-click:")) {
                updatedLine = "  double-click: " + main.getConfig().getBoolean("sorting-hotkeys.double-click");
            } else if (defaultLine.startsWith("  shift-right-click:")) {
                updatedLine = "  shift-right-click: " + main.getConfig().getBoolean("sorting-hotkeys.shift-right-click");
            } else if (defaultLine.startsWith("  left-click:")) {
                updatedLine = "  left-click: " + main.getConfig().getBoolean("additional-hotkeys.left-click");
            } else if (defaultLine.startsWith("  right-click:")) {
                updatedLine = "  right-click: " + main.getConfig().getBoolean("additional-hotkeys.right-click");
            }
            else if (defaultLine.startsWith("-") || defaultLine.startsWith(" -") || defaultLine.startsWith("  -")) {
                debug(logger, "Not including default String list entry: " + defaultLine);
                updatedLine = null;
            } else if (lineContainsIgnoredNode(defaultLine)) {
                debug(logger, "Not updating this line: " + defaultLine);
            } else if (lineIsStringList(defaultLine)) {
                updatedLine = null;
                newConfig.add(defaultLine);
                final String node = defaultLine.split(":")[0];
                for (final String entry : main.getConfig().getStringList(node)) {
                    newConfig.add("- " + entry);
                }
            } else {
                for (final String node : oldConfigNodes) {
                    // Iterate through all keys from the old config file.
                    if (defaultLine.startsWith(node + ":")) {
                        // This key from the old file matches this line from the new file! Updating...
                        final String quotes = getQuotes(node);
                        String value = main.getConfig().get(node).toString();

                        // The hologram text needs special escaping for the newline symbols
                        if (node.equals("hologram-text")) {
                            value = value.replaceAll("\n", "\\\\n");
                        }

                        updatedLine = node + ": " + quotes + value + quotes;
                    }
                }
            }

            if (updatedLine != null) {
                newConfig.add(updatedLine);
            }
        }

        saveArrayListToConfig(main, newConfig);
    }
}
