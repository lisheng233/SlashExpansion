package com.lisheng.slashexpansion.specialattack;

import com.lisheng.slashexpansion.config.SlashExpansionConfig;
import com.lisheng.slashexpansion.vfx.SaVfx;
import mods.flammpfeil.slashblade.util.TargetSelector;
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

    public static void doSlash(LivingEntity player, boolean critical, double damage) {
        if (player.level().isClientSide()) return;

        Level world = player.level();
        int range = SlashExpansionConfig.Sa.Inferno.radius();
        AABB aabb = player.getBoundingBox().inflate(range);

        List<LivingEntity> targets = world.getEntitiesOfClass(LivingEntity.class, aabb,
                e -> e != player && e.isAlive() && !e.isSpectator()
                        && TargetSelector.test.test(player, e));

        // ---- 动画：火环扩散 → 业火升腾 → 焚城爆发 ----
        SaVfx.inferno(player, targets, range);

        float finalDamage = (float) (damage * SlashExpansionConfig.Sa.Inferno.damageMultiplier());
        float netherMultiplier = SlashExpansionConfig.Sa.Inferno.netherDamageMultiplier();
        int fireSeconds = SlashExpansionConfig.Sa.Inferno.fireSeconds();

        for (LivingEntity target : targets) {
            float targetDamage = finalDamage;

            boolean isNetherMob = target instanceof Piglin || target instanceof Hoglin ||
                                  target instanceof Zoglin || target instanceof Strider ||
                                  target instanceof MagmaCube;
            if (isNetherMob) {
                targetDamage *= netherMultiplier;
            }

            target.hurt(player.damageSources().mobAttack(player), targetDamage);
            target.setSecondsOnFire(fireSeconds);
        }
    }
}
