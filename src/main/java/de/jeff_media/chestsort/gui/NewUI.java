package de.jeff_media.chestsort.gui;

import de.jeff_media.chestsort.ChestSortPlugin;
import de.jeff_media.chestsort.enums.Hotkey;
import de.jeff_media.jefflib.ItemStackUtils;
import de.jeff_media.jefflib.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class NewUI {

    private static final ChestSortPlugin main = ChestSortPlugin.getInstance();
    private final YamlConfiguration conf = main.getGuiConfig();
    private final Player player;

    public NewUI(Player player) {
        this.player = player;
    }

    private ItemStack getItem(int slot) {
        if(conf.isConfigurationSection("slots." + slot)) {
            return ItemStackUtils.fromConfigurationSection(conf.getConfigurationSection("slots." + slot));
        }
        if(conf.isString("slots." + slot)) {
            String buttonName = conf.getString("slots." + slot);
            //if(!player.hasPermission("chestsort.hotkey." + buttonName)) {
            if(!Hotkey.fromPermission(buttonName).hasPermission(player)) {
                buttonName = buttonName + "-nopermission";
            } else {
                boolean enabled = Hotkey.fromPermission(buttonName).hasEnabled(player);
                //System.out.println(buttonName + " is enabled: " + enabled);
                buttonName = buttonName + (enabled ? "-enabled" : "-disabled");
            }
            ItemStack button = ItemStackUtils.fromConfigurationSection(conf.getConfigurationSection("items." + buttonName));
            //System.out.println(button);
            if(button.hasItemMeta() && !buttonName.endsWith("-nopermission")) {
                ItemMeta meta = button.getItemMeta();
                assert meta != null;
                meta.getPersistentDataContainer().set(new NamespacedKey(main,"function"),PersistentDataType.STRING, buttonName.split("-")[0]);
                button.setItemMeta(meta);
            }
            return button;
        }
        return null;
    }

    public void showGUI() {

        NewUI gui = new NewUI(player);

        int size = conf.getInt("size");
        String title = TextUtils.format(conf.getString("title"));

        ChestSortGUIHolder holder = new ChestSortGUIHolder();
        Inventory inv = Bukkit.createInventory(holder, size, title);
        holder.setInventory(inv);

        for(int i = 0; i < size; i++) {
            ItemStack item = getItem(i);
            inv.setItem(i, item);
        }

        player.openInventory(inv);

    }

}
