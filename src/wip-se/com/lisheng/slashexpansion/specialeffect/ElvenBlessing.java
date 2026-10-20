package com.lisheng.slashexpansion.specialeffect;

import com.lisheng.slashexpansion.config.SlashExpansionConfig;
import com.lisheng.slashexpansion.registry.SlashExpansionSpecialEffectsRegistry;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * 精灵祝福：白天移速加成、夜晚攻击加成、免疫中毒、箭矢伤害加成。
 * 数值来自 {@code [se.elven_blessing]}。
 */
@net.neoforged.fml.common.EventBusSubscriber(modid = com.lisheng.slashexpansion.SlashExpansion.MOD_ID)
public class ElvenBlessing extends SpecialEffect {

    private static final UUID SPEED_MOD_ID = UUID.nameUUIDFromBytes(
            "slashexpansion:elven_blessing_speed".getBytes(StandardCharsets.UTF_8));
    private static final String SPEED_MOD_NAME = "elven_blessing_day_speed";

    public ElvenBlessing() {
        super(0, true, false);
    }

    private static boolean bladeHasSE(ItemStack blade) {
        if (!(blade.getItem() instanceof ItemSlashBlade)) return false;
        return BladeStateAccess.of(blade)
                .map(state -> state.hasSpecialEffect(SlashExpansionSpecialEffectsRegistry.ELVEN_BLESSING.getKey().location()))
                .orElse(false);
    }

    /** 白天移速加成。 */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        if (player.level().isClientSide()) return;
        if (player.tickCount % 20 != 0) return;

        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) return;

        boolean active = bladeHasSE(player.getMainHandItem()) && player.level().isDay();
        if (active) {
            double amount = SlashExpansionConfig.Se.ElvenBlessing.daySpeedBonus();
            AttributeModifier existing = speed.getModifier(SPEED_MOD_ID);
            if (existing == null) {
                speed.addTransientModifier(new AttributeModifier(
                        SPEED_MOD_ID, SPEED_MOD_NAME, amount, AttributeModifier.Operation.MULTIPLY_BASE));
            } else if (Double.compare(existing.getAmount(), amount) != 0) {
                speed.removeModifier(SPEED_MOD_ID);
                speed.addTransientModifier(new AttributeModifier(
                        SPEED_MOD_ID, SPEED_MOD_NAME, amount, AttributeModifier.Operation.MULTIPLY_BASE));
            }
        } else if (speed.getModifier(SPEED_MOD_ID) != null) {
            speed.removeModifier(SPEED_MOD_ID);
        }
    }

    /** 夜晚攻击加成 / 箭矢伤害加成。 */
    @SubscribeEvent
    public static void onLivingHurt(LivingIncomingDamageEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        if (!bladeHasSE(player.getMainHandItem())) return;

        if (!player.level().isDay()) {
            double bonus = SlashExpansionConfig.Se.ElvenBlessing.nightAttackBonus();
            event.setAmount(event.getAmount() * (float) (1.0 + bonus));
        }

        if (event.getSource().is(DamageTypes.ARROW)) {
            double bonus = SlashExpansionConfig.Se.ElvenBlessing.arrowDamageBonus();
            event.setAmount(event.getAmount() * (float) (1.0 + bonus));
        }
    }

    /** 免疫中毒。 */
    @SubscribeEvent
    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        if (!SlashExpansionConfig.Se.ElvenBlessing.immuneToPoison()) return;
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getEffectInstance().getEffect() != MobEffects.POISON) return;
        if (!bladeHasSE(player.getMainHandItem())) return;

        event.setResult(Event.Result.DENY);
    }
}
