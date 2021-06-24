package de.jeff_media.chestsort.gui;

import de.jeff_media.chestsort.ChestSortPlugin;
import de.jeff_media.chestsort.config.Messages;
import de.jeff_media.chestsort.data.PlayerSetting;
import de.jeff_media.chestsort.enums.Hotkey;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SettingsGUI implements Listener {

    public static final int slotMiddleClick = 1;
    public static final int slotShiftClick = 3;
    public static final int slotDoubleClick = 5;
    public static final int slotShiftRightClick = 7;
    public static final int slotLeftClickFromOutside = 4 + 9;
    public static final int slotLeftClick = 2 + 18;
    public static final int slotRightClick = 6 + 18;
    final static Material red = Material.REDSTONE_BLOCK;
    final static Material green = Material.EMERALD_BLOCK;
    final ChestSortPlugin plugin;

    public SettingsGUI(ChestSortPlugin plugin) {
        this.plugin = plugin;
    }

    ItemStack getItem(boolean active, Hotkey hotkey, Player player) {

        //System.out.println("Getting Item for hotkey " + hotkey);

        ItemStack is;
        String suffix;

        if(!player.hasPermission(Hotkey.getPermission(hotkey))) {
            //System.out.println(" Player does NOT have permission " + hotkey.name());
            return null;
        }

        if (active) {
            is = new ItemStack(green);
            suffix = Messages.MSG_GUI_ENABLED;
        } else {
            is = new ItemStack(red);
            suffix = Messages.MSG_GUI_DISABLED;
        }

        ItemMeta meta = is.getItemMeta();

        switch (hotkey) {
            case MIDDLE_CLICK:
                meta.setDisplayName(ChatColor.RESET + Messages.MSG_GUI_MIDDLECLICK + ": " + suffix);
                break;
            case SHIFT_CLICK:
                meta.setDisplayName(ChatColor.RESET + Messages.MSG_GUI_SHIFTCLICK + ": " + suffix);
                break;
            case DOUBLE_CLICK:
                meta.setDisplayName(ChatColor.RESET + Messages.MSG_GUI_DOUBLECLICK + ": " + suffix);
                break;
            case SHIFT_RIGHT_CLICK:
                meta.setDisplayName(ChatColor.RESET + Messages.MSG_GUI_SHIFTRIGHTCLICK + ": " + suffix);
                break;
            case OUTSIDE:
                meta.setDisplayName(ChatColor.RESET + Messages.MSG_GUI_LEFTCLICKOUTSIDE + ": " + suffix);
                break;
            case LEFT_CLICK:
                meta.setDisplayName(ChatColor.RESET + Messages.MSG_GUI_LEFTCLICK + ": " + suffix);
                break;
            case RIGHT_CLICK:
                meta.setDisplayName(ChatColor.RESET + Messages.MSG_GUI_RIGHTCLICK + ": " + suffix);
                break;
            default:
                break;
        }

        is.setItemMeta(meta);

        return is;
    }

    public void openGUI(Player player) {
        Inventory inventory = createGUI("ChestSort", player);

        PlayerSetting setting = plugin.getPerPlayerSettings().get(player.getUniqueId().toString());

        if (plugin.getConfig().getBoolean("allow-sorting-hotkeys")) {
			if(player.hasPermission(Hotkey.getPermission(Hotkey.MIDDLE_CLICK)))  inventory.setItem(slotMiddleClick, getItem(setting.middleClick, Hotkey.MIDDLE_CLICK, player));
            if(player.hasPermission(Hotkey.getPermission(Hotkey.SHIFT_CLICK)))  inventory.setItem(slotShiftClick, getItem(setting.shiftClick, Hotkey.SHIFT_CLICK, player));
			if(player.hasPermission(Hotkey.getPermission(Hotkey.DOUBLE_CLICK))) inventory.setItem(slotDoubleClick, getItem(setting.doubleClick, Hotkey.DOUBLE_CLICK, player));
			if(player.hasPermission(Hotkey.getPermission(Hotkey.SHIFT_RIGHT_CLICK)))  inventory.setItem(slotShiftRightClick, getItem(setting.shiftRightClick, Hotkey.SHIFT_RIGHT_CLICK, player));
        }
        if (plugin.getConfig().getBoolean("allow-left-click-to-sort")) {
			if(player.hasPermission(Hotkey.getPermission(Hotkey.OUTSIDE)))  inventory.setItem(slotLeftClickFromOutside, getItem(setting.leftClickOutside, Hotkey.OUTSIDE, player));
        }
        if (plugin.getConfig().getBoolean("allow-additional-hotkeys")) {
			if(player.hasPermission(Hotkey.getPermission(Hotkey.LEFT_CLICK)))  inventory.setItem(slotLeftClick, getItem(setting.leftClick, Hotkey.LEFT_CLICK, player));
			if(player.hasPermission(Hotkey.getPermission(Hotkey.RIGHT_CLICK))) inventory.setItem(slotRightClick, getItem(setting.rightClick, Hotkey.RIGHT_CLICK, player));
        }

        setting.guiInventory = inventory;
        player.openInventory(inventory);
    }

    Inventory createGUI(String name, Player inventoryHolder) {
        return Bukkit.createInventory(inventoryHolder, InventoryType.CHEST, name);
    }

    @EventHandler
    void onGUIInteract(InventoryClickEvent event) {
        if (!plugin.isHotkeyGUI()) {
            return;
        }
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        plugin.registerPlayerIfNeeded(player);
        PlayerSetting setting = plugin.getPerPlayerSettings().get(player.getUniqueId().toString());

        if (setting.guiInventory == null) {
            return;
        }

        if (event.getClickedInventory() == null) {
            return;
        }
        if (!event.getClickedInventory().equals(setting.guiInventory)) {
            return;
        }

        // We only get this far if the player has clicked inside his GUI inventory
        event.setCancelled(true);
        if (event.getClick() != ClickType.LEFT) {
            return;
        }

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
            return;
        }

        if (event.getSlot() == SettingsGUI.slotMiddleClick) {
			if(player.hasPermission(Hotkey.getPermission(Hotkey.MIDDLE_CLICK))) setting.toggleMiddleClick();
            plugin.getSettingsGUI().openGUI(player);
        } else if (event.getSlot() == SettingsGUI.slotShiftClick) {
			if(player.hasPermission(Hotkey.getPermission(Hotkey.SHIFT_CLICK))) setting.toggleShiftClick();
            plugin.getSettingsGUI().openGUI(player);
        } else if (event.getSlot() == SettingsGUI.slotDoubleClick) {
			if(player.hasPermission(Hotkey.getPermission(Hotkey.DOUBLE_CLICK))) setting.toggleDoubleClick();
            plugin.getSettingsGUI().openGUI(player);
        } else if (event.getSlot() == SettingsGUI.slotLeftClickFromOutside) {
			if(player.hasPermission(Hotkey.getPermission(Hotkey.OUTSIDE))) setting.toggleLeftClickOutside();
            plugin.getSettingsGUI().openGUI(player);
        } else if (event.getSlot() == SettingsGUI.slotShiftRightClick) {
			if(player.hasPermission(Hotkey.getPermission(Hotkey.SHIFT_RIGHT_CLICK))) setting.toggleShiftRightClick();
            plugin.getSettingsGUI().openGUI(player);
        } else if (event.getSlot() == SettingsGUI.slotLeftClick) {
			if(player.hasPermission(Hotkey.getPermission(Hotkey.LEFT_CLICK))) setting.toggleLeftClick();
            plugin.getSettingsGUI().openGUI(player);
        } else if (event.getSlot() == SettingsGUI.slotRightClick) {
			if(player.hasPermission(Hotkey.getPermission(Hotkey.RIGHT_CLICK))) setting.toggleRightClick();
            plugin.getSettingsGUI().openGUI(player);
        }

    }
}
