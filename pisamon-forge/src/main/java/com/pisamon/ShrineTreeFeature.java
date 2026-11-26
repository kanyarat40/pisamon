package com.pisamon;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** ต้นไม้ใหญ่ + ศาลผีชุดดำใต้ต้นไม้ + ผีชุดดำยืนเฝ้า */
public class ShrineTreeFeature extends Feature<NoneFeatureConfiguration> {
    private static final BlockState LOG = Blocks.DARK_OAK_LOG.defaultBlockState();
    private static final BlockState LEAF = Blocks.DARK_OAK_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true);

    public ShrineTreeFeature() { super(NoneFeatureConfiguration.CODEC); }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctx) {
        WorldGenLevel lv = ctx.level();
        BlockPos o = ctx.origin();
        RandomSource r = ctx.random();
        if (!lv.getBlockState(o.below()).is(BlockTags.DIRT)) return false;

        tree(lv, o, r);
        shrine(lv, o.offset(5, 0, 1));
        spawnGhost(lv, o.offset(2, 0, -2), r);
        return true;
    }

    private void soft(WorldGenLevel lv, BlockPos p, BlockState s) {
        if (lv.getBlockState(p).canBeReplaced()) lv.setBlock(p, s, 2);
    }

    private void blob(WorldGenLevel lv, BlockPos c, int rad, RandomSource r) {
        for (int dx = -rad; dx <= rad; dx++)
            for (int dy = -rad; dy <= rad; dy++)
                for (int dz = -rad; dz <= rad; dz++) {
                    int d = dx * dx + dz * dz + dy * dy * 2;
                    if (d <= rad * rad + r.nextInt(rad + 1)) soft(lv, c.offset(dx, dy, dz), LEAF);
                }
    }

    private void tree(WorldGenLevel lv, BlockPos o, RandomSource r) {
        int h = 14 + r.nextInt(5);
        for (int y = 0; y < h; y++)
            for (int dx = 0; dx < 2; dx++)
                for (int dz = 0; dz < 2; dz++)
                    lv.setBlock(o.offset(dx, y, dz), LOG, 2);
        // รากไม้
        int[][] roots = {{-1, 0}, {2, 0}, {0, -1}, {1, 2}, {-1, 1}, {2, 1}, {1, -1}, {0, 2}};
        for (int[] rt : roots) {
            BlockPos p = o.offset(rt[0], 0, rt[1]);
            lv.setBlock(p, LOG, 2);
            if (r.nextBoolean()) soft(lv, p.above(), LOG);
        }
        // พุ่มใบหลัก + กิ่ง
        blob(lv, o.offset(1, h - 2, 1), 6, r);
        for (int i = 0; i < 4; i++) {
            Direction d = Direction.from2DDataValue(i);
            int by = h - 7 + r.nextInt(3);
            BlockPos end = o;
            for (int k = 1; k <= 5; k++) {
                end = o.offset(1 + d.getStepX() * k, by + k / 2, 1 + d.getStepZ() * k);
                lv.setBlock(end, LOG, 2);
            }
            blob(lv, end, 3, r);
        }
    }

    private void shrine(WorldGenLevel lv, BlockPos c) {
        BlockState plank = Blocks.DARK_OAK_PLANKS.defaultBlockState();
        BlockState fence = Blocks.DARK_OAK_FENCE.defaultBlockState();
        BlockState slab = Blocks.DARK_OAK_SLAB.defaultBlockState();
        BlockState air = Blocks.AIR.defaultBlockState();

        // ถมดิน + เคลียร์พื้นที่
        for (int dx = -2; dx <= 2; dx++)
            for (int dz = -2; dz <= 2; dz++) {
                for (int dy = -1; dy >= -3; dy--) soft(lv, c.offset(dx, dy, dz), Blocks.DIRT.defaultBlockState());
                lv.setBlock(c.offset(dx, -1, dz), Blocks.GRASS_BLOCK.defaultBlockState(), 2);
                for (int dy = 0; dy <= 5; dy++) lv.setBlock(c.offset(dx, dy, dz), air, 2);
            }
        // ขา 4 มุม
        for (int sx = -1; sx <= 1; sx += 2)
            for (int sz = -1; sz <= 1; sz += 2) {
                lv.setBlock(c.offset(sx, 0, sz), fence, 2);
                lv.setBlock(c.offset(sx, 1, sz), fence, 2);
            }
        // พื้นศาล
        for (int dx = -1; dx <= 1; dx++)
            for (int dz = -1; dz <= 1; dz++)
                lv.setBlock(c.offset(dx, 2, dz), plank, 2);
        // ผนังหลัง + ข้าง (ด้านหน้าเปิด)
        for (int dx = -1; dx <= 1; dx++) lv.setBlock(c.offset(dx, 3, 1), plank, 2);
        lv.setBlock(c.offset(-1, 3, 0), plank, 2);
        lv.setBlock(c.offset(1, 3, 0), plank, 2);
        // เทียน + โคมวิญญาณ
        lv.setBlock(c.offset(0, 3, 0), Blocks.BLACK_CANDLE.defaultBlockState()
                .setValue(CandleBlock.CANDLES, 3).setValue(CandleBlock.LIT, true), 2);
        lv.setBlock(c.offset(0, 3, -1), Blocks.SOUL_LANTERN.defaultBlockState(), 2);
        // หลังคา 2 ชั้น
        for (int dx = -2; dx <= 2; dx++)
            for (int dz = -2; dz <= 2; dz++)
                lv.setBlock(c.offset(dx, 4, dz), slab, 2);
        for (int dx = -1; dx <= 1; dx++)
            for (int dz = -1; dz <= 1; dz++)
                lv.setBlock(c.offset(dx, 5, dz), slab, 2);
        // ของเซ่นหน้าศาล
        lv.setBlock(c.offset(0, 0, -2), Blocks.BLACK_CANDLE.defaultBlockState()
                .setValue(CandleBlock.CANDLES, 2).setValue(CandleBlock.LIT, true), 2);
    }

    private void spawnGhost(WorldGenLevel lv, BlockPos p, RandomSource r) {
        BlackGhost g = ModEntities.BLACK_GHOST.get().create(lv.getLevel());
        if (g == null) return;
        g.moveTo(p.getX() + 0.5, p.getY(), p.getZ() + 0.5, r.nextFloat() * 360F, 0F);
        g.setPersistenceRequired();
        g.finalizeSpawn(lv, lv.getCurrentDifficultyAt(p), MobSpawnType.STRUCTURE, null, null);
        lv.addFreshEntityWithPassengers(g);
    }
}
