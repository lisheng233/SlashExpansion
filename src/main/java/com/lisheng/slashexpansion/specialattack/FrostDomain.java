package com.lisheng.slashexpansion.specialattack;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import java.util.List;

public class FrostDomain {

    public static void doSlash(LivingEntity player, boolean critical, double damage, float speed) {
        if (player.level().isClientSide()) return;

        Level world = player.level();
        int range = 15;
        AABB aabb = player.getBoundingBox().inflate(range);
        List<LivingEntity> targets = world.getEntitiesOfClass(LivingEntity.class, aabb,
                e -> e != player && e.isAlive() && !e.isSpectator());

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
            // 魔法伤害
            target.hurt(player.damageSources().magic(), (float) damage);

            // 效果：迟缓 x（120秒）、虚弱 II（100秒）、挖掘疲劳（80秒）
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120, 9));
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 1));
            target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 80, 2));

            // 冰锥粒子
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