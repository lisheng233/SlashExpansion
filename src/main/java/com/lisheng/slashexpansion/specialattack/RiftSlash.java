package com.lisheng.slashexpansion.specialattack;

import com.lisheng.slashexpansion.config.SlashExpansionConfig;
import com.lisheng.slashexpansion.vfx.SaVfx;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.entity.EntityDrive;
import mods.flammpfeil.slashblade.util.KnockBacks;
import mods.flammpfeil.slashblade.util.VectorHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class RiftSlash {

    public static void doSlash(LivingEntity player, boolean critical, double damage) {
        if (player.level().isClientSide()) return;

        // ---- 冲刺 ----
        Vec3 lookVec = player.getLookAngle();
        double horizontalSpeed = SlashExpansionConfig.Sa.RiftSlash.dashHorizontalSpeed();
        double verticalSpeed = SlashExpansionConfig.Sa.RiftSlash.dashVerticalSpeed();
        Vec3 dashVec = new Vec3(lookVec.x * horizontalSpeed, verticalSpeed, lookVec.z * horizontalSpeed);

        player.setDeltaMovement(dashVec);
        player.hurtMarked = true;

        // ---- 动画：残影 + 空间裂缝 + 冲击环 ----
        SaVfx.riftSlash(player);

        int count = SlashExpansionConfig.Sa.RiftSlash.bladeCount();
        float angleStep = 360F / count;
        float finalDamage = (float) (damage * SlashExpansionConfig.Sa.RiftSlash.damageMultiplier());
        float speed = SlashExpansionConfig.Sa.RiftSlash.projectileSpeed();

        for (int i = 0; i < count; i++) {
            float angle = i * angleStep;
            EntityDrive drive = new EntityDrive(SlashBlade.RegistryEvents.Drive, player.level());

            Vec3 dir = VectorHelper.getVectorForRotation(0, player.getYRot() + angle);
            drive.setPos(player.getX(), player.getY() + 0.5, player.getZ());
            drive.setOwner(player);
            drive.setDamage(finalDamage);
            drive.setIsCritical(critical);
            drive.setKnockBack(KnockBacks.toss);

            drive.shoot(dir.x, dir.y, dir.z, speed, 0.3f);
            player.level().addFreshEntity(drive);
        }
    }
}
