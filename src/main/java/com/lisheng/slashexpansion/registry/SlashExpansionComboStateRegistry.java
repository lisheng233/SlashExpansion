package com.lisheng.slashexpansion.registry;

import com.lisheng.slashexpansion.SlashExpansion;
import com.lisheng.slashexpansion.specialattack.RiftSlash;
import com.lisheng.slashexpansion.specialattack.FrostDomain;
import com.lisheng.slashexpansion.util.DamageCalculator;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.init.DefaultResources;
import mods.flammpfeil.slashblade.registry.combo.ComboState;
import mods.flammpfeil.slashblade.util.AttackManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Consumer;

/**
 * SA 的 ComboState 注册。
 *
 * <p>每个 SA 占两个状态：{@code <id>} 是出招动作（第 3 tick 触发技能本体），
 * {@code <id>_end} 是收刀动作。1.21.1 的 {@code ComboState.Builder} 与 1.20.1 完全一致，
 * 因此这里只有注册 API 从 {@code RegistryObject} 换成了 {@code DeferredHolder}。</p>
 *
 * <p>阶段一只接入万剑归宗与裂空斩，其余 10 个在阶段二补齐。</p>
 */
public class SlashExpansionComboStateRegistry {

    public static final DeferredRegister<ComboState> COMBO_STATES =
            DeferredRegister.create(ComboState.REGISTRY_KEY, SlashExpansion.MOD_ID);

    // ===== 冰霜领域 =====
    public static final DeferredHolder<ComboState, ComboState> FROST_DOMAIN = slash("frost_domain", entity -> {
        float damage = DamageCalculator.calculateDamage(entity, entity.getMainHandItem());
        FrostDomain.doSlash(entity, false, damage);
    });

    // ===== 裂空斩 =====
    public static final DeferredHolder<ComboState, ComboState> RIFT_SLASH = slash("rift_slash", entity -> {
        float damage = DamageCalculator.calculateDamage(entity, entity.getMainHandItem());
        RiftSlash.doSlash(entity, false, damage);
    });

    /** 注册一个 SA 的出招状态与配套收刀状态。 */
    private static DeferredHolder<ComboState, ComboState> slash(String name, Consumer<LivingEntity> action) {
        COMBO_STATES.register(name + "_end", () -> ComboState.Builder.newInstance()
                .startAndEnd(459, 488)
                .priority(50)
                .motionLoc(DefaultResources.ExMotionLocation)
                .next(entity -> SlashBlade.prefix("none"))
                .nextOfTimeout(entity -> SlashBlade.prefix("none"))
                .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                        .put(0, AttackManager::playQuickSheathSoundAction)
                        .build())
                .releaseAction(ComboState::releaseActionQuickCharge)
                .build());

        return COMBO_STATES.register(name, () -> ComboState.Builder.newInstance()
                .startAndEnd(400, 459)
                .priority(50)
                .motionLoc(DefaultResources.ExMotionLocation)
                .next(ComboState.TimeoutNext.buildFromFrame(15, entity -> SlashBlade.prefix("none")))
                .nextOfTimeout(entity -> SlashExpansion.prefix(name + "_end"))
                .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                        .put(2, entity -> AttackManager.doSlash(entity, -30F, Vec3.ZERO, false, false, 0.1F))
                        .put(3, action)
                        .build())
                .build());
    }
}
