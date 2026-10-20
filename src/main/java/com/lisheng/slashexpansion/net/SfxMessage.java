package com.lisheng.slashexpansion.net;

import net.minecraft.network.FriendlyByteBuf;

/**
 * 服务端 → 客户端：屏幕效果（闪光、暗角、镜头震动）。
 */
public record SfxMessage(int color, float intensity, int ticks, float shake) {

    public static void encode(SfxMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.color());
        buf.writeFloat(message.intensity());
        buf.writeInt(message.ticks());
        buf.writeFloat(message.shake());
    }

    public static SfxMessage decode(FriendlyByteBuf buf) {
        return new SfxMessage(buf.readInt(), buf.readFloat(), buf.readInt(), buf.readFloat());
    }
}
