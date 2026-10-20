package com.lisheng.slashexpansion.registry;

import com.lisheng.slashexpansion.SlashExpansion;
import mods.flammpfeil.slashblade.registry.combo.ComboState;
import mods.flammpfeil.slashblade.slasharts.SlashArts;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** 12 个 SA（SlashArts）注册。构造器签名与 1.20.1 相同：传入「返回 ComboState id」的函数。 */
public class SlashExpansionSlashArtsRegistry {

    public static final DeferredRegister<SlashArts> SLASH_ARTS =
            DeferredRegister.create(SlashArts.REGISTRY_KEY, SlashExpansion.MOD_ID);

    public static final DeferredHolder<SlashArts, SlashArts> WAN_JIAN_GUI_ZONG = arts("wan_jian_gui_zong", SlashExpansionComboStateRegistry.WAN_JIAN_GUI_ZONG);
    public static final DeferredHolder<SlashArts, SlashArts> SENBONZAKURA = arts("senbonzakura", SlashExpansionComboStateRegistry.SENBONZAKURA);
    public static final DeferredHolder<SlashArts, SlashArts> RIFT_SLASH = arts("rift_slash", SlashExpansionComboStateRegistry.RIFT_SLASH);
    public static final DeferredHolder<SlashArts, SlashArts> FROST_DOMAIN = arts("frost_domain", SlashExpansionComboStateRegistry.FROST_DOMAIN);
    public static final DeferredHolder<SlashArts, SlashArts> INFERNO = arts("inferno", SlashExpansionComboStateRegistry.INFERNO);
    public static final DeferredHolder<SlashArts, SlashArts> ZHAN_TIAN_BA_JIAN_SHU = arts("zhan_tian_ba_jian_shu", SlashExpansionComboStateRegistry.ZHAN_TIAN_BA_JIAN_SHU);
    public static final DeferredHolder<SlashArts, SlashArts> JIAN_DANG_BA_HUANG = arts("jian_dang_ba_huang", SlashExpansionComboStateRegistry.JIAN_DANG_BA_HUANG);
    public static final DeferredHolder<SlashArts, SlashArts> GENG_JIN_JIAN_JUE = arts("geng_jin_jian_jue", SlashExpansionComboStateRegistry.GENG_JIN_JIAN_JUE);
    public static final DeferredHolder<SlashArts, SlashArts> QI_SHA_JIAN_JIE = arts("qi_sha_jian_jie", SlashExpansionComboStateRegistry.QI_SHA_JIAN_JIE);
    public static final DeferredHolder<SlashArts, SlashArts> BLOOD_MOON_SLASH = arts("blood_moon_slash", SlashExpansionComboStateRegistry.BLOOD_MOON_SLASH);
    public static final DeferredHolder<SlashArts, SlashArts> THORS_WRATH = arts("thors_wrath", SlashExpansionComboStateRegistry.THORS_WRATH);
    public static final DeferredHolder<SlashArts, SlashArts> IMMOVABLE_KING = arts("immovable_king", SlashExpansionComboStateRegistry.IMMOVABLE_KING);

    private static DeferredHolder<SlashArts, SlashArts> arts(String id, DeferredHolder<ComboState, ComboState> combo) {
        return SLASH_ARTS.register(id, () -> new SlashArts(entity -> id(combo)));
    }

    /** DeferredHolder -> ResourceLocation。 */
    public static ResourceLocation id(DeferredHolder<?, ?> holder) {
        return holder.getKey().location();
    }
}