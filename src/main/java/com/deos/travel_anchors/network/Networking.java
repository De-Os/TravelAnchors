package com.deos.travel_anchors.network;

import com.deos.travel_anchors.TravelAnchorList;
import com.deos.travel_anchors.TravelAnchors;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.moddingx.libx.mod.ModX;
import org.moddingx.libx.network.NetworkX;

import javax.annotation.Nullable;

public class Networking extends NetworkX {


    public Networking(ModX mod) {
        super(mod);
    }


    public static void registerPackets(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(TravelAnchors.MODID).versioned("1.0");

        registrar.playBidirectional(AnchorListUpdate.TYPE, AnchorListUpdate.STREAM_CODEC, AnchorListUpdate::handle);
        registrar.playBidirectional(AnchorLock.TYPE, AnchorLock.STREAM_CODEC, AnchorLock::handle);
        registrar.playBidirectional(AnchorNameChange.TYPE, AnchorNameChange.STREAM_CODEC, AnchorNameChange::handle);
        registrar.playBidirectional(ClientEventMessage.TYPE, ClientEventMessage.STREAM_CODEC, ClientEventMessage::handle);

    }

    public void sendNameChange(Level level, BlockPos pos, String name) {
        if (level.isClientSide) {
            PacketDistributor.sendToServer(new AnchorNameChange(name, pos));
        }
    }

    public void sendLock(Level level, BlockPos pos) {
        if (level.isClientSide) {
            PacketDistributor.sendToServer(new AnchorLock(pos));
        }
    }

    public void updateTravelAnchorList(Level level, @Nullable TravelAnchorList list) {
        if (!level.isClientSide && level instanceof ServerLevel) {
            if (list == null) {
                list = TravelAnchorList.get(level);
            }
            PacketDistributor.sendToPlayersInDimension((ServerLevel) level, new AnchorListUpdate(list.save(new CompoundTag())));
        }
    }

    public void updateTravelAnchorList(Player player) {
        if (!player.getCommandSenderWorld().isClientSide && player instanceof ServerPlayer) {
            PacketDistributor.sendToPlayer(
                    (ServerPlayer) player,
                    new AnchorListUpdate(
                            TravelAnchorList
                                    .get(player.getCommandSenderWorld())
                                    .save(new CompoundTag())
                    )
            );
        }
    }

    public void sendClientEventToServer(Level level, String actionType) {
        if (level.isClientSide) {
            PacketDistributor.sendToServer(new ClientEventMessage(actionType));
        }
    }

    @Override
    protected String getVersion() {
        return "1";
    }
}