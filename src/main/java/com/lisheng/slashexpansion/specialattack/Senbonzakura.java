package com.lisheng.slashexpansion.specialattack;

import com.lisheng.slashexpansion.entity.SenbonzakuraSwordEntity;
import com.lisheng.slashexpansion.registry.SlashExpansionEntities;
import mods.flammpfeil.slashblade.util.TargetSelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import java.util.List;

public class Senbonzakura {

    public static void doSlash(LivingEntity player, boolean critical, double damage, float speed) {
        if (player.level().isClientSide()) return;

        Level world = player.level();
        int range = 30;
        AABB aabb = player.getBoundingBox().inflate(range);

        // ★ 直接用 TargetSelector.test 过滤
        List<LivingEntity> targets = world.getEntitiesOfClass(LivingEntity.class, aabb,
                e -> e != player && e.isAlive() && !e.isSpectator()
                        && TargetSelector.test.test(player, e));

        if (targets.isEmpty()) return;

        for (LivingEntity target : targets) {
            int count = 4 + world.random.nextInt(3);
            for (int i = 0; i < count; i++) {
                SenbonzakuraSwordEntity sword = new SenbonzakuraSwordEntity(
                        SlashExpansionEntities.SENBONZAKURA_SWORD.get(), world);

                double angle = world.random.nextDouble() * Math.PI * 2;
                double radius = 3 + world.random.nextDouble() * 2;
                double x = target.getX() + Math.cos(angle) * radius;
                double z = target.getZ() + Math.sin(angle) * radius;
                double y = target.getY() + 10 + world.random.nextDouble() * 8;

                sword.setPos(x, y, z);
                sword.setDamage(damage);
                sword.setIsCritical(critical);
                sword.setOwner(player);

                Vec3 targetPos = target.getEyePosition();
                Vec3 direction = targetPos.subtract(sword.position()).normalize();
                sword.shoot(direction.x, direction.y, direction.z, speed, 0.5f);

                world.addFreshEntity(sword);
            }
        }
    }
}