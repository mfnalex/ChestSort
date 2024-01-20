package de.jeff_media.chestsort.listeners;

import com.jeff_media.jefflib.ProtectionUtils;
import de.jeff_media.chestsort.ChestSortPlugin;
import de.jeff_media.chestsort.api.ChestSortEvent;
import de.jeff_media.chestsort.api.ChestSortPostSortEvent;
import de.jeff_media.chestsort.api.ISortable;
import de.jeff_media.chestsort.config.Messages;
import de.jeff_media.chestsort.data.PlayerSetting;
import de.jeff_media.chestsort.enums.Hotkey;
import de.jeff_media.chestsort.events.ChestSortLeftClickHotkeyEvent;
import de.jeff_media.chestsort.handlers.Logger;
import de.jeff_media.chestsort.hooks.AdvancedChestsHook;
import de.jeff_media.chestsort.hooks.CrateReloadedHook;
import de.jeff_media.chestsort.hooks.GoldenCratesHook;
import de.jeff_media.chestsort.hooks.HeadDatabaseHook;
import de.jeff_media.chestsort.hooks.MinepacksHook;
import de.jeff_media.chestsort.hooks.ShulkerPacksHook;
import de.jeff_media.chestsort.utils.LlamaUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChestSortListener implements org.bukkit.event.Listener {

    final List<String> blacklistedInventoryHolderClassNames = new ArrayList<String>() {
        {
            add("ICInventoryHolder"); // Interactive Chat
        }
    };
    private static Event ignoredEvent;
    public final MinepacksHook minepacksHook;
    final ChestSortPlugin plugin;
    final HeadDatabaseHook headDatabaseHook;
    final GoldenCratesHook goldenCratesHook;
    final AdvancedChestsHook advancedChestsHook;

    public ChestSortListener(ChestSortPlugin plugin) {
        this.plugin = plugin;
        this.minepacksHook = new MinepacksHook(plugin);
        this.headDatabaseHook = new HeadDatabaseHook(plugin);
        this.goldenCratesHook = new GoldenCratesHook(plugin);
        this.advancedChestsHook = new AdvancedChestsHook(plugin);
    }

    private boolean isPossiblyBlacklisted(InventoryView view) {
        Inventory top = view.getTopInventory();
        Inventory bottom = view.getBottomInventory();
        Set<String> toCheck = new HashSet<>();
        if (top != null) {
            InventoryHolder holder = top.getHolder();
            if (holder != null) {
                String className = holder.getClass().getName();
                toCheck.add(className);

            }
        }

        if (bottom != null) {
            InventoryHolder holder = bottom.getHolder();
            if (holder != null) {
                String className = holder.getClass().getName();
                toCheck.add(className);
            }
        }

        for (String className : toCheck) {
            for (String blacklistedClassName : blacklistedInventoryHolderClassNames) {
                if (className.contains(blacklistedClassName)) {
                    return true;
                }
                for(Pattern pattern : plugin.blacklistedInventoryHolderClassNames) {
                    Matcher matcher = pattern.matcher(className);
                    if(matcher.matches()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @EventHandler
    public void onLeftClickChest(PlayerInteractEvent event) {
        if (event instanceof ChestSortLeftClickHotkeyEvent) {
            return;
        }
        // checking in lower case for lazy admins
        if (plugin.getDisabledWorlds().contains(event.getPlayer().getWorld().getName().toLowerCase())) {
            return;
        }
        if (!event.getPlayer().hasPermission("chestsort.use")) {
            return;
        }
        if (!event.getPlayer().hasPermission(Hotkey.getPermission(Hotkey.OUTSIDE))) {
            return;
        }
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }
        if (!plugin.getConfig().getBoolean("allow-left-click-to-sort")) {
            return;
        }
        Block clickedBlock = event.getClickedBlock();
        if (!(clickedBlock.getState() instanceof Container)) {
            return;
        }
        if (!belongsToChestLikeBlock(((Container) clickedBlock.getState()).getInventory())) {
            return;
        }
        if (CrateReloadedHook.isCrate(clickedBlock)) {
            return;
        }

        plugin.registerPlayerIfNeeded(event.getPlayer());
        PlayerSetting playerSetting = plugin.getPlayerSetting(event.getPlayer());
        if (!playerSetting.leftClickOutside) {
            return;
        }

        if (plugin.getConfig().getBoolean("mute-protection-plugins")) {
            if (!ProtectionUtils.canBreak(event.getPlayer(),
                    clickedBlock.getLocation()/*, plugin.getConfig().getBoolean("mute-protection-plugins")*/)) {
                //System.out.println("ChestSort: cannot interact!");
                return;
            }
        }
        else {
            ChestSortLeftClickHotkeyEvent testEvent = new ChestSortLeftClickHotkeyEvent(event.getPlayer(),
                    Action.RIGHT_CLICK_BLOCK,
                    event.getPlayer().getInventory().getItemInMainHand(),
                    clickedBlock,
                    BlockFace.UP,
                    EquipmentSlot.HAND);
            Bukkit.getPluginManager().callEvent(testEvent);
            if (testEvent.isCancelled() || testEvent.useInteractedBlock() == Event.Result.DENY) {
                return;
            }
        }

        Container containerState = (Container) clickedBlock.getState();
        Inventory inventory = containerState.getInventory();

        try {
            if (!advancedChestsHook.handleAChestSortingIfPresent(clickedBlock.getLocation())) {
                plugin.getOrganizer().sortInventory(inventory);
            }
        } catch (Throwable ignored) {
            // TODO: Remove when everyone updated AdvancedChests
        }
        event.getPlayer()
                .spigot()
                .sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Messages.MSG_CONTAINER_SORTED));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        plugin.getPermissionsHandler().addPermissions(event.getPlayer());

        // Put player into our perPlayerSettings map
        plugin.registerPlayerIfNeeded(event.getPlayer());

        plugin.getLgr().logPlayerJoin(event.getPlayer());

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getPermissionsHandler().removePermissions(event.getPlayer());
        plugin.unregisterPlayer(event.getPlayer());
    }

    @EventHandler
    public void onBackPackClose(InventoryCloseEvent event) {
        if (plugin.getConfig().getString("sort-time").equalsIgnoreCase("close") ||
                plugin.getConfig().getString("sort-time").equalsIgnoreCase("both")) {
            onBackPackUse(event.getInventory(), (Player) event.getPlayer());
        }
    }

    @EventHandler
    public void onBackPackOpen(InventoryOpenEvent event) {
        if (plugin.getConfig().getString("sort-time").equalsIgnoreCase("open") ||
                plugin.getConfig().getString("sort-time").equalsIgnoreCase("both")) {
            onBackPackUse(event.getInventory(), (Player) event.getPlayer());
        }
    }

    void onBackPackUse(Inventory inv, Player p) {
        if (!plugin.getConfig().getBoolean("allow-automatic-sorting")) {
            return; //TODO: Maybe change to allow-automatic-inventory-sorting ?
        }
        if (!minepacksHook.isMinepacksBackpack(inv)) {
            return;
        }
        if (!p.hasPermission("chestsort.use")) {
            return;
        }
        if (!p.hasPermission("chestsort.automatic")) {
            return;
        }
        plugin.registerPlayerIfNeeded(p);
        PlayerSetting setting = plugin.getPerPlayerSettings().get(p.getUniqueId().toString());
        if (!setting.sortingEnabled) {
            return;
        }
        plugin.getOrganizer().sortInventory(inv);
    }

    @EventHandler
    public void onPlayerInventoryClose(InventoryCloseEvent event) {

        plugin.debug("Attempt to automatically sort a player inventory");

        if(isPossiblyBlacklisted(event.getView())) {
            plugin.debug("Abort: holder is blacklisted");
        }

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
            plugin.debug("Abort: type != CRAFTING, but " + event.getInventory().getType().name());
            return; // Weird! Returns CRAFTING instead of PLAYER
        }

        if (!(event.getInventory().getHolder() instanceof Player)) {
            plugin.debug("Abort: holder ! instanceof Player");
            return;
        }

        if (!plugin.getConfig().getBoolean("allow-automatic-inventory-sorting")) {
            plugin.debug("allow-automatic-inventory-sorting is false");
            return;
        }

        Player p = (Player) event.getInventory().getHolder();

        if (!p.hasPermission("chestsort.use.inventory") || !p.hasPermission("chestsort.automatic")) {
            plugin.debug("Missing permission chestsort.use.inventory or chestsort.automatic");
            return;
        }
        plugin.registerPlayerIfNeeded(p);

        PlayerSetting setting = plugin.getPerPlayerSettings().get(p.getUniqueId().toString());
        if (!setting.invSortingEnabled) {
            plugin.debug("auto inv sorting not enabled for player " + p.getName());
            return;
        }

        plugin.getLgr().logSort(p, Logger.SortCause.INV_CLOSE);

        plugin.getOrganizer().sortInventory(p.getInventory(), 9, 35);

    }

    // This event fires when someone closes an inventory
    // We check if the closed inventory belongs to a chest, advancedchest, shulkerbox or barrel,
    // and then call the Organizer to sort the inventory (if the player has
    // the chestsort.use permission and has /chestsort enabled)
    @EventHandler
    public void onChestClose(InventoryCloseEvent event) {

        if (!plugin.getConfig().getBoolean("allow-automatic-sorting")) {
            return;
        }

        if(isPossiblyBlacklisted(event.getView())) {
            plugin.debug("Abort: holder is blacklisted");
        }

        if (!(plugin.getConfig().getString("sort-time").equalsIgnoreCase("close") ||
                plugin.getConfig().getString("sort-time").equalsIgnoreCase("both"))) {
            return;
        }

        // event.getPlayer returns HumanEntity, so it could also be an NPC or something
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        Player p = (Player) event.getPlayer();
        Inventory inventory = event.getInventory();

        if (!p.hasPermission("chestsort.automatic")) {
            return;
        }

        if (!isAPICall(inventory) && !belongsToChestLikeBlock(inventory) &&
                !plugin.getEnderContainersHook().isEnderchest(inventory) && !LlamaUtils.belongsToLlama(inventory) &&
                !advancedChestsHook.isAnAdvancedChest(inventory) &&
                !plugin.getOrganizer().isMarkedAsSortable(inventory)) {
            return;
        }

        if (!isReadyToSort(p)) {
            return;
        }

        // Finally call the Organizer to sort the inventory

        plugin.getLgr().logSort(p, Logger.SortCause.CONT_CLOSE);

        // Llama inventories need special start/end slots
        if (LlamaUtils.belongsToLlama(event.getInventory())) {
            ChestedHorse llama = (ChestedHorse) event.getInventory().getHolder();
            plugin.getOrganizer().sortInventory(event.getInventory(), 2, LlamaUtils.getLlamaChestSize(llama) + 1);
            return;
        }

        // If the involved inventory belongs to an AdvancedChest, sort all the pages.
        if (advancedChestsHook.handleAChestSortingIfPresent(event.getInventory())) {
            return;
        }

        // Normal container inventories can be sorted completely
        plugin.getOrganizer().sortInventory(event.getInventory());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChestOpen(InventoryOpenEvent event) {

        plugin.debug("onChestOpen (InventoryOpenEvent");

        if(isPossiblyBlacklisted(event.getView())) {
            plugin.debug("Abort: holder is blacklisted");
        }

        if (!plugin.getConfig().getBoolean("allow-automatic-sorting")) {
            return;
        }

        if (!(plugin.getConfig().getString("sort-time").equalsIgnoreCase("open") ||
                plugin.getConfig().getString("sort-time").equalsIgnoreCase("both"))) {
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

        if (!p.hasPermission("chestsort.automatic")) {
            return;
        }

        if (!isAPICall(inventory) && !belongsToChestLikeBlock(inventory) &&
                !plugin.getEnderContainersHook().isEnderchest(inventory) && !LlamaUtils.belongsToLlama(inventory) &&
                !advancedChestsHook.isAnAdvancedChest(inventory) &&
                !plugin.getOrganizer().isMarkedAsSortable(inventory)) {
            return;
        }

        if (!isReadyToSort(p)) {
            return;
        }

        // Finally call the Organizer to sort the inventory

        plugin.getLgr().logSort(p, Logger.SortCause.CONT_OPEN);

        // Llama inventories need special start/end slots
        if (LlamaUtils.belongsToLlama(event.getInventory())) {
            ChestedHorse llama = (ChestedHorse) event.getInventory().getHolder();
            plugin.getOrganizer().sortInventory(event.getInventory(), 2, LlamaUtils.getLlamaChestSize(llama) + 1);
            return;
        }

        // If the involved inventory belongs to an AdvancedChest, sort all the pages.
        if (advancedChestsHook.handleAChestSortingIfPresent(event.getInventory())) {
            return;
        }

        // Normal container inventories can be sorted completely
        plugin.getOrganizer().sortInventory(event.getInventory());

    }

    private boolean belongsToChestLikeBlock(Inventory inventory) {

        // Check by InventoryType
        if (inventory.getType() == InventoryType.ENDER_CHEST ||
                inventory.getType().name().equalsIgnoreCase("SHULKER_BOX")) {
            return true;
        }

        if (inventory.getHolder() != null &&
                inventory.getHolder().getClass().getName().toLowerCase().contains("boat")) {
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
        return inventory.getHolder() instanceof Chest || inventory.getHolder() instanceof DoubleChest ||
                inventory.getHolder().getClass().toString().endsWith(".CraftMinecartChest") ||
                inventory.getHolder().getClass().toString().endsWith(".CraftShulkerBox")
                //Obsolete, is checked above by InventoryType
                || inventory.getHolder().getClass().toString().endsWith(".CraftBarrel");
    }

    private boolean isReadyToSort(Player p) {
        if (!p.hasPermission("chestsort.use")) {
            return false;
        }

        // checking in lower case for lazy admins
        if (plugin.getDisabledWorlds().contains(p.getWorld().getName().toLowerCase())) {
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
        PlayerSetting setting = plugin.getPerPlayerSettings().get(p.getUniqueId().toString());

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
                    p.sendMessage(Messages.MSG_COMMANDMESSAGE);
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
                    p.sendMessage(Messages.MSG_COMMANDMESSAGE2);
                }
            }
        }
        return true;
    }

    @EventHandler
    public void onEnderChestOpen(InventoryOpenEvent event) {

        if (!plugin.getConfig().getBoolean("allow-automatic-sorting")) {
            return;
        }

        if(isPossiblyBlacklisted(event.getView())) {
            plugin.debug("Abort: holder is blacklisted");
        }

        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        Player p = (Player) event.getPlayer();

        if (!p.hasPermission("chestsort.automatic")) {
            return;
        }

        // Check if this is an EnderChest (is there a smarter way?)
        if (!event.getInventory().equals(p.getEnderChest())) {
            return;
        }

        if (isReadyToSort(p)) {

            // Finally call the Organizer to sort the inventory

            plugin.getLgr().logSort(p, Logger.SortCause.EC_OPEN);

            plugin.getOrganizer().sortInventory(event.getInventory());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onHotkey(InventoryClickEvent event) {

        plugin.debug2("Hotkey?");

        if(isPossiblyBlacklisted(event.getView())) {
            plugin.debug("Abort: holder is blacklisted");
        }

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
        if (!isAPICall && (plugin.getGenericHook().isPluginGUI(event.getInventory()) ||
                plugin.getGenericHook().isPluginGUI(event.getInventory()))) {
            plugin.debug("Aborting hotkey sorting: no API call & generic GUI detected");
            return;
        }

        // Possible fix for #57
        if (!isAPICall &&
                (event.getClickedInventory().getHolder() != null && event.getClickedInventory().getHolder() == p &&
                        event.getClickedInventory() != p.getInventory())) {
            return;
        }

        // End Possible fix for #57
        InventoryHolder holder = event.getClickedInventory().getHolder();

        boolean sort = false;
        Logger.SortCause cause = null;

        PlayerSetting setting = plugin.getPerPlayerSettings().get(p.getUniqueId().toString());

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
                cause = Logger.SortCause.H_MIDDLE;
                //if(plugin.getConfig().getBoolean("hotkeys.middle-click")) {
                if (setting.middleClick && p.hasPermission(Hotkey.getPermission(Hotkey.MIDDLE_CLICK))) {
                    if (event.getWhoClicked().getGameMode() != GameMode.CREATIVE ||
                            (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR)) {
                        sort = true;
                    }
                }
                break;
            case DOUBLE_CLICK:
                if (event.isShiftClick()) {
                    return;
                }
                cause = Logger.SortCause.H_DOUBLE;
                //if(plugin.getConfig().getBoolean("hotkeys.double-click")) {
                if (setting.doubleClick && p.hasPermission(Hotkey.getPermission(Hotkey.DOUBLE_CLICK))) {
                    // We need getCursor() instead of getCurrentItem(), because after picking up the item, it is gone into the cursor
                    if (event.getCursor() == null ||
                            (event.getCursor() != null && event.getCursor().getType() == Material.AIR)) {
                        sort = true;
                    }
                }
                break;
            case SHIFT_LEFT:
                cause = Logger.SortCause.H_SHIFT;
                //if(plugin.getConfig().getBoolean("hotkeys.shift-click")) {
                if (setting.shiftClick && p.hasPermission(Hotkey.getPermission(Hotkey.SHIFT_CLICK))) {
                    if (event.getCurrentItem() == null ||
                            (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.AIR)) {
                        sort = true;
                    }
                }
                break;
            case SHIFT_RIGHT:
                cause = Logger.SortCause.H_SHIFTRIGHT;
                //if(plugin.getConfig().getBoolean("hotkeys.shift-right-click")) {
                if (setting.shiftRightClick && p.hasPermission(Hotkey.getPermission(Hotkey.SHIFT_RIGHT_CLICK))) {
                    if (event.getCurrentItem() == null ||
                            (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.AIR)) {
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
        event.setCancelled(true);

        if (plugin.isInHotkeyCooldown(p.getUniqueId())) {
            plugin.debug("Skipping: hotkey cooldown");
            return;
        }

        plugin.debug("Hotkey triggered: " + event.getClick().name());

        if (isAPICall || belongsToChestLikeBlock(event.getClickedInventory()) || (event.getClickedInventory() != null &&
                event.getClickedInventory().getType() == InventoryType.HOPPER) ||
                plugin.getOrganizer().isMarkedAsSortable(event.getClickedInventory()) ||
                LlamaUtils.belongsToLlama(event.getClickedInventory()) ||
                minepacksHook.isMinepacksBackpack(event.getClickedInventory()) ||
                plugin.getPlayerVaultsHook().isPlayerVault(event.getClickedInventory()) ||
                plugin.getEnderContainersHook().isEnderchest(event.getClickedInventory()) ||
                advancedChestsHook.isAnAdvancedChest(event.getClickedInventory())) {

            if (!p.hasPermission("chestsort.use")) {
                return;
            }

            plugin.getLgr().logSort(p, cause);

            if (LlamaUtils.belongsToLlama(event.getClickedInventory())) {
                ChestedHorse llama = (ChestedHorse) event.getInventory().getHolder();
                plugin.getOrganizer()
                        .sortInventory(event.getClickedInventory(), 2, LlamaUtils.getLlamaChestSize(llama) + 1);
                plugin.getOrganizer().updateInventoryView(event);
                return;
            }

            if (advancedChestsHook.handleAChestSortingIfPresent(event.getInventory())) {
                plugin.getOrganizer().updateInventoryView(event);
                return;
            }

            plugin.getOrganizer().sortInventory(event.getClickedInventory());
            plugin.getOrganizer().updateInventoryView(event);
        }
        else if (holder instanceof Player) {

            if (!p.hasPermission("chestsort.use.inventory")) {
                return;
            }

            if (event.getSlotType() == SlotType.QUICKBAR) {
                plugin.getLgr().logSort(p, cause);
                plugin.getOrganizer().sortInventory(p.getInventory(), 0, 8);
                plugin.getOrganizer().updateInventoryView(event);

            }
            else if (event.getSlotType() == SlotType.CONTAINER) {
                plugin.getLgr().logSort(p, cause);
                plugin.getOrganizer().sortInventory(p.getInventory(), 9, 35);
                plugin.getOrganizer().updateInventoryView(event);

            }
        }
    }

    private boolean isAPICall(Inventory inv) {
        if (inv == null) {
            return false;
        }
        return inv.getHolder() instanceof ISortable || plugin.getOrganizer().isMarkedAsSortable(inv);
    }

    @EventHandler
    public void onAdditionalHotkeys(InventoryClickEvent e) {

        if(isPossiblyBlacklisted(e.getView())) {
            plugin.debug("Abort: holder is blacklisted");
        }

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

        boolean isAdvancedChest = advancedChestsHook.isAnAdvancedChest(e.getClickedInventory()) ||
                advancedChestsHook.isAnAdvancedChest(e.getInventory());

        // Possible fix for #57
        if (e.getInventory().getHolder() == null && !e.getView().getTopInventory().equals(p.getEnderChest()) &&
                !isAdvancedChest) {
            return;
        }
        if (e.getInventory().getHolder() == p && e.getInventory() != p.getInventory()) {
            return;
        }
        // End Possible fix for #57
        if (e.getInventory().getType() != InventoryType.CHEST &&
                e.getInventory().getType() != InventoryType.DISPENSER &&
                e.getInventory().getType() != InventoryType.DROPPER &&
                e.getInventory().getType() != InventoryType.ENDER_CHEST &&
                e.getInventory().getType() != InventoryType.HOPPER &&
                !e.getInventory().getType().name().equalsIgnoreCase("SHULKER_BOX") &&
                (e.getInventory().getHolder() == null ||
                        !e.getInventory().getHolder().getClass().toString().endsWith(".CraftBarrel")) &&
                e.getInventory() != p.getEnderChest() && !(e.getInventory().getHolder() instanceof ISortable)) {
            return;
        }

        // HeadDatabase hook
        if (headDatabaseHook.isHeadDB(e.getClickedInventory()) || headDatabaseHook.isHeadDB(e.getInventory())) {
            return;
        }

        // CrateReloaded hook
        if (CrateReloadedHook.isCrate(e.getClickedInventory()) || CrateReloadedHook.isCrate(e.getInventory())) {
            //if(plugin.debug) plugin.getLogger().info("Aborting hotkey because this is a CrateReloaded crate");
            return;
        }

        // GoldenCrates hook
        if (goldenCratesHook.isCrate(e.getClickedInventory()) || goldenCratesHook.isCrate(e.getInventory())) {
            //if(plugin.debug) plugin.getLogger().info("Aborting hotkey because this is a CrateReloaded crate");
            return;
        }

        // Detect generic GUIs
        if (!isAPICall(e.getInventory()) && !isAPICall(e.getClickedInventory()) &&
                (plugin.getGenericHook().isPluginGUI(e.getInventory()) ||
                        plugin.getGenericHook().isPluginGUI(e.getInventory()))) {
            return;
        }

        // Don't sort inventories belonging to BossShopPro
        if (e.getInventory() != null && e.getInventory().getHolder() != null && e.getInventory()
                .getHolder()
                .getClass()
                .getName()
                .equalsIgnoreCase("org.black_ixx.bossshop.core.BSShopHolder")) {
            return;
        }

        // BetterBackpacks
        if (e.getInventory() != null && e.getInventory().getHolder() != null && e.getInventory()
                .getHolder()
                .getClass()
                .getName()
                .equals("com.alonsoaliaga.betterbackpacks.others.BackpackHolder")) {
            return;
        }

        // ShulkerPacks
        if (ShulkerPacksHook.isOpenShulkerView(e.getView())) {
            return;
        }

        if (!p.hasPermission("chestsort.use")) {
            return;
        }

        plugin.registerPlayerIfNeeded(p);
        PlayerSetting setting = plugin.getPerPlayerSettings().get(p.getUniqueId().toString());

        ChestSortEvent chestSortEvent = new ChestSortEvent(e.getInventory());
        chestSortEvent.setPlayer(e.getWhoClicked());
        chestSortEvent.setLocation(e.getWhoClicked().getLocation());

        chestSortEvent.setSortableMaps(new HashMap<>());
        ItemStack[] contents = e.getInventory().getContents();
        int contentsLength = (!isAdvancedChest) ? contents.length : contents.length - 9;
        for (int i = 0; i < contentsLength; i++) {
            ItemStack item = contents[i];
            chestSortEvent.getSortableMaps().put(item, plugin.getOrganizer().getSortableMap(item));
        }

        Bukkit.getPluginManager().callEvent(chestSortEvent);
        if (chestSortEvent.isCancelled()) {
            return;
        }

        if (e.isLeftClick() && setting.leftClick && p.hasPermission(Hotkey.getPermission(Hotkey.LEFT_CLICK))) {
            plugin.getLgr().logSort(p, Logger.SortCause.H_LEFT);
            if (setting.getCurrentDoubleClick(plugin, PlayerSetting.DoubleClickType.LEFT_CLICK) ==
                    PlayerSetting.DoubleClickType.LEFT_CLICK) {
                // Left double click: put everything into destination
                plugin.getOrganizer()
                        .stuffPlayerInventoryIntoAnother(p.getInventory(), e.getInventory(), false, chestSortEvent);
                if (isAdvancedChest) {
                    plugin.getOrganizer().sortInventory(e.getInventory(), 0, e.getInventory().getSize() - 10);
                }
                else {
                    plugin.getOrganizer().sortInventory(e.getInventory());
                }
            }
            else {
                // Left single click: put only matching items into destination
                plugin.getOrganizer()
                        .stuffPlayerInventoryIntoAnother(p.getInventory(), e.getInventory(), true, chestSortEvent);
            }

        }
        else if (e.isRightClick() && setting.rightClick && p.hasPermission(Hotkey.getPermission(Hotkey.RIGHT_CLICK))) {
            plugin.getLgr().logSort(p, Logger.SortCause.H_RIGHT);
            if (setting.getCurrentDoubleClick(plugin, PlayerSetting.DoubleClickType.RIGHT_CLICK) ==
                    PlayerSetting.DoubleClickType.RIGHT_CLICK) {
                // Right double click: put everything into player inventory
                plugin.getOrganizer()
                        .stuffInventoryIntoAnother(e.getInventory(), p.getInventory(), e.getInventory(), false);
                plugin.getOrganizer().sortInventory(p.getInventory(), 9, 35);
            }
            else {
                // Right single click: put only matching items into player inventory
                plugin.getOrganizer()
                        .stuffInventoryIntoAnother(e.getInventory(), p.getInventory(), e.getInventory(), true);
            }

        }
        //plugin.organizer.sortInventory(e.getInventory());
        plugin.getOrganizer().updateInventoryView(e.getInventory());
        plugin.getOrganizer().updateInventoryView(p.getInventory());

        Bukkit.getPluginManager().callEvent(new ChestSortPostSortEvent(chestSortEvent));
    }

}
