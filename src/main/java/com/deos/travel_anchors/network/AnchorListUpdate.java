package com.deos.travel_anchors.network;

import com.deos.travel_anchors.TravelAnchorList;
import com.deos.travel_anchors.TravelAnchors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record AnchorListUpdate (
        CompoundTag nbt
) implements CustomPacketPayload{

    public static final Type<AnchorListUpdate> TYPE = new Type<>(TravelAnchors.location("anchor_list_update"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AnchorListUpdate> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG, AnchorListUpdate::nbt,
            AnchorListUpdate::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        if(ctx.player().level() != null)
        {
            TravelAnchorList.get(ctx.player().level()).load(this.nbt);
        }
    }
}
