package com.lisheng.slashexpansion.specialattack;

import com.lisheng.slashexpansion.config.SlashExpansionConfig;
import com.lisheng.slashexpansion.vfx.SaVfx;
import mods.flammpfeil.slashblade.util.KnockBacks;
import mods.flammpfeil.slashblade.util.TargetSelector;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 不动明王：短时间内免疫伤害且无法移动，结束时对周围造成范围伤害并击退。
 */
@Mod.EventBusSubscriber
public class ImmovableKing {

    private record Pending(long endTick, float damage) {
    }

    private static final Map<UUID, Pending> PENDING = new ConcurrentHashMap<>();

    public static void doSlash(LivingEntity player, boolean critical, double damage) {
        if (player.level().isClientSide()) return;
        if (PENDING.containsKey(player.getUUID())) return;

        long endTick = player.level().getGameTime() + SlashExpansionConfig.Sa.ImmovableKing.durationTicks();
        float burstDamage = (float) (damage * SlashExpansionConfig.Sa.ImmovableKing.damageMultiplier());
        PENDING.put(player.getUUID(), new Pending(endTick, burstDamage));

        // ---- 动画：金色曼陀罗展开 + 八方法柱 + 护体金光 ----
        SaVfx.immovableKingCast(player, SlashExpansionConfig.Sa.ImmovableKing.durationTicks());

        applyImmobilize(player);
    }

    private static void applyImmobilize(LivingEntity player) {
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,
                40, SlashExpansionConfig.Sa.ImmovableKing.immobilizeAmplifier(),
                false, false, false));
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        if (player.level().isClientSide()) return;

        Pending pending = PENDING.get(player.getUUID());
        if (pending == null) return;

        if (player.level().getGameTime() >= pending.endTick()) {
            PENDING.remove(player.getUUID());
            releaseImmobilize(player);
            burst(player, pending.damage());
        } else {
            applyImmobilize(player);
            // 定身期间禁止位移
            player.setDeltaMovement(0.0, Math.min(0.0, player.getDeltaMovement().y), 0.0);
            player.hurtMarked = true;
        }
    }

    private static void releaseImmobilize(Player player) {
        MobEffectInstance current = player.getEffect(MobEffects.MOVEMENT_SLOWDOWN);
        if (current != null
                && current.getAmplifier() >= SlashExpansionConfig.Sa.ImmovableKing.immobilizeAmplifier()) {
            player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
        }
    }

    /** 免疫伤害。 */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (PENDING.containsKey(player.getUUID())) {
            event.setCanceled(true);
        }
    }

    private static void burst(Player player, float damage) {
        Level world = player.level();
        int radius = SlashExpansionConfig.Sa.ImmovableKing.radius();
        AABB aabb = player.getBoundingBox().inflate(radius);

        List<LivingEntity> targets = world.getEntitiesOfClass(LivingEntity.class, aabb,
                e -> e != player && e.isAlive() && !e.isSpectator()
                        && TargetSelector.test.test(player, e));

        for (LivingEntity target : targets) {
            target.invulnerableTime = 0;
            target.hurt(player.damageSources().playerAttack(player), damage);
            KnockBacks.smash.action.accept(target);
        }

        // ---- 动画：金身炸裂 ----
        SaVfx.immovableKingRelease(player, radius);
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        PENDING.remove(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            PENDING.remove(player.getUUID());
        }
    }
}
