package com.deos.travel_anchors.block.custom;

import com.deos.travel_anchors.TravelAnchorList;
import com.deos.travel_anchors.TravelAnchors;
import com.deos.travel_anchors.block.ModBlocks;
import com.deos.travel_anchors.gui.LangHolder;
import com.deos.travel_anchors.gui.TravelAnchorMenu;
import com.deos.travel_anchors.gui.TravelAnchorMenuFactory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.moddingx.libx.base.tile.MenuBlockBE;
import org.moddingx.libx.menu.type.AdvancedMenuType;
import org.moddingx.libx.registration.RegistrationContext;

import javax.annotation.Nonnull;
import java.util.List;

public class TravelAnchorBlock extends MenuBlockBE<TravelAnchorTile, TravelAnchorMenu> {

    private static final VoxelShape SHAPE = Shapes.box(0.01, 0.01, 0.01, 0.99, 0.99, 0.99);

    public TravelAnchorBlock() {
        super(
                TravelAnchors.getInstance(),
                TravelAnchorTile.class,
                AdvancedMenuType.create(new TravelAnchorMenuFactory(), BlockPos.STREAM_CODEC),
                Properties.of()
                        .strength(2f)
                        .requiresCorrectToolForDrops()
                        .sound(SoundType.METAL)
        );
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerAdditional(RegistrationContext ctx, EntryCollector builder) {
        BlockEntityRenderers.register(ModBlocks.travelAnchor.getBlockEntityType(), context -> new TravelAnchorRender());
        super.registerAdditional(ctx, builder);
    }

    @Override
    protected int getLightBlock(@NotNull BlockState state, BlockGetter level, @NotNull BlockPos pos) {
        BlockEntity tile = level.getBlockEntity(pos);
        if (tile instanceof TravelAnchorTile tatile) {
            BlockState mimic = tatile.getMimic();
            if (mimic != null) {
                return mimic.getLightBlock(level, pos);
            }
        }

        return super.getLightBlock(state, level, pos);
    }

    @Nonnull
    @Override
    public VoxelShape getShape(@Nonnull BlockState state, BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        BlockEntity tile = level.getBlockEntity(pos);
        if (tile instanceof TravelAnchorTile) {
            BlockState mimic = ((TravelAnchorTile) tile).getMimic();
            if (mimic != null) {
                return mimic.getShape(level, pos, context);
            }
        }
        return Shapes.block();
    }

    @Nonnull
    @Override
    public VoxelShape getOcclusionShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos) {
        BlockEntity tile = level.getBlockEntity(pos);
        if (tile instanceof TravelAnchorTile) {
            BlockState mimic = ((TravelAnchorTile) tile).getMimic();
            if (mimic != null) {
                return mimic.getBlockSupportShape(level, pos);
            }
        }
        return SHAPE;
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            Item.@NotNull TooltipContext context,
            List<Component> tooltipComponents,
            @NotNull TooltipFlag tooltipFlag
    ) {
        tooltipComponents.add(LangHolder.BLOCKS_TRAVEL_ANCHOR_TOOLTIP.getComponent().withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(LangHolder.BLOCKS_TRAVEL_ANCHOR_TOOLTIP_SECONDARY.getComponent().withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public @NotNull InteractionResult useWithoutItem(
            @NotNull BlockState state,
            @NotNull Level level,
            @NotNull BlockPos pos,
            @NotNull Player player,
            @NotNull BlockHitResult hit
    ) {
        BlockEntity be = level.getBlockEntity(pos);

        if (
                be instanceof TravelAnchorTile anchor
                        && anchor.isLocked()
                        && player.isShiftKeyDown()
                        && !level.isClientSide
        ) {
            anchor.setLocked(false);
            player.displayClientMessage(LangHolder.MESSAGES_ANCHOR_UNLOCKED.getComponent(), true);
            return InteractionResult.PASS;
        }

        return super.useWithoutItem(state, level, pos, player, hit);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(
            @NotNull ItemStack stack,
            @NotNull BlockState state,
            Level level,
            @NotNull BlockPos pos,
            @NotNull Player player,
            @NotNull InteractionHand hand,
            @NotNull BlockHitResult hitResult
    ) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof TravelAnchorTile anchor) {
            if (!stack.isEmpty() && stack.getItem() instanceof BlockItem blockItem) {
                if (!level.isClientSide) {

                    if (!isValidStateForMimic(blockItem.getBlock(), level, pos)) {
                        player.displayClientMessage(LangHolder.MESSAGES_CANT_USE_AS_MIMIC.getComponent(), true);
                        return ItemInteractionResult.SUCCESS;
                    }

                    BlockState mimicState = blockItem.getBlock().getStateForPlacement(
                            new BlockPlaceContext(
                                    player,
                                    InteractionHand.MAIN_HAND,
                                    stack,
                                    hitResult
                            )
                    );

                    if (mimicState == null || mimicState.getBlock() == this) {
                        anchor.setMimic(null);
                    } else {
                        anchor.setMimic(mimicState);
                    }
                    player.playNotifySound(SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1F, 1F);
                }
                return ItemInteractionResult.SUCCESS;
            }

            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        } else {
            return ItemInteractionResult.SUCCESS;
        }
    }

    @Override
    public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        super.onRemove(state, level, pos, newState, isMoving);
        TravelAnchorList.get(level).setAnchor(level, pos, null, ModBlocks.travelAnchor.defaultBlockState());
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    private boolean isValidStateForMimic(Block block, Level level, BlockPos pos) {
        if (block == this) {
            return true;
        }

        BlockState state = block.defaultBlockState();

        if (state.getRenderShape() != RenderShape.MODEL) {
            return false;
        }

        if (state.getCollisionShape(level, pos).isEmpty()) {
            return false;
        }

        if (state.getShape(level, pos) != Shapes.block()) {
            return false;
        }

        return true;
    }
}
