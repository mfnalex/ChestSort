package de.jeff_media.chestsort.commands;

import de.jeff_media.chestsort.ChestSortPlugin;
import de.jeff_media.jefflib.NBTAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AdminCommand implements CommandExecutor {

    private final ChestSortPlugin plugin;

    public AdminCommand(ChestSortPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {

        if(!sender.hasPermission("chestsort.admin")) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }

        if(args.length==0) {
            sender.sendMessage(new String[] {
                    "Available admin commands:",
                    "reset <player> -- Resets a player's hotkey settings"
            });
            return true;
        }

        switch(args[0].toLowerCase()) {
            case "reset":
                reset(sender, args);
                break;
            default:
                sender.sendMessage("Unknown command: "+args[0]);
                break;
        }

        return true;
    }

    private void reset(CommandSender sender, String[] args) {
        if(args.length!=2) {
            sender.sendMessage("Usage: /chestsortadmin reset <player>");
            return;
        }

        Player player = Bukkit.getPlayer(args[1]);
        if(player == null) {
            sender.sendMessage("Could not find player "+args[1]);
            return;
        }

        plugin.unregisterPlayer(player);

        String[] tags = new String[] {
                "sortingEnabled",
                "invSortingEnabled",
                "leftClick",
                "rightClick",
                "shiftClick",
                "doubleClick",
                "middleClick",
                "shiftRightClick",
                "leftClickOutside",
                "hasSeenMessage"
        };

        for(String nbtTag : tags) {
            NBTAPI.removeNBT(player,nbtTag);
        }

        sender.sendMessage("Reset hotkey settings for player "+player.getName());
    }
}
