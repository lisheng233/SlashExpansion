package com.lisheng.slashexpansion.specialeffect;

import com.lisheng.slashexpansion.config.SlashExpansionConfig;
import com.lisheng.slashexpansion.registry.SlashExpansionSpecialEffectsRegistry;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.minecraft.core.registries.Registries;

import java.util.Map;
import java.util.Random;

/**
 * 超越的意志，数值来自 {@code [se.transcendent_will]}。
 */
@net.neoforged.fml.common.EventBusSubscriber(modid = com.lisheng.slashexpansion.SlashExpansion.MOD_ID)
public class TranscendentWill extends SpecialEffect {

    private static final Random RANDOM = new Random();

    private static final String[] EXCLUDED_ENCHANTMENTS = {
            "minecraft:infinity",
            "minecraft:mending",
            "minecraft:binding_curse",
            "minecraft:vanishing_curse"
    };

    public TranscendentWill() {
        super(0, true, false);
    }

    // ===== 1. 附魔等级提升（永久生效） =====
    @SubscribeEvent
    public static void onBladeUpdate(SlashBladeEvent.UpdateEvent event) {
        if (event.getEntity().level().isClientSide()) return;

        boolean hasSE = event.getSlashBladeState()
                .hasSpecialEffect(SlashExpansionSpecialEffectsRegistry.TRANSCENDENT_WILL.getKey().location());

        if (!hasSE) return;

        ItemStack blade = event.getBlade();

        // 检查是否已经应用过附魔提升
        if (!blade.getOrCreateTag().getBoolean("TranscendentApplied")) {
            applyEnchantmentBoost(blade);
            blade.getOrCreateTag().putBoolean("TranscendentApplied", true);
        }
    }

    private static void applyEnchantmentBoost(ItemStack blade) {
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(blade);
        boolean modified = false;
        int boost = SlashExpansionConfig.Se.TranscendentWill.enchantBoost();

        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            Enchantment ench = entry.getKey();
            int level = entry.getValue();

            String enchId = ForgeRegistries.ENCHANTMENTS.getKey(ench).toString();
            boolean excluded = false;
            for (String excludedId : EXCLUDED_ENCHANTMENTS) {
                if (enchId.equals(excludedId)) {
                    excluded = true;
                    break;
                }
            }

            if (!excluded) {
                enchantments.put(ench, level + boost);
                modified = true;
            }
        }

        if (modified) {
            EnchantmentHelper.setEnchantments(enchantments, blade);
        }
    }

    // ===== 2. 伤害倍率 =====
    @SubscribeEvent
    public static void onLivingHurt(LivingIncomingDamageEvent event) {
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) return;
        ItemStack blade = attacker.getMainHandItem();
        if (!(blade.getItem() instanceof ItemSlashBlade)) return;

        boolean hasSE = BladeStateAccess.of(blade)
                .map(state -> state.hasSpecialEffect(SlashExpansionSpecialEffectsRegistry.TRANSCENDENT_WILL.getKey().location()))
                .orElse(false);

        if (hasSE) {
            event.setAmount(event.getAmount() * SlashExpansionConfig.Se.TranscendentWill.damageMultiplier());
        }
    }

    @SubscribeEvent
    public static void doSlash(SlashBladeEvent.DoSlashEvent event) {
        boolean hasSE = event.getSlashBladeState()
                .hasSpecialEffect(SlashExpansionSpecialEffectsRegistry.TRANSCENDENT_WILL.getKey().location());

        if (!hasSE) return;
        if (RANDOM.nextFloat() < SlashExpansionConfig.Se.TranscendentWill.critChance()) {
            event.setCritical(true);
        }
    }

    // ===== 3. 暴击附加伤害 =====
    @SubscribeEvent
    public static void onHitEntity(SlashBladeEvent.HitEvent event) {
        boolean hasSE = event.getSlashBladeState()
                .hasSpecialEffect(SlashExpansionSpecialEffectsRegistry.TRANSCENDENT_WILL.getKey().location());

        if (!hasSE) return;

        if (RANDOM.nextFloat() < SlashExpansionConfig.Se.TranscendentWill.critChance()) {
            LivingEntity target = event.getTarget();
            LivingEntity user = event.getUser();

            float extraDamage = (float) event.getSlashBladeState().getBaseAttackModifier()
                    * SlashExpansionConfig.Se.TranscendentWill.critBonusDamageMultiplier();
            if (extraDamage > 0 && user instanceof Player player) {
                target.hurt(user.damageSources().playerAttack(player), extraDamage);
            }
        }
    }
}
