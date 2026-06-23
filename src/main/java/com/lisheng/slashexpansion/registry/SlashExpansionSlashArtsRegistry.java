package com.lisheng.slashexpansion.registry;

import com.lisheng.slashexpansion.SlashExpansion;
import mods.flammpfeil.slashblade.slasharts.SlashArts;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class SlashExpansionSlashArtsRegistry {

    public static final DeferredRegister<SlashArts> SLASH_ARTS =
            DeferredRegister.create(SlashArts.REGISTRY_KEY, SlashExpansion.MOD_ID);

    // ===== 万剑归宗 =====
    public static final RegistryObject<SlashArts> WAN_JIAN_GUI_ZONG =
            SLASH_ARTS.register("wan_jian_gui_zong",
                    () -> new SlashArts((e) -> SlashExpansionComboStateRegistry.WAN_JIAN_GUI_ZONG.getId()));

    // ===== 千本樱 =====
    public static final RegistryObject<SlashArts> SENBONZAKURA =
            SLASH_ARTS.register("senbonzakura",
                    () -> new SlashArts((e) -> SlashExpansionComboStateRegistry.SENBONZAKURA.getId()));

    // ===== 裂空斩 =====
    public static final RegistryObject<SlashArts> RIFT_SLASH =
            SLASH_ARTS.register("rift_slash",
                    () -> new SlashArts((e) -> SlashExpansionComboStateRegistry.RIFT_SLASH.getId()));

    // ===== 冰霜领域 =====
    public static final RegistryObject<SlashArts> FROST_DOMAIN =
            SLASH_ARTS.register("frost_domain",
                    () -> new SlashArts((e) -> SlashExpansionComboStateRegistry.FROST_DOMAIN.getId()));

    // ===== 业火焚城 =====
    public static final RegistryObject<SlashArts> INFERNO =
            SLASH_ARTS.register("inferno",
                    () -> new SlashArts((e) -> SlashExpansionComboStateRegistry.INFERNO.getId()));

    // ================================================================
    // ★★★ 新增 4 个 SA ★★★
    // ================================================================

    // ===== 斩天拔剑术 =====
    public static final RegistryObject<SlashArts> ZHAN_TIAN_BA_JIAN_SHU =
            SLASH_ARTS.register("zhan_tian_ba_jian_shu",
                    () -> new SlashArts((e) -> SlashExpansionComboStateRegistry.ZHAN_TIAN_BA_JIAN_SHU.getId()));

    // ===== 剑荡八荒 =====
    public static final RegistryObject<SlashArts> JIAN_DANG_BA_HUANG =
            SLASH_ARTS.register("jian_dang_ba_huang",
                    () -> new SlashArts((e) -> SlashExpansionComboStateRegistry.JIAN_DANG_BA_HUANG.getId()));

    // ===== 庚金剑诀 =====
    public static final RegistryObject<SlashArts> GENG_JIN_JIAN_JUE =
            SLASH_ARTS.register("geng_jin_jian_jue",
                    () -> new SlashArts((e) -> SlashExpansionComboStateRegistry.GENG_JIN_JIAN_JUE.getId()));

    // ===== 七杀剑界 =====
    public static final RegistryObject<SlashArts> QI_SHA_JIAN_JIE =
            SLASH_ARTS.register("qi_sha_jian_jie",
                    () -> new SlashArts((e) -> SlashExpansionComboStateRegistry.QI_SHA_JIAN_JIE.getId()));
}