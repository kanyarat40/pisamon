package com.pisamon;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * ผีเกิดในป่าใหญ่ (biome tag pisamon:ghost_forest)
 * - กลางคืนเยอะ ดึกสุดเที่ยงคืนเยอะสุด เกิดเป็นฝูง ฝน/ฟ้าร้องเยอะขึ้น
 * - กลางวันแทบไม่เจอ (ต้องอยู่ใต้ร่มไม้ทึบ ๆ) แล้วจางหายเองเมื่อออกกลางแจ้ง
 */
public class ForestSpawner {
    public static final TagKey<Biome> GHOST_FOREST =
            TagKey.create(Registries.BIOME, new ResourceLocation(PisamonMod.MODID, "ghost_forest"));

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (e.phase != TickEvent.Phase.END || e.side.isClient()) return;
        if (!(e.player instanceof ServerPlayer p) || p.isSpectator()) return;
        if (p.tickCount % 40 != 0) return;

        ServerLevel lv = p.serverLevel();
        if (lv.dimension() != Level.OVERWORLD) return;
        if (!lv.getBiome(p.blockPosition()).is(GHOST_FOREST)) return;

        RandomSource rng = lv.getRandom();
        long t = lv.getDayTime() % 24000L;
        boolean night = t >= 12500L && t <= 23500L;
        // depth: 0.3 (หัวค่ำ/ใกล้สว่าง) -> 1.0 (เที่ยงคืน)
        double depth = night ? 0.3 + 0.7 * (1.0 - Math.abs(t - 18000L) / 5500.0) : 0.0;

        int cap = night ? Math.round((float) (Tuning.NIGHT_CAP_MIN + (Tuning.NIGHT_CAP_MAX - Tuning.NIGHT_CAP_MIN) * depth))
                        : Tuning.DAY_CAP;
        double chance = night ? Tuning.NIGHT_CHANCE : Tuning.DAY_CHANCE;
        if (lv.isRaining()) chance *= Tuning.RAIN_BONUS;
        if (rng.nextDouble() > chance) return;

        var box = p.getBoundingBox().inflate(Tuning.NEARBY_RADIUS);
        int near = lv.getEntitiesOfClass(BlackGhost.class, box).size();
        if (near >= cap) return;
        boolean tigerNearby = !lv.getEntitiesOfClass(TigerSpirit.class, box).isEmpty();

        for (int tries = 0; tries < 8; tries++) {
            double ang = rng.nextDouble() * Math.PI * 2;
            double r = Tuning.SPAWN_MIN_DIST + rng.nextDouble() * (Tuning.SPAWN_MAX_DIST - Tuning.SPAWN_MIN_DIST);
            int x = Mth.floor(p.getX() + Math.cos(ang) * r);
            int z = Mth.floor(p.getZ() + Math.sin(ang) * r);
            BlockPos anchor = spot(lv, x, z, night);
            if (anchor == null) continue;

            boolean tiger = night && !tigerNearby && rng.nextDouble() < Tuning.TIGER_SHARE;
            if (tiger) { spawn(lv, anchor, true); return; }

            int pack = night ? 1 + rng.nextInt(Tuning.PACK_MAX) : 1;
            pack = Math.min(pack, cap - near);
            spawn(lv, anchor, false);
            for (int i = 1; i < pack; i++) {
                BlockPos q = spot(lv, anchor.getX() + rng.nextInt(7) - 3, anchor.getZ() + rng.nextInt(7) - 3, night);
                if (q != null) spawn(lv, q, false);
            }
            return;
        }
    }

    /** หาจุดยืนบนพื้นป่าที่เหมาะ (มืด ไม่ใช่น้ำ ไม่ใกล้ผู้เล่น) ไม่เจอคืน null */
    private BlockPos spot(ServerLevel lv, int x, int z, boolean night) {
        if (!lv.hasChunkAt(new BlockPos(x, 64, z))) return null;
        int y = lv.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        BlockPos pos = new BlockPos(x, y, z);
        if (!lv.getBiome(pos).is(GHOST_FOREST)) return null;
        BlockPos below = pos.below();
        BlockState floor = lv.getBlockState(below);
        if (!floor.isFaceSturdy(lv, below, Direction.UP)) return null;
        if (!lv.getFluidState(pos).isEmpty()) return null;
        if (!lv.getBlockState(pos).canBeReplaced()) return null;
        if (lv.getBrightness(LightLayer.BLOCK, pos) > 7) return null;           // ไกลจากคบไฟ
        if (!night && lv.getBrightness(LightLayer.SKY, pos) > 6) return null;   // กลางวัน: ต้องใต้ร่มทึบเท่านั้น
        if (lv.getNearestPlayer(x + 0.5, y, z + 0.5, 16, false) != null) return null;
        return pos;
    }

    private void spawn(ServerLevel lv, BlockPos pos, boolean tiger) {
        Monster m = tiger ? ModEntities.TIGER.get().create(lv) : ModEntities.BLACK_GHOST.get().create(lv);
        if (m == null) return;
        m.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, lv.getRandom().nextFloat() * 360F, 0F);
        m.finalizeSpawn(lv, lv.getCurrentDifficultyAt(pos), MobSpawnType.NATURAL, null, null);
        lv.addFreshEntityWithPassengers(m);
    }
}
