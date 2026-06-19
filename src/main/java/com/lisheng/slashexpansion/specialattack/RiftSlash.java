package com.lisheng.slashexpansion.specialattack;

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

    public static void doSlash(LivingEntity player, boolean critical, double damage, float speed) {
        if (player.level().isClientSide()) return;

        Vec3 lookVec = player.getLookAngle();
        Vec3 targetPos = player.position().add(lookVec.scale(20));

        // 瞬移特效
        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.END_ROD,
                    player.getX(), player.getY() + 1, player.getZ(),
                    50, 1, 1, 1, 0.3);
        }

        // 瞬移
        player.teleportTo(targetPos.x, targetPos.y, targetPos.z);

        // 8 道剑气
        int count = 8;
        float angleStep = 360F / count;
        for (int i = 0; i < count; i++) {
            float angle = i * angleStep;
            EntityDrive drive = new EntityDrive(SlashBlade.RegistryEvents.Drive, player.level());

            Vec3 dir = VectorHelper.getVectorForRotation(0, player.getYRot() + angle);
            drive.setPos(player.getX(), player.getY() + 1, player.getZ());
            drive.setOwner(player);
            drive.setDamage(damage);
            drive.setIsCritical(critical);
            drive.setKnockBack(KnockBacks.toss);
            drive.shoot(dir.x, dir.y, dir.z, speed, 0.3f);

            player.level().addFreshEntity(drive);
        }

        player.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
    }
}