package com.lisheng.slashexpansion.specialattack;

import com.lisheng.slashexpansion.config.SlashExpansionConfig;
import com.lisheng.slashexpansion.vfx.SaVfx;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.entity.EntityDrive;
import mods.flammpfeil.slashblade.util.KnockBacks;
import mods.flammpfeil.slashblade.util.VectorHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
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

        // ── 关于这里的除以攻击力 ────────────────────────────────────────────
        // SlashBlade 的 EntityDrive#onHitEntity 会把 setDamage() 的值再乘一次
        // 「持有者攻击力」（getAttributeValue(ATTACK_DAMAGE)）与刀伤倍率；
        // 而 DamageCalculator 算出的基础伤害里已经包含玩家攻击力了。
        // 如果直接把 finalDamage 塞进去，攻击力就会被计算两次，
        // 实战中一刀能打出十几倍的溢出伤害（足够秒掉监守者）。
        // 先除回去，让每把剑气的最终伤害正好等于 finalDamage，
        // 这也与万剑归宗 / 千本樱 / 庚金剑诀使用的幻影剑（不乘攻击力）保持一致。
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
