package com.pisamon;

import net.minecraft.resources.ResourceLocation;

public class MagicAmmo {
    /** กติกา: ammo id ที่ลงท้ายด้วย "_arkom" ถือเป็นกระสุนอาคม เช่น pisamon:12g_arkom, pisamon:9mm_arkom */
    public static boolean isMagic(ResourceLocation ammoId) {
        return ammoId != null && ammoId.getPath().endsWith("_arkom");
    }
}
