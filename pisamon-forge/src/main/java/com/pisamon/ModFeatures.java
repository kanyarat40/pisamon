package com.pisamon;

import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(ForgeRegistries.FEATURES, PisamonMod.MODID);

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> SHRINE_TREE =
            FEATURES.register("shrine_tree", ShrineTreeFeature::new);
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> HANG =
            FEATURES.register("hang", HangFeature::new);
}
