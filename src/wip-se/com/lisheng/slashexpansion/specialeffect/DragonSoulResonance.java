package com.lisheng.slashexpansion.specialeffect;

import com.lisheng.slashexpansion.config.SlashExpansionConfig;
import com.lisheng.slashexpansion.registry.SlashExpansionSpecialEffectsRegistry;
import com.lisheng.slashexpansion.util.DamageCalculator;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import mods.flammpfeil.slashblade.util.TargetSelector;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.List;
import java.util.Random;

/**
 * 龙魂共鸣：击杀末影龙后该刀永久增伤，攻击有概率触发龙息范围伤害。
 * 数值来自 {@code [se.dragon_soul_resonance]}。
 */
@net.neoforged.fml.common.EventBusSubscriber(modid = com.lisheng.slashexpansion.SlashExpansion.MOD_ID)
public class DragonSoulResonance extends SpecialEffect {

    private static final String UNLOCK_KEY = "DragonSoulResonanceUnlocked";
    private static final Random RANDOM = new Random();

    public DragonSoulResonance() {
        super(0, true, false);
    }

    private static boolean bladeHasSE(ItemStack blade) {
        if (!(blade.getItem() instanceof ItemSlashBlade)) return false;
        return BladeStateAccess.of(blade)
                .map(state -> state.hasSpecialEffect(SlashExpansionSpecialEffectsRegistry.DRAGON_SOUL_RESONANCE.getKey().location()))
                .orElse(false);
    }

    /** 击杀末影龙 -> 该刀解锁永久增伤。 */
    @SubscribeEvent
    public static void onDragonDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof EnderDragon)) return;
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        ItemStack blade = player.getMainHandItem();
        if (!bladeHasSE(blade)) return;

        blade.getOrCreateTag().putBoolean(UNLOCK_KEY, true);
    }

    /** 永久增伤。 */
    @SubscribeEvent
    public static void onLivingHurt(LivingIncomingDamageEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        ItemStack blade = player.getMainHandItem();
        if (!bladeHasSE(blade)) return;
        if (!blade.getOrCreateTag().getBoolean(UNLOCK_KEY)) return;

        double bonus = SlashExpansionConfig.Se.DragonSoulResonance.permanentAttackBonus();
        event.setAmount(event.getAmount() * (float) (1.0 + bonus));
    }

    /** 攻击时概率触发龙息范围伤害。 */
    @SubscribeEvent
    public static void onHitEntity(SlashBladeEvent.HitEvent event) {
        boolean hasSE = event.getSlashBladeState()
                .hasSpecialEffect(SlashExpansionSpecialEffectsRegistry.DRAGON_SOUL_RESONANCE.getKey().location());
        if (!hasSE) return;

        LivingEntity user = event.getUser();
        if (user.level().isClientSide()) return;
        if (RANDOM.nextFloat() >= SlashExpansionConfig.Se.DragonSoulResonance.breathChance()) return;

        LivingEntity target = event.getTarget();
        Level world = user.level();
        double radius = SlashExpansionConfig.Se.DragonSoulResonance.breathRadius();
        float damage = (float) (DamageCalculator.calculateDamage(user, event.getBlade())
                * SlashExpansionConfig.Se.DragonSoulResonance.breathDamageMultiplier());

        AABB aabb = target.getBoundingBox().inflate(radius);
        List<LivingEntity> victims = world.getEntitiesOfClass(LivingEntity.class, aabb,
                e -> e != user && e.isAlive() && !e.isSpectator()
                        && TargetSelector.test.test(user, e));

        for (LivingEntity victim : victims) {
            victim.invulnerableTime = 0;
            victim.hurt(user.damageSources().dragonBreath(), damage);
        }

        if (world instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.DRAGON_BREATH,
                    target.getX(), target.getY() + 0.5, target.getZ(),
                    40, radius * 0.6, 0.8, radius * 0.6, 0.2);
        }
    }
}
