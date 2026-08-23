package com.lisheng.slashexpansion.specialeffect;

import com.lisheng.slashexpansion.registry.SlashExpansionSpecialEffectsRegistry;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.Random;

@Mod.EventBusSubscriber
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

    // ===== 1. 附魔等级 +5（永久生效） =====
    @SubscribeEvent
    public static void onBladeUpdate(SlashBladeEvent.UpdateEvent event) {
        if (event.getEntity().level().isClientSide()) return;

        boolean hasSE = event.getSlashBladeState()
                .hasSpecialEffect(SlashExpansionSpecialEffectsRegistry.TRANSCENDENT_WILL.getId());

        if (!hasSE) return;

        ItemStack blade = event.getBlade();

        // 检查是否已经应用过 +5 附魔
        if (!blade.getOrCreateTag().getBoolean("TranscendentApplied")) {
            applyEnchantmentBoost(blade);
            blade.getOrCreateTag().putBoolean("TranscendentApplied", true);
        }
    }

    private static void applyEnchantmentBoost(ItemStack blade) {
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(blade);
        boolean modified = false;

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
                enchantments.put(ench, level + 5);
                modified = true;
            }
        }

        if (modified) {
            EnchantmentHelper.setEnchantments(enchantments, blade);
        }
    }

    // ===== 2. 伤害 ×1.5 倍 =====
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) return;
        ItemStack blade = attacker.getMainHandItem();
        if (!(blade.getItem() instanceof ItemSlashBlade)) return;

        boolean hasSE = blade.getCapability(ItemSlashBlade.BLADESTATE)
                .map(state -> state.hasSpecialEffect(SlashExpansionSpecialEffectsRegistry.TRANSCENDENT_WILL.getId()))
                .orElse(false);

        if (hasSE) {
            event.setAmount(event.getAmount() * 1.5f);
        }
    }

    // ===== 3. 暴击率 +25% =====
    @SubscribeEvent
    public static void onHitEntity(SlashBladeEvent.HitEvent event) {
        boolean hasSE = event.getSlashBladeState()
                .hasSpecialEffect(SlashExpansionSpecialEffectsRegistry.TRANSCENDENT_WILL.getId());

        if (!hasSE) return;

        if (RANDOM.nextFloat() < 0.25f) {
            LivingEntity target = event.getTarget();
            LivingEntity user = event.getUser();

            float extraDamage = (float) event.getSlashBladeState().getBaseAttackModifier() * 0.5f;
            if (extraDamage > 0 && user instanceof Player) {
                target.hurt(user.damageSources().playerAttack((Player) user), extraDamage);
            }
        }
    }
}