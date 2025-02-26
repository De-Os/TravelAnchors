package com.deos.travel_anchors.gui;

import com.deos.travel_anchors.block.custom.TravelAnchorTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.moddingx.libx.menu.BlockEntityMenu;

public class TravelAnchorMenu extends BlockEntityMenu<TravelAnchorTile>  {

    public TravelAnchorMenu(@Nullable MenuType<? extends BlockEntityMenu<?>> type, int windowId, Level level, BlockPos pos, Player player, Inventory inventory, int firstOutputSlot , int firstInventorySlot) {
        super(type, windowId, level, pos, player, inventory, firstOutputSlot, firstInventorySlot);
        this.layoutPlayerInventorySlots(8, 51);
    }
}