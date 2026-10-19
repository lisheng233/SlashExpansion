package com.lisheng.slashexpansion.util;

import com.lisheng.slashexpansion.config.SlashExpansionConfig;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;

/**
 * SA 的基础伤害计算，数值全部来自配置文件 {@code [damage]} 分组。
 *
 * <pre>
 * 基础伤害 = max(minDamage, 刀面板 + 锻造数 × refineBonus + 力量等级 × powerEnchantBonus + 玩家攻击力)
 * </pre>
 */
public class DamageCalculator {

    public static float calculateDamage(LivingEntity player, ItemStack blade) {
        float minDamage = SlashExpansionConfig.Damage.minDamage();

        if (!(blade.getItem() instanceof ItemSlashBlade)) {
            return minDamage;
        }

        ISlashBladeState state = blade.getCapability(ItemSlashBlade.BLADESTATE).orElse(null);
        if (state == null) {
            return minDamage;
        }

        float base = state.getBaseAttackModifier();
        int refine = state.getRefine();
        float refineBonus = refine * SlashExpansionConfig.Damage.refineBonus();

        int power = blade.getEnchantmentLevel(Enchantments.POWER_ARROWS);
        float powerBonus = power * SlashExpansionConfig.Damage.powerEnchantBonus();

        float playerAttack = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);

        return Math.max(minDamage, base + refineBonus + powerBonus + playerAttack);
    }
}
