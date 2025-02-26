package com.deos.travel_anchors.tags;

import com.deos.travel_anchors.TravelAnchors;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModTags {
    public static class Items{

        public static final TagKey<Item> TELEPORTATION_RANGE_APPLICABLE = createTag("teleportation_range_applicable");

        private static TagKey<Item> createTag(String name){
            return ItemTags.create(TravelAnchors.getInstance().resource(name));
        }
    }
}
