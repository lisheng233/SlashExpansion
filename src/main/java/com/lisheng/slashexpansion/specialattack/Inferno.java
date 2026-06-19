package com.lisheng.slashexpansion.specialattack;

import mods.flammpfeil.slashblade.util.TargetSelector;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import java.util.List;

public class Inferno {

    public static void doSlash(LivingEntity player, boolean critical, double damage, float speed) {
        if (player.level().isClientSide()) return;

        Level world = player.level();
        int range = 18;
        AABB aabb = player.getBoundingBox().inflate(range);

        // ★ 直接用 TargetSelector.test 过滤
        List<LivingEntity> targets = world.getEntitiesOfClass(LivingEntity.class, aabb,
                e -> e != player && e.isAlive() && !e.isSpectator()
                        && TargetSelector.test.test(player, e));

        if (world instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 200; i++) {
                double radius = 2 + world.random.nextDouble() * range;
                double angle = world.random.nextDouble() * Math.PI * 2;
                double x = player.getX() + Math.cos(angle) * radius;
                double z = player.getZ() + Math.sin(angle) * radius;
                double y = player.getY() + world.random.nextDouble() * 2;
                serverLevel.sendParticles(ParticleTypes.FLAME, x, y, z, 1, 0.2, 0.5, 0.2, 0.1);
                serverLevel.sendParticles(ParticleTypes.LAVA, x, y, z, 1, 0, 0, 0, 0);
            }
        }

        for (LivingEntity target : targets) {
            float finalDamage = (float) damage;

            boolean isNetherMob = target instanceof Piglin || target instanceof Hoglin ||
                                  target instanceof Zoglin || target instanceof Strider ||
                                  target instanceof MagmaCube;
            if (isNetherMob) {
                finalDamage *= 1.5f;
            }

            target.hurt(player.damageSources().mobAttack(player), finalDamage);
            target.setSecondsOnFire(12);

            if (world instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER,
                        target.getX(), target.getY(), target.getZ(), 1, 0, 0, 0, 0);
                serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                        target.getX(), target.getY() + 0.5, target.getZ(),
                        10, 0.5, 0.5, 0.5, 0.1);
            }
        }
    }
}