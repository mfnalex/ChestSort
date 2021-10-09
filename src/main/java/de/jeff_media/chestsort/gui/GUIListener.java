package de.jeff_media.chestsort.gui;

import de.jeff_media.chestsort.ChestSortPlugin;
import de.jeff_media.chestsort.data.PlayerSetting;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class GUIListener implements Listener {

    private static final ChestSortPlugin main = ChestSortPlugin.getInstance();

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if(event.getView().getTopInventory().getHolder() instanceof ChestSortGUIHolder) {
            event.setCancelled(true);
        }

        ItemStack clicked = event.getCurrentItem();
        if(clicked == null || !clicked.hasItemMeta()) return;

        if(!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        PlayerSetting setting = main.getPlayerSetting(player);
        String function = clicked.getItemMeta().getPersistentDataContainer().getOrDefault(new NamespacedKey(main,"function"), PersistentDataType.STRING,"");

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
}
