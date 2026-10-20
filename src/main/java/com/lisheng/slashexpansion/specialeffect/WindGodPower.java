package com.lisheng.slashexpansion.specialeffect;

import com.lisheng.slashexpansion.config.SlashExpansionConfig;
import com.lisheng.slashexpansion.registry.SlashExpansionSpecialEffectsRegistry;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.capability.slashblade.BladeStateAccess;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * 风神的伟力，数值来自 {@code [se.wind_god_power]}。
 */
@net.neoforged.fml.common.EventBusSubscriber(modid = com.lisheng.slashexpansion.SlashExpansion.MOD_ID)
public class WindGodPower extends SpecialEffect {

    private static final ResourceLocation SPEED_MOD_ID = ResourceLocation.fromNamespaceAndPath("slashexpansion", "wind_god_power_speed");

    // 用于记录玩家原本的飞行速度，以便恢复
    private static final String ORIGINAL_FLY_SPEED_KEY = "wind_god_original_fly_speed";
    private static final String HAS_ACTIVE_EFFECT_KEY = "wind_god_power_active";

    public WindGodPower() {
        super(0, true, false);
    }

    // ★ 在玩家 tick 中检查是否持有 SE（主手），施加所有效果
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;

        int interval = Math.max(1, SlashExpansionConfig.Se.WindGodPower.checkInterval());
        if (player.tickCount % interval != 0) return;

        CompoundTag persistentData = player.getPersistentData();
        if (!persistentData.contains(HAS_ACTIVE_EFFECT_KEY)) {
            persistentData.putBoolean(HAS_ACTIVE_EFFECT_KEY, false);
        }

        // ★★★ 检查主手是否持有带有 SE 的刀 ★★★
        ItemStack blade = player.getMainHandItem();
        if (!(blade.getItem() instanceof ItemSlashBlade)) {
            if (persistentData.getBoolean(HAS_ACTIVE_EFFECT_KEY)) {
                removeAllEffects(player);
                persistentData.putBoolean(HAS_ACTIVE_EFFECT_KEY, false);
            }
            return;
        }

        boolean hasSE = BladeStateAccess.of(blade)
                .map(state -> state.hasSpecialEffect(SlashExpansionSpecialEffectsRegistry.WIND_GOD_POWER.getKey().location()))
                .orElse(false);
        boolean wasActive = persistentData.getBoolean(HAS_ACTIVE_EFFECT_KEY);
        if (!hasSE) {
            if (wasActive) {
                removeAllEffects(player);
                persistentData.putBoolean(HAS_ACTIVE_EFFECT_KEY, false);
            }
            return;
        }

        // ★ 1. 跳跃提升（只加短暂持续时间，反复刷新）
        player.addEffect(new MobEffectInstance(MobEffects.JUMP,
                SlashExpansionConfig.Se.WindGodPower.jumpDuration(),
                SlashExpansionConfig.Se.WindGodPower.jumpAmplifier(),
                false, false, false));

        // ★ 2. 移动速度加成
        applySpeedModifier(player);

        if (!player.getAbilities().mayfly) {
            player.getAbilities().mayfly = true;
            player.onUpdateAbilities();
        }

        // ★ 3. 飞行速度倍率
        if (player.getAbilities().mayfly) {
            if (!persistentData.contains(ORIGINAL_FLY_SPEED_KEY)) {
                persistentData.putFloat(ORIGINAL_FLY_SPEED_KEY, player.getAbilities().getFlyingSpeed());
            }
            float original = persistentData.getFloat(ORIGINAL_FLY_SPEED_KEY);
            if (original <= 0) original = 0.05f; // 默认飞行速度
            player.getAbilities().setFlyingSpeed(original * SlashExpansionConfig.Se.WindGodPower.flySpeedMultiplier());
            player.onUpdateAbilities();
        }
        persistentData.putBoolean(HAS_ACTIVE_EFFECT_KEY, true);
    }

    private static void applySpeedModifier(Player player) {
        AttributeInstance speedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr == null) return;

        double amount = SlashExpansionConfig.Se.WindGodPower.moveSpeedMultiplier();
        AttributeModifier existing = speedAttr.getModifier(SPEED_MOD_ID);
        if (existing == null) {
            speedAttr.addTransientModifier(new AttributeModifier(SPEED_MOD_ID, amount, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        } else if (Double.compare(existing.amount(), amount) != 0) {
            speedAttr.removeModifier(SPEED_MOD_ID);
            speedAttr.addTransientModifier(new AttributeModifier(SPEED_MOD_ID, amount, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        }
    }

    // ★ 免疫摔落伤害（不改变下落速度）
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingFall(LivingFallEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        ItemStack blade = player.getMainHandItem();
        if (!(blade.getItem() instanceof ItemSlashBlade)) return;

        boolean hasSE = BladeStateAccess.of(blade)
                .map(state -> state.hasSpecialEffect(SlashExpansionSpecialEffectsRegistry.WIND_GOD_POWER.getKey().location()))
                .orElse(false);

        if (hasSE) {
            event.setCanceled(true);
            event.setDamageMultiplier(0.0F);
        }
    }

    private static void removeAllEffects(Player player) {
        AttributeInstance speedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null && speedAttr.getModifier(SPEED_MOD_ID) != null) {
            speedAttr.removeModifier(SPEED_MOD_ID);
        }

        if (player.getAbilities().mayfly && !player.isCreative() && !player.isSpectator()) {
            player.getAbilities().mayfly = false;
            player.getAbilities().flying = false;
            player.onUpdateAbilities();
        }

        if (player.getPersistentData().contains(ORIGINAL_FLY_SPEED_KEY)) {
            float original = player.getPersistentData().getFloat(ORIGINAL_FLY_SPEED_KEY);
            player.getAbilities().setFlyingSpeed(original);
            player.getPersistentData().remove(ORIGINAL_FLY_SPEED_KEY);
            player.onUpdateAbilities();
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;
        CompoundTag persistentData = player.getPersistentData();
        if (persistentData.getBoolean(HAS_ACTIVE_EFFECT_KEY)) {
            removeAllEffects(player);
            persistentData.putBoolean(HAS_ACTIVE_EFFECT_KEY, false);
        }
    }
}
