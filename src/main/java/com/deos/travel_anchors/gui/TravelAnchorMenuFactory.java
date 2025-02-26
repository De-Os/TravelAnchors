package com.deos.travel_anchors.gui;

import com.deos.travel_anchors.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import org.moddingx.libx.menu.type.AdvancedMenuFactory;

public class TravelAnchorMenuFactory implements AdvancedMenuFactory<TravelAnchorMenu, BlockPos> {

    @Override
    public TravelAnchorMenu createMenu(MenuType<TravelAnchorMenu> menuType, int windowId, Level level, BlockPos payload, Player player, Inventory inventory) {
        return new TravelAnchorMenu(menuType, windowId, level, payload, player, inventory, 0, 0);
    }

    public static void registerScreen(RegisterMenuScreensEvent event)
    {
        event.register(ModBlocks.travelAnchor.menu, TravelAnchorScreen::new);
    }
}