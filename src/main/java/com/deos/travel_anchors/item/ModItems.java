package com.deos.travel_anchors.item;

import com.deos.travel_anchors.TravelAnchors;
import com.deos.travel_anchors.item.custom.TravelStaffItem;
import net.minecraft.world.item.Item;
import org.moddingx.libx.annotation.registration.RegisterClass;
import org.moddingx.libx.base.ItemBase;

@RegisterClass(registry = "ITEM")
public class ModItems {
    public static final ItemBase travelStaff = new TravelStaffItem(
            TravelAnchors.getInstance(),
            new Item.Properties()
                    .stacksTo(1)
    );
}
