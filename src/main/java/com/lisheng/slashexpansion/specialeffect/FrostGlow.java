package com.lisheng.slashexpansion.specialeffect;

import com.lisheng.slashexpansion.registry.SlashExpansionSpecialEffectsRegistry;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
        float baseDamage = (float) user.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE);
        float bladeDamage = event.getSlashBladeState().getBaseAttackModifier();
        float extraDamage = bladeDamage + baseDamage;

        target.hurt(user.damageSources().freeze(), Math.max(extraDamage*0.75f,4.0f));
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 4,false, false,false));
    }

    @SubscribeEvent
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