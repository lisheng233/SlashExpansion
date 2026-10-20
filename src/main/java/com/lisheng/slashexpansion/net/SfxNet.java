package com.lisheng.slashexpansion.net;

import com.lisheng.slashexpansion.SlashExpansion;
import com.lisheng.slashexpansion.config.SlashExpansionConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

/**
 * 本模组的网络通道。目前只用来下发屏幕特效。
 */
public final class SfxNet {

    private static final String PROTOCOL = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            SlashExpansion.prefix("main"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals);

    public static void register() {
        CHANNEL.messageBuilder(SfxMessage.class, 0, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(SfxMessage::encode)
                .decoder(SfxMessage::decode)
                .consumerMainThread(SfxNet::handle)
                .add();
    }

    private static void handle(SfxMessage message, Supplier<NetworkEvent.Context> context) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> com.lisheng.slashexpansion.client.ClientSfx.handle(message));
        context.get().setPacketHandled(true);
    }

    /**
     * 向中心点附近的玩家下发屏幕效果。
     *
     * @param color     ARGB
     * @param intensity 闪光强度（0~1）
     * @param ticks     持续刻数
     * @param shake     镜头震动幅度（度）
     */
    public static void screenEffect(Level level, Vec3 center, double radius,
                                    int color, float intensity, int ticks, float shake) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (!SlashExpansionConfig.Vfx.screenEffects()) return;

        float scale = SlashExpansionConfig.Vfx.screenEffectScale();
        if (scale <= 0.0F) return;

        float finalShake = SlashExpansionConfig.Vfx.screenShake() ? shake * scale : 0.0F;
        SfxMessage message = new SfxMessage(color, intensity * scale, Math.max(1, ticks), finalShake);

        double sqr = radius * radius;
        for (ServerPlayer player : serverLevel.players()) {
            if (player.distanceToSqr(center) <= sqr) {
                CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), message);
            }
        }
    }

    public static void screenEffect(Level level, Vec3 center, double radius, int color,
                                    float intensity, int ticks) {
        screenEffect(level, center, radius, color, intensity, ticks, 0.0F);
    }

    private SfxNet() {
    }
}
