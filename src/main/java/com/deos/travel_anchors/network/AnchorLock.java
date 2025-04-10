package com.deos.travel_anchors.network;

import com.deos.travel_anchors.TravelAnchors;
import com.deos.travel_anchors.block.custom.TravelAnchorTile;
import com.deos.travel_anchors.gui.LangHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record AnchorLock(
        BlockPos pos
) implements CustomPacketPayload {

    public static final Type<AnchorLock> TYPE = new Type<>(TravelAnchors.location("anchor_lock"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AnchorLock> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, AnchorLock::pos,
            AnchorLock::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        Player player = ctx.player();
        Level level = player.level();

        if (level instanceof ServerLevel sl) {
            if (sl.hasChunkAt(this.pos)) {
                BlockEntity be = sl.getBlockEntity(this.pos);
                if (be instanceof TravelAnchorTile tatile) {
                    tatile.setLocked(true);
                    player.displayClientMessage(LangHolder.MESSAGES_ANCHOR_LOCKED.getComponent(), true);
                    tatile.setChanged();
                }
            }
        }
    }
}