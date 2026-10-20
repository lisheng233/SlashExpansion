package com.lisheng.slashexpansion.registry;

import com.lisheng.slashexpansion.SlashExpansion;
import com.lisheng.slashexpansion.specialeffect.*;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class SlashExpansionSpecialEffectsRegistry {

    public static final DeferredRegister<SpecialEffect> SPECIAL_EFFECTS =
            DeferredRegister.create(SpecialEffect.REGISTRY_KEY, SlashExpansion.MOD_ID);

    public static final RegistryObject<SpecialEffect> WIND_GOD_POWER =
            SPECIAL_EFFECTS.register("wind_god_power", () -> new WindGodPower());

    public static final RegistryObject<SpecialEffect> FROST_GLOW =
            SPECIAL_EFFECTS.register("frost_glow", () -> new FrostGlow());

    public static final RegistryObject<SpecialEffect> TRANSCENDENT_WILL =
            SPECIAL_EFFECTS.register("transcendent_will", () -> new TranscendentWill());

    public static final RegistryObject<SpecialEffect> ABYSS_WHISPER =
            SPECIAL_EFFECTS.register("abyss_whisper", () -> new AbyssWhisper());

    public static final RegistryObject<SpecialEffect> ADMIN =
            SPECIAL_EFFECTS.register("admin", () -> new Admin());

    public static final RegistryObject<SpecialEffect> VOID_POWER = 
            SPECIAL_EFFECTS.register("void_power", () -> new VoidPower());

    // ================================================================
    // ★★★ 1.6 新增 6 个 SE ★★★
    // ================================================================

    public static final RegistryObject<SpecialEffect> FLAME_HEART =
            SPECIAL_EFFECTS.register("flame_heart", () -> new FlameHeart());

    public static final RegistryObject<SpecialEffect> VOID_WALKER =
            SPECIAL_EFFECTS.register("void_walker", () -> new VoidWalker());

    public static final RegistryObject<SpecialEffect> TEMPERED_SOUL =
            SPECIAL_EFFECTS.register("tempered_soul", () -> new TemperedSoul());

    public static final RegistryObject<SpecialEffect> GRIM_REAPERS_PACT =
            SPECIAL_EFFECTS.register("grim_reapers_pact", () -> new GrimReapersPact());

    public static final RegistryObject<SpecialEffect> ELVEN_BLESSING =
            SPECIAL_EFFECTS.register("elven_blessing", () -> new ElvenBlessing());

    public static final RegistryObject<SpecialEffect> DRAGON_SOUL_RESONANCE =
            SPECIAL_EFFECTS.register("dragon_soul_resonance", () -> new DragonSoulResonance());

}