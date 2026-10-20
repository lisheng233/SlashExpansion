package com.lisheng.slashexpansion.specialattack;

import com.lisheng.slashexpansion.config.SlashExpansionConfig;
import com.lisheng.slashexpansion.vfx.SaVfx;
import mods.flammpfeil.slashblade.util.TargetSelector;
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

        // ---- 动画：冰霜法阵 → 冰棱拔地而起 → 极寒漩涡 ----
        SaVfx.frostDomain(player, targets, range);

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
