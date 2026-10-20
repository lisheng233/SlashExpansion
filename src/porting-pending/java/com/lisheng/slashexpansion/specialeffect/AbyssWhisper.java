package com.lisheng.slashexpansion.specialeffect;

import com.lisheng.slashexpansion.config.SlashExpansionConfig;
import com.lisheng.slashexpansion.registry.SlashExpansionSpecialEffectsRegistry;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 深渊的低语，数值来自 {@code [se.abyss_whisper]}。
 */
@Mod.EventBusSubscriber
public class AbyssWhisper extends SpecialEffect {

    public AbyssWhisper() {
        super(0, true, false);
    }

    // ★ 定时检查，持续施加自身效果
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        if (player.level().isClientSide()) return;

        int interval = Math.max(1, SlashExpansionConfig.Se.AbyssWhisper.checkInterval());
        if (player.tickCount % interval != 0) return;

        var blade = player.getMainHandItem();
        if (!(blade.getItem() instanceof ItemSlashBlade)) return;

        boolean hasSE = blade.getCapability(ItemSlashBlade.BLADESTATE)
                .map(state -> state.hasSpecialEffect(SlashExpansionSpecialEffectsRegistry.ABYSS_WHISPER.getId()))
                .orElse(false);

        if (!hasSE) return;

        int xpLevel = player.experienceLevel;

        if (xpLevel <= SlashExpansionConfig.Se.AbyssWhisper.highLevelThreshold()) {
            // ★ 低等级：自身获得负面效果（持续）
            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS,
                    SlashExpansionConfig.Se.AbyssWhisper.selfLowBlindnessDuration(), 0, false, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION,
                    SlashExpansionConfig.Se.AbyssWhisper.selfLowConfusionDuration(), 0, false, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.HUNGER,
                    SlashExpansionConfig.Se.AbyssWhisper.selfLowHungerDuration(), 0, false, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.UNLUCK,
                    SlashExpansionConfig.Se.AbyssWhisper.selfLowUnluckDuration(),
                    SlashExpansionConfig.Se.AbyssWhisper.selfLowUnluckAmplifier(), false, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.WITHER,
                    SlashExpansionConfig.Se.AbyssWhisper.selfLowWitherDuration(),
                    SlashExpansionConfig.Se.AbyssWhisper.selfLowWitherAmplifier(), false, false, false));
        } else {
            // ★ 高等级：自身获得较轻负面效果
            player.addEffect(new MobEffectInstance(MobEffects.UNLUCK,
                    SlashExpansionConfig.Se.AbyssWhisper.selfHighUnluckDuration(),
                    SlashExpansionConfig.Se.AbyssWhisper.selfHighUnluckAmplifier(), false, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.WITHER,
                    SlashExpansionConfig.Se.AbyssWhisper.selfHighWitherDuration(), 0, false, false, false));
        }
    }

    // ★ 攻击时给目标施加效果
    @SubscribeEvent
    public static void onHitEntity(SlashBladeEvent.HitEvent event) {
        LivingEntity target = event.getTarget();

        boolean hasSE = event.getSlashBladeState()
                .hasSpecialEffect(SlashExpansionSpecialEffectsRegistry.ABYSS_WHISPER.getId());

        if (!hasSE) return;

        target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS,
                SlashExpansionConfig.Se.AbyssWhisper.targetBlindnessDuration(), 0, false, false, false));
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,
                SlashExpansionConfig.Se.AbyssWhisper.targetSlowDuration(),
                SlashExpansionConfig.Se.AbyssWhisper.targetSlowAmplifier(), false, false, false));
        target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN,
                SlashExpansionConfig.Se.AbyssWhisper.targetDigSlowDuration(),
                SlashExpansionConfig.Se.AbyssWhisper.targetDigSlowAmplifier(), false, false, false));
        target.addEffect(new MobEffectInstance(MobEffects.WITHER,
                SlashExpansionConfig.Se.AbyssWhisper.targetWitherDuration(),
                SlashExpansionConfig.Se.AbyssWhisper.targetWitherAmplifier(), true, false));
        target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,
                SlashExpansionConfig.Se.AbyssWhisper.targetWeaknessDuration(),
                SlashExpansionConfig.Se.AbyssWhisper.targetWeaknessAmplifier(), true, false));
    }
}
