package com.lisheng.slashexpansion.registry;

import com.lisheng.slashexpansion.SlashExpansion;
import com.lisheng.slashexpansion.specialeffect.FlameHeart;
import com.lisheng.slashexpansion.specialeffect.TemperedSoul;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * SE 注册。阶段一只接入烈焰之心与千锤百炼，用于验证「事件型 SE」与「刀状态型 SE」两条路径。
 */
public class SlashExpansionSpecialEffectsRegistry {

    public static final DeferredRegister<SpecialEffect> SPECIAL_EFFECTS =
            DeferredRegister.create(SpecialEffect.REGISTRY_KEY, SlashExpansion.MOD_ID);

    public static final DeferredHolder<SpecialEffect, SpecialEffect> FLAME_HEART =
            SPECIAL_EFFECTS.register("flame_heart", FlameHeart::new);

    public static final DeferredHolder<SpecialEffect, SpecialEffect> TEMPERED_SOUL =
            SPECIAL_EFFECTS.register("tempered_soul", TemperedSoul::new);
}
