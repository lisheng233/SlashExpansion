package com.lisheng.slashexpansion.net;

import com.lisheng.slashexpansion.SlashExpansion;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** 服务端 -> 客户端：屏幕效果（闪光、暗角、镜头震动）。1.21 用 CustomPacketPayload。 */
public record SfxMessage(int color, float intensity, int ticks, float shake) implements CustomPacketPayload {

    public static final Type<SfxMessage> TYPE = new Type<>(SlashExpansion.prefix("sfx"));

    public static final StreamCodec<FriendlyByteBuf, SfxMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SfxMessage::color,
            ByteBufCodecs.FLOAT, SfxMessage::intensity,
            ByteBufCodecs.VAR_INT, SfxMessage::ticks,
            ByteBufCodecs.FLOAT, SfxMessage::shake,
            SfxMessage::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}