package com.lisheng.slashexpansion.registry;

import com.lisheng.slashexpansion.SlashExpansion;
import com.lisheng.slashexpansion.specialattack.BloodMoonSlash;
import com.lisheng.slashexpansion.specialattack.FrostDomain;
import com.lisheng.slashexpansion.specialattack.GengJinJianJue;
import com.lisheng.slashexpansion.specialattack.ImmovableKing;
import com.lisheng.slashexpansion.specialattack.Inferno;
import com.lisheng.slashexpansion.specialattack.JianDangBaHuang;
import com.lisheng.slashexpansion.specialattack.QiShaJianJie;
import com.lisheng.slashexpansion.specialattack.RiftSlash;
import com.lisheng.slashexpansion.specialattack.Senbonzakura;
import com.lisheng.slashexpansion.specialattack.ThorsWrath;
import com.lisheng.slashexpansion.specialattack.WanJianGuiZong;
import com.lisheng.slashexpansion.specialattack.ZhanTianBaJianShu;
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
 * 12 个 SA 的 ComboState 注册。
 *
 * <p>每个 SA 占两个状态：{@code <id>} 是出招动作（第 3 tick 触发技能本体），
 * {@code <id>_end} 是收刀动作。1.21.1 的 {@code ComboState.Builder} 与 1.20.1 完全一致，
 * 只有注册 API 从 {@code RegistryObject} 换成了 {@code DeferredHolder}。</p>
 */
public class SlashExpansionComboStateRegistry {

    public static final DeferredRegister<ComboState> COMBO_STATES =
            DeferredRegister.create(ComboState.REGISTRY_KEY, SlashExpansion.MOD_ID);

    private static final String[] SA_IDS = {
            "wan_jian_gui_zong", "senbonzakura", "rift_slash", "frost_domain",
            "inferno", "zhan_tian_ba_jian_shu", "jian_dang_ba_huang", "geng_jin_jian_jue",
            "qi_sha_jian_jie", "blood_moon_slash", "thors_wrath", "immovable_king"
    };

    public static final DeferredHolder<ComboState, ComboState> WAN_JIAN_GUI_ZONG =
            slash("wan_jian_gui_zong", (e, d) -> WanJianGuiZong.doSlash(e, false, d));
    public static final DeferredHolder<ComboState, ComboState> SENBONZAKURA =
            slash("senbonzakura", (e, d) -> Senbonzakura.doSlash(e, false, d));
    public static final DeferredHolder<ComboState, ComboState> RIFT_SLASH =
            slash("rift_slash", (e, d) -> RiftSlash.doSlash(e, false, d));
    public static final DeferredHolder<ComboState, ComboState> FROST_DOMAIN =
            slash("frost_domain", (e, d) -> FrostDomain.doSlash(e, false, d));
    public static final DeferredHolder<ComboState, ComboState> INFERNO =
            slash("inferno", (e, d) -> Inferno.doSlash(e, false, d));
    public static final DeferredHolder<ComboState, ComboState> ZHAN_TIAN_BA_JIAN_SHU =
            slash("zhan_tian_ba_jian_shu", (e, d) -> ZhanTianBaJianShu.doSlash(e, false, d));
    public static final DeferredHolder<ComboState, ComboState> JIAN_DANG_BA_HUANG =
            slash("jian_dang_ba_huang", (e, d) -> JianDangBaHuang.doSlash(e, false, d));
    public static final DeferredHolder<ComboState, ComboState> GENG_JIN_JIAN_JUE =
            slash("geng_jin_jian_jue", (e, d) -> GengJinJianJue.doSlash(e, false, d));
    public static final DeferredHolder<ComboState, ComboState> QI_SHA_JIAN_JIE =
            slash("qi_sha_jian_jie", (e, d) -> QiShaJianJie.doSlash(e, false, d));
    public static final DeferredHolder<ComboState, ComboState> BLOOD_MOON_SLASH =
            slash("blood_moon_slash", (e, d) -> BloodMoonSlash.doSlash(e, false, d));
    public static final DeferredHolder<ComboState, ComboState> THORS_WRATH =
            slash("thors_wrath", (e, d) -> ThorsWrath.doSlash(e, false, d));
    public static final DeferredHolder<ComboState, ComboState> IMMOVABLE_KING =
            slash("immovable_king", (e, d) -> ImmovableKing.doSlash(e, false, d));

    /** 注册一个 SA 的出招状态与配套收刀状态。 */
    private static DeferredHolder<ComboState, ComboState> slash(String name, SlashAction action) {
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

        Consumer<LivingEntity> tick = entity -> {
            float damage = DamageCalculator.calculateDamage(entity, entity.getMainHandItem());
            action.run(entity, damage);
        };

        return COMBO_STATES.register(name, () -> ComboState.Builder.newInstance()
                .startAndEnd(400, 459)
                .priority(50)
                .motionLoc(DefaultResources.ExMotionLocation)
                .next(ComboState.TimeoutNext.buildFromFrame(15, entity -> SlashBlade.prefix("none")))
                .nextOfTimeout(entity -> SlashExpansion.prefix(name + "_end"))
                .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                        .put(2, entity -> AttackManager.doSlash(entity, -30F, Vec3.ZERO, false, false, 0.1F))
                        .put(3, tick)
                        .build())
                .build());
    }

    @FunctionalInterface
    private interface SlashAction {
        void run(LivingEntity entity, float damage);
    }
}