package com.lisheng.slashexpansion.specialattack;

import com.lisheng.slashexpansion.entity.WanJianGuiZongSwordEntity; // 复用万剑归宗的实体，但颜色为金色
import com.lisheng.slashexpansion.registry.SlashExpansionEntities;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.util.TargetSelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.server.level.ServerLevel;

import java.util.List;

public class GengJinJianJue {

    public static void doSlash(LivingEntity player, boolean critical, double damage, float speed) {
        if (player.level().isClientSide()) return;

        Level world = player.level();
        // 获取杀敌数（从刀的状态获取）
        int killCount = 0;
        int refine = 0;
        if (player.getMainHandItem().getItem() instanceof ItemSlashBlade) {
            killCount = player.getMainHandItem()
                .getCapability(ItemSlashBlade.BLADESTATE)
                .map(state -> state.getKillCount())
                .orElse(0);
            refine = player.getMainHandItem()
                .getCapability(ItemSlashBlade.BLADESTATE)
                .map(state -> state.getRefine())
                .orElse(0);
        }

        // 剑数量：最少1，最多100
        int swordCount = Math.min(100, Math.max(1, refine / 10)); // 每10杀敌1把，最多100
        // 范围：杀敌<1000为20格，否则36格
        int range = (killCount < 1000) ? 20 : 36;

        AABB aabb = player.getBoundingBox().inflate(range);
        List<LivingEntity> targets = world.getEntitiesOfClass(LivingEntity.class, aabb,
            e -> e != player && e.isAlive() && !e.isSpectator()
                && TargetSelector.test.test(player, e)
                && player.hasLineOfSight(e));

        if (targets.isEmpty()) return;

        // 分配剑数
        int perTarget = swordCount / targets.size();
        int extra = swordCount % targets.size();

        for (int idx = 0; idx < targets.size(); idx++) {
            LivingEntity target = targets.get(idx);
            int count = perTarget + (idx < extra ? 1 : 0);
            for (int i = 0; i < count; i++) {
                WanJianGuiZongSwordEntity sword = new WanJianGuiZongSwordEntity(
                    SlashExpansionEntities.WAN_JIAN_SWORD.get(), world);
                double offsetX = (world.random.nextDouble() - 0.5) * 4;
                double offsetZ = (world.random.nextDouble() - 0.5) * 4;
                double height = 10 + world.random.nextDouble() * 8;
                sword.setPos(
                    target.getX() + offsetX,
                    target.getY() + height,
                    target.getZ() + offsetZ
                );
                sword.setDamage(damage * 1.6); // 160%
                sword.setIsCritical(true);
                sword.setSpeed(speed);
                sword.setOwner(player);
                // 设置金色（0xFFD700）
                sword.setColor(0xFFD700);
                
                sword.fireAt(target);
                world.addFreshEntity(sword);
            }
        }

        // 粒子：金色闪光
        if (world instanceof ServerLevel serverLevel) {
            for (LivingEntity target : targets) {
                serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.ELECTRIC_SPARK,
                    target.getX(), target.getY() + 1, target.getZ(),
                    20, 1, 1, 1, 0.2);
            }
        }
    }
}