package com.lisheng.slashexpansion.mixin;

import com.lisheng.slashexpansion.util.AdminProtection;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 拦截「被杀死亡」导致的实体移除，避免管理员被其它模组或指令直接移除。
 *
 * <p>两个入口都拦：</p>
 * <ul>
 *   <li>{@code remove(KILLED)}：常规移除；</li>
 *   <li>{@code setRemoved(KILLED)}：部分指令/模组直接调用，会绕过 {@code remove}
 *       把玩家留在连接里造成「半移除」幽灵玩家，进而卡死服务器。</li>
 * </ul>
 */
@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "remove", at = @At("HEAD"), cancellable = true)
    private void slashexpansion$guardAdminRemoval(Entity.RemovalReason reason, CallbackInfo ci) {
        if (reason == Entity.RemovalReason.KILLED && AdminProtection.shouldBlockRemoval((Entity) (Object) this)) {
            ci.cancel();
        }
    }

    @Inject(method = "setRemoved", at = @At("HEAD"), cancellable = true)
    private void slashexpansion$guardAdminSetRemoved(Entity.RemovalReason reason, CallbackInfo ci) {
        if (reason == Entity.RemovalReason.KILLED && AdminProtection.shouldBlockRemoval((Entity) (Object) this)) {
            ci.cancel();
        }
    }
}