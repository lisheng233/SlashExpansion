package com.lisheng.slashexpansion.specialattack;

import mods.flammpfeil.slashblade.util.TargetSelector;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import java.util.List;

public class FrostDomain {

    public static void doSlash(LivingEntity player, boolean critical, double damage, float speed) {
        if (player.level().isClientSide()) return;

        Level world = player.level();
        int range = 24;
        AABB aabb = player.getBoundingBox().inflate(range);
        
        // ★ 使用 TargetSelector
        List<Entity> entityTargets = TargetSelector.getTargettableEntitiesWithinAABB(world, player, aabb);
        List<LivingEntity> targets = entityTargets.stream()
                .filter(e -> e instanceof LivingEntity)
                .map(e -> (LivingEntity) e)
                .filter(e -> e != player && e.isAlive() && !e.isSpectator())
                .toList();

        // 冰霜粒子领域
        if (world instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 100; i++) {
                double x = player.getX() + (world.random.nextDouble() - 0.5) * range * 2;
                double z = player.getZ() + (world.random.nextDouble() - 0.5) * range * 2;
                double y = player.getY() + world.random.nextDouble() * 3;
                serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, x, y, z, 1, 0, 0, 0, 0);
            }
        }

        for (LivingEntity target : targets) {
            target.hurt(player.damageSources().magic(), (float) damage);
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120, 3));
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 1));
            target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 80, 3));

            if (world instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SNOWFLAKE,
                        target.getX(), target.getY() + 1, target.getZ(),
                        30, 0.5, 1, 0.5, 0.1);
                serverLevel.sendParticles(ParticleTypes.ITEM_SNOWBALL,
                        target.getX(), target.getY() + 2, target.getZ(),
                        5, 0.3, 0.5, 0.3, 0.05);
            }
        }
    }
}