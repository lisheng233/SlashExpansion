package com.lisheng.slashexpansion.specialattack;

import com.lisheng.slashexpansion.config.SlashExpansionConfig;
import com.lisheng.slashexpansion.entity.QiShaJianJieDomainEntity;
import com.lisheng.slashexpansion.vfx.SaVfx;
import mods.flammpfeil.slashblade.capability.slashblade.BladeStateAccess;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class QiShaJianJie {

    public static void doSlash(LivingEntity player, boolean critical, double damage) {
        if (player.level().isClientSide()) return;

        Level world = player.level();
        int killCount = 0;
        if (player.getMainHandItem().getItem() instanceof ItemSlashBlade) {
            killCount = BladeStateAccess.of(player.getMainHandItem())
                    .map(state -> state.getKillCount())
                    .orElse(0);
        }

        int baseRadius = SlashExpansionConfig.Sa.QiShaJianJie.baseRadius();
        int maxRadius = SlashExpansionConfig.Sa.QiShaJianJie.maxRadius();
        int killsPerRadius = Math.max(1, SlashExpansionConfig.Sa.QiShaJianJie.killsPerRadius());
        int radius = Math.max(baseRadius, Math.min(maxRadius, baseRadius + killCount / killsPerRadius));

        float finalDamage = (float) (damage * SlashExpansionConfig.Sa.QiShaJianJie.damageMultiplier());

        // ---- 动画：七杀法阵 + 七方杀剑光柱 ----
        int spawnDistance = SlashExpansionConfig.Sa.QiShaJianJie.spawnDistance();
        Vec3 look = player.getLookAngle();
        Vec3 domainCenter = player.position().add(look.x * spawnDistance, 0.2, look.z * spawnDistance);
        SaVfx.qiShaJianJie(player, domainCenter, radius);

        QiShaJianJieDomainEntity domain = new QiShaJianJieDomainEntity(world, player, finalDamage, radius);
        world.addFreshEntity(domain);
    }
}
