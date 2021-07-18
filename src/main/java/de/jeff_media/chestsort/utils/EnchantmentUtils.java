package de.jeff_media.chestsort.utils;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class EnchantmentUtils {

    public static String getEnchantmentString(ItemStack item) {
        StringBuilder builder = new StringBuilder(",");
        builder.append(getInversedEnchantmentAmount(item));
        if(!item.hasItemMeta()) return builder.toString();
        ItemMeta meta = item.getItemMeta();
        if(!meta.hasEnchants() && !(meta instanceof EnchantmentStorageMeta)) return builder.toString();
        List<Enchantment> sortedEnchants = new ArrayList<>(meta.getEnchants().keySet());
        sortedEnchants.sort(Comparator.comparing(o -> o.getKey().getKey()));
        for(Enchantment enchantment : sortedEnchants) {
            builder.append(",");
            builder.append(enchantment.getKey().getKey());
            builder.append(",");
            builder.append(Integer.MAX_VALUE - meta.getEnchantLevel(enchantment));
        }
        if(meta instanceof EnchantmentStorageMeta) {
            List<Enchantment> sortedStoredEnchants = new ArrayList<>(((EnchantmentStorageMeta)meta).getStoredEnchants().keySet());
            for(Enchantment enchantment : sortedStoredEnchants) {
                builder.append(",");
                builder.append(enchantment.getKey().getKey());
                builder.append(",");
                builder.append(Integer.MAX_VALUE - meta.getEnchantLevel(enchantment));
            }
        }
        return builder.toString();
    }

    public static int getInversedEnchantmentAmount(ItemStack item) {
        int amount = Integer.MAX_VALUE;
        ItemMeta meta = item.getItemMeta();
        if(!meta.hasEnchants() && !(meta instanceof EnchantmentStorageMeta)) return amount;
        for(int level : meta.getEnchants().values()) {
            amount -= level;
        }
        if(meta instanceof EnchantmentStorageMeta) {
            for(int level : ((EnchantmentStorageMeta)meta).getStoredEnchants().values()) {
                amount -= level;
            }
        }
        return amount;
    }

}
