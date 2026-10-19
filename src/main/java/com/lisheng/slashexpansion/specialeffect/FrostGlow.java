package com.lisheng.slashexpansion.specialeffect;

import com.lisheng.slashexpansion.config.SlashExpansionConfig;
import com.lisheng.slashexpansion.registry.SlashExpansionSpecialEffectsRegistry;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 寒霜的辉光，数值来自 {@code [se.frost_glow]}。
 */
@Mod.EventBusSubscriber
public class FrostGlow extends SpecialEffect {

    public FrostGlow() {
        super(0, true, false);
    }

    @SubscribeEvent
    public static void onHitEntity(SlashBladeEvent.HitEvent event) {
        LivingEntity target = event.getTarget();
        LivingEntity user = event.getUser();

        boolean hasSE = event.getSlashBladeState()
                .hasSpecialEffect(SlashExpansionSpecialEffectsRegistry.FROST_GLOW.getId());

        if (!hasSE) return;

        // 额外霜冻伤害：拔刀剑面板 + 玩家攻击属性
        float baseDamage = (float) user.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float bladeDamage = event.getSlashBladeState().getBaseAttackModifier();
        float extraDamage = (bladeDamage + baseDamage)
                * SlashExpansionConfig.Se.FrostGlow.bonusDamageMultiplier();

        target.invulnerableTime = 0;
        target.hurt(user.damageSources().freeze(),
                Math.max(extraDamage, SlashExpansionConfig.Se.FrostGlow.minBonusDamage()));
        target.invulnerableTime = 0;
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,
                SlashExpansionConfig.Se.FrostGlow.slowDuration(),
                SlashExpansionConfig.Se.FrostGlow.slowAmplifier(),
                false, false, false));
        target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN,
                SlashExpansionConfig.Se.FrostGlow.digSlowDuration(),
                SlashExpansionConfig.Se.FrostGlow.digSlowAmplifier(),
                false, false, false));
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!event.getSource().is(net.minecraft.world.damagesource.DamageTypes.FREEZE)) return;
        LivingEntity entity = event.getEntity();

        var blade = entity.getMainHandItem();
        if (!(blade.getItem() instanceof ItemSlashBlade)) return;

        boolean hasSE = blade.getCapability(ItemSlashBlade.BLADESTATE)
                .map(state -> state.hasSpecialEffect(SlashExpansionSpecialEffectsRegistry.FROST_GLOW.getId()))
                .orElse(false);

        if (hasSE) {
            event.setCanceled(true);
        }
    }
}
