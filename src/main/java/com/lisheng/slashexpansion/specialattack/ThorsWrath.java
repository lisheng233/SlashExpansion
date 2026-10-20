package com.lisheng.slashexpansion.specialattack;

import com.lisheng.slashexpansion.config.SlashExpansionConfig;
import com.lisheng.slashexpansion.vfx.SaVfx;
import mods.flammpfeil.slashblade.util.TargetSelector;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 雷神之怒：向范围内随机敌人依次降下闪电，造成伤害并使目标麻痹。
 */
@Mod.EventBusSubscriber
public class ThorsWrath {

    /** 被麻痹的目标 UUID -> 麻痹结束的 game time。 */
    private static final Map<UUID, Long> PARALYZED_UNTIL = new ConcurrentHashMap<>();

    public static void doSlash(LivingEntity player, boolean critical, double damage) {
        if (player.level().isClientSide()) return;

        Level world = player.level();
        int range = SlashExpansionConfig.Sa.ThorsWrath.searchRange();
        AABB aabb = player.getBoundingBox().inflate(range);

        List<LivingEntity> targets = world.getEntitiesOfClass(LivingEntity.class, aabb,
                e -> e != player && e.isAlive() && !e.isSpectator()
                        && TargetSelector.test.test(player, e));

        if (targets.isEmpty()) return;

        // ---- 动画：雷云压顶 → 落雷 → 电弧连锁 ----
        SaVfx.thorsWrath(player, targets);

        int boltCount = SlashExpansionConfig.Sa.ThorsWrath.boltCount();
        float finalDamage = (float) (damage * SlashExpansionConfig.Sa.ThorsWrath.damageMultiplier());
        int duration = SlashExpansionConfig.Sa.ThorsWrath.paralysisDuration();
        int amplifier = SlashExpansionConfig.Sa.ThorsWrath.paralysisAmplifier();
        boolean disableAttacks = SlashExpansionConfig.Sa.ThorsWrath.disableAttacks();
        long now = world.getGameTime();

        for (int i = 0; i < boltCount; i++) {
            LivingEntity target = targets.get(world.random.nextInt(targets.size()));

            LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(world);
            if (bolt != null) {
                bolt.moveTo(target.getX(), target.getY(), target.getZ());
                if (SlashExpansionConfig.Sa.ThorsWrath.visualOnly()) {
                    bolt.setVisualOnly(true);
                }
                if (player instanceof ServerPlayer serverPlayer) {
                    bolt.setCause(serverPlayer);
                }
                world.addFreshEntity(bolt);
            }

            target.invulnerableTime = 0;
            target.hurt(world.damageSources().lightningBolt(), finalDamage);
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,
                    duration, amplifier, false, false, false));

            if (disableAttacks) {
                PARALYZED_UNTIL.put(target.getUUID(), now + duration);
            }
        }
    }

    /** 麻痹期间禁止目标攻击。 */
    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        if (!SlashExpansionConfig.Sa.ThorsWrath.disableAttacks()) return;

        Entity attacker = event.getSource().getEntity();
        if (!(attacker instanceof LivingEntity living)) return;

        Long until = PARALYZED_UNTIL.get(living.getUUID());
        if (until == null) return;

        if (living.level().getGameTime() > until) {
            PARALYZED_UNTIL.remove(living.getUUID());
            return;
        }
        event.setCanceled(true);
    }
}
