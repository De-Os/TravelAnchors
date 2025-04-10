package com.deos.travel_anchors.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public enum LangHolder {
    ITEMGROUP_TRAVELANCHORS("itemGroup.travelanchors"),

    ITEMS_TRAVEL_STAFF_LABEL("item.travelanchors.travel_staff"),
    ITEMS_TRAVEL_STAFF_TOOLTIP("tooltip.travelanchors.travel_staff"),
    ITEMS_TRAVEL_STAFF_TOOLTIP_SECONDARY("tooltip.travelanchors.travel_staff_secondary"),

    BLOCKS_TRAVEL_ANCHOR_LABEL("block.travelanchors.travel_anchor"),
    BLOCKS_TRAVEL_ANCHOR_TOOLTIP("tooltip.travelanchors.travel_anchor_block"),
    BLOCKS_TRAVEL_ANCHOR_TOOLTIP_SECONDARY("tooltip.travelanchors.travel_anchor_block_secondary"),

    ENCHANTMENTS_TRAVEL_ANCHORS_TELEPORTATION_RANGE_LABEL("enchantment.travelanchors.teleportation_range"),
    ENCHANTMENTS_TRAVEL_ANCHORS_TELEPORTATION_RANGE_DESC("enchantment.travelanchors.teleportation_range.desc"),

    MESSAGES_TP_SUCCESS("travelanchors.tp.success"),
    MESSAGES_TP_FAIL("travelanchors.tp.fail"),
    MESSAGES_HOP_FAIL("travelanchors.hop.fail"),
    MESSAGES_CANT_USE_AS_MIMIC("travelanchors.mimic.cant_use"),
    MESSAGES_ANCHOR_LOCKED("travelanchors.lock.locked"),
    MESSAGES_ANCHOR_UNLOCKED("travelanchors.lock.unlocked"),

    GUI_BUTTON_SAVE_LABEL("travelanchors.save.button");


    private final String name;

    LangHolder(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public MutableComponent getComponent(Object... args) {
        return Component.translatable(this.name, args);
    }

    public String getLocalized() {
        return this.getComponent().getString();
    }
}
