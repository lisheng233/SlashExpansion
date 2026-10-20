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

/**
 * 冰霜领域：范围冻结 + 迟缓/虚弱/挖掘疲劳。
 *
 * <p>阶段一先用原版粒子做反馈；阶段二接回 {@code vfx} 包的自定义粒子动画。</p>
 */
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
            // TODO(phase2): 换成 SaVfx.frostDomain(...) 的分阶段动画
            serverLevel.sendParticles(ParticleTypes.SNOWFLAKE,
                    player.getX(), player.getY() + 1.0, player.getZ(),
                    60, range * 0.6, 1.5, range * 0.6, 0.02);
            serverLevel.sendParticles(ParticleTypes.ITEM_SNOWBALL,
                    player.getX(), player.getY() + 0.5, player.getZ(),
                    20, range * 0.4, 0.4, range * 0.4, 0.05);
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
        }
    }
}