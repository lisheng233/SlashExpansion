package com.lisheng.slashexpansion.specialattack;

import com.lisheng.slashexpansion.config.SlashExpansionConfig;
import com.lisheng.slashexpansion.entity.WanJianGuiZongSwordEntity;
import com.lisheng.slashexpansion.registry.SlashExpansionEntities;
import mods.flammpfeil.slashblade.util.TargetSelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class WanJianGuiZong {

    public static void doSlash(LivingEntity player, boolean critical, double damage) {
        if (player.level().isClientSide()) return;

        Level world = player.level();
        int range = SlashExpansionConfig.Sa.WanJianGuiZong.searchRange();
        AABB aabb = player.getBoundingBox().inflate(range);

        List<LivingEntity> targets = world.getEntitiesOfClass(LivingEntity.class, aabb,
                e -> e != player && e.isAlive() && !e.isSpectator()
                        && TargetSelector.test.test(player, e)
                        && player.hasLineOfSight(e));

        if (targets.isEmpty()) return;

        int minSwords = SlashExpansionConfig.Sa.WanJianGuiZong.minSwords();
        int maxSwords = SlashExpansionConfig.Sa.WanJianGuiZong.maxSwords();
        int swordCount = minSwords;
        if (player instanceof Player p) {
            swordCount = Math.min(maxSwords, Math.max(minSwords, p.experienceLevel));
        }

        float finalDamage = (float) (damage * SlashExpansionConfig.Sa.WanJianGuiZong.damageMultiplier());
        float speed = SlashExpansionConfig.Sa.WanJianGuiZong.projectileSpeed();

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
                sword.setDamage(finalDamage);
                sword.setIsCritical(critical);
                sword.setSpeed(speed);
                sword.setOwner(player);
                sword.fireAt(target);

                world.addFreshEntity(sword);
            }
        }
    }
}
