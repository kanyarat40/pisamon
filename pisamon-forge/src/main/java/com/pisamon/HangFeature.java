package com.pisamon;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.ArrayList;
import java.util.List;

/** ห้างไม้ยกพื้นกลางป่า: ขาสูง 5 บล็อก บันได พื้น 5x5 ราวกั้น หลังคาฟาง ที่นั่ง 2 ที่ + ถังเสบียง */
public class HangFeature extends Feature<NoneFeatureConfiguration> {
    private static final BlockState LOG = Blocks.JUNGLE_LOG.defaultBlockState();
    private static final BlockState PLANK = Blocks.JUNGLE_PLANKS.defaultBlockState();
    private static final BlockState FENCE = Blocks.JUNGLE_FENCE.defaultBlockState();
    private static final BlockState HAY = Blocks.HAY_BLOCK.defaultBlockState();
    private static final BlockState AIR = Blocks.AIR.defaultBlockState();

    public HangFeature() { super(NoneFeatureConfiguration.CODEC); }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctx) {
        WorldGenLevel lv = ctx.level();
        BlockPos o = ctx.origin();
        RandomSource r = ctx.random();
        if (!lv.getBlockState(o.below()).is(BlockTags.DIRT)) return false;
        if (!lv.getFluidState(o).isEmpty()) return false;

        // เคลียร์พื้นที่ (x -1..3, z -2..3, สูง 0..10)
        for (int dx = -1; dx <= 3; dx++)
            for (int dz = -2; dz <= 3; dz++)
                for (int dy = 0; dy <= 10; dy++)
                    lv.setBlock(o.offset(dx, dy, dz), AIR, 2);

        // ขา 4 มุม + เสาบันได (ลงดินจนสุดทาง)
        int[][] legs = {{-1, -1}, {3, -1}, {-1, 3}, {3, 3}, {1, -1}};
        for (int[] l : legs) pillar(lv, o.offset(l[0], 4, l[1]));

        // พื้น 5x5 ที่ y=5
        for (int dx = -1; dx <= 3; dx++)
            for (int dz = -1; dz <= 3; dz++)
                lv.setBlock(o.offset(dx, 5, dz), PLANK, 2);

        // บันได (ด้านหน้า z=-2 หันไปทางเหนือ เกาะเสาที่ z=-1)
        BlockState ladder = Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, Direction.NORTH);
        for (int y = 0; y <= 5; y++) lv.setBlock(o.offset(1, y, -2), ladder, 2);

        // ราวกั้นรอบพื้น (เว้นช่องตรงทางขึ้น) + เสามุมสูงรับหลังคา
        List<BlockPos> fences = new ArrayList<>();
        for (int dx = -1; dx <= 3; dx++)
            for (int dz = -1; dz <= 3; dz++) {
                boolean edge = dx == -1 || dx == 3 || dz == -1 || dz == 3;
                if (!edge || (dx == 1 && dz == -1)) continue;
                fences.add(o.offset(dx, 6, dz));
            }
        for (int[] c : new int[][]{{-1, -1}, {3, -1}, {-1, 3}, {3, 3}}) {
            fences.add(o.offset(c[0], 7, c[1]));
            fences.add(o.offset(c[0], 8, c[1]));
        }
        for (BlockPos p : fences) lv.setBlock(p, FENCE, 2);
        for (BlockPos p : fences) lv.setBlock(p, Block.updateFromNeighbourShapes(lv.getBlockState(p), lv, p), 2);

        // หลังคาฟาง 2 ชั้น
        for (int dx = -1; dx <= 3; dx++)
            for (int dz = -1; dz <= 3; dz++)
                lv.setBlock(o.offset(dx, 9, dz), HAY, 2);
        for (int dx = 0; dx <= 2; dx++)
            for (int dz = 0; dz <= 2; dz++)
                lv.setBlock(o.offset(dx, 10, dz), HAY, 2);

        // ที่นั่ง 2 ที่ (คลิกขวามือเปล่า) + ถังเสบียงตรงกลาง + ตะเกียงห้อยหลังคา
        BlockState seat = Blocks.JUNGLE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.NORTH);
        lv.setBlock(o.offset(0, 6, 2), seat, 2);
        lv.setBlock(o.offset(2, 6, 2), seat, 2);
        BlockPos barrel = o.offset(1, 6, 2);
        lv.setBlock(barrel, Blocks.BARREL.defaultBlockState().setValue(BarrelBlock.FACING, Direction.UP), 2);
        BlockEntity be = lv.getBlockEntity(barrel);
        if (be instanceof RandomizableContainerBlockEntity c)
            c.setLootTable(new ResourceLocation(PisamonMod.MODID, "chests/hang"), r.nextLong());
        lv.setBlock(o.offset(1, 8, 1), Blocks.LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true), 2);
        return true;
    }

    /** เสาไม้จาก top ลงมา 5 บล็อก แล้วต่อลงดินตราบที่ยังเป็นอากาศ/พืช/น้ำ */
    private void pillar(WorldGenLevel lv, BlockPos top) {
        for (int dy = 0; dy <= 14; dy++) {
            BlockPos p = top.below(dy);
            if (dy > 4 && !lv.getBlockState(p).canBeReplaced()) break;
            lv.setBlock(p, LOG, 2);
        }
    }
}
