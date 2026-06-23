package com.lisheng.slashexpansion.specialattack;

import com.lisheng.slashexpansion.entity.GiantSlashEntity;
import mods.flammpfeil.slashblade.util.KnockBacks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class JianDangBaHuang {

    public static void doSlash(LivingEntity player, boolean critical, double damage, float speed) {
        if (player.level().isClientSide()) return;

        Level world = player.level();
        Vec3 lookVec = player.getLookAngle();

        GiantSlashEntity slash = new GiantSlashEntity(
            world,
            player,
            damage * 1.5, // 150%
            KnockBacks.smash, // 击退
            false, // 横向
            lookVec
        );

        world.addFreshEntity(slash);
    }
}