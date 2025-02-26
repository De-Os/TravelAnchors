package com.deos.travel_anchors.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import org.moddingx.libx.base.ItemBase;
import org.moddingx.libx.mod.ModX;

import java.util.List;

public class TravelStaffItem extends ItemBase {

    public TravelStaffItem(ModX mod, Properties properties) {
        super(mod, properties);
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            List<Component> tooltipComponents,
            @NotNull TooltipFlag tooltipFlag
    ) {
        tooltipComponents.add(Component.translatable("tooltip.travelanchors.travel_staff").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public int getEnchantmentValue(@NotNull ItemStack stack) {
        return 20;
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return true;
    }
}
