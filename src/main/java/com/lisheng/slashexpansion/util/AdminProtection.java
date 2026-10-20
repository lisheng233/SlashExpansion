package com.lisheng.slashexpansion.util;

import com.lisheng.slashexpansion.config.SlashExpansionConfig;
import com.lisheng.slashexpansion.specialeffect.Admin;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.function.BooleanSupplier;

/**
 * 供 Mixin 使用的管理员保护判定。
 *
 * <p>Mixin 会在非常早的阶段被加载，因此这里对所有可能尚未初始化的依赖都做了容错：
 * 配置尚未加载、类尚未就绪时一律视为「未开启保护」。</p>
 */
public final class AdminProtection {

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
