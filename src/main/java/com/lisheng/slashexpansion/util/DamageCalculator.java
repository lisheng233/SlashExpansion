package com.lisheng.slashexpansion.util;

import com.lisheng.slashexpansion.config.SlashExpansionConfig;
import mods.flammpfeil.slashblade.capability.slashblade.BladeStateAccess;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

/**
 * SA 的基础伤害计算，数值全部来自配置文件 {@code [damage]} 分组。
 *
 * <pre>
 * 基础伤害 = max(minDamage, 刀面板 + 锻造数 × refineBonus + 力量等级 × powerEnchantBonus + 玩家攻击力)
 * </pre>
 *
 * <p>1.21 起拔刀剑的刀状态从 Capability 改成 DataComponent，
 * 统一通过 {@link BladeStateAccess#of(ItemStack)} 读取。</p>
 */
public class DamageCalculator {

    public static float calculateDamage(LivingEntity player, ItemStack blade) {
        float minDamage = SlashExpansionConfig.Damage.minDamage();

        if (!(blade.getItem() instanceof ItemSlashBlade)) {
            return minDamage;
        }

        ISlashBladeState state = BladeStateAccess.of(blade).orElse(null);
        if (state == null) {
            return minDamage;
        }

        float base = state.getBaseAttackModifier();
        int refine = state.getRefine();
        float refineBonus = refine * SlashExpansionConfig.Damage.refineBonus();

        // 1.21：附魔改为 Holder，Enchantments.POWER 取代旧的 POWER_ARROWS
        int power = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER, blade);
        float powerBonus = power * SlashExpansionConfig.Damage.powerEnchantBonus();

        float playerAttack = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);

        return Math.max(minDamage, base + refineBonus + powerBonus + playerAttack);
    }
}
