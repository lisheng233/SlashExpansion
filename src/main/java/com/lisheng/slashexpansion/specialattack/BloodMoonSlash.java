package com.lisheng.slashexpansion.specialattack;

import com.lisheng.slashexpansion.SlashExpansion;
import com.lisheng.slashexpansion.config.SlashExpansionConfig;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.util.TargetSelector;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * 血月斩：消耗自身当前生命值，对前方范围内的敌人造成
 * 「基础伤害（正常伤害，受护甲影响） + 消耗生命兑换的伤害（无视护甲，随锻造数提升）」。
 *
 * <p>兑换部分使用自定义伤害类型 {@code slashexpansion:blood_moon_slash}，
 * 并在数据包中被加入 {@code minecraft:bypasses_armor} 标签，因此不会被护甲减免。</p>
 */
public class BloodMoonSlash {

    public static final ResourceKey<DamageType> BLOOD_MOON_DAMAGE =
            ResourceKey.create(Registries.DAMAGE_TYPE, SlashExpansion.prefix("blood_moon_slash"));

    public static void doSlash(LivingEntity player, boolean critical, double baseDamage) {
        if (player.level().isClientSide()) return;

        Level world = player.level();

        // ---- 1. 扣除当前生命值 ----
        float currentHealth = player.getHealth();
        float costPercent = SlashExpansionConfig.Sa.BloodMoonSlash.healthCostPercent();
        float minHealth = SlashExpansionConfig.Sa.BloodMoonSlash.minHealthAfterCost();
        float newHealth = Math.min(currentHealth,
                Math.max(minHealth, currentHealth * (1.0F - costPercent)));
        float consumed = Math.max(0.0F, currentHealth - newHealth);
        player.setHealth(newHealth);

        // ---- 2. 消耗生命兑换伤害（与锻造数相关） ----
        int refine = 0;
        ItemStack blade = player.getMainHandItem();
        if (blade.getItem() instanceof ItemSlashBlade) {
            refine = blade.getCapability(ItemSlashBlade.BLADESTATE)
                    .map(ISlashBladeState::getRefine)
                    .orElse(0);
        }
        float conversion = consumed
                * SlashExpansionConfig.Sa.BloodMoonSlash.damagePerHealth()
                * (1.0F + refine * SlashExpansionConfig.Sa.BloodMoonSlash.refineScalingPerLevel());

        // ---- 3. 前方扇形范围索敌 ----
        int range = SlashExpansionConfig.Sa.BloodMoonSlash.range();
        AABB aabb = player.getBoundingBox().inflate(range);
        Vec3 look = player.getLookAngle();

        List<LivingEntity> targets = world.getEntitiesOfClass(LivingEntity.class, aabb,
                e -> e != player && e.isAlive() && !e.isSpectator()
                        && TargetSelector.test.test(player, e)
                        && isInFront(player, e, look));

        if (!targets.isEmpty() && (baseDamage > 0.0D || conversion > 0.0F)) {
            Holder<DamageType> holder = world.registryAccess()
                    .registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(BLOOD_MOON_DAMAGE);
            DamageSource convertedSource = new DamageSource(holder, player);
            DamageSource normalSource = player instanceof Player actualPlayer
                    ? player.damageSources().playerAttack(actualPlayer)
                    : player.damageSources().mobAttack(player);

            for (LivingEntity target : targets) {
                // 3.1 基础伤害：正常伤害，受护甲影响
                if (baseDamage > 0.0D) {
                    target.invulnerableTime = 0;
                    target.hurt(normalSource, (float) baseDamage);
                }
                // 3.2 兑换伤害：无视护甲
                if (conversion > 0.0F) {
                    target.invulnerableTime = 0;
                    target.hurt(convertedSource, conversion);
                }
            }
        }

        // ---- 4. 血月特效 ----
        if (world instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.CRIT,
                    player.getX(), player.getY() + 1.0, player.getZ(),
                    40, 1.2, 1.0, 1.2, 0.4);
            for (LivingEntity target : targets) {
                serverLevel.sendParticles(ParticleTypes.DAMAGE_INDICATOR,
                        target.getX(), target.getY() + target.getBbHeight() * 0.5, target.getZ(),
                        8, 0.3, 0.3, 0.3, 0.1);
            }
        }
    }

    private static boolean isInFront(LivingEntity player, LivingEntity target, Vec3 look) {
        Vec3 toTarget = target.position()
                .add(0.0, target.getBbHeight() * 0.5, 0.0)
                .subtract(player.getEyePosition());
        if (toTarget.lengthSqr() < 1.0E-4) {
            return true;
        }
        return toTarget.normalize().dot(look) > 0.0;
    }
}
