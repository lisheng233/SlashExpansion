package com.lisheng.slashexpansion.specialeffect;

import com.lisheng.slashexpansion.config.SlashExpansionConfig;
import com.lisheng.slashexpansion.registry.SlashExpansionSpecialEffectsRegistry;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.capability.slashblade.BladeStateAccess;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.ItemEnchantments;
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
        if (!applied(blade)) {
            applyEnchantmentBoost(blade);
            markApplied(blade);
        }
    }

    private static final String APPLIED_KEY = "TranscendentApplied";

    private static boolean applied(ItemStack blade) {
        return blade.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean(APPLIED_KEY);
    }

    private static void markApplied(ItemStack blade) {
        net.minecraft.nbt.CompoundTag tag = blade.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putBoolean(APPLIED_KEY, true);
        blade.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    /** 1.21 的附魔是数据驱动注册表：ItemEnchantments 以 Holder<Enchantment> 为键。 */
    private static void applyEnchantmentBoost(ItemStack blade) {
        ItemEnchantments enchantments = blade.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(enchantments);
        boolean modified = false;
        int boost = SlashExpansionConfig.Se.TranscendentWill.enchantBoost();

        for (var entry : enchantments.entrySet()) {
            Holder<Enchantment> holder = entry.getKey();
            String enchId = holder.unwrapKey().map(key -> key.location().toString()).orElse("");
            boolean excluded = false;
            for (String excludedId : EXCLUDED_ENCHANTMENTS) {
                if (enchId.equals(excludedId)) {
                    excluded = true;
                    break;
                }
            }
            if (!excluded) {
                mutable.set(holder, entry.getIntValue() + boost);
                modified = true;
            }
        }

        if (modified) {
            blade.set(DataComponents.ENCHANTMENTS, mutable.toImmutable());
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
