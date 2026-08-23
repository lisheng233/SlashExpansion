package com.lisheng.slashexpansion.registry;

import com.lisheng.slashexpansion.SlashExpansion;
import com.lisheng.slashexpansion.specialattack.*;
import com.lisheng.slashexpansion.util.DamageCalculator;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.init.DefaultResources;
import mods.flammpfeil.slashblade.registry.combo.ComboState;
import mods.flammpfeil.slashblade.util.AttackManager;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class SlashExpansionComboStateRegistry {

    public static final DeferredRegister<ComboState> COMBO_STATES =
            DeferredRegister.create(ComboState.REGISTRY_KEY, SlashExpansion.MOD_ID);

    // ===== 万剑归宗 =====
    public static final RegistryObject<ComboState> WAN_JIAN_GUI_ZONG = COMBO_STATES.register(
            "wan_jian_gui_zong",
            () -> ComboState.Builder.newInstance()
                    .startAndEnd(400, 459)
                    .priority(50)
                    .motionLoc(DefaultResources.ExMotionLocation)
                    .next(ComboState.TimeoutNext.buildFromFrame(15, entity -> SlashBlade.prefix("none")))
                    .nextOfTimeout(entity -> SlashExpansion.prefix("wan_jian_gui_zong_end"))
                    .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                            .put(2, (entity) -> AttackManager.doSlash(entity, -30F, Vec3.ZERO, false, false, 0.1F))
                            .put(3, (entity) -> {
                                float damage = DamageCalculator.calculateDamage(entity, entity.getMainHandItem());
                                WanJianGuiZong.doSlash(entity, false, damage * 0.8f, 2f);
                            })
                            .build())
                    .build()
    );
    public static final RegistryObject<ComboState> WAN_JIAN_GUI_ZONG_END = COMBO_STATES.register(
            "wan_jian_gui_zong_end",
            () -> ComboState.Builder.newInstance()
                    .startAndEnd(459, 488)
                    .priority(50)
                    .motionLoc(DefaultResources.ExMotionLocation)
                    .next(entity -> SlashBlade.prefix("none"))
                    .nextOfTimeout(entity -> SlashBlade.prefix("none"))
                    .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                            .put(0, AttackManager::playQuickSheathSoundAction)
                            .build())
                    .releaseAction(ComboState::releaseActionQuickCharge)
                    .build()
    );

    // ===== 千本樱 =====
    public static final RegistryObject<ComboState> SENBONZAKURA = COMBO_STATES.register(
            "senbonzakura",
            () -> ComboState.Builder.newInstance()
                    .startAndEnd(400, 459)
                    .priority(50)
                    .motionLoc(DefaultResources.ExMotionLocation)
                    .next(ComboState.TimeoutNext.buildFromFrame(15, entity -> SlashBlade.prefix("none")))
                    .nextOfTimeout(entity -> SlashExpansion.prefix("senbonzakura_end"))
                    .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                            .put(2, (entity) -> AttackManager.doSlash(entity, -30F, Vec3.ZERO, false, false, 0.1F))
                            .put(3, (entity) -> {
                                float damage = DamageCalculator.calculateDamage(entity, entity.getMainHandItem());
                                Senbonzakura.doSlash(entity, false, damage * 1.2, 1.5f);
                            })
                            .build())
                    .build()
    );
    public static final RegistryObject<ComboState> SENBONZAKURA_END = COMBO_STATES.register(
            "senbonzakura_end",
            () -> ComboState.Builder.newInstance()
                    .startAndEnd(459, 488)
                    .priority(50)
                    .motionLoc(DefaultResources.ExMotionLocation)
                    .next(entity -> SlashBlade.prefix("none"))
                    .nextOfTimeout(entity -> SlashBlade.prefix("none"))
                    .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                            .put(0, AttackManager::playQuickSheathSoundAction)
                            .build())
                    .releaseAction(ComboState::releaseActionQuickCharge)
                    .build()
    );

    // ===== 裂空斩 =====
    public static final RegistryObject<ComboState> RIFT_SLASH = COMBO_STATES.register(
            "rift_slash",
            () -> ComboState.Builder.newInstance()
                    .startAndEnd(400, 459)
                    .priority(50)
                    .motionLoc(DefaultResources.ExMotionLocation)
                    .next(ComboState.TimeoutNext.buildFromFrame(15, entity -> SlashBlade.prefix("none")))
                    .nextOfTimeout(entity -> SlashExpansion.prefix("rift_slash_end"))
                    .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                            .put(2, (entity) -> AttackManager.doSlash(entity, -30F, Vec3.ZERO, false, false, 0.1F))
                            .put(3, (entity) -> {
                                float damage = DamageCalculator.calculateDamage(entity, entity.getMainHandItem());
                                RiftSlash.doSlash(entity, false, damage * 1.8, 2f);
                            })
                            .build())
                    .build()
    );
    public static final RegistryObject<ComboState> RIFT_SLASH_END = COMBO_STATES.register(
            "rift_slash_end",
            () -> ComboState.Builder.newInstance()
                    .startAndEnd(459, 488)
                    .priority(50)
                    .motionLoc(DefaultResources.ExMotionLocation)
                    .next(entity -> SlashBlade.prefix("none"))
                    .nextOfTimeout(entity -> SlashBlade.prefix("none"))
                    .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                            .put(0, AttackManager::playQuickSheathSoundAction)
                            .build())
                    .releaseAction(ComboState::releaseActionQuickCharge)
                    .build()
    );

    // ===== 冰霜领域 =====
    public static final RegistryObject<ComboState> FROST_DOMAIN = COMBO_STATES.register(
            "frost_domain",
            () -> ComboState.Builder.newInstance()
                    .startAndEnd(400, 459)
                    .priority(50)
                    .motionLoc(DefaultResources.ExMotionLocation)
                    .next(ComboState.TimeoutNext.buildFromFrame(15, entity -> SlashBlade.prefix("none")))
                    .nextOfTimeout(entity -> SlashExpansion.prefix("frost_domain_end"))
                    .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                            .put(2, (entity) -> AttackManager.doSlash(entity, -30F, Vec3.ZERO, false, false, 0.1F))
                            .put(3, (entity) -> {
                                float damage = DamageCalculator.calculateDamage(entity, entity.getMainHandItem());
                                FrostDomain.doSlash(entity, false, damage * 0.8, 0);
                            })
                            .build())
                    .build()
    );
    public static final RegistryObject<ComboState> FROST_DOMAIN_END = COMBO_STATES.register(
            "frost_domain_end",
            () -> ComboState.Builder.newInstance()
                    .startAndEnd(459, 488)
                    .priority(50)
                    .motionLoc(DefaultResources.ExMotionLocation)
                    .next(entity -> SlashBlade.prefix("none"))
                    .nextOfTimeout(entity -> SlashBlade.prefix("none"))
                    .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                            .put(0, AttackManager::playQuickSheathSoundAction)
                            .build())
                    .releaseAction(ComboState::releaseActionQuickCharge)
                    .build()
    );

    // ===== 业火焚城 =====
    public static final RegistryObject<ComboState> INFERNO = COMBO_STATES.register(
            "inferno",
            () -> ComboState.Builder.newInstance()
                    .startAndEnd(400, 459)
                    .priority(50)
                    .motionLoc(DefaultResources.ExMotionLocation)
                    .next(ComboState.TimeoutNext.buildFromFrame(15, entity -> SlashBlade.prefix("none")))
                    .nextOfTimeout(entity -> SlashExpansion.prefix("inferno_end"))
                    .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                            .put(2, (entity) -> AttackManager.doSlash(entity, -30F, Vec3.ZERO, false, false, 0.1F))
                            .put(3, (entity) -> {
                                float damage = DamageCalculator.calculateDamage(entity, entity.getMainHandItem());
                                Inferno.doSlash(entity, false, damage * 1.0, 0);
                            })
                            .build())
                    .build()
    );
    public static final RegistryObject<ComboState> INFERNO_END = COMBO_STATES.register(
            "inferno_end",
            () -> ComboState.Builder.newInstance()
                    .startAndEnd(459, 488)
                    .priority(50)
                    .motionLoc(DefaultResources.ExMotionLocation)
                    .next(entity -> SlashBlade.prefix("none"))
                    .nextOfTimeout(entity -> SlashBlade.prefix("none"))
                    .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                            .put(0, AttackManager::playQuickSheathSoundAction)
                            .build())
                    .releaseAction(ComboState::releaseActionQuickCharge)
                    .build()
    );

    // ================================================================
    // ★★★ 新增 4 个 SA ★★★
    // ================================================================

    // ===== 斩天拔剑术 =====
    public static final RegistryObject<ComboState> ZHAN_TIAN_BA_JIAN_SHU = COMBO_STATES.register(
            "zhan_tian_ba_jian_shu",
            () -> ComboState.Builder.newInstance()
                    .startAndEnd(400, 459)
                    .priority(50)
                    .motionLoc(DefaultResources.ExMotionLocation)
                    .next(ComboState.TimeoutNext.buildFromFrame(15, entity -> SlashBlade.prefix("none")))
                    .nextOfTimeout(entity -> SlashExpansion.prefix("zhan_tian_ba_jian_shu_end"))
                    .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                            .put(2, (entity) -> AttackManager.doSlash(entity, -30F, Vec3.ZERO, false, false, 0.1F))
                            .put(3, (entity) -> {
                                float damage = DamageCalculator.calculateDamage(entity, entity.getMainHandItem());
                                ZhanTianBaJianShu.doSlash(entity, false, damage, 5f);
                            })
                            .build())
                    .build()
    );
    public static final RegistryObject<ComboState> ZHAN_TIAN_BA_JIAN_SHU_END = COMBO_STATES.register(
            "zhan_tian_ba_jian_shu_end",
            () -> ComboState.Builder.newInstance()
                    .startAndEnd(459, 488)
                    .priority(50)
                    .motionLoc(DefaultResources.ExMotionLocation)
                    .next(entity -> SlashBlade.prefix("none"))
                    .nextOfTimeout(entity -> SlashBlade.prefix("none"))
                    .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                            .put(0, AttackManager::playQuickSheathSoundAction)
                            .build())
                    .releaseAction(ComboState::releaseActionQuickCharge)
                    .build()
    );

    // ===== 剑荡八荒 =====
    public static final RegistryObject<ComboState> JIAN_DANG_BA_HUANG = COMBO_STATES.register(
            "jian_dang_ba_huang",
            () -> ComboState.Builder.newInstance()
                    .startAndEnd(400, 459)
                    .priority(50)
                    .motionLoc(DefaultResources.ExMotionLocation)
                    .next(ComboState.TimeoutNext.buildFromFrame(15, entity -> SlashBlade.prefix("none")))
                    .nextOfTimeout(entity -> SlashExpansion.prefix("jian_dang_ba_huang_end"))
                    .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                            .put(2, (entity) -> AttackManager.doSlash(entity, -30F, Vec3.ZERO, false, false, 0.1F))
                            .put(3, (entity) -> {
                                float damage = DamageCalculator.calculateDamage(entity, entity.getMainHandItem());
                                JianDangBaHuang.doSlash(entity, false, damage, 5f);
                            })
                            .build())
                    .build()
    );
    public static final RegistryObject<ComboState> JIAN_DANG_BA_HUANG_END = COMBO_STATES.register(
            "jian_dang_ba_huang_end",
            () -> ComboState.Builder.newInstance()
                    .startAndEnd(459, 488)
                    .priority(50)
                    .motionLoc(DefaultResources.ExMotionLocation)
                    .next(entity -> SlashBlade.prefix("none"))
                    .nextOfTimeout(entity -> SlashBlade.prefix("none"))
                    .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                            .put(0, AttackManager::playQuickSheathSoundAction)
                            .build())
                    .releaseAction(ComboState::releaseActionQuickCharge)
                    .build()
    );

    // ===== 庚金剑诀 =====
    public static final RegistryObject<ComboState> GENG_JIN_JIAN_JUE = COMBO_STATES.register(
            "geng_jin_jian_jue",
            () -> ComboState.Builder.newInstance()
                    .startAndEnd(400, 459)
                    .priority(50)
                    .motionLoc(DefaultResources.ExMotionLocation)
                    .next(ComboState.TimeoutNext.buildFromFrame(15, entity -> SlashBlade.prefix("none")))
                    .nextOfTimeout(entity -> SlashExpansion.prefix("geng_jin_jian_jue_end"))
                    .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                            .put(2, (entity) -> AttackManager.doSlash(entity, -30F, Vec3.ZERO, false, false, 0.1F))
                            .put(3, (entity) -> {
                                float damage = DamageCalculator.calculateDamage(entity, entity.getMainHandItem());
                                GengJinJianJue.doSlash(entity, false, damage, 2f);
                            })
                            .build())
                    .build()
    );
    public static final RegistryObject<ComboState> GENG_JIN_JIAN_JUE_END = COMBO_STATES.register(
            "geng_jin_jian_jue_end",
            () -> ComboState.Builder.newInstance()
                    .startAndEnd(459, 488)
                    .priority(50)
                    .motionLoc(DefaultResources.ExMotionLocation)
                    .next(entity -> SlashBlade.prefix("none"))
                    .nextOfTimeout(entity -> SlashBlade.prefix("none"))
                    .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                            .put(0, AttackManager::playQuickSheathSoundAction)
                            .build())
                    .releaseAction(ComboState::releaseActionQuickCharge)
                    .build()
    );

    // ===== 七杀剑界 =====
    public static final RegistryObject<ComboState> QI_SHA_JIAN_JIE = COMBO_STATES.register(
            "qi_sha_jian_jie",
            () -> ComboState.Builder.newInstance()
                    .startAndEnd(400, 459)
                    .priority(50)
                    .motionLoc(DefaultResources.ExMotionLocation)
                    .next(ComboState.TimeoutNext.buildFromFrame(15, entity -> SlashBlade.prefix("none")))
                    .nextOfTimeout(entity -> SlashExpansion.prefix("qi_sha_jian_jie_end"))
                    .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                            .put(2, (entity) -> AttackManager.doSlash(entity, -30F, Vec3.ZERO, false, false, 0.1F))
                            .put(3, (entity) -> {
                                float damage = DamageCalculator.calculateDamage(entity, entity.getMainHandItem());
                                QiShaJianJie.doSlash(entity, false, damage, 0);
                            })
                            .build())
                    .build()
    );
    public static final RegistryObject<ComboState> QI_SHA_JIAN_JIE_END = COMBO_STATES.register(
            "qi_sha_jian_jie_end",
            () -> ComboState.Builder.newInstance()
                    .startAndEnd(459, 488)
                    .priority(50)
                    .motionLoc(DefaultResources.ExMotionLocation)
                    .next(entity -> SlashBlade.prefix("none"))
                    .nextOfTimeout(entity -> SlashBlade.prefix("none"))
                    .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                            .put(0, AttackManager::playQuickSheathSoundAction)
                            .build())
                    .releaseAction(ComboState::releaseActionQuickCharge)
                    .build()
    );
}