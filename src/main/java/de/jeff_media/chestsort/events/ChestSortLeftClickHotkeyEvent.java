package de.jeff_media.chestsort.events;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChestSortLeftClickHotkeyEvent extends PlayerInteractEvent {
    public ChestSortLeftClickHotkeyEvent(@NotNull Player who, @NotNull Action action, @Nullable ItemStack item, @Nullable Block clickedBlock, @NotNull BlockFace clickedFace) {
        super(who, action, item, clickedBlock, clickedFace);
    }

    public ChestSortLeftClickHotkeyEvent(@NotNull Player who, @NotNull Action action, @Nullable ItemStack item, @Nullable Block clickedBlock, @NotNull BlockFace clickedFace, @Nullable EquipmentSlot hand) {
        super(who, action, item, clickedBlock, clickedFace, hand);
    }
}
