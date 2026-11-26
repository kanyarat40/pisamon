package com.pisamon;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

public final class GhostUtil {
    private GhostUtil() {}

    /** ฟ้าสาง: ผีที่ไม่ได้ไล่ใคร และอยู่กลางแจ้ง จะจางหายไปเอง (ตัวที่เกิดจากศาลไม่หาย) */
    public static void dawnFade(Monster m) {
        Level lv = m.level();
        if (lv.isClientSide || m.isPersistenceRequired() || m.getTarget() != null) return;
        if (!lv.isDay() || lv.getBrightness(LightLayer.SKY, m.blockPosition()) <= 6) return;
        if (m.getRandom().nextInt(100) != 0) return;
        if (lv instanceof ServerLevel sl)
            sl.sendParticles(ParticleTypes.LARGE_SMOKE, m.getX(), m.getY() + 1, m.getZ(), 15, 0.3, 0.7, 0.3, 0.02);
        m.discard();
    }
}
