package com.pisamon;

import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PisamonMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {
    @SubscribeEvent
    public static void attrs(EntityAttributeCreationEvent e) {
        e.put(ModEntities.BLACK_GHOST.get(), BlackGhost.createAttributes().build());
        e.put(ModEntities.TIGER.get(), TigerSpirit.createAttributes().build());
    }
}
