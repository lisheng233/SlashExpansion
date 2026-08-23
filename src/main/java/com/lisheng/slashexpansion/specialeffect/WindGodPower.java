package com.lisheng.slashexpansion.specialeffect;

import com.lisheng.slashexpansion.registry.SlashExpansionSpecialEffectsRegistry;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.Random;

@Mod.EventBusSubscriber
public class WindGodPower extends SpecialEffect {

    private static final UUID SPEED_MOD_ID = UUID.nameUUIDFromBytes(
            "slashexpansion:wind_god_power_speed".getBytes(StandardCharsets.UTF_8)
    );
    private static final AttributeModifier SPEED_MOD = new AttributeModifier(
            SPEED_MOD_ID, "wind_god_speed", 1.0, AttributeModifier.Operation.MULTIPLY_BASE
    );
    // 用于记录玩家原本的飞行速度，以便恢复
    private static final String ORIGINAL_FLY_SPEED_KEY = "wind_god_original_fly_speed";

    public WindGodPower() {
        super(0, true, false);
    }

    // ★ 在玩家 tick 中检查是否持有 SE（主手），施加所有效果
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        if (player.level().isClientSide()) return;
        if (new Random().nextInt(100) >20) return; // ★ 每5 tick检查一次，减少性能消耗

        // ★★★ 检查主手是否持有带有 SE 的刀 ★★★
        ItemStack blade = player.getMainHandItem();
        if (!(blade.getItem() instanceof ItemSlashBlade)) {
            removeAllEffects(player);
            return;
        }

        boolean hasSE = blade.getCapability(ItemSlashBlade.BLADESTATE)
                .map(state -> state.hasSpecialEffect(SlashExpansionSpecialEffectsRegistry.WIND_GOD_POWER.getId()))
                .orElse(false);

        if (!hasSE) {
            removeAllEffects(player);
            return;
        }

        // ★ 1. 跳跃提升 III（只加5秒，刷新持续）
        player.addEffect(new MobEffectInstance(MobEffects.JUMP, 60, 2, false, false,false));

        // ★ 2. 移动速度 +100%
        var speedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null && !speedAttr.hasModifier(SPEED_MOD)) {
            speedAttr.addTransientModifier(SPEED_MOD);
        }
        if (!player.getAbilities().mayfly) {
            // 如果玩家没有飞行能力，给予飞行能力
            player.getAbilities().mayfly = true;
            player.onUpdateAbilities();
        }

        // ★ 3. 创造模式飞行速度 +150%（使用 setFlyingSpeed）
        if (player.getAbilities().mayfly) {
            // 获取当前飞行速度，如果没有保存过原始值则保存
            if (!player.getPersistentData().contains(ORIGINAL_FLY_SPEED_KEY)) {
                player.getPersistentData().putFloat(ORIGINAL_FLY_SPEED_KEY, player.getAbilities().getFlyingSpeed());
            }
            // 设置飞行速度 = 原始值 × 2.5（+150%）
            float original = player.getPersistentData().getFloat(ORIGINAL_FLY_SPEED_KEY);
            if (original <= 0) original = 0.05f; // 默认飞行速度
            player.getAbilities().setFlyingSpeed(original * 2.5f);
            // 更新能力（重要！否则不生效）
            player.onUpdateAbilities();
        }
    }

    // ★ 免疫摔落伤害（不改变下落速度）
    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        ItemStack blade = player.getMainHandItem();
        if (!(blade.getItem() instanceof ItemSlashBlade)) return;

        boolean hasSE = blade.getCapability(ItemSlashBlade.BLADESTATE)
                .map(state -> state.hasSpecialEffect(SlashExpansionSpecialEffectsRegistry.WIND_GOD_POWER.getId()))
                .orElse(false);

        if (hasSE) {
            // ★ 取消摔落伤害
            event.setCanceled(true);
            event.setDamageMultiplier(0.0F);
        }
    }

    private static void removeAllEffects(Player player) {
        // 移除移动速度加成
        var speedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null && speedAttr.hasModifier(SPEED_MOD)) {
            speedAttr.removeModifier(SPEED_MOD);
        }

        if (player.getAbilities().mayfly && !player.isCreative() && !player.isSpectator()) {
            // 如果玩家没有其他原因需要飞行能力，则移除飞行能力
            player.getAbilities().mayfly = false;
            player.onUpdateAbilities();
        }

        // ★ 恢复飞行速度
        if (player.getPersistentData().contains(ORIGINAL_FLY_SPEED_KEY)) {
            float original = player.getPersistentData().getFloat(ORIGINAL_FLY_SPEED_KEY);
            player.getAbilities().setFlyingSpeed(original);
            player.getPersistentData().remove(ORIGINAL_FLY_SPEED_KEY);
            player.onUpdateAbilities();
        }

        // 移除跳跃提升效果（但不要强制移除，让自然消失）
        // 如果玩家不再持有刀，跳跃提升会在5秒后自然消失
    }
}