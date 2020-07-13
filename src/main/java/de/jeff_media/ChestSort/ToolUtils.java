package de.jeff_media.ChestSort;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

public class ToolUtils implements Listener {

    // Configurable
    boolean hotbarOnly = false;

    static int hotbarSize = 9;
    static int inventorySize = 36;
    static int favoriteSlot = hotbarSize-1;

    HashMap<Material,Tool> toolMap = new HashMap<>();
    ArrayList<Tag> usedTags = new ArrayList<>();
    final Material[] pickaxes = {
            Material.NETHERITE_PICKAXE,
            Material.DIAMOND_PICKAXE,
            Material.IRON_PICKAXE,
            Material.STONE_PICKAXE,
            Material.WOODEN_PICKAXE};
    final Material[] axes = {
            Material.NETHERITE_AXE,
            Material.DIAMOND_AXE,
            Material.IRON_AXE,
            Material.STONE_AXE,
            Material.WOODEN_AXE};
    final Material[] shovels = {
            Material.NETHERITE_SHOVEL,
            Material.DIAMOND_SHOVEL,
            Material.IRON_SHOVEL,
            Material.STONE_SHOVEL,
            Material.WOODEN_SHOVEL};

    final Material[] hoes = {
            Material.NETHERITE_HOE,
            Material.DIAMOND_HOE,
            Material.IRON_HOE,
            Material.STONE_HOE,
            Material.WOODEN_HOE};

    ToolUtils() {
        initMap();
    }

    enum Tool {
        PICKAXE,
        SHOVEL,
        SHEARS,
        AXE,
        HOE,
        NONE
    }

    private void initMap() {
        long startTime = System.nanoTime();
        tagToMap(Tag.ANVIL,Tool.PICKAXE);
        tagToMap(Tag.BEEHIVES,Tool.AXE);
        tagToMap(Tag.CRIMSON_STEMS,Tool.AXE);
        tagToMap(Tag.BAMBOO_PLANTABLE_ON,Tool.SHOVEL);
        tagToMap(Tag.ICE,Tool.PICKAXE);
        tagToMap(Tag.LOGS,Tool.AXE);
        tagToMap(Tag.PLANKS,Tool.AXE);
        tagToMap(Tag.RAILS,Tool.PICKAXE);
        tagToMap(Tag.SIGNS,Tool.AXE);

        tagToMap(Tag.WALLS,Tool.PICKAXE);
        tagToMap(Tag.WOOL,Tool.SHEARS);

        tagToMap(Tag.CROPS,Tool.NONE);
        tagToMap(Tag.FENCE_GATES,Tool.AXE);
        tagToMap(Tag.FENCES,Tool.AXE);
        tagToMap(Tag.FLOWERS,Tool.NONE);
        tagToMap(Tag.LEAVES,Tool.SHEARS);

        // Order is important
        tagToMap(Tag.PRESSURE_PLATES, Tool.PICKAXE);
        tagToMap(Tag.WOODEN_PRESSURE_PLATES,Tool.AXE);
        tagToMap(Tag.DOORS,Tool.AXE);
        tagToMap(Tag.DOORS,Tool.PICKAXE,"IRON");
        tagToMap(Tag.TRAPDOORS,Tool.AXE);
        tagToMap(Tag.TRAPDOORS,Tool.PICKAXE,"IRON");
        tagToMap(Tag.BUTTONS,Tool.AXE);
        tagToMap(Tag.BUTTONS,Tool.PICKAXE,"STONE");

        tagToMap(Tag.SAND,Tool.SHOVEL);
        tagToMap(Tag.SHULKER_BOXES,Tool.PICKAXE);
        tagToMap(Tag.STONE_BRICKS,Tool.PICKAXE);

        addToMap(Material.VINE,Tool.SHEARS);
        long endTime = System.nanoTime();
        printMap();
        System.out.println(String.format("Building the map took %d ms",(endTime-startTime)/1000000));
    }

    private void printMap() {
        toolMap.forEach((mat, tool) -> System.out.println(String.format("%0$30s -> %s", mat.name(), tool.name())));
    }

    private void addToMap(Material mat, Tool tool) {
        toolMap.put(mat, tool);
    }

    private void tagToMap(Tag<Material> tag, Tool tool) {
        /*for(Material mat : tag.getValues() ) {
            addToMap(mat,tool);
        }*/
        tagToMap(tag,tool,null);
    }

    private void tagToMap(Tag<Material> tag, Tool tool, @Nullable String match) {
        for(Material mat : tag.getValues()) {
            if(match==null) {
                addToMap(mat,tool);
            } else {
                if (mat.name().contains(match)) {
                    addToMap(mat,tool);
                }
            }
        }
        usedTags.add(tag);
    }

    /**
     * Gets the best tool type for a material
     * @param mat The block's material
     * @return Best tool type for that material
     */
    @NotNull
    Tool getBestToolType(Material mat) {
        Tool bestTool = toolMap.get(mat);
        if(bestTool == null) bestTool = Tool.NONE;
        System.out.println("Best ToolType for "+mat+" is "+bestTool.name());
        return bestTool;
    }

    /**
     * Searches through and array and returns the ItemStack that matches this material
     * @param mat Material to look for
     * @param items Player's items (whole inventory or hotbar)
     * @return Matching ItemStack
     */
    @Nullable
    ItemStack getItemStackFromArray(Material mat, ItemStack[] items) {
        for(ItemStack item : items) {
            if(item==null) continue;
            if(item.getType()==mat) return item;
        }
        return null;
    }

    /**
     * Searches the player's inventory for the best matching tool and returns its ItemStack
     * @param type Tool type
     * @param items Player's items (whole inventory or hotbar)
     * @return
     */
    @Nullable
    ItemStack typeToItem(Tool type, ItemStack[] items) {

        Objects.requireNonNull(type,"type cannot be null.");

        switch(type) {

            case PICKAXE:
                for(Material pickaxe : pickaxes) {
                    ItemStack itemStack = getItemStackFromArray(pickaxe, items);
                    if(itemStack != null) return itemStack;
                }
                return null;

            case AXE:
                for(Material axe : axes) {
                    ItemStack itemStack = getItemStackFromArray(axe, items);
                    if(itemStack != null) return itemStack;
                }
                return null;

            case SHOVEL:
                for(Material shovel : shovels) {
                    ItemStack itemStack = getItemStackFromArray(shovel, items);
                    if(itemStack != null) return itemStack;
                }
                System.out.println("typeToItem -> shovel -> null");
                return null;

            case HOE:
                for(Material hoe : hoes) {
                    ItemStack itemStack = getItemStackFromArray(hoe, items);
                    if(itemStack != null) return itemStack;
                }
                return null;

            case SHEARS:
                return getItemStackFromArray(Material.SHEARS, items);

            default:
                return null;
        }
    }

    /**
     * Tries to get the ItemStack that is the best for this block
     * @param mat The block's material
     * @param inv Player's inventory
     * @return
     */
    @Nullable
    ItemStack getBestToolFromInventory(Material mat, PlayerInventory inv) {
        ItemStack[] hotbar = new ItemStack[(hotbarOnly ? hotbarSize : inventorySize)];
        Tool bestType = getBestToolType(mat);
        for(int i = 0; i < (hotbarOnly ? hotbarSize : inventorySize); i++) {
            hotbar[i] = inv.getItem(i);
        }
        ItemStack debug = typeToItem(bestType,hotbar);
        if(debug == null) System.out.println("debug == null");
        return debug;
    }



    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;
        /*if (event.getHand() != EquipmentSlot.HAND)
            return;*/

        PlayerInventory inv = event.getPlayer().getInventory();
        Block block = event.getClickedBlock();
        if (block == null) return;

        ItemStack bestTool = getBestToolFromInventory(block.getType(), inv);
        if(bestTool == null) {
            freeSlot(favoriteSlot,inv);
            //System.out.println("Could not find any appropiate tool");
            return;
        }
        int positionInInventory = getPositionInInventory(bestTool,inv) ;
        if(positionInInventory != 0) {
            moveToolToSlot(positionInInventory,favoriteSlot,inv);
        } else {
            freeSlot(favoriteSlot,inv);
        }
    }

    /**
     * Gets the slot number of a given ItemStack
     * @param item ItemStack that we need the slot number of
     * @param inv Player's inventory
     * @return
     */
    int getPositionInInventory(ItemStack item, PlayerInventory inv) {
        for(int i = 0; i < inv.getSize(); i++) {
            ItemStack currentItem = inv.getItem(i);
            if(currentItem==null) continue;
            if(currentItem.equals(item)) {
                System.out.println(String.format("Found perfect tool %s at slot %d",currentItem.getType().name(),i));
                return i;
            }
        }
        return 0;
    }

    /**
     * Moves a tool to the given slot
     * @param source Slot where the tool is
     * @param dest Slot where the tool should be
     * @param inv Player's inventory
     */
    private void moveToolToSlot(int source, int dest, PlayerInventory inv) {
        System.out.println(String.format("Moving item from slot %d to %d",source,dest));
        inv.setHeldItemSlot(dest);
        if(source==dest) return;
        ItemStack sourceItem = inv.getItem(source);
        ItemStack destItem = inv.getItem(dest);
        if(source < hotbarSize) {
            inv.setHeldItemSlot(source);
            return;
        }
        if(destItem == null) {
            inv.setItem(dest,sourceItem);
            inv.setItem(source,null);
        } else {
            inv.setItem(source, destItem);
            inv.setItem(dest, sourceItem);
        }
    }

    /**
     * Tries to free the slot if it is occupied with a damageable item
     * @param source Slot to free
     * @param inv Player's inventory
     */
    private void freeSlot(int source, PlayerInventory inv) {
        System.out.println(String.format("Trying to free slot %d",source));
        ItemStack item = inv.getItem(source);

        // If current slot is empty, we don't have to change it
        if(item == null) return;

        // If the item is not damageable, we don't have to move it
        ItemMeta meta = item.getItemMeta();
        if(!(meta instanceof Damageable)) return;

        // Try to combine the item with existing stacks
        inv.setItem(source, null);
        inv.addItem(item);

        // If the item was moved to the same slot, we have to move it somewhere else
        if(inv.getItem(source)==null) return;
        for(int i = source; i < inventorySize; i++) {
            if(inv.getItem(i)==null) {
                inv.setItem(i,item);
                inv.setItem(source,null);
                return;
            }
        }
        // TODO: If all of that didn't work, change to some block that is not damageable
    }

}
