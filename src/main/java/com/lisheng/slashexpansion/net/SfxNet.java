package com.lisheng.slashexpansion.net;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * 屏幕特效下发（1.21.1 占位版）。
 *
 * <p>1.21 的 NeoForge 用 {@code RegisterPayloadHandlersEvent + CustomPacketPayload}
 * 取代了 SimpleChannel，阶段 2c 会按新 API 重写。</p>
 */
public final class SfxNet {

    public static void screenEffect(Level level, Vec3 center, double radius,
                                    int color, float intensity, int ticks, float shake) {
        // TODO(phase2c): CustomPacketPayload 通道
    }

    public static void screenEffect(Level level, Vec3 center, double radius, int color,
                                    float intensity, int ticks) {
        screenEffect(level, center, radius, color, intensity, ticks, 0.0F);
    }

    public static void register() {
        // TODO(phase2c)
    }

    private SfxNet() {
    }
}