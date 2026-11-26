package com.pisamon;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** เสือสมิง: เสือยักษ์พุ่งกระโจนใส่ พอเลือดต่ำกว่าครึ่งจะคำรามโกรธ วิ่งเร็วขึ้น ตีแรงขึ้น */
public class TigerSpirit extends Monster {
    private boolean enraged = false;

    public TigerSpirit(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 30;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, Tuning.TIGER_HP)
                .add(Attributes.MOVEMENT_SPEED, 0.36)
                .add(Attributes.ATTACK_DAMAGE, 9.0)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new LeapAtTargetGoal(this, 0.55F));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, true));
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 14.0F));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public boolean hurt(DamageSource src, float amount) {
        boolean r = super.hurt(src, amount);
        if (r && !enraged && !level().isClientSide && getHealth() < getMaxHealth() * 0.5F) enrage();
        return r;
    }

    private void enrage() {
        enraged = true;
        addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * 90, 1));
        addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20 * 90, 0));
        playSound(SoundEvents.RAVAGER_ROAR, 2.0F, 0.8F);
        if (level() instanceof ServerLevel sl)
            sl.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, getX(), getY() + 1, getZ(), 30, 0.5, 0.6, 0.5, 0.05);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        GhostUtil.dawnFade(this);
    }

    @Override protected SoundEvent getAmbientSound() { return SoundEvents.CAT_HISS; }
    @Override protected SoundEvent getHurtSound(DamageSource s) { return SoundEvents.OCELOT_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.OCELOT_DEATH; }
}
