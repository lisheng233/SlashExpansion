package com.lisheng.slashexpansion.util;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

/** 负面效果清理（管理员之力 / 虚空之力共用）。 */
public final class EffectCleanup {

    public static void removeNegativeEffects(Player player) {
        List<Holder<MobEffect>> toRemove = new ArrayList<>();
        for (MobEffectInstance instance : player.getActiveEffects()) {
            if (!instance.getEffect().value().isBeneficial()) {
                toRemove.add(instance.getEffect());
            }
        }
        for (Holder<MobEffect> effect : toRemove) {
            player.removeEffect(effect);
        }
    }

    private EffectCleanup() {
    }
}