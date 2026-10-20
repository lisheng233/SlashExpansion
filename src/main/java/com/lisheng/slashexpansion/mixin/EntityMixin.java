package com.lisheng.slashexpansion.mixin;

import com.lisheng.slashexpansion.util.AdminProtection;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 拦截「被杀死」导致的实体移除，避免管理员被其他模组或指令直接移除。
 *
 * <p>只拦截 {@link Entity.RemovalReason#KILLED}，不干扰区块卸载、维度切换等正常流程。
 * 具体的放行规则（濒死、同 tick 重复请求等）见 {@link AdminProtection#shouldBlockRemoval}。</p>
 */
@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "remove", at = @At("HEAD"), cancellable = true)
    private void slashexpansion$guardAdminRemoval(Entity.RemovalReason reason, CallbackInfo ci) {
        if (reason == Entity.RemovalReason.KILLED && AdminProtection.shouldBlockRemoval((Entity) (Object) this)) {
            ci.cancel();
        }
    }
}
