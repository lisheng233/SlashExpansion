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
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

/**
 * 虚空之力，数值来自 {@code [se.void_power]}。
 */
@net.neoforged.fml.common.EventBusSubscriber(modid = com.lisheng.slashexpansion.SlashExpansion.MOD_ID)
public class VoidPower extends SpecialEffect {

    public VoidPower() {
        super(0, true, false);
    }

    @SubscribeEvent
    public static void onHitEntity(SlashBladeEvent.HitEvent event) {
        LivingEntity target = event.getTarget();
        LivingEntity user = event.getUser();

        boolean hasSE = event.getSlashBladeState()
                .hasSpecialEffect(SlashExpansionSpecialEffectsRegistry.VOID_POWER.getKey().location());

        if (!hasSE) return;

        float baseDamage = (float) user.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float bladeDamage = event.getSlashBladeState().getBaseAttackModifier();
        float extraDamage = (bladeDamage + baseDamage)
                * SlashExpansionConfig.Se.VoidPower.bonusDamageMultiplier();

        target.invulnerableTime = 0;
        target.hurt(user.damageSources().fellOutOfWorld(),
                Math.max(extraDamage, SlashExpansionConfig.Se.VoidPower.minBonusDamage()));
        target.invulnerableTime = 0;
        target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS,
                SlashExpansionConfig.Se.VoidPower.blindnessDuration(), 0, false, false, false));
        target.addEffect(new MobEffectInstance(MobEffects.CONFUSION,
                SlashExpansionConfig.Se.VoidPower.confusionDuration(), 0, false, false, false));
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingHurt(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();

        var blade = entity.getMainHandItem();
        if (!(blade.getItem() instanceof ItemSlashBlade)) return;

        boolean hasSE = BladeStateAccess.of(blade)
                .map(state -> state.hasSpecialEffect(SlashExpansionSpecialEffectsRegistry.VOID_POWER.getKey().location()))
                .orElse(false);

        if (hasSE && entity instanceof Player player) {
            if (event.getSource().is(net.minecraft.world.damagesource.DamageTypes.FELL_OUT_OF_WORLD)) {
                event.setCanceled(true);
            }
            Admin.removeNegativeEffects(player);
        }
    }
}
