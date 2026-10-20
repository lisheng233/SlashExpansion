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
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;

/**
 * 裂空斩：冲刺突进 + 环形剑气。
 *
 * <p>阶段一用原版粒子做反馈；阶段二接回 {@code vfx} 包的分阶段动画。</p>
 */
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

        if (player.level() instanceof ServerLevel serverLevel) {
            // TODO(phase2): 换成 SaVfx.riftSlash(...) 的残影 + 空间裂缝动画
            serverLevel.sendParticles(ParticleTypes.END_ROD,
                    player.getX(), player.getY() + 0.9, player.getZ(), 40, 0.6, 0.6, 0.6, 0.08);
            serverLevel.sendParticles(ParticleTypes.SQUID_INK,
                    player.getX(), player.getY() + 0.9, player.getZ(), 12, 0.4, 0.4, 0.4, 0.02);
        }
        player.playSound(SoundEvents.ENDERMAN_TELEPORT, 0.8F, 1.2F);

        int count = SlashExpansionConfig.Sa.RiftSlash.bladeCount();
        float angleStep = 360F / count;
        float finalDamage = (float) (damage * SlashExpansionConfig.Sa.RiftSlash.damageMultiplier());
        float speed = SlashExpansionConfig.Sa.RiftSlash.projectileSpeed();

        // SlashBlade 的 EntityDrive#onHitEntity 命中时会再乘一次「持有者攻击力」，
        // 而 DamageCalculator 的结果里已经含攻击力了，这里先除回去避免双重计算。
        double attack = Math.max(1.0D, player.getAttributeValue(Attributes.ATTACK_DAMAGE));
        float driveCoefficient = (float) (finalDamage / attack);

        for (int i = 0; i < count; i++) {
            float angle = i * angleStep;
            EntityDrive drive = new EntityDrive(SlashBlade.RegistryEvents.Drive, player.level());

            Vec3 dir = VectorHelper.getVectorForRotation(0, player.getYRot() + angle);
            drive.setPos(player.getX(), player.getY() + 0.5, player.getZ());
            drive.setOwner(player);
            drive.setDamage(driveCoefficient);
            drive.setIsCritical(critical);
            drive.setKnockBack(KnockBacks.toss);

            drive.shoot(dir.x, dir.y, dir.z, speed, 0.3f);
            player.level().addFreshEntity(drive);
        }
    }
}