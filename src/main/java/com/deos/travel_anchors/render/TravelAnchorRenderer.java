package com.deos.travel_anchors.render;

import com.deos.travel_anchors.TeleportHandler;
import com.deos.travel_anchors.TravelAnchorList;
import com.deos.travel_anchors.TravelAnchors;
import com.deos.travel_anchors.block.ModBlocks;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Matrix4f;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;
import org.moddingx.libx.annotation.model.Model;
import org.moddingx.libx.render.RenderHelperLevel;

import javax.annotation.Nullable;
import java.util.OptionalDouble;


@OnlyIn(Dist.CLIENT)
public class TravelAnchorRenderer {

    public static final RenderType LINES = createLines("lines", 1);
    public static final RenderType BOLDER_LINES = createLines("bolder_lines", 2);
    public static final RenderType BOLD_LINES = createLines("bold_lines", 3);
    public static final RenderType BOLDER_BOLD_LINES = createLines("bolder_bold_lines", 4);
    public static final RenderType VERLY_BOLD_LINES = createLines("very_bold_lines", 5);

    private static RenderType createLines(String name, int strength) {
        return RenderType.create(TravelAnchors.getInstance().modid + "_" + name,
                DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.LINES, 256, false, false,
                RenderType.CompositeState.builder().setShaderState(RenderStateShard.RENDERTYPE_LINES_SHADER)
                        .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.of(strength)))
                        .setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
                        .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                        .setOutputState(RenderStateShard.ITEM_ENTITY_TARGET)
                        .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
                        .setCullState(RenderStateShard.NO_CULL)
                        .createCompositeState(false)
        );
    }

    @Model("block/travel_anchor")
    public static BakedModel MODEL = null;

    public static void renderAnchors(RenderLevelStageEvent event) {

        PoseStack poseStack = event.getPoseStack();
        ClientLevel level = Minecraft.getInstance().level;
        LocalPlayer player = Minecraft.getInstance().player;

        if (level == null || player == null || event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS) {
            return;
        }

        if (!TeleportHandler.canBlockTeleport(player) && !TeleportHandler.canItemTeleport(player, InteractionHand.MAIN_HAND) && !TeleportHandler.canItemTeleport(player, InteractionHand.OFF_HAND)) {
            return;
        }

        double maxDistanceSq = TeleportHandler.getMaxDistance(player);
        maxDistanceSq *= maxDistanceSq;

        TravelAnchorList anchorsList = TravelAnchorList.get(level);

        double posX = Mth.lerp(event.getPartialTick().getRealtimeDeltaTicks(), player.xo, player.getX());
        double posY = Mth.lerp(event.getPartialTick().getRealtimeDeltaTicks(), player.yo, player.getY());
        double posZ = Mth.lerp(event.getPartialTick().getRealtimeDeltaTicks(), player.zo, player.getZ());

        Pair<BlockPos, String> anchor = TeleportHandler.getAnchorToTeleport(level, player, player.blockPosition().below());

        for (BlockPos pos : anchorsList.anchors.keySet()) {
            double distanceSq = pos.distToCenterSqr(posX, posY, posZ);
            if (distanceSq > maxDistanceSq) {
                continue;
            }

            TravelAnchorList.Entry anchorEntry = anchorsList.getEntry(pos);

            if (anchor == null) {
                continue;
            }

            int light;
            if (level.hasChunkAt(pos)) {
                light = LightTexture.pack(level.getBrightness(LightLayer.BLOCK, pos), level.getBrightness(LightLayer.SKY, pos));
            } else {
                light = LightTexture.FULL_BLOCK;
            }

            boolean active = anchor != null && pos.equals(anchor.getLeft());
            boolean directText = distanceSq <= 50 * 50;

            poseStack.pushPose();
            RenderHelperLevel.loadCameraPosition(event.getCamera(), poseStack, pos);

            if (distanceSq > 50 * 50) {
                double distance = Math.sqrt(distanceSq);
                poseStack.translate(0.5, 0.5, 0.5);

                double log = Math.log(distance) / 2.3;
                float scale = (float) (log * log * log);
                poseStack.scale(scale, scale, scale);
                poseStack.translate(-0.5, -0.5, -0.5);
            }

            renderAnchor(
                    poseStack,
                    OutlineBuffer.INSTANCE,
                    directText ? anchorEntry.name : null,
                    anchorEntry.state,
                    light,
                    true,
                    active,
                    distanceSq,
                    null
            );
            poseStack.popPose();
        }

        Minecraft.getInstance().renderBuffers().bufferSource().endBatch();
    }

    @OnlyIn(Dist.CLIENT)
    public static void renderAnchor(PoseStack poseStack, MultiBufferSource buffer, @Nullable String name, BlockState state, int light, boolean glow, boolean active, double distanceSq, @Nullable ModelData modelData) {

        if(state == null || state.getBlock() == ModBlocks.travelAnchor){
            Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
                    poseStack.last(),
                    buffer.getBuffer(RenderType.solid()),
                    state,
                    MODEL,
                    1,
                    1,
                    1,
                    light,
                    OverlayTexture.NO_OVERLAY,
                    modelData == null ? ModelData.EMPTY : modelData,
                    RenderType.SOLID
            );
        }else{
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
                    state,
                    poseStack,
                    buffer,
                    light,
                    OverlayTexture.NO_OVERLAY,
                    modelData == null ? ModelData.EMPTY : modelData,
                    RenderType.SOLID
            );
        }



        if (glow) {
            RenderType type;
            if (distanceSq > 85 * 85) {
                type = RenderType.lines();
            } else if (distanceSq > 38 * 38) {
                type = RenderType.LINES;
            } else {
                type = BOLDER_LINES;
            }
            LevelRenderer.renderLineBox(
                    poseStack,
                    buffer.getBuffer(type),
                    0,
                    0,
                    0,
                    1,
                    1,
                    1,
                    1,
                    1,
                    1,
                    1
            );
        }

        if (name == null || name.trim().isEmpty()) {
            return;
        }

        double doubleScale = Math.sqrt(0.0035 * Math.sqrt(distanceSq));
        if (doubleScale < 0.1f) {
            doubleScale = 0.1f;
        }

        doubleScale *= (Math.sin(Math.toRadians(Minecraft.getInstance().options.fov().get()) / 4d));
        if (active) {
            doubleScale *= 1.3;
        }
        float scale = (float) doubleScale;

        poseStack.pushPose();
        poseStack.translate(
                0.5,
                1.05 + (doubleScale * Minecraft.getInstance().font.lineHeight),
                0.5
        );
        poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        poseStack.scale(scale, -scale, scale);

        int color = active && ChatFormatting.GOLD.getColor() != null ? ChatFormatting.GOLD.getColor() : 0xFFFFFF;


        Matrix4f matrix4f = poseStack.last().pose();
        Component anchorText = Component.literal(name.trim());

        float textOpacitySetting = Minecraft.getInstance().options.getBackgroundOpacity(0.5f);
        int alpha = (int) (textOpacitySetting * 255) << 24;
        float halfWidth = (float) (-Minecraft.getInstance().font.width(anchorText) / 2);

        Minecraft.getInstance().font.drawInBatch(
                anchorText,
                halfWidth,
                0,
                color,
                false,
                matrix4f,
                buffer,
                Font.DisplayMode.SEE_THROUGH,
                alpha,
                LightTexture.FULL_BRIGHT
        );

        Minecraft.getInstance().font.drawInBatch(
                anchorText,
                halfWidth,
                0,
                color,
                false,
                matrix4f,
                buffer,
                Font.DisplayMode.NORMAL,
                0,
                LightTexture.FULL_BRIGHT
        );

        poseStack.popPose();
    }

    // Y ist the direction pointing directly out of the circle.
    private static CircleRotation rotateCircle(double x, double y, double z) {
        float yr = Float.NaN;
        if (z != 0) {
            yr = (float) (Math.atan2(x, z) + (Math.PI / 2));
        }
        double hor = Math.sqrt((x * x) + (z * z));
        float zr = (float) (Math.atan2(hor, y) + Math.PI);
        return new CircleRotation(yr, zr);
    }

    private record CircleRotation(float y, float z) {

        public void apply(PoseStack poseStack) {
            if (!Float.isNaN(this.y)) {
                poseStack.mulPose(Axis.YP.rotation(this.y));
            }
            poseStack.mulPose(Axis.ZP.rotation(this.z));
        }

        public void reverse(PoseStack poseStack) {
            poseStack.mulPose(Axis.ZP.rotation(-this.z));
            if (!Float.isNaN(this.y)) {
                poseStack.mulPose(Axis.YP.rotation(-this.y));
            }
        }
    }
}

