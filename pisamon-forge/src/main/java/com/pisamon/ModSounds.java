package com.pisamon;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(Registries.SOUND_EVENT, PisamonMod.MODID);

    /** ผีหัวเราะ + "มึงทำไรกูไม่ได้หรอก กระสุนกาก ๆ ของมึง" (โดนกระสุนธรรมดา) */
    public static final RegistryObject<SoundEvent> GHOST_TAUNT = register("ghost_taunt");
    /** เสียงร้องโหยหวน (โดนกระสุนอาคม / มีดอาคม) */
    public static final RegistryObject<SoundEvent> GHOST_WAIL = register("ghost_wail");

    /** true = เล่นเสียงของ vanilla ซ้อนไปด้วย (ไว้ใช้ตอนยังไม่มีไฟล์ .ogg ของจริง) ใส่ไฟล์เสียงแล้วเปลี่ยนเป็น false ได้ */
    public static final boolean VANILLA_PLACEHOLDER = true;

    private static RegistryObject<SoundEvent> register(String name) {
        return SOUNDS.register(name,
                () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(PisamonMod.MODID, name)));
    }
}
