package com.lisheng.slashexpansion.specialeffect;

import com.lisheng.slashexpansion.registry.SlashExpansionSpecialEffectsRegistry;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber
public class Admin extends SpecialEffect {

    private static final String ADMIN_TAG = "SlashExpansion_AdminMode";
    private static final String ADMIN_ENABLED_TAG = "AdminEnabled";
    
    private static final int RESISTANCE_DURATION = 100;
    private static final int RESISTANCE_AMPLIFIER = 255;
    private static final int MAX_FOOD_LEVEL = 20;
    private static final int MESSAGE_COOLDOWN_TICKS = 200;

    private static final ConcurrentHashMap<UUID, Integer> messageCooldown = new ConcurrentHashMap<>();

    public Admin() {
        super(0, true, false);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Player player = event.player;

        if (player.level().isClientSide()) {
            return;
        }

        ItemStack mainHand = player.getMainHandItem();
        
        // 检查是否应该拥有管理员模式
        boolean shouldHaveAdmin = false;
        if (mainHand.getItem() instanceof ItemSlashBlade) {
            shouldHaveAdmin = mainHand.getCapability(ItemSlashBlade.BLADESTATE)
                    .map(state -> state.hasSpecialEffect(SlashExpansionSpecialEffectsRegistry.ADMIN.getId()))
                    .orElse(false) && player.hasPermissions(4);
        }

        boolean isInAdminMode = isAdminModeEnabled(player);

        if (shouldHaveAdmin && !isInAdminMode) {
            // 进入管理员模式
            setAdminMode(player, true);
            applyAdminEffects(player);
        } else if (!shouldHaveAdmin && isInAdminMode) {
            // 退出管理员模式
            setAdminMode(player, false);
            revokeAdminAbilities(player);
        } else if (shouldHaveAdmin && isInAdminMode) {
            // 持续应用效果
            applyAdminEffects(player);
        }
    }

    /**
     * 检查玩家是否处于管理员模式
     */
    public static boolean isAdminModeEnabled(Player player) {
        CompoundTag persistentData = player.getPersistentData();
        if (persistentData.contains(ADMIN_TAG)) {
            CompoundTag adminData = persistentData.getCompound(ADMIN_TAG);
            return adminData.getBoolean(ADMIN_ENABLED_TAG);
        }
        return false;
    }

    /**
     * 设置玩家管理员模式状态
     */
    private static void setAdminMode(Player player, boolean enabled) {
        CompoundTag persistentData = player.getPersistentData();
        CompoundTag adminData = persistentData.getCompound(ADMIN_TAG);
        adminData.putBoolean(ADMIN_ENABLED_TAG, enabled);
        persistentData.put(ADMIN_TAG, adminData);
    }

    /**
     * 发送权限不足提示消息
     */
    private static void sendPermissionDeniedMessage(Player player) {
        UUID playerId = player.getUUID();
        int currentTick = player.tickCount;

        Integer lastMessageTick = messageCooldown.get(playerId);
        if (lastMessageTick != null && (currentTick - lastMessageTick) < MESSAGE_COOLDOWN_TICKS) {
            return;
        }

        messageCooldown.put(playerId, currentTick);

        Component message = Component.literal("")
                .append(Component.literal("你没有权限使用管理员之力！").withStyle(ChatFormatting.RED))
                .append(Component.literal("\n"))
                .append(Component.literal("  → 需要 OP 权限等级 4").withStyle(ChatFormatting.GRAY))
                .append(Component.literal("  → 请联系服务器管理员获取权限").withStyle(ChatFormatting.GRAY));

        player.sendSystemMessage(message);

        Component actionBarMessage = Component.literal("")
                .append(Component.literal("⚠ ").withStyle(ChatFormatting.RED))
                .append(Component.literal("权限不足！").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD))
                .append(Component.literal(" 需要 OP 权限等级 4").withStyle(ChatFormatting.GRAY));

        player.displayClientMessage(actionBarMessage, true);
    }

    /**
     * 应用管理员效果
     */
    private static void applyAdminEffects(Player player) {
        // 1. 抗性提升效果
        player.addEffect(new MobEffectInstance(
                MobEffects.DAMAGE_RESISTANCE,
                RESISTANCE_DURATION,
                RESISTANCE_AMPLIFIER,
                false,
                false,
                false
        ));

        // 2. 生命值回满
        if (player.getHealth() < player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }

        // 3. 饱食度回满
        FoodData foodData = player.getFoodData();
        if (foodData.getFoodLevel() < MAX_FOOD_LEVEL) {
            foodData.setFoodLevel(MAX_FOOD_LEVEL);
            foodData.setSaturation(MAX_FOOD_LEVEL);
        }

        // 4. 氧气回满
        if (player.getAirSupply() < player.getMaxAirSupply()) {
            player.setAirSupply(player.getMaxAirSupply());
        }

        // 5. 启用无敌模式
        if (!player.getAbilities().invulnerable) {
            player.getAbilities().invulnerable = true;
            player.onUpdateAbilities();
        }

        // 6. 启用飞行模式
        if (!player.getAbilities().mayfly) {
            player.getAbilities().mayfly = true;
            player.onUpdateAbilities();
        }
        // 7. 移除所有负面效果
        removeNegativeEffects(player);

        // 8. 自动获得经验
        int xpNeeded = player.getXpNeededForNextLevel() - player.totalExperience;
        int xpToGive = Math.max(1, xpNeeded);
        if ((Integer.MAX_VALUE-player.totalExperience) >= xpToGive) {
            player.giveExperiencePoints(xpToGive);
        }

    }

    /**
     * 安全移除所有负面效果
     */
    public static void removeNegativeEffects(Player player) {
        List<MobEffect> effectsToRemove = new ArrayList<>();
        
        for (MobEffectInstance effect : player.getActiveEffects()) {
            if (!effect.getEffect().isBeneficial()) {
                effectsToRemove.add(effect.getEffect());
            }
        }
        
        for (MobEffect effect : effectsToRemove) {
            player.removeEffect(effect);
        }
    }

    /**
     * 撤销管理员能力
     */
    private static void revokeAdminAbilities(Player player) {
        if (player.getAbilities().invulnerable) {
            player.getAbilities().invulnerable = false;
            player.onUpdateAbilities();
        }

        if (player.getAbilities().mayfly && !player.isCreative() && !player.isSpectator()) {
            player.getAbilities().mayfly = false;
            player.getAbilities().flying = false;
            player.onUpdateAbilities();
        }
        
        // 清除管理员模式标记
        setAdminMode(player, false);
        messageCooldown.remove(player.getUUID());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerDie(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (isAdminModeEnabled(player)) {
            event.cancel();
            player.deathTime = 0;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void killEntity(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        if (isAdminModeEnabled(player)){
            entity.invulnerableTime=0;
            entity.setHealth(0);
            entity.die(event.getSource());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();

        if (!(entity instanceof Player player)) {
            return;
        }

        ItemStack mainHand = player.getMainHandItem();
        if (!(mainHand.getItem() instanceof ItemSlashBlade)) {
            return;
        }

        boolean hasAdminSE = mainHand.getCapability(ItemSlashBlade.BLADESTATE)
                .map(state -> state.hasSpecialEffect(SlashExpansionSpecialEffectsRegistry.ADMIN.getId()))
                .orElse(false);

        if (hasAdminSE) {
            if (!player.hasPermissions(4) || !isAdminModeEnabled(player)) {
                event.setCanceled(false);
                sendPermissionDeniedMessage(player);
                return;
            }
            event.setCanceled(true);
        }
    }
}