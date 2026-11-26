package com.pisamon;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** ผีชุดดำ */
public class BlackGhost extends Monster {
    private int magicWindow = 0;   // tick ที่อนุญาตให้รับดาเมจ (หลังโดนกระสุนอาคม)
    private int dodgeCooldown = 0;
    private int tauntCooldown = 0; // กันเสียงซ้อนตอนยิงรัว
    private int wailCooldown = 0;

    public BlackGhost(EntityType<? extends Monster> type, Level level) { super(type, level); }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, Tuning.GHOST_HP)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.1, false));
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 12.0F));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public void openMagicWindow() { magicWindow = 2; }

    @Override
    public boolean hurt(DamageSource src, float amount) {
        if (src.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return super.hurt(src, amount); // /kill, void
        boolean knife = src.getDirectEntity() instanceof LivingEntity le
                && le.getMainHandItem().getItem() instanceof MagicKnife;
        if (magicWindow > 0 || knife) {
            if (knife) { sparks(); wail(); }
            return super.hurt(src, amount);
        }
        taunt(src.getEntity());
        return false; // กระสุนธรรมดา/อาวุธทั่วไป ไม่เข้า
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (magicWindow > 0) magicWindow--;
        if (dodgeCooldown > 0) dodgeCooldown--;
        if (tauntCooldown > 0) tauntCooldown--;
        if (wailCooldown > 0) wailCooldown--;
        GhostUtil.dawnFade(this); // ฟ้าสางผีจางหาย (ตัวจากศาลไม่หาย)
        if (!level().isClientSide && getTarget() != null && distanceTo(getTarget()) > 8 && random.nextInt(120) == 0) {
            teleportNear(getTarget(), 3, 6); // วาปเข้าหาเหยื่อ
        }
    }

    /** ลองหลบกระสุนอาคม (วาปหนี) */
    public boolean tryDodge(Entity attacker) {
        if (dodgeCooldown > 0 || random.nextFloat() > 0.35F) return false;
        dodgeCooldown = 40;
        return teleportNear(attacker != null ? attacker : this, 4, 9);
    }

    public boolean teleportNear(Entity around, double minR, double maxR) {
        for (int i = 0; i < 16; i++) {
            double ang = random.nextDouble() * Math.PI * 2;
            double r = minR + random.nextDouble() * (maxR - minR);
            double x = around.getX() + Math.cos(ang) * r;
            double z = around.getZ() + Math.sin(ang) * r;
            smoke();
            if (randomTeleport(x, around.getY(), z, true)) { smoke(); return true; }
        }
        return false;
    }

    private void smoke() {
        if (level() instanceof ServerLevel sl)
            sl.sendParticles(ParticleTypes.LARGE_SMOKE, getX(), getY() + 1, getZ(), 12, 0.3, 0.6, 0.3, 0.02);
    }

    /** กระสุนธรรมดา: ควันจางๆ + ผีหัวเราะเยาะ "มึงทำไรกูไม่ได้หรอก กระสุนกาก ๆ ของมึง" ไม่เสียเลือด */
    public void taunt(Entity attacker) {
        if (!(level() instanceof ServerLevel sl)) return;
        sl.sendParticles(ParticleTypes.SMOKE, getX(), getY() + 1.2, getZ(), 6, 0.2, 0.3, 0.2, 0.01);
        if (tauntCooldown > 0) return; // ยิงรัวๆ ไม่ให้เสียงทับกัน
        tauntCooldown = 40;            // 2 วินาที (ปรับตามความยาวไฟล์เสียง)
        sl.playSound(null, getX(), getY(), getZ(), ModSounds.GHOST_TAUNT.get(), SoundSource.HOSTILE, 1.6F, 1.0F);
        if (ModSounds.VANILLA_PLACEHOLDER) {
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.WITCH_CELEBRATE, SoundSource.HOSTILE, 1.2F, 0.6F);
        }
        if (attacker instanceof Player p) {
            p.displayClientMessage(Component.literal("\u00a78ผีชุดดำ: \u00a77มึงทำไรกูไม่ได้หรอก กระสุนกาก ๆ ของมึง"), true);
        }
    }

    /** โดนกระสุนอาคม/มีดอาคม: ร้องโหยหวน */
    public void wail() {
        if (!(level() instanceof ServerLevel sl)) return;
        if (wailCooldown > 0) return;
        wailCooldown = 30;
        sl.playSound(null, getX(), getY(), getZ(), ModSounds.GHOST_WAIL.get(), SoundSource.HOSTILE, 1.8F, 1.0F);
        if (ModSounds.VANILLA_PLACEHOLDER) {
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GHAST_SCREAM, SoundSource.HOSTILE, 1.2F, 0.5F);
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.SOUL_ESCAPE, SoundSource.HOSTILE, 1.0F, 0.6F);
        }
    }

    /** เศษสะเก็ดกระสุนอาคม */
    public void sparks() {
        if (level() instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.ENCHANTED_HIT, getX(), getY() + 1.2, getZ(), 25, 0.3, 0.4, 0.3, 0.25);
            sl.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, getX(), getY() + 1.2, getZ(), 10, 0.25, 0.35, 0.25, 0.08);
            sl.sendParticles(ParticleTypes.CRIT, getX(), getY() + 1.2, getZ(), 15, 0.3, 0.4, 0.3, 0.3);
        }
    }
}
