package com.lisheng.slashexpansion.net;

import com.lisheng.slashexpansion.SlashExpansion;
import com.lisheng.slashexpansion.config.SlashExpansionConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/** 本模组的网络通道（1.21：RegisterPayloadHandlersEvent + CustomPacketPayload）。 */
public final class SfxNet {

    private static final String PROTOCOL = "1";

    /** 由主类在 MOD 事件总线上注册。 */
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL);
        // 1.21 移除了 DistExecutor：playToClient 只会在客户端执行，用 FMLEnvironment 兜一层保险
        registrar.playToClient(SfxMessage.TYPE, SfxMessage.STREAM_CODEC, (message, context) ->
                context.enqueueWork(() -> {
                    if (FMLEnvironment.dist == Dist.CLIENT) {
                        com.lisheng.slashexpansion.client.ClientSfx.handle(message);
                    }
                }));
    }

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
                PacketDistributor.sendToPlayer(player, message);
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