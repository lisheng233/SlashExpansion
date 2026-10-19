package com.lisheng.slashexpansion.specialattack;

import com.lisheng.slashexpansion.config.SlashExpansionConfig;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.entity.EntityDrive;
import mods.flammpfeil.slashblade.util.KnockBacks;
import mods.flammpfeil.slashblade.util.VectorHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class RiftSlash {

    public static void doSlash(LivingEntity player, boolean critical, double damage) {
        if (player.level().isClientSide()) return;

        // ---- 冲刺效果 ----
        Vec3 lookVec = player.getLookAngle();
        double horizontalSpeed = SlashExpansionConfig.Sa.RiftSlash.dashHorizontalSpeed();
        double verticalSpeed = SlashExpansionConfig.Sa.RiftSlash.dashVerticalSpeed();
        Vec3 dashVec = new Vec3(lookVec.x * horizontalSpeed, verticalSpeed, lookVec.z * horizontalSpeed);

        player.setDeltaMovement(dashVec);
        player.hurtMarked = true;

        if (player.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 30; i++) {
                double offsetX = (player.level().random.nextDouble() - 0.5) * 1.5;
                double offsetY = (player.level().random.nextDouble() - 0.5) * 1.5;
                double offsetZ = (player.level().random.nextDouble() - 0.5) * 1.5;
                serverLevel.sendParticles(ParticleTypes.END_ROD,
                        player.getX() + offsetX,
                        player.getY() + 0.5 + offsetY,
                        player.getZ() + offsetZ,
                        1, 0, 0, 0, 0.1);
            }
        }

        player.playSound(SoundEvents.ENDERMAN_TELEPORT, 0.8F, 1.2F);

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

        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.DRAGON_BREATH,
                    player.getX(), player.getY() + 0.5, player.getZ(),
                    20, 1.0, 1.0, 1.0, 0.2);
        }
    }
}
