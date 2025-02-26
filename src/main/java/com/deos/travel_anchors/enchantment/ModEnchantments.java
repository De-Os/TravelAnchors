package com.deos.travel_anchors.enchantment;

import com.deos.travel_anchors.TravelAnchors;
import com.deos.travel_anchors.tags.ModTags;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

public class ModEnchantments {

    public static final ResourceKey<Enchantment> TELEPORTATION_RANGE = ResourceKey.create(
            Registries.ENCHANTMENT,
            TravelAnchors.getInstance().resource("teleportation_range")
    );

    public static void bootstrap(BootstrapContext<Enchantment> context){
        var items = context.lookup(Registries.ITEM);

        register(context, TELEPORTATION_RANGE, Enchantment.enchantment(
                Enchantment.definition(
                        items.getOrThrow(ModTags.Items.TELEPORTATION_RANGE_APPLICABLE),
                        1,
                        3,
                        Enchantment.dynamicCost(25, 25),
                        Enchantment.dynamicCost(75, 25),
                        8
                )
        ));
    }

    private static void register(BootstrapContext<Enchantment> registry, ResourceKey<Enchantment> key, Enchantment.Builder builder) {
        registry.register(key, builder.build(key.location()));
    }
}
