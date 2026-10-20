package com.lisheng.slashexpansion.mixin;

import com.lisheng.slashexpansion.util.AdminProtection;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 阻止服务器把处于管理员模式的玩家踢出（移动过快检测、违规检测、/kick 等都会走这里）。
 *
 * <p>以下情况一律放行，避免挡住关服或把服务器卡在异常状态：</p>
 * <ul>
 *   <li>服务器正在关闭；</li>
 *   <li>玩家实体已经被移除（例如被其他指令 setRemoved 造成的幽灵状态）；</li>
 *   <li>玩家已经死亡/濒死。</li>
 * </ul>
 */
@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {

    @Shadow
    public ServerPlayer player;

    @Inject(method = "disconnect", at = @At("HEAD"), cancellable = true)
    private void slashexpansion$preventAdminKick(Component reason, CallbackInfo ci) {
        if (this.player == null) {
            return;
        }
        if (this.player.server != null && !this.player.server.isRunning()) {
            return;
        }
        if (this.player.isRemoved() || this.player.isDeadOrDying()) {
            return;
        }
        if (AdminProtection.shouldPreventKick(this.player)) {
            ci.cancel();
        }
    }
}
