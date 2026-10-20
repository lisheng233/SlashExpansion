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
 */
@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {

    @Shadow
    public ServerPlayer player;

    @Inject(method = "disconnect", at = @At("HEAD"), cancellable = true)
    private void slashexpansion$preventAdminKick(Component reason, CallbackInfo ci) {
        if (AdminProtection.shouldPreventKick(this.player)) {
            ci.cancel();
        }
    }
}
