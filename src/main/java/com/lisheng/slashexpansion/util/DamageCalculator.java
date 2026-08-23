package com.lisheng.slashexpansion.util;

import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;

public class DamageCalculator {
    
    public static float calculateDamage(LivingEntity player, ItemStack blade) {
        if (!(blade.getItem() instanceof ItemSlashBlade)) {
            return 10.0f;
        }
        
        ISlashBladeState state = blade.getCapability(ItemSlashBlade.BLADESTATE).orElse(null);
        if (state == null) return 10.0f;
        
        float base = state.getBaseAttackModifier();
        int refine = 0;
        try {
            refine = state.getRefine();
        } catch (Exception e) {
            System.err.println("Error getting refine level: " + e.getMessage());
        }
        float refineBonus = refine * 0.5f;
        int power = blade.getEnchantmentLevel(Enchantments.POWER_ARROWS);
        float powerBonus = power * 1.0f;
        float playerAttack = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
        
        return Math.max(10.0f, base + refineBonus + powerBonus*2.0f + playerAttack);
    }
}