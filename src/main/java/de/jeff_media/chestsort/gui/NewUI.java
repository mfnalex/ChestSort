package de.jeff_media.chestsort.gui;

import com.jeff_media.morepersistentdatatypes.DataType;
import de.jeff_media.chestsort.ChestSortPlugin;
import de.jeff_media.chestsort.enums.Hotkey;
import com.jeff_media.jefflib.ItemStackUtils;
import com.jeff_media.jefflib.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

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
            Hotkey key = Hotkey.fromPermission(buttonName);
            if(key != null && !Hotkey.fromPermission(buttonName).hasPermission(player)) {
                buttonName = buttonName + "-nopermission";
            } else {
                boolean enabled = true;
                if(key != null) enabled = Hotkey.fromPermission(buttonName).hasEnabled(player);
                //System.out.println(buttonName + " is enabled: " + enabled);
                if(key != null) buttonName = buttonName + (enabled ? "-enabled" : "-disabled");
            }
            if(main.isDebug()) System.out.println("Button name: " + buttonName);
            ItemStack button = ItemStackUtils.fromConfigurationSection(conf.getConfigurationSection("items." + buttonName));
            //System.out.println(button);
            if(button.hasItemMeta() && !buttonName.endsWith("-nopermission")) {
                ItemMeta meta = button.getItemMeta();
                assert meta != null;
                meta.getPersistentDataContainer().set(new NamespacedKey(main,"function"),PersistentDataType.STRING, buttonName.split("-")[0]);
                List<String> userCommands = conf.getStringList("items." + buttonName + ".commands.player");
                List<String> adminCommands = conf.getStringList("items." + buttonName + ".commands.console");
                meta.getPersistentDataContainer().set(new NamespacedKey(main,"user-commands"), DataType.asList(DataType.STRING), userCommands);
                meta.getPersistentDataContainer().set(new NamespacedKey(main,"admin-commands"), DataType.asList(DataType.STRING), adminCommands);
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
