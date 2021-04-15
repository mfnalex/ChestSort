package de.jeff_media.ChestSort;

import de.jeff_media.ChestSort.hooks.*;
import de.jeff_media.ChestSort.utils.LlamaUtils;
import de.jeff_media.ChestSortAPI.ChestSortAPI;
import de.jeff_media.ChestSortAPI.ChestSortEvent;
import de.jeff_media.ChestSortAPI.ISortable;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ChestSortListener implements Listener {

    final ChestSortPlugin plugin;
    final MinepacksHook minepacksHook;
    final HeadDatabaseHook headDatabaseHook;
    final CrateReloadedHook crateReloadedHook;
    final GoldenCratesHook goldenCratesHook;

    ChestSortListener(ChestSortPlugin plugin) {
        this.plugin = plugin;
        this.minepacksHook = new MinepacksHook(plugin);
        this.headDatabaseHook = new HeadDatabaseHook(plugin);
        this.crateReloadedHook = new CrateReloadedHook(plugin);
        this.goldenCratesHook = new GoldenCratesHook(plugin);
    }

    @EventHandler
    public void onLeftClickChest(PlayerInteractEvent event) {
        if(!event.getPlayer().hasPermission("chestsort.use")) return;
        if(event.getHand() != EquipmentSlot.HAND) return;
        if(event.getAction() != Action.LEFT_CLICK_BLOCK) return;
        if(!plugin.getConfig().getBoolean("allow-left-click-to-sort")) return;
        Block clickedBlock = event.getClickedBlock();
        if(!(clickedBlock.getState() instanceof Container)) return;
        plugin.registerPlayerIfNeeded(event.getPlayer());
        ChestSortPlayerSetting playerSetting = plugin.getPlayerSetting(event.getPlayer());
        if(!playerSetting.leftClickOutside) return;
        Container containerState = (Container) clickedBlock.getState();
        Inventory inventory = containerState.getInventory();
        plugin.getAPI().sortInventory(inventory);
        event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(plugin.messages.MSG_CONTAINER_SORTED));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        plugin.permissionsHandler.addPermissions(event.getPlayer());

        // Put player into our perPlayerSettings map
        plugin.registerPlayerIfNeeded(event.getPlayer());

        plugin.lgr.logPlayerJoin(event.getPlayer());

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.permissionsHandler.removePermissions(event.getPlayer());
        plugin.unregisterPlayer(event.getPlayer());
    }


    @EventHandler
    public void onBackPackClose(InventoryCloseEvent event) {
        if (plugin.getConfig().getString("sort-time").equalsIgnoreCase("close")
                || plugin.getConfig().getString("sort-time").equalsIgnoreCase("both"))
            onBackPackUse(event.getInventory(), (Player) event.getPlayer());
    }

    @EventHandler
    public void onBackPackOpen(InventoryOpenEvent event) {
        if (plugin.getConfig().getString("sort-time").equalsIgnoreCase("open")
                || plugin.getConfig().getString("sort-time").equalsIgnoreCase("both"))
            onBackPackUse(event.getInventory(), (Player) event.getPlayer());
    }

    void onBackPackUse(Inventory inv, Player p) {
        if(!plugin.getConfig().getBoolean("allow-automatic-sorting")) return; //TODO: Maybe change to allow-automatic-inventory-sorting ?
        if (!minepacksHook.isMinepacksBackpack(inv)) return;
        if (!p.hasPermission("chestsort.use")) return;
        plugin.registerPlayerIfNeeded(p);
        ChestSortPlayerSetting setting = plugin.perPlayerSettings.get(p.getUniqueId().toString());
        if (!setting.sortingEnabled) return;
        plugin.organizer.sortInventory(inv);
    }

    @EventHandler
    public void onPlayerInventoryClose(InventoryCloseEvent event) {

        plugin.debug("Attempt to automatically sort a player inventory");

        if (event.getInventory().getHolder() == null) {
            plugin.debug("Abort: holder == null");
            return;
        }
        // Might be obsolete, because its @NotNull in 1.15, but who knows if thats for 1.8
        if (event.getInventory().getType() == null) {
            plugin.debug("Abort: type == null");
            return;
        }
        if (event.getInventory().getType() != InventoryType.CRAFTING) {
            plugin.debug("Abort: type != CRAFTING, but "+event.getInventory().getType().name());
            return; // Weird! Returns CRAFTING instead of PLAYER
        }

        if (!(event.getInventory().getHolder() instanceof Player)) {
            plugin.debug("Abort: holder ! instanceof Player");
            return;
        }


        if(!plugin.getConfig().getBoolean("allow-automatic-inventory-sorting")) {
            plugin.debug("allow-automatic-inventory-sorting is false");
            return;
        }

        Player p = (Player) event.getInventory().getHolder();

        if (!p.hasPermission("chestsort.use.inventory")) {
            plugin.debug("Missing permission chestsort.use.inventory");
            return;
        }
        plugin.registerPlayerIfNeeded(p);

        ChestSortPlayerSetting setting = plugin.perPlayerSettings.get(p.getUniqueId().toString());
        if (!setting.invSortingEnabled) {
            plugin.debug("auto inv sorting not enabled for player "+p.getName());
            return;
        }

        plugin.lgr.logSort(p, ChestSortLogger.SortCause.INV_CLOSE);

        plugin.organizer.sortInventory(p.getInventory(), 9, 35);

    }

    // This event fires when someone closes an inventory
    // We check if the closed inventory belongs to a chest, shulkerbox or barrel,
    // and then call the Organizer to sort the inventory (if the player has
    // the chestsort.use permission and has /chestsort enabled)
    @EventHandler
    public void onChestClose(InventoryCloseEvent event) {

        if(!plugin.getConfig().getBoolean("allow-automatic-sorting")) return;

        if (!(plugin.getConfig().getString("sort-time").equalsIgnoreCase("close")
                || plugin.getConfig().getString("sort-time").equalsIgnoreCase("both"))) {
            return;
        }

        // event.getPlayer returns HumanEntity, so it could also be an NPC or something
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        Player p = (Player) event.getPlayer();
        Inventory inventory = event.getInventory();

        if (!isAPICall(inventory)
                && !belongsToChestLikeBlock(inventory)
                && !plugin.enderContainersHook.isEnderchest(inventory)
                && !LlamaUtils.belongsToLlama(inventory)) {
            return;
        }

        if (!isReadyToSort(p)) {
            return;
        }

        // Finally call the Organizer to sort the inventory

        plugin.lgr.logSort(p, ChestSortLogger.SortCause.CONT_CLOSE);

        // Llama inventories need special start/end slots
        if (LlamaUtils.belongsToLlama(event.getInventory())) {
            ChestedHorse llama = (ChestedHorse) event.getInventory().getHolder();
            plugin.organizer.sortInventory(event.getInventory(), 2, LlamaUtils.getLlamaChestSize(llama) + 1);
            return;
        }

        // Normal container inventories can be sorted completely
        plugin.organizer.sortInventory(event.getInventory());

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChestOpen(InventoryOpenEvent event) {

        plugin.debug("onChestOpen (InventoryOpenEvent");

        if(!plugin.getConfig().getBoolean("allow-automatic-sorting")) return;

        if (!(plugin.getConfig().getString("sort-time").equalsIgnoreCase("open")
                || plugin.getConfig().getString("sort-time").equalsIgnoreCase("both"))) {
            return;
        }

        if (event.isCancelled()) {
            return;
        }

        // event.getPlayer returns HumanEntity, so it could also be an NPC or something
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        Player p = (Player) event.getPlayer();
        Inventory inventory = event.getInventory();

        if (!isAPICall(inventory)
                && !belongsToChestLikeBlock(inventory)
                && !plugin.enderContainersHook.isEnderchest(inventory)
                && !LlamaUtils.belongsToLlama(inventory)) {
            return;
        }

        if (!isReadyToSort(p)) {
            return;
        }

        // Finally call the Organizer to sort the inventory

        plugin.lgr.logSort(p, ChestSortLogger.SortCause.CONT_OPEN);


        // Llama inventories need special start/end slots
        if (LlamaUtils.belongsToLlama(event.getInventory())) {
            ChestedHorse llama = (ChestedHorse) event.getInventory().getHolder();
            plugin.organizer.sortInventory(event.getInventory(), 2, LlamaUtils.getLlamaChestSize(llama) + 1);
            return;
        }

        // Normal container inventories can be sorted completely
        plugin.organizer.sortInventory(event.getInventory());

    }

    private boolean belongsToChestLikeBlock(Inventory inventory) {

        // Check by InventoryType
        if (inventory.getType() == InventoryType.ENDER_CHEST || inventory.getType().name().equalsIgnoreCase("SHULKER_BOX")) {
            return true;
        }

        // Possible Fix for https://github.com/JEFF-Media-GbR/Spigot-ChestSort/issues/13
        if (inventory.getHolder() == null) {
            return false;
        }

        // Check by InventoryHolder
        // Only continue if the inventory belongs to one of the following:
        // - a chest,
        // - double chest,
        // - shulkerbox (MC 1.11) (obsolete, is checked above by InventoryType
        // - barrel (MC 1.14)
        // - Minecart with Chest (MC 1.0)
        // NOTE: Hoppers are NOT included because it may break item sorters like those: https://minecraft.gamepedia.com/Tutorials/Hopper#Item_sorter
        // NOTE: We use .getClass().toString() for new items instead of directly
        // comparing the ENUM, because we
        // want to keep compatability between different minecraft versions (e.g. there
        // is no BARREL prior 1.14 and no shulker box prior 1.11)
        // WARNING: The names are inconsistent! A chest will return
        // org.bukkit.craftbukkit.v1_14_R1.block.CraftChest
        // in Spigot 1.14 while a double chest returns org.bukkit.block.DoubleChest
        return inventory.getHolder() instanceof Chest || inventory.getHolder() instanceof DoubleChest
                || inventory.getHolder().getClass().toString().endsWith(".CraftMinecartChest")
                || inventory.getHolder().getClass().toString().endsWith(".CraftShulkerBox") //Obsolete, is checked above by InventoryType
                || inventory.getHolder().getClass().toString().endsWith(".CraftBarrel");
    }

    private boolean isReadyToSort(Player p) {
        if (!p.hasPermission("chestsort.use")) {
            return false;
        }

        // checking in lower case for lazy admins
        if (plugin.disabledWorlds.contains(p.getWorld().getName().toLowerCase())) {
            return false;
        }

        // Don't sort automatically when player is spectator or in adventure mode
        // TODO: Make this configurable in config.yml
        if (p.getGameMode() == GameMode.SPECTATOR || p.getGameMode() == GameMode.ADVENTURE) {
            return false;
        }

        // Fixes exception when using Spigot's stupid /reload command
        plugin.registerPlayerIfNeeded(p);

        // Get the current player's settings
        // We do not immediately cancel when sorting is disabled because we might want
        // to show the hint message
        ChestSortPlayerSetting setting = plugin.perPlayerSettings.get(p.getUniqueId().toString());

        // Show "how to enable ChestSort" message when ALL of the following criteria are
        // met:
        // - Player has sorting disabled
        // - Player has not seen the message yet (whether or not this resets after a
        // logout
        // is defined by the config setting "show-message-again-after-logout")
        // - "show-message-when-using-chest" is set to true in the config.yml
        if (!plugin.isSortingEnabled(p)) {
            if (!setting.hasSeenMessage) {
                setting.hasSeenMessage = true;
                if (plugin.getConfig().getBoolean("show-message-when-using-chest")) {
                    p.sendMessage(plugin.messages.MSG_COMMANDMESSAGE);
                }
            }
            return false;
        }
        // Show "how to disable ChestSort" message when ALL of the following criteria
        // are met:
        // - Player has sorting enabled
        // - Player has not seen the message yet (whether or not this resets after a
        // logout
        // is defined by the config setting "show-message-again-after-logout")
        // - "show-message-when-using-chest-and-sorting-is-enabled" is set to true in
        // the config.yml
        else {
            if (!setting.hasSeenMessage) {
                setting.hasSeenMessage = true;
                if (plugin.getConfig().getBoolean("show-message-when-using-chest-and-sorting-is-enabled")) {
                    p.sendMessage(plugin.messages.MSG_COMMANDMESSAGE2);
                }
            }
        }
        return true;
    }

    @EventHandler
    public void onEnderChestOpen(InventoryOpenEvent event) {

        if(!plugin.getConfig().getBoolean("allow-automatic-sorting")) return;

        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        Player p = (Player) event.getPlayer();

        // Check if this is an EnderChest (is there a smarter way?)
        if (!event.getInventory().equals(p.getEnderChest())) {
            return;
        }

        if (isReadyToSort(p)) {

            // Finally call the Organizer to sort the inventory

            plugin.lgr.logSort(p, ChestSortLogger.SortCause.EC_OPEN);

            plugin.organizer.sortInventory(event.getInventory());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onHotkey(InventoryClickEvent event) {

        plugin.debug2("Hotkey?");

        if (!(event.getWhoClicked() instanceof Player)) {
            plugin.debug2("exit: 0");
            return;
        }


        Player p = (Player) event.getWhoClicked();

        plugin.registerPlayerIfNeeded(p);

        if (!plugin.getConfig().getBoolean("allow-sorting-hotkeys")) {
            plugin.debug2("exit: 1");
            return;
        }


        if (!p.hasPermission("chestsort.use") && !p.hasPermission("chestsort.use.inventory")) {
            plugin.debug2("exit: 2");
            return;
        }


        //InventoryHolder holder = event.getInventory().getHolder();
        if (event.getClickedInventory() == null) {
            plugin.debug2("exit: 3");
            return;
        }


        boolean isAPICall = isAPICall(event.getClickedInventory());

        // Detect generic GUIs
        if(!isAPICall &&
                (plugin.genericHook.isPluginGUI(event.getInventory())
                || plugin.genericHook.isPluginGUI(event.getInventory()))) {
            plugin.debug("Aborting hotkey sorting: no API call & generic GUI detected");
            return;
        }


        // Possible fix for #57
        if (!isAPICall && (event.getClickedInventory().getHolder() != null
                && event.getClickedInventory().getHolder() == p
                && event.getClickedInventory() != p.getInventory())) {
            return;
        }


        // End Possible fix for #57
        InventoryHolder holder = event.getClickedInventory().getHolder();

        boolean sort = false;
        ChestSortLogger.SortCause cause = null;

        ChestSortPlayerSetting setting = plugin.perPlayerSettings.get(p.getUniqueId().toString());

        // Do not sort the GUI inventory
        if (event.getClickedInventory() == setting.guiInventory) {
            return;
        }


        // Prevent player from putting items into GUI inventory
        if (event.getInventory() == setting.guiInventory) {
            event.setCancelled(true);
            return;
        }


        switch (event.getClick()) {
            case MIDDLE:
                cause = ChestSortLogger.SortCause.H_MIDDLE;
                //if(plugin.getConfig().getBoolean("hotkeys.middle-click")) {
                if (setting.middleClick) {
                    if (event.getWhoClicked().getGameMode() != GameMode.CREATIVE) {
                        sort = true;
                    } else {
                        if (event.getCurrentItem() != null || event.getCurrentItem().getType() != Material.AIR) {
                            sort = false;
                        }
                    }
                }
                break;
            case DOUBLE_CLICK:
                if(event.isShiftClick()) return;
                cause = ChestSortLogger.SortCause.H_DOUBLE;
                //if(plugin.getConfig().getBoolean("hotkeys.double-click")) {
                if (setting.doubleClick) {
                    // We need getCursor() instead of getCurrentItem(), because after picking up the item, it is gone into the cursor
                    if (event.getCursor() == null || (event.getCursor() != null && event.getCursor().getType() == Material.AIR)) {
                        sort = true;
                    }
                }
                break;
            case SHIFT_LEFT:
                cause = ChestSortLogger.SortCause.H_SHIFT;
                //if(plugin.getConfig().getBoolean("hotkeys.shift-click")) {
                if (setting.shiftClick) {
                    if (event.getCurrentItem() == null || (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.AIR)) {
                        sort = true;
                    }
                }
                break;
            case SHIFT_RIGHT:
                cause = ChestSortLogger.SortCause.H_SHIFTRIGHT;
                //if(plugin.getConfig().getBoolean("hotkeys.shift-right-click")) {
                if (setting.shiftRightClick) {
                    if (event.getCurrentItem() == null || (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.AIR)) {
                        sort = true;
                    }
                }
                break;
            default:
                break;
        }

        if (!sort) {
            return;
        }


        if(plugin.isInHotkeyCooldown(p.getUniqueId())) {
            plugin.debug("Skipping: hotkey cooldown");
            return;
        }


        plugin.debug("Hotkey triggered: " + event.getClick().name());

        if (isAPICall
                || belongsToChestLikeBlock(event.getClickedInventory())
                || LlamaUtils.belongsToLlama(event.getClickedInventory())
                || minepacksHook.isMinepacksBackpack(event.getClickedInventory())
                || plugin.playerVaultsHook.isPlayerVault(event.getClickedInventory())
                || plugin.enderContainersHook.isEnderchest(event.getClickedInventory())) {


            if (!p.hasPermission("chestsort.use")) {
                return;
            }

            if (LlamaUtils.belongsToLlama(event.getClickedInventory())) {

                plugin.lgr.logSort(p,cause);
                ChestedHorse llama = (ChestedHorse) event.getInventory().getHolder();
                plugin.organizer.sortInventory(event.getClickedInventory(), 2, LlamaUtils.getLlamaChestSize(llama) + 1);
                plugin.organizer.updateInventoryView(event);
                return;
            }

            plugin.lgr.logSort(p,cause);
            plugin.organizer.sortInventory(event.getClickedInventory());
            plugin.organizer.updateInventoryView(event);
        } else if (holder instanceof Player) {

            if (!p.hasPermission("chestsort.use.inventory")) {
                return;
            }

            if (event.getSlotType() == SlotType.QUICKBAR) {
                plugin.lgr.logSort(p,cause);
                plugin.organizer.sortInventory(p.getInventory(), 0, 8);
                plugin.organizer.updateInventoryView(event);

            } else if (event.getSlotType() == SlotType.CONTAINER) {
                plugin.lgr.logSort(p,cause);
                plugin.organizer.sortInventory(p.getInventory(), 9, 35);
                plugin.organizer.updateInventoryView(event);

            }
        }
    }

    private boolean isAPICall(Inventory inv) {
        if(inv==null) return false;
        return inv.getHolder() instanceof ISortable;
    }

    @EventHandler
    public void onAdditionalHotkeys(InventoryClickEvent e) {

        if (LlamaUtils.belongsToLlama(e.getInventory()) || LlamaUtils.belongsToLlama(e.getClickedInventory())) {
            return;
        }

        if (!plugin.getConfig().getBoolean("allow-additional-hotkeys")) {
            return;
        }
        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }
        Player p = (Player) e.getWhoClicked();
        // Only continue if clicked outside of the chest
        if (e.getClickedInventory() != null) {
            return;
        }
        // Only continue if hand is empty
        if (e.getCursor() != null && e.getCursor().getType() != null && e.getCursor().getType() != Material.AIR) {
            return;
        }
        // Possible fix for #57
        if (e.getInventory().getHolder() == null) return;
        if (e.getInventory().getHolder() == p && e.getInventory() != p.getInventory()) return;
        // End Possible fix for #57
        if (e.getInventory().getType() != InventoryType.CHEST
                && e.getInventory().getType() != InventoryType.DISPENSER
                && e.getInventory().getType() != InventoryType.DROPPER
                && e.getInventory().getType() != InventoryType.ENDER_CHEST
                && !e.getInventory().getType().name().equalsIgnoreCase("SHULKER_BOX")
                && (e.getInventory().getHolder() == null || !e.getInventory().getHolder().getClass().toString().endsWith(".CraftBarrel"))
                && !(e.getInventory().getHolder() instanceof ISortable)) {
            return;
        }

        // HeadDatabase hook
        if(headDatabaseHook.isHeadDB(e.getClickedInventory())
                || headDatabaseHook.isHeadDB(e.getInventory())) {
            return;
        }

        // CrateReloaded hook
        if(crateReloadedHook.isCrate(e.getClickedInventory())
                || crateReloadedHook.isCrate(e.getInventory())) {
            //if(plugin.debug) plugin.getLogger().info("Aborting hotkey because this is a CrateReloaded crate");
            return;
        }

        // GoldenCrates hook
        if(goldenCratesHook.isCrate(e.getClickedInventory())
                || goldenCratesHook.isCrate(e.getInventory())) {
            //if(plugin.debug) plugin.getLogger().info("Aborting hotkey because this is a CrateReloaded crate");
            return;
        }

        // Detect generic GUIs
        if(!isAPICall(e.getInventory()) && !isAPICall(e.getClickedInventory()) &&
                (plugin.genericHook.isPluginGUI(e.getInventory())
                        || plugin.genericHook.isPluginGUI(e.getInventory()))) {
            return;
        }

        // Don't sort inventories belonging to BossShopPro
        if (e.getInventory() != null && e.getInventory().getHolder() != null && e.getInventory().getHolder().getClass().getName().equalsIgnoreCase("org.black_ixx.bossshop.core.BSShopHolder")) {
            return;
        }

        if (!p.hasPermission("chestsort.use")) return;

        plugin.registerPlayerIfNeeded(p);
        ChestSortPlayerSetting setting = plugin.perPlayerSettings.get(p.getUniqueId().toString());


        ChestSortEvent chestSortEvent = new ChestSortEvent(e.getInventory());
        chestSortEvent.setPlayer(e.getWhoClicked());
        chestSortEvent.setLocation(e.getWhoClicked().getLocation());

        chestSortEvent.setSortableMaps(new HashMap<ItemStack, Map<String, String>>());
        for (ItemStack item : e.getInventory().getContents()) {
            chestSortEvent.getSortableMaps().put(item, plugin.organizer.getSortableMap(item));
        }

        Bukkit.getPluginManager().callEvent(chestSortEvent);
        if (chestSortEvent.isCancelled()) {
            return;
        }

        if (e.isLeftClick() && setting.leftClick) {
            plugin.lgr.logSort(p, ChestSortLogger.SortCause.H_LEFT);
            if (setting.getCurrentDoubleClick(plugin, ChestSortPlayerSetting.DoubleClickType.LEFT_CLICK)
                    == ChestSortPlayerSetting.DoubleClickType.LEFT_CLICK) {
            	// Left double click: put everything into destination
                plugin.organizer.stuffPlayerInventoryIntoAnother(p.getInventory(), e.getInventory(), false, chestSortEvent);
                plugin.organizer.sortInventory(e.getInventory());
            } else {
            	// Left single click: put only matching items into destination
                plugin.organizer.stuffPlayerInventoryIntoAnother(p.getInventory(), e.getInventory(), true, chestSortEvent);
            }

        } else if (e.isRightClick() && setting.rightClick) {
            plugin.lgr.logSort(p, ChestSortLogger.SortCause.H_RIGHT);
            if (setting.getCurrentDoubleClick(plugin, ChestSortPlayerSetting.DoubleClickType.RIGHT_CLICK)
                    == ChestSortPlayerSetting.DoubleClickType.RIGHT_CLICK) {
            	// Right double click: put everything into player inventory
                plugin.organizer.stuffInventoryIntoAnother(e.getInventory(), p.getInventory(), e.getInventory(), false);
                plugin.organizer.sortInventory(p.getInventory(),9,35);
            } else {
            	// Right single click: put only matching items into player inventory
                plugin.organizer.stuffInventoryIntoAnother(e.getInventory(), p.getInventory(), e.getInventory(), true);
            }

        }
        //plugin.organizer.sortInventory(e.getInventory());
        plugin.organizer.updateInventoryView(e.getInventory());
        plugin.organizer.updateInventoryView(p.getInventory());
    }


}
