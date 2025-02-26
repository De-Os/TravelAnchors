package com.deos.travel_anchors.network;

import com.deos.travel_anchors.TravelAnchors;
import com.deos.travel_anchors.block.custom.TravelAnchorTile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record AnchorNameChange(
        String name,
        BlockPos pos
) implements CustomPacketPayload {

    public static final Type<AnchorNameChange> TYPE = new Type<>(TravelAnchors.location("anchor_name_change"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AnchorNameChange> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, AnchorNameChange::name,
            BlockPos.STREAM_CODEC, AnchorNameChange::pos,
            AnchorNameChange::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        Level level = ctx.player().level();
        if (level.hasChunkAt(this.pos)) {
            BlockEntity be = level.getBlockEntity(this.pos);
            if (be instanceof TravelAnchorTile) {
                ((TravelAnchorTile) be).setName(this.name);
                be.setChanged();
            }
        }
    }
}
