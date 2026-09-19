package com.lincstoel.hotpeppers;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = HotPeppers.MOD_ID)
public class ClientSetup {

    // Vanilla crops/plants render as cutout (binary alpha test); without this a custom block
    // defaults to the opaque "solid" layer, which ignores alpha entirely and shows the
    // texture's transparent pixels as solid black instead of see-through. Every setRenderLayer
    // overload is marked deprecated (since 1.19) with no replacement - vanilla's own static init
    // still uses this exact overload for WHEAT, SWEET_BERRY_BUSH, etc., so it's the correct call.
    @SuppressWarnings("deprecation")
    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(HotPeppers.HOT_PEPPER_CROP.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(HotPeppers.WILD_HOT_PEPPER.get(), RenderType.cutout());
        });
    }
}
