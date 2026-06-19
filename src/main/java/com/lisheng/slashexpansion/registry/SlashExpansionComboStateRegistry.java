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
                                Senbonzakura.doSlash(entity, false, damage * 1.2, 1.5f); // 千本樱额外 20% 加成
                            })
                            .build())
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
                                RiftSlash.doSlash(entity, false, damage * 1.8, 2f); // 裂空斩额外 80% 加成
                            })
                            .build())
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
                                FrostDomain.doSlash(entity, false, damage * 0.8, 0); // 冰霜领域削弱 20%（因为带强控）
                            })
                            .build())
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
                                Inferno.doSlash(entity, false, damage * 1.0, 0); // 业火焚城 100% 伤害 + 持续灼烧
                            })
                            .build())
                    .build()
    );
}