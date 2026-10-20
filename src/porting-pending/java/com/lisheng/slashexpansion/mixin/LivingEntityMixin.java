package com.lisheng.slashexpansion.mixin;

import com.lisheng.slashexpansion.util.AdminProtection;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 拦截直接设置生命值的下调操作，作为伤害事件之外的最终兜底。
 *
 * <p>仅在「把生命值改小」时拦截，回血（如管理员模式每 tick 回满）不受影响。</p>
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "setHealth", at = @At("HEAD"), cancellable = true)
    private void slashexpansion$guardAdminHealth(float health, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (health < self.getHealth() && AdminProtection.isGuardActive(self)) {
            ci.cancel();
        }
    }
}
