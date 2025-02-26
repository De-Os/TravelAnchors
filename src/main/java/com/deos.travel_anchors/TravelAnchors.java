package com.deos.travel_anchors;

import com.deos.travel_anchors.block.custom.TravelAnchorTile;
import com.deos.travel_anchors.config.ClientConfig;
import com.deos.travel_anchors.config.Config;
import com.deos.travel_anchors.item.ModItems;
import com.deos.travel_anchors.gui.TravelAnchorMenuFactory;
import com.deos.travel_anchors.network.Networking;
import com.deos.travel_anchors.render.TravelAnchorRenderer;
import com.deos.travel_anchors.tabs.ModTab;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;
import org.moddingx.libx.mod.ModXRegistration;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

import javax.annotation.Nonnull;

@Mod(TravelAnchors.MODID)
public final class TravelAnchors extends ModXRegistration
{
    public static final String MODID = "travelanchors";

    private static TravelAnchors instance;
    private static Networking network;
    private static ModTab tab;

    public TravelAnchors(IEventBus modEventBus, ModContainer modContainer)
    {
        instance = this;
        network = new Networking(this);
        tab = new ModTab(this);


        NeoForge.EVENT_BUS.register(EventListener.class);

        if(FMLEnvironment.dist == Dist.CLIENT) {
            NeoForge.EVENT_BUS.addListener(TravelAnchorRenderer::renderAnchors);
        }

        modEventBus.addListener(Networking::registerPackets);
        modEventBus.addListener(TravelAnchorMenuFactory::registerScreen);

        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    @Nonnull
    public static Networking getNetwork() {
        return network;
    }

    @Nonnull
    public static TravelAnchors getInstance() {
        return instance;
    }

    public static ResourceLocation location(String path)
    {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    @Override
    protected void setup(FMLCommonSetupEvent event) {
    }

    @Override
    protected void clientSetup(FMLClientSetupEvent event) {

    }
}