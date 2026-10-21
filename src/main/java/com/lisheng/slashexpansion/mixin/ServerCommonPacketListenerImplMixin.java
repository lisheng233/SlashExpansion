package com.lisheng.slashexpansion.mixin;

import com.lisheng.slashexpansion.util.AdminProtection;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 阻止服务器把处于管理员模式的玩家踢出。
 *
 * <p>1.21 里 {@code disconnect(Component)} 已上移到
 * {@link ServerCommonPacketListenerImpl}，所以注入点放在这里，
 * 再用 {@link ServerGamePacketListenerImpl#player} 取出玩家。</p>
 *
 * <p>以下情况一律放行，避免挡住关服或把服务器卡在异常状态：</p>
 * <ul>
 *   <li>服务器正在关闭；</li>
 *   <li>玩家实体已被移除（例如被其它指令 setRemoved 造成的幽灵状态）；</li>
 *   <li>玩家已经死亡/濒死。</li>
 * </ul>
 */
@Mixin(ServerCommonPacketListenerImpl.class)
public abstract class ServerCommonPacketListenerImplMixin {

    @Inject(method = "disconnect(Lnet/minecraft/network/chat/Component;)V", at = @At("HEAD"), cancellable = true)
    private void slashexpansion$preventAdminKick(Component reason, CallbackInfo ci) {
        if (!((Object) this instanceof ServerGamePacketListenerImpl impl)) {
            return;
        }

        ServerPlayer player = impl.player;
        if (player == null) {
            return;
        }
        if (player.server != null && !player.server.isRunning()) {
            return;
        }
        if (player.isRemoved() || player.isDeadOrDying()) {
            return;
        }
        if (AdminProtection.shouldPreventKick(player)) {
            ci.cancel();
        }
    }
}