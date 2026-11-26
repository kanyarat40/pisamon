package com.pisamon;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(PisamonMod.MODID)
public class PisamonMod {
    public static final String MODID = "pisamon";

    public PisamonMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModEntities.ENTITIES.register(bus);
        ModItems.ITEMS.register(bus);
        ModSounds.SOUNDS.register(bus);
        ModItems.TABS.register(bus);
        ModFeatures.FEATURES.register(bus);
        MinecraftForge.EVENT_BUS.register(new GunEvents());
        MinecraftForge.EVENT_BUS.register(new ForestSpawner());
        MinecraftForge.EVENT_BUS.register(new SeatEvents());
    }
}
