package com.pisamon;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, PisamonMod.MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, PisamonMod.MODID);

    public static final RegistryObject<Item> MAGIC_KNIFE = ITEMS.register("magic_knife",
            () -> new MagicKnife(new Item.Properties().durability(250)));
    public static final RegistryObject<Item> GHOST_EGG = ITEMS.register("black_ghost_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.BLACK_GHOST, 0x050505, 0x9a9aa5, new Item.Properties()));

    public static final RegistryObject<Item> TIGER_EGG = ITEMS.register("tiger_spirit_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.TIGER, 0xd9822b, 0x111111, new Item.Properties()));

    public static final RegistryObject<CreativeModeTab> TAB = TABS.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.pisamon"))
                    .icon(() -> new ItemStack(MAGIC_KNIFE.get()))
                    .displayItems((p, out) -> { out.accept(MAGIC_KNIFE.get()); out.accept(GHOST_EGG.get()); out.accept(TIGER_EGG.get()); })
                    .build());
}
