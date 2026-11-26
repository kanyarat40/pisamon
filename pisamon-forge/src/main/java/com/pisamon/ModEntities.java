package com.pisamon;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, PisamonMod.MODID);

    public static final RegistryObject<EntityType<BlackGhost>> BLACK_GHOST = ENTITIES.register("black_ghost",
            () -> EntityType.Builder.of(BlackGhost::new, MobCategory.MONSTER)
                    .sized(0.65F, 2.4F).clientTrackingRange(10).build("black_ghost"));

    public static final RegistryObject<EntityType<TigerSpirit>> TIGER = ENTITIES.register("tiger_spirit",
            () -> EntityType.Builder.of(TigerSpirit::new, MobCategory.MONSTER)
                    .sized(1.3F, 1.5F).clientTrackingRange(10).build("tiger_spirit"));

    public static final RegistryObject<EntityType<SeatEntity>> SEAT = ENTITIES.register("seat",
            () -> EntityType.Builder.<SeatEntity>of(SeatEntity::new, MobCategory.MISC)
                    .sized(0.0F, 0.0F).noSave().fireImmune().clientTrackingRange(8).updateInterval(20).build("seat"));
}
