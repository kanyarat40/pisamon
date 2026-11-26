package com.pisamon;

import net.minecraft.client.renderer.entity.EntityRenderersEvent;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PisamonMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    @SubscribeEvent
    public static void renderers(EntityRenderersEvent.RegisterRenderers e) {
        e.registerEntityRenderer(ModEntities.BLACK_GHOST.get(), GhostRenderer::new);
        e.registerEntityRenderer(ModEntities.TIGER.get(), TigerRenderer::new);
        e.registerEntityRenderer(ModEntities.SEAT.get(), NoopRenderer::new);
    }
}
