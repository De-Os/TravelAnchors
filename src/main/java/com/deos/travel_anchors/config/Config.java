package com.deos.travel_anchors.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.moddingx.libx.annotation.config.RegisterConfig;
import org.moddingx.libx.config.validate.DoubleRange;

@RegisterConfig("common")
public class Config
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec SPEC = BUILDER.build();

    @org.moddingx.libx.config.Config("The maximum angle you can look at the Travel Anchor to teleport.")
    @DoubleRange(min = 1)
    public static double max_angle = 30;

    @org.moddingx.libx.config.Config("The maximum distance you are allowed to teleport.")
    @DoubleRange(min = 1)
    public static double max_distance = 64;

    @org.moddingx.libx.config.Config("The maximum distance you can short-range teleport with shift-click.")
    @DoubleRange(min = 2, max = 15)
    public static double max_short_tp_distance = 7;

    @org.moddingx.libx.config.Config({
            "Fire an EntityTeleportEvent before allowing the teleport.",
            "This allows other mods to prevent the teleport or change the destination."
    })
    public static boolean fireTeleportEvent = true;
}
