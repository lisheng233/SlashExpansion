package com.lisheng.slashexpansion.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityInLevelCallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * 读取 {@link Entity} 内部记录 level 侧回调的字段。
 *
 * <p>用于绕过 {@link Entity#remove} 直接触发 level 侧移除回调，
 * 从而避开那些拦截 {@code Entity#remove} 的保护类模组。</p>
 */
@Mixin(Entity.class)
public interface EntityLevelCallbackAccessor {

    @Accessor("levelCallback")
    EntityInLevelCallback slashexpansion$getLevelCallback();
}
