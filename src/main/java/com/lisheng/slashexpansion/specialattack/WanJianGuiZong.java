package com.lisheng.slashexpansion.specialattack;

import com.lisheng.slashexpansion.entity.WanJianGuiZongSwordEntity;
import com.lisheng.slashexpansion.registry.SlashExpansionEntities;
import mods.flammpfeil.slashblade.util.TargetSelector;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import java.util.List;

public class WanJianGuiZong {

    public static void doSlash(LivingEntity player, boolean critical, double damage, float speed) {
        if (player.level().isClientSide()) return;

        Level world = player.level();
        int range = 64;
        AABB aabb = player.getBoundingBox().inflate(range);
        
        // ★ 使用 TargetSelector.getTargettableEntitiesWithinAABB
        List<Entity> entityTargets = TargetSelector.getTargettableEntitiesWithinAABB(world, player, aabb);
        List<LivingEntity> targets = entityTargets.stream()
                .filter(e -> e instanceof LivingEntity)
                .map(e -> (LivingEntity) e)
                .filter(e -> e != player && e.isAlive() && !e.isSpectator() && player.hasLineOfSight(e))
                .toList();

        if (targets.isEmpty()) return;

        // 剑数量：经验等级
        int swordCount = 10;
        if (player instanceof Player) {
            swordCount = Math.min(256, Math.max(10, ((Player) player).experienceLevel));
        }

        int perTarget = swordCount / targets.size();
        int extra = swordCount % targets.size();

        for (int idx = 0; idx < targets.size(); idx++) {
            LivingEntity target = targets.get(idx);
            int countForTarget = perTarget + (idx < extra ? 1 : 0);

            for (int i = 0; i < countForTarget; i++) {
                WanJianGuiZongSwordEntity sword = new WanJianGuiZongSwordEntity(
                        SlashExpansionEntities.WAN_JIAN_SWORD.get(), world);

                double offsetX = (world.random.nextDouble() - 0.5) * 8.0;
                double offsetZ = (world.random.nextDouble() - 0.5) * 8.0;
                double height = 15.0 + world.random.nextDouble() * 12.0;

                sword.setPos(
                        target.getX() + offsetX,
                        target.getY() + height,
                        target.getZ() + offsetZ
                );
                sword.setDamage(damage);
                sword.setIsCritical(critical);
                sword.setSpeed(speed);
                sword.setOwner(player);
                sword.fireAt(target);

                world.addFreshEntity(sword);
            }
        }
    }
}