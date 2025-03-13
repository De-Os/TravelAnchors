package com.deos.travel_anchors;

import com.deos.travel_anchors.config.ClientConfig;
import com.deos.travel_anchors.config.Config;
import com.deos.travel_anchors.network.ClientEventMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.moddingx.libx.event.InteractBlockEmptyHandEvent;

public class EventListener {

    @SubscribeEvent
    public static void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        TravelAnchors.getNetwork().updateTravelAnchorList(event.getEntity());
    }

    @SubscribeEvent
    public static void playerChangeDim(PlayerEvent.PlayerChangedDimensionEvent event) {
        TravelAnchors.getNetwork().updateTravelAnchorList(event.getEntity());
    }

    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickItem event) {
        Level level = event.getLevel();
        Player player = event.getEntity();
        if (TeleportHandler.canPlayerTeleport(player, event.getHand()) && !event.getItemStack().isEmpty()) {
            if (player.isShiftKeyDown() && TeleportHandler.canItemTeleport(player, event.getHand())) {
                if (TeleportHandler.shortTeleport(level, player, event.getHand())) {
                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));
                    player.getCooldowns().addCooldown(event.getItemStack().getItem(), Config.short_tp_delay_ticks);
                }
            } else {
                if (TeleportHandler.anchorTeleport(level, player, player.blockPosition().immutable().below(), event.getHand())) {
                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEmptyClick(PlayerInteractEvent.RightClickEmpty event) {
        Level level = event.getLevel();
        Player player = event.getEntity();
        if (TeleportHandler.canBlockTeleport(player) && !player.isShiftKeyDown() && event.getHand() == InteractionHand.MAIN_HAND && event.getEntity().getItemInHand(InteractionHand.OFF_HAND).isEmpty() && event.getItemStack().isEmpty()) {
            TravelAnchors.getNetwork().sendClientEventToServer(level, ClientEventMessage.actionTypeEmptyHandInteract);
//            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }

    @SubscribeEvent
    public static void emptyBlockClick(InteractBlockEmptyHandEvent event) {
        if (event.getHand() == InteractionHand.MAIN_HAND) {
            // Empty offhand does not count. In that case the main hand will either produce
            // this event or PlayerInteractEvent.RightClickItem
            if (TeleportHandler.canPlayerTeleport(event.getPlayer(), event.getHand())) {
                if (!event.getPlayer().isShiftKeyDown()) {
                    if (TeleportHandler.anchorTeleport(event.getLevel(), event.getPlayer(), event.getPlayer().blockPosition().immutable().below(), event.getHand())) {
                        event.setCanceled(true);
                        event.setCancellationResult(InteractionResult.SUCCESS);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onJump(LivingEvent.LivingJumpEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (ClientConfig.disable_elevation) {
                if (TeleportHandler.canBlockTeleport(player) && !player.isShiftKeyDown()) {
                    TravelAnchors.getNetwork().sendClientEventToServer(player.getCommandSenderWorld(), ClientEventMessage.actionTypeJumpTeleport);
                }
            } else {
                if (TeleportHandler.canElevate(player) && !player.isShiftKeyDown()) {
                    TravelAnchors.getNetwork().sendClientEventToServer(player.getCommandSenderWorld(), ClientEventMessage.actionTypeJump);
                }
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onSneak(MovementInputUpdateEvent event) {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().options.keyShift.consumeClick()) {
            if (!ClientConfig.disable_elevation) {
                if (TeleportHandler.canElevate(Minecraft.getInstance().player)) {
                    TravelAnchors.getNetwork().sendClientEventToServer(Minecraft.getInstance().player.getCommandSenderWorld(), ClientEventMessage.actionTypeSneak);
                }
            }
        }
    }
}