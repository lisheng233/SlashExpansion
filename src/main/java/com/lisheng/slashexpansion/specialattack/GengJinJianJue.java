package com.lisheng.slashexpansion.specialattack;

import com.lisheng.slashexpansion.config.SlashExpansionConfig;
import com.lisheng.slashexpansion.entity.WanJianGuiZongSwordEntity;
import com.lisheng.slashexpansion.registry.SlashExpansionEntities;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.util.TargetSelector;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class GengJinJianJue {

    public static void doSlash(LivingEntity player, boolean critical, double damage) {
        if (player.level().isClientSide()) return;

        Level world = player.level();

        // 获取杀敌数与锻造数（从刀的状态获取）
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

        int minSwords = SlashExpansionConfig.Sa.GengJinJianJue.minSwords();
        int maxSwords = SlashExpansionConfig.Sa.GengJinJianJue.maxSwords();
        int perSword = Math.max(1, SlashExpansionConfig.Sa.GengJinJianJue.refinePerSword());
        int swordCount = Math.min(maxSwords, Math.max(minSwords, refine / perSword));

        int range = killCount < SlashExpansionConfig.Sa.GengJinJianJue.farKillThreshold()
                ? SlashExpansionConfig.Sa.GengJinJianJue.nearRange()
                : SlashExpansionConfig.Sa.GengJinJianJue.farRange();

        AABB aabb = player.getBoundingBox().inflate(range);
        List<LivingEntity> targets = world.getEntitiesOfClass(LivingEntity.class, aabb,
                e -> e != player && e.isAlive() && !e.isSpectator()
                        && TargetSelector.test.test(player, e)
                        && player.hasLineOfSight(e));

        if (targets.isEmpty()) return;

        float finalDamage = (float) (damage * SlashExpansionConfig.Sa.GengJinJianJue.damageMultiplier());
        float speed = SlashExpansionConfig.Sa.GengJinJianJue.projectileSpeed();

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
                sword.setDamage(finalDamage);
                sword.setIsCritical(true);
                sword.setSpeed(speed);
                sword.setOwner(player);
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
