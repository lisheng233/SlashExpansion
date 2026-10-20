package com.lisheng.slashexpansion.mixin;

import com.lisheng.slashexpansion.util.AdminProtection;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** 兜底拦截「直接把生命值改小」的操作；回血不受影响。 */
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