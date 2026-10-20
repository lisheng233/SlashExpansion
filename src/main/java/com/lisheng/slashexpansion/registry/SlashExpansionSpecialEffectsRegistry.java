package com.lisheng.slashexpansion.registry;

import com.lisheng.slashexpansion.SlashExpansion;
import com.lisheng.slashexpansion.specialeffect.AbyssWhisper;
import com.lisheng.slashexpansion.specialeffect.Admin;
import com.lisheng.slashexpansion.specialeffect.DragonSoulResonance;
import com.lisheng.slashexpansion.specialeffect.ElvenBlessing;
import com.lisheng.slashexpansion.specialeffect.FlameHeart;
import com.lisheng.slashexpansion.specialeffect.FrostGlow;
import com.lisheng.slashexpansion.specialeffect.GrimReapersPact;
import com.lisheng.slashexpansion.specialeffect.TemperedSoul;
import com.lisheng.slashexpansion.specialeffect.TranscendentWill;
import com.lisheng.slashexpansion.specialeffect.VoidPower;
import com.lisheng.slashexpansion.specialeffect.VoidWalker;
import com.lisheng.slashexpansion.specialeffect.WindGodPower;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * SE 注册。
 *
 * <p>管理员之力（admin）依赖 1.21 的 Mixin 与移除/生命值拦截，单独在后续批次接入。</p>
 */
public class SlashExpansionSpecialEffectsRegistry {

    public static final DeferredRegister<SpecialEffect> SPECIAL_EFFECTS =
            DeferredRegister.create(SpecialEffect.REGISTRY_KEY, SlashExpansion.MOD_ID);

    public static final DeferredHolder<SpecialEffect, SpecialEffect> ADMIN =
            SPECIAL_EFFECTS.register("admin", Admin::new);
    public static final DeferredHolder<SpecialEffect, SpecialEffect> WIND_GOD_POWER =
            SPECIAL_EFFECTS.register("wind_god_power", WindGodPower::new);
    public static final DeferredHolder<SpecialEffect, SpecialEffect> FROST_GLOW =
            SPECIAL_EFFECTS.register("frost_glow", FrostGlow::new);
    public static final DeferredHolder<SpecialEffect, SpecialEffect> TRANSCENDENT_WILL =
            SPECIAL_EFFECTS.register("transcendent_will", TranscendentWill::new);
    public static final DeferredHolder<SpecialEffect, SpecialEffect> ABYSS_WHISPER =
            SPECIAL_EFFECTS.register("abyss_whisper", AbyssWhisper::new);
    public static final DeferredHolder<SpecialEffect, SpecialEffect> VOID_POWER =
            SPECIAL_EFFECTS.register("void_power", VoidPower::new);
    public static final DeferredHolder<SpecialEffect, SpecialEffect> FLAME_HEART =
            SPECIAL_EFFECTS.register("flame_heart", FlameHeart::new);
    public static final DeferredHolder<SpecialEffect, SpecialEffect> VOID_WALKER =
            SPECIAL_EFFECTS.register("void_walker", VoidWalker::new);
    public static final DeferredHolder<SpecialEffect, SpecialEffect> TEMPERED_SOUL =
            SPECIAL_EFFECTS.register("tempered_soul", TemperedSoul::new);
    public static final DeferredHolder<SpecialEffect, SpecialEffect> GRIM_REAPERS_PACT =
            SPECIAL_EFFECTS.register("grim_reapers_pact", GrimReapersPact::new);
    public static final DeferredHolder<SpecialEffect, SpecialEffect> ELVEN_BLESSING =
            SPECIAL_EFFECTS.register("elven_blessing", ElvenBlessing::new);
    public static final DeferredHolder<SpecialEffect, SpecialEffect> DRAGON_SOUL_RESONANCE =
            SPECIAL_EFFECTS.register("dragon_soul_resonance", DragonSoulResonance::new);
}