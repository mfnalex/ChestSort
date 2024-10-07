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
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.HumanEntity;
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

    private boolean isPossiblyBlacklisted(InventoryView view, InventoryHolder topHolder) {
        return isPossiblyBlacklisted(topHolder, view.getBottomInventory().getHolder());
    }

    private boolean isPossiblyBlacklisted(InventoryHolder topHolder, InventoryHolder bottomHolder) {
        Set<String> toCheck = new HashSet<>();

        if (topHolder != null) {
            String className = topHolder.getClass().getName();
            toCheck.add(className);

        }

        if (bottomHolder != null) {
            String className = bottomHolder.getClass().getName();
            toCheck.add(className);
        }

        for (String className : toCheck) {
            plugin.debug("Checking blacklisted holder: " + className);
            for (String blacklistedClassName : blacklistedInventoryHolderClassNames) {
                plugin.debug("Checking against hardcoded blacklisted holder: " + blacklistedClassName);
                if (className.contains(blacklistedClassName)) {
                    plugin.debug("Blacklisted holder found: " + className);
                    return true;
                }
            }

            for(Pattern pattern : plugin.blacklistedInventoryHolderClassNames) {
                plugin.debug("Checking against regex blacklisted holder: " + pattern.pattern());
                Matcher matcher = pattern.matcher(className);
                if(matcher.matches()) {
                    plugin.debug("Blacklisted holder found: " + className);
                    return true;
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
        BlockState state = clickedBlock.getState();

        if (!(state instanceof Container)) {
            return;
        }
        if (!belongsToChestLikeBlock(((Container) state).getInventory())) {
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

        Container containerState = (Container) state;
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
        if (!minepacksHook.isMinepacksBackpack(inv, inv.getHolder())) {
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
        Inventory inventory = event.getInventory();
        InventoryHolder holder = inventory.getHolder();

        plugin.debug("Attempt to automatically sort a player inventory");

        if(isPossiblyBlacklisted(event.getView(), holder)) {
            plugin.debug("Abort: holder is blacklisted");
            return;
        }

        if (holder == null) {
            plugin.debug("Abort: holder == null");
            return;
        }
        // Might be obsolete, because its @NotNull in 1.15, but who knows if thats for 1.8
        if (inventory.getType() == null) {
            plugin.debug("Abort: type == null");
            return;
        }
        if (inventory.getType() != InventoryType.CRAFTING) {
            plugin.debug("Abort: type != CRAFTING, but " + inventory.getType().name());
            return; // Weird! Returns CRAFTING instead of PLAYER
        }

        if (!(holder instanceof Player)) {
            plugin.debug("Abort: holder ! instanceof Player");
            return;
        }

        if (!plugin.getConfig().getBoolean("allow-automatic-inventory-sorting")) {
            plugin.debug("allow-automatic-inventory-sorting is false");
            return;
        }

        Player p = (Player) holder;

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
        InventoryView view = event.getView();
        Inventory inventory = event.getInventory();

        if (!plugin.getConfig().getBoolean("allow-automatic-sorting")) {
            return;
        }

        InventoryHolder holder = inventory.getHolder();

        if(isPossiblyBlacklisted(view, holder)) {
            plugin.debug("Abort: holder is blacklisted");
            return;
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

        if (!p.hasPermission("chestsort.automatic")) {
            return;
        }

        if (!isAPICall(inventory, holder) && !belongsToChestLikeBlock(inventory, holder) &&
                !plugin.getEnderContainersHook().isEnderchest(inventory, holder) &&
                !LlamaUtils.belongsToLlama(inventory, holder) &&
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
        if (LlamaUtils.belongsToLlama(inventory, holder)) {
            ChestedHorse llama = (ChestedHorse) holder;
            plugin.getOrganizer().sortInventory(inventory, 2, LlamaUtils.getLlamaChestSize(llama) + 1);
            return;
        }

        // If the involved inventory belongs to an AdvancedChest, sort all the pages.
        if (advancedChestsHook.handleAChestSortingIfPresent(inventory)) {
            return;
        }

        // Normal container inventories can be sorted completely
        plugin.getOrganizer().sortInventory(inventory);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChestOpen(InventoryOpenEvent event) {
        InventoryView view = event.getView();
        Inventory inventory = event.getInventory();
        InventoryHolder holder = inventory.getHolder();

        plugin.debug("onChestOpen (InventoryOpenEvent");

        if(isPossiblyBlacklisted(view, holder)) {
            plugin.debug("Abort: holder is blacklisted");
            return;
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

        if (!p.hasPermission("chestsort.automatic")) {
            return;
        }

        if (!isAPICall(inventory, holder) && !belongsToChestLikeBlock(inventory, holder) &&
                !plugin.getEnderContainersHook().isEnderchest(inventory, holder) && !LlamaUtils.belongsToLlama(inventory, holder) &&
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
        if (LlamaUtils.belongsToLlama(inventory, holder)) {
            ChestedHorse llama = (ChestedHorse) holder;
            plugin.getOrganizer().sortInventory(inventory, 2, LlamaUtils.getLlamaChestSize(llama) + 1);
            return;
        }

        // If the involved inventory belongs to an AdvancedChest, sort all the pages.
        if (advancedChestsHook.handleAChestSortingIfPresent(inventory)) {
            return;
        }

        // Normal container inventories can be sorted completely
        plugin.getOrganizer().sortInventory(inventory);

    }

    private boolean belongsToChestLikeBlock(Inventory inventory) {
        return belongsToChestLikeBlock(inventory, inventory.getHolder());
    }

    private boolean belongsToChestLikeBlock(Inventory inventory, InventoryHolder holder) {
        // Check by InventoryType
        if (inventory.getType() == InventoryType.ENDER_CHEST ||
                inventory.getType().name().equalsIgnoreCase("SHULKER_BOX")) {
            return true;
        }

        if (holder != null &&
                holder.getClass().getName().toLowerCase().contains("boat")) {
            return true;
        }

        // Possible Fix for https://github.com/JEFF-Media-GbR/Spigot-ChestSort/issues/13
        if (holder == null) {
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
        String holderClassName = holder.getClass().toString();
        return holder instanceof Chest || holder instanceof DoubleChest ||
                holderClassName.endsWith(".CraftMinecartChest") ||
                holderClassName.endsWith(".CraftShulkerBox")
                //Obsolete, is checked above by InventoryType
                || holderClassName.endsWith(".CraftBarrel");
    }

    private boolean isReadyToSort(Player player) {
        if (!player.hasPermission("chestsort.use")) {
            return false;
        }

        // checking in lower case for lazy admins
        if (plugin.getDisabledWorlds().contains(player.getWorld().getName().toLowerCase())) {
            return false;
        }

        // Don't sort automatically when player is spectator or in adventure mode
        // TODO: Make this configurable in config.yml
        if (player.getGameMode() == GameMode.SPECTATOR || player.getGameMode() == GameMode.ADVENTURE) {
            return false;
        }

        // Fixes exception when using Spigot's stupid /reload command
        plugin.registerPlayerIfNeeded(player);

        // Get the current player's settings
        // We do not immediately cancel when sorting is disabled because we might want
        // to show the hint message
        PlayerSetting setting = plugin.getPerPlayerSettings().get(player.getUniqueId().toString());

        // Show "how to enable ChestSort" message when ALL of the following criteria are
        // met:
        // - Player has sorting disabled
        // - Player has not seen the message yet (whether or not this resets after a
        // logout
        // is defined by the config setting "show-message-again-after-logout")
        // - "show-message-when-using-chest" is set to true in the config.yml
        if (!plugin.isSortingEnabled(player)) {
            if (!setting.hasSeenMessage) {
                setting.hasSeenMessage = true;
                if (plugin.getConfig().getBoolean("show-message-when-using-chest")) {
                    player.sendMessage(Messages.MSG_COMMANDMESSAGE);
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
                    player.sendMessage(Messages.MSG_COMMANDMESSAGE2);
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

        InventoryView view = event.getView();
        Inventory inventory = event.getInventory();
        InventoryHolder holder = inventory.getHolder();

        if(isPossiblyBlacklisted(view, holder)) {
            plugin.debug("Abort: holder is blacklisted");
            return;
        }

        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        Player p = (Player) event.getPlayer();

        if (!p.hasPermission("chestsort.automatic")) {
            return;
        }

        // Check if this is an EnderChest (is there a smarter way?)
        if (!inventory.equals(p.getEnderChest())) {
            return;
        }

        if (isReadyToSort(p)) {

            // Finally call the Organizer to sort the inventory

            plugin.getLgr().logSort(p, Logger.SortCause.EC_OPEN);

            plugin.getOrganizer().sortInventory(inventory);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onHotkey(InventoryClickEvent event) {

        plugin.debug2("Hotkey?");
        InventoryView view = event.getView();
        Inventory inventory = event.getInventory();
        InventoryHolder topHolder = inventory.getHolder();
        Inventory clicked = event.getClickedInventory();

        if(isPossiblyBlacklisted(view, topHolder)) {
            plugin.debug("Abort: holder is blacklisted");
            return;
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
        if (clicked == null) {
            plugin.debug2("exit: 3");
            return;
        }

        InventoryHolder holder = clicked.getHolder();
        boolean isAPICall = isAPICall(clicked, holder);

        // Detect generic GUIs
        if (!isAPICall && (plugin.getGenericHook().isPluginGUI(inventory, holder) ||
                plugin.getGenericHook().isPluginGUI(inventory, holder))) {
            plugin.debug("Aborting hotkey sorting: no API call & generic GUI detected");
            return;
        }


        // Possible fix for #57
        if (!isAPICall &&
                (holder != null && holder == p &&
                        clicked != p.getInventory())) {
            return;
        }

        // End Possible fix for #57

        boolean sort = false;
        Logger.SortCause cause = null;

        PlayerSetting setting = plugin.getPerPlayerSettings().get(p.getUniqueId().toString());

        // Do not sort the GUI inventory
        if (clicked == setting.guiInventory) {
            return;
        }

        // Prevent player from putting items into GUI inventory
        if (inventory == setting.guiInventory) {
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

        if (isAPICall || belongsToChestLikeBlock(clicked, holder) || (clicked != null &&
                clicked.getType() == InventoryType.HOPPER) ||
                plugin.getOrganizer().isMarkedAsSortable(clicked) ||
                LlamaUtils.belongsToLlama(clicked, holder) ||
                minepacksHook.isMinepacksBackpack(clicked, holder) ||
                plugin.getPlayerVaultsHook().isPlayerVault(clicked, holder) ||
                plugin.getEnderContainersHook().isEnderchest(clicked, holder) ||
                advancedChestsHook.isAnAdvancedChest(clicked)) {

            if (!p.hasPermission("chestsort.use")) {
                return;
            }

            plugin.getLgr().logSort(p, cause);

            if (LlamaUtils.belongsToLlama(clicked, topHolder)) {
                ChestedHorse llama = (ChestedHorse) topHolder;
                plugin.getOrganizer()
                        .sortInventory(clicked, 2, LlamaUtils.getLlamaChestSize(llama) + 1);
                plugin.getOrganizer().updateInventoryView(event);
                return;
            }

            if (advancedChestsHook.handleAChestSortingIfPresent(inventory)) {
                plugin.getOrganizer().updateInventoryView(event);
                return;
            }

            plugin.getOrganizer().sortInventory(clicked);
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

    private boolean isAPICall(Inventory inv, InventoryHolder holder) {
        if (inv == null) {
            return false;
        }
        return holder instanceof ISortable || plugin.getOrganizer().isMarkedAsSortable(inv);
    }

    @EventHandler
    public void onAdditionalHotkeys(InventoryClickEvent event) {
        InventoryView view = event.getView();
        HumanEntity whoClicked = event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();
        Inventory inventory = event.getInventory();
        InventoryHolder holder = inventory.getHolder();

        if(isPossiblyBlacklisted(view, holder)) {
            plugin.debug("Abort: holder is blacklisted");
            return;
        }




        if (!plugin.getConfig().getBoolean("allow-additional-hotkeys")) {
            return;
        }
        if (!(whoClicked instanceof Player)) {
            return;
        }
        Player player = (Player) whoClicked;
        // Only continue if clicked outside of the chest
        if (clickedInventory != null) {
            return;
        }

        // Only continue if hand is empty
        if (event.getCursor() != null && event.getCursor().getType() != null && event.getCursor().getType() != Material.AIR) {
            return;
        }

        if (LlamaUtils.belongsToLlama(inventory, holder) /*|| LlamaUtils.belongsToLlama(clickedInventory, clickedInventory.getHolder())*/) {
            return;
        }

        boolean isAdvancedChest = advancedChestsHook.isAnAdvancedChest(clickedInventory) ||
                advancedChestsHook.isAnAdvancedChest(inventory);

        // Possible fix for #57
        if (holder == null && !view.getTopInventory().equals(player.getEnderChest()) &&
                !isAdvancedChest) {
            return;
        }
        if (holder == player && inventory != player.getInventory()) {
            return;
        }
        // End Possible fix for #57
        if (inventory.getType() != InventoryType.CHEST &&
                inventory.getType() != InventoryType.DISPENSER &&
                inventory.getType() != InventoryType.DROPPER &&
                inventory.getType() != InventoryType.ENDER_CHEST &&
                inventory.getType() != InventoryType.HOPPER &&
                !inventory.getType().name().equalsIgnoreCase("SHULKER_BOX") &&
                (holder == null ||
                        !holder.getClass().toString().endsWith(".CraftBarrel")) &&
                inventory != player.getEnderChest() && !(holder instanceof ISortable)) {
            return;
        }

        // HeadDatabase hook
        if (headDatabaseHook.isHeadDB(clickedInventory, holder) || headDatabaseHook.isHeadDB(inventory, holder)) {
            return;
        }

        // CrateReloaded hook
        if (CrateReloadedHook.isCrate(clickedInventory, holder) || CrateReloadedHook.isCrate(inventory, holder)) {
            //if(plugin.debug) plugin.getLogger().info("Aborting hotkey because this is a CrateReloaded crate");
            return;
        }

        // GoldenCrates hook
        if (goldenCratesHook.isCrate(clickedInventory, holder) || goldenCratesHook.isCrate(inventory, holder)) {
            //if(plugin.debug) plugin.getLogger().info("Aborting hotkey because this is a CrateReloaded crate");
            return;
        }

        // Detect generic GUIs
        if (!isAPICall(inventory, holder) && !isAPICall(clickedInventory, holder) &&
                (plugin.getGenericHook().isPluginGUI(inventory, holder) ||
                        plugin.getGenericHook().isPluginGUI(inventory, holder))) {
            return;
        }

        // Don't sort inventories belonging to BossShopPro
        if (inventory != null && holder != null && holder
                .getClass()
                .getName()
                .equalsIgnoreCase("org.black_ixx.bossshop.core.BSShopHolder")) {
            return;
        }

        // BetterBackpacks
        if (inventory != null && holder != null && holder
                .getClass()
                .getName()
                .equals("com.alonsoaliaga.betterbackpacks.others.BackpackHolder")) {
            return;
        }

        // ShulkerPacks
        if (ShulkerPacksHook.isOpenShulkerView(holder)) {
            return;
        }

        if (!player.hasPermission("chestsort.use")) {
            return;
        }

        plugin.registerPlayerIfNeeded(player);
        PlayerSetting setting = plugin.getPerPlayerSettings().get(player.getUniqueId().toString());

        ChestSortEvent chestSortEvent = new ChestSortEvent(inventory);
        chestSortEvent.setPlayer(whoClicked);
        chestSortEvent.setLocation(whoClicked.getLocation());

        chestSortEvent.setSortableMaps(new HashMap<>());
        ItemStack[] contents = inventory.getContents();
        int contentsLength = (!isAdvancedChest) ? contents.length : contents.length - 9;
        for (int i = 0; i < contentsLength; i++) {
            ItemStack item = contents[i];
            chestSortEvent.getSortableMaps().put(item, plugin.getOrganizer().getSortableMap(item));
        }

        Bukkit.getPluginManager().callEvent(chestSortEvent);
        if (chestSortEvent.isCancelled()) {
            return;
        }

        if (event.isLeftClick() && setting.leftClick && player.hasPermission(Hotkey.getPermission(Hotkey.LEFT_CLICK))) {
            plugin.getLgr().logSort(player, Logger.SortCause.H_LEFT);
            if (setting.getCurrentDoubleClick(plugin, PlayerSetting.DoubleClickType.LEFT_CLICK) ==
                    PlayerSetting.DoubleClickType.LEFT_CLICK) {
                // Left double click: put everything into destination
                plugin.getOrganizer()
                        .stuffPlayerInventoryIntoAnother(player.getInventory(), inventory, false, chestSortEvent);
                if (isAdvancedChest) {
                    plugin.getOrganizer().sortInventory(inventory, 0, inventory.getSize() - 10);
                }
                else {
                    plugin.getOrganizer().sortInventory(inventory);
                }
            }
            else {
                // Left single click: put only matching items into destination
                plugin.getOrganizer()
                        .stuffPlayerInventoryIntoAnother(player.getInventory(), inventory, true, chestSortEvent);
            }

        }
        else if (event.isRightClick() && setting.rightClick && player.hasPermission(Hotkey.getPermission(Hotkey.RIGHT_CLICK))) {
            plugin.getLgr().logSort(player, Logger.SortCause.H_RIGHT);
            if (setting.getCurrentDoubleClick(plugin, PlayerSetting.DoubleClickType.RIGHT_CLICK) ==
                    PlayerSetting.DoubleClickType.RIGHT_CLICK) {
                // Right double click: put everything into player inventory
                plugin.getOrganizer()
                        .stuffInventoryIntoAnother(inventory, player.getInventory(), inventory, false);
                plugin.getOrganizer().sortInventory(player.getInventory(), 9, 35);
            }
            else {
                // Right single click: put only matching items into player inventory
                plugin.getOrganizer()
                        .stuffInventoryIntoAnother(inventory, player.getInventory(), inventory, true);
            }

        }
        //plugin.organizer.sortInventory(event.getInventory());
        plugin.getOrganizer().updateInventoryView(inventory);
        plugin.getOrganizer().updateInventoryView(player.getInventory());

        Bukkit.getPluginManager().callEvent(new ChestSortPostSortEvent(chestSortEvent));
    }

}
