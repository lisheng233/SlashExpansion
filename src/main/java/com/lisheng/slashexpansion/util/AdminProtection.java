package com.lisheng.slashexpansion.util;

import com.lisheng.slashexpansion.config.SlashExpansionConfig;
import com.lisheng.slashexpansion.specialeffect.Admin;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.BooleanSupplier;

/**
 * 供 Mixin 使用的管理员保护判定。
 *
 * <p>Mixin 会在非常早的阶段被加载，因此这里对所有可能尚未初始化的依赖都做了容错：
 * 配置尚未加载、类尚未就绪时一律视为「未开启保护」。</p>
 */
public final class AdminProtection {

    /** 同一 tick 内最多拦截多少次移除请求；超出后放行，避免某些模组的 while 循环把服务器卡死。 */
    private static final int MAX_REMOVAL_BLOCKS_PER_TICK = 8;

    private static final Map<Entity, long[]> REMOVAL_ATTEMPTS =
            Collections.synchronizedMap(new WeakHashMap<>());

    private AdminProtection() {
    }

    /** 是否处于管理员模式且开启了伤害/移除保护（对应 [se.admin] damageImmunity）。 */
    public static boolean isGuardActive(Entity entity) {
        if (!(entity instanceof Player player)) {
            return false;
        }
        return isAdmin(player) && configBoolean(() -> SlashExpansionConfig.Se.Admin.damageImmunity());
    }

    /** 是否阻止服务器踢出该玩家（对应 [se.admin] preventKick）。 */
    public static boolean shouldPreventKick(Player player) {
        if (player == null) {
            return false;
        }
        return isAdmin(player) && configBoolean(() -> SlashExpansionConfig.Se.Admin.preventKick());
    }

    /**
     * 是否拦截这次 {@code remove(KILLED)} 调用。
     *
     * <p>三种情况会放行，保证永远不会把服务器/死亡流程卡住：</p>
     * <ol>
     *   <li>玩家已经死亡或濒死——交给原版死亡与重生流程；</li>
     *   <li>同一 tick 内被反复请求移除超过阈值——避免别人的 while 循环变成死循环；</li>
     *   <li>未处于管理员保护状态。</li>
     * </ol>
     */
    public static boolean shouldBlockRemoval(Entity entity) {
        if (!isGuardActive(entity)) {
            return false;
        }
        if (entity instanceof LivingEntity living && living.isDeadOrDying()) {
            return false;
        }

        long tick = entity.level().getGameTime();
        long[] state = REMOVAL_ATTEMPTS.computeIfAbsent(entity, key -> new long[]{tick, 0L});
        synchronized (state) {
            if (state[0] != tick) {
                state[0] = tick;
                state[1] = 0L;
            }
            state[1]++;
            return state[1] <= MAX_REMOVAL_BLOCKS_PER_TICK;
        }
    }

    private static boolean isAdmin(Player player) {
        try {
            return Admin.isAdminModeEnabled(player);
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static boolean configBoolean(BooleanSupplier supplier) {
        try {
            return supplier.getAsBoolean();
        } catch (Throwable ignored) {
            return false;
        }
    }
}
