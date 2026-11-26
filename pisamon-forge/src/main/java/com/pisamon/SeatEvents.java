package com.pisamon;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * ระบบนั่งห้าง
 * - มือเปล่า + คลิกขวาที่บันได (stairs ครึ่งล่าง) เพื่อนั่ง / กด Shift เพื่อลุก
 * - ตอนนั่งอยู่ ดวงตาผีในรัศมี 32 บล็อกจะเรืองแสงให้เห็นในความมืด (เล็งยิงจากห้างได้สบาย ๆ)
 */
public class SeatEvents {

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.RightClickBlock e) {
        if (e.getHand() != InteractionHand.MAIN_HAND) return;
        Player p = e.getEntity();
        Level lv = e.getLevel();
        if (!p.getMainHandItem().isEmpty() || p.isShiftKeyDown() || p.isPassenger()) return;

        BlockPos pos = e.getPos();
        BlockState st = lv.getBlockState(pos);
        if (!(st.getBlock() instanceof StairBlock) || st.getValue(StairBlock.HALF) != Half.BOTTOM) return;
        BlockPos above = pos.above();
        if (!lv.getBlockState(above).getCollisionShape(lv, above).isEmpty()) return;
        if (!lv.getEntitiesOfClass(SeatEntity.class, new AABB(pos).inflate(0.2)).isEmpty()) return; // มีคนนั่งแล้ว

        if (!lv.isClientSide) {
            SeatEntity seat = ModEntities.SEAT.get().create(lv);
            if (seat == null) return;
            seat.setPos(pos.getX() + 0.5, pos.getY() + 0.75, pos.getZ() + 0.5); // ปรับความสูงนั่งตรงนี้ได้
            lv.addFreshEntity(seat);
            p.startRiding(seat, true);
            p.displayClientMessage(Component.literal("\u00a77นั่งห้าง... ดวงตาผีจะเรืองแสงในความมืด (Shift = ลุก)"), true);
        }
        e.setCancellationResult(InteractionResult.SUCCESS);
        e.setCanceled(true);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (e.phase != TickEvent.Phase.END || e.side.isClient()) return;
        Player p = e.player;
        if (p.tickCount % 20 != 0 || !(p.getVehicle() instanceof SeatEntity)) return;
        AABB box = p.getBoundingBox().inflate(32);
        for (Monster m : p.level().getEntitiesOfClass(Monster.class, box, x -> x instanceof BlackGhost || x instanceof TigerSpirit)) {
            m.addEffect(new MobEffectInstance(MobEffects.GLOWING, 60, 0, false, false));
        }
    }
}
