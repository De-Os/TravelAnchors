package com.deos.travel_anchors.block;

import com.deos.travel_anchors.block.custom.TravelAnchorBlock;
import com.deos.travel_anchors.gui.TravelAnchorMenu;
import com.deos.travel_anchors.block.custom.TravelAnchorTile;
import org.moddingx.libx.annotation.registration.RegisterClass;
import org.moddingx.libx.base.tile.MenuBlockBE;

@RegisterClass(registry = "BLOCK", priority = 1)
public class ModBlocks {

    public static final MenuBlockBE<TravelAnchorTile, TravelAnchorMenu> travelAnchor = new TravelAnchorBlock();
}
