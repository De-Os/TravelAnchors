package com.deos.travel_anchors.network;

import com.deos.travel_anchors.TeleportHandler;
import com.deos.travel_anchors.TravelAnchors;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ClientEventMessage(
        String action
) implements CustomPacketPayload {

    public static final Type<ClientEventMessage> TYPE = new Type<>(TravelAnchors.location("client_event_message"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientEventMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ClientEventMessage::action,
            ClientEventMessage::new
    );

    public static final String actionTypeJump = "jump";
    public static final String actionTypeSneak = "sneak";
    public static final String actionTypeEmptyHandInteract = "empty_hand_interact";
    public static final String actionTypeJumpTeleport = "jump_teleport";

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        Player player = ctx.player();
        switch (this.action){
            case actionTypeJump:
                if(TeleportHandler.canElevate(player)){
                    if(TeleportHandler.elevateUp(player)){
                        player.setDeltaMovement(player.getDeltaMovement().multiply(1, 0, 1));
                    }
                }
                break;
            case actionTypeSneak:
                if(TeleportHandler.canElevate(player)){
                    if(TeleportHandler.elevateDown(player)){
                        player.setDeltaMovement(player.getDeltaMovement().multiply(1, 0, 1));
                    }
                }
                break;
            case actionTypeEmptyHandInteract:
                if(TeleportHandler.canBlockTeleport(player) && !player.isShiftKeyDown()){
                    TeleportHandler.anchorTeleport(player.getCommandSenderWorld(), player, player.blockPosition().immutable().below(), InteractionHand.MAIN_HAND);
                }
                break;
            case actionTypeJumpTeleport:
                if(TeleportHandler.canBlockTeleport(player) && !player.isShiftKeyDown()){
                    TeleportHandler.anchorTeleport(player.getCommandSenderWorld(), player, player.blockPosition().immutable().below(), null);
                }
                break;
        }
    }
}