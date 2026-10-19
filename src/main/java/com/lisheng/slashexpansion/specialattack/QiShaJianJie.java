package com.lisheng.slashexpansion.specialattack;

import com.lisheng.slashexpansion.config.SlashExpansionConfig;
import com.lisheng.slashexpansion.entity.QiShaJianJieDomainEntity;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class QiShaJianJie {

    public static void doSlash(LivingEntity player, boolean critical, double damage) {
        if (player.level().isClientSide()) return;

        Level world = player.level();
        int killCount = 0;
        if (player.getMainHandItem().getItem() instanceof ItemSlashBlade) {
            killCount = player.getMainHandItem()
                    .getCapability(ItemSlashBlade.BLADESTATE)
                    .map(state -> state.getKillCount())
                    .orElse(0);
        }

        int baseRadius = SlashExpansionConfig.Sa.QiShaJianJie.baseRadius();
        int maxRadius = SlashExpansionConfig.Sa.QiShaJianJie.maxRadius();
        int killsPerRadius = Math.max(1, SlashExpansionConfig.Sa.QiShaJianJie.killsPerRadius());
        int radius = Math.max(baseRadius, Math.min(maxRadius, baseRadius + killCount / killsPerRadius));

        float finalDamage = (float) (damage * SlashExpansionConfig.Sa.QiShaJianJie.damageMultiplier());

        QiShaJianJieDomainEntity domain = new QiShaJianJieDomainEntity(world, player, finalDamage, radius);
        world.addFreshEntity(domain);
    }
}
