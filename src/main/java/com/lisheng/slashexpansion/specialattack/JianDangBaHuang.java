package com.lisheng.slashexpansion.specialattack;

import com.lisheng.slashexpansion.config.SlashExpansionConfig;
import com.lisheng.slashexpansion.entity.GiantSlashEntity;
import com.lisheng.slashexpansion.vfx.SaVfx;
import mods.flammpfeil.slashblade.util.KnockBacks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class JianDangBaHuang {

    public static void doSlash(LivingEntity player, boolean critical, double damage) {
        if (player.level().isClientSide()) return;

        Level world = player.level();
        Vec3 lookVec = player.getLookAngle();

        // ---- 动画：宽幅横扫刀光 + 八荒震荡 ----
        SaVfx.jianDangBaHuang(player);

        GiantSlashEntity slash = new GiantSlashEntity(
                world,
                player,
                damage * SlashExpansionConfig.Sa.JianDangBaHuang.damageMultiplier(),
                KnockBacks.smash,
                false,
                lookVec
        );

        world.addFreshEntity(slash);
    }
}
