package com.lisheng.slashexpansion.specialattack;

import com.lisheng.slashexpansion.config.SlashExpansionConfig;
import mods.flammpfeil.slashblade.util.TargetSelector;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class FrostDomain {

    public static void doSlash(LivingEntity player, boolean critical, double damage) {
        if (player.level().isClientSide()) return;

        Level world = player.level();
        int range = SlashExpansionConfig.Sa.FrostDomain.radius();
        AABB aabb = player.getBoundingBox().inflate(range);

        List<LivingEntity> targets = world.getEntitiesOfClass(LivingEntity.class, aabb,
                e -> e != player && e.isAlive() && !e.isSpectator()
                        && TargetSelector.test.test(player, e));

        if (world instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 100; i++) {
                double x = player.getX() + (world.random.nextDouble() - 0.5) * range * 2;
                double z = player.getZ() + (world.random.nextDouble() - 0.5) * range * 2;
                double y = player.getY() + world.random.nextDouble() * 3;
                serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, x, y, z, 1, 0, 0, 0, 0);
            }
        }

        float finalDamage = (float) (damage * SlashExpansionConfig.Sa.FrostDomain.damageMultiplier());

        for (LivingEntity target : targets) {
            target.hurt(player.damageSources().magic(), finalDamage);
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,
                    SlashExpansionConfig.Sa.FrostDomain.slowDuration(),
                    SlashExpansionConfig.Sa.FrostDomain.slowAmplifier()));
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,
                    SlashExpansionConfig.Sa.FrostDomain.weaknessDuration(),
                    SlashExpansionConfig.Sa.FrostDomain.weaknessAmplifier()));
            target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN,
                    SlashExpansionConfig.Sa.FrostDomain.digSlowDuration(),
                    SlashExpansionConfig.Sa.FrostDomain.digSlowAmplifier()));

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
