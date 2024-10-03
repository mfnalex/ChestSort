package de.jeff_media.chestsort.gui;

import com.jeff_media.morepersistentdatatypes.DataType;
import de.jeff_media.chestsort.ChestSortPlugin;
import de.jeff_media.chestsort.data.PlayerSetting;
import de.jeff_media.chestsort.gui.tracker.CustomGUITracker;
import de.jeff_media.chestsort.gui.tracker.CustomGUIType;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GUIListener implements Listener {

    private static final ChestSortPlugin main = ChestSortPlugin.getInstance();

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if(CustomGUITracker.getType(event.getView()) == CustomGUIType.NEW) {
            event.setCancelled(true);
        }

        ItemStack clicked = event.getCurrentItem();
        if(clicked == null || !clicked.hasItemMeta()) return;

        if(!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        PlayerSetting setting = main.getPlayerSetting(player);
        String function = Objects.requireNonNull(clicked.getItemMeta()).getPersistentDataContainer().getOrDefault(new NamespacedKey(main,"function"), PersistentDataType.STRING,"");
        List<String> userCommands = clicked.getItemMeta().getPersistentDataContainer().getOrDefault(new NamespacedKey(main,"user-commands"), DataType.asList(DataType.STRING), new ArrayList<>());
        List<String> adminCommands = clicked.getItemMeta().getPersistentDataContainer().getOrDefault(new NamespacedKey(main,"admin-commands"), DataType.asList(DataType.STRING), new ArrayList<>());

        executeCommands(player, player, userCommands);
        executeCommands(player, Bukkit.getConsoleSender(), adminCommands);
        //System.out.println("Click in GUI: " + function);

        switch (function) {
            case "": return;
            case "leftclick": setting.toggleLeftClick(); break;
            case "rightclick": setting.toggleRightClick(); break;
            case "shiftclick": setting.toggleShiftClick(); break;
            case "middleclick": setting.toggleMiddleClick(); break;
            case "shiftrightclick": setting.toggleShiftRightClick(); break;
            case "doubleclick": setting.toggleDoubleClick(); break;
            case "outside": setting.toggleLeftClickOutside(); break;
            case "autosorting": setting.toggleChestSorting(); break;
            case "autoinvsorting": setting.toggleInvSorting(); break;
        }

        new NewUI(player).showGUI();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClose(InventoryCloseEvent event) {
        CustomGUITracker.close(event.getView());
    }

    private void executeCommands(Player player, CommandSender sender, List<String> commands) {
        for(String command : commands) {
            main.getServer().dispatchCommand(sender, command.replace("{player}", player.getName()));
        }
    }

}
