package com.deos.travel_anchors.block.custom;

import com.deos.travel_anchors.render.TravelAnchorRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;

import javax.annotation.Nonnull;

public class TravelAnchorRender implements BlockEntityRenderer<TravelAnchorTile> {

    @Override
    public void render(@Nonnull TravelAnchorTile blockEntity, float partialTicks, @Nonnull PoseStack matrixStack, @Nonnull MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        TravelAnchorRenderer.renderAnchor(matrixStack, buffer, null, blockEntity.getMimic(), combinedLight, false, false, 0, null);
//        LocalPlayer player = Minecraft.getInstance().player;
//        if (player == null || (!TeleportHandler.canBlockTeleport(player) && !TeleportHandler.canItemTeleport(player, InteractionHand.MAIN_HAND)
//                && !TeleportHandler.canItemTeleport(player, InteractionHand.OFF_HAND)) || (blockEntity.getLevel() != null && TravelAnchorList.get(blockEntity.getLevel()).getAnchor(blockEntity.getBlockPos()) == null)) {
//            TravelAnchorRenderer.renderAnchor(matrixStack, buffer, null, blockEntity.getMimic(), combinedLight, false, false, 0, null);
//        }
    }
}
