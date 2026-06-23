package com.lisheng.slashexpansion.specialattack;

import com.lisheng.slashexpansion.entity.QiShaJianJieDomainEntity;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class QiShaJianJie {

    public static void doSlash(LivingEntity player, boolean critical, double damage, float speed) {
        if (player.level().isClientSide()) return;

        Level world = player.level();
        // 获取杀敌数决定半径
        int killCount = 0;
        if (player.getMainHandItem().getItem() instanceof ItemSlashBlade) {
            killCount = player.getMainHandItem()
                .getCapability(ItemSlashBlade.BLADESTATE)
                .map(state -> state.getKillCount())
                .orElse(0);
        }
        int radius = Math.max(3, Math.min(64, 3 + killCount / 49)); // 范围 3~64

        // 生成领域实体
        QiShaJianJieDomainEntity domain = new QiShaJianJieDomainEntity(world, player, damage, radius);
        world.addFreshEntity(domain);
    }
}