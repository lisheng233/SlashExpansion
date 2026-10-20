package com.lisheng.slashexpansion.specialeffect;

import com.lisheng.slashexpansion.config.SlashExpansionConfig;
import com.lisheng.slashexpansion.mixin.EntityLevelCallbackAccessor;
import com.lisheng.slashexpansion.registry.SlashExpansionSpecialEffectsRegistry;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.entity.EntityInLevelCallback;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理员之力，数值来自 {@code [se.admin]}。
 *
 * <p>防御：免疫所有伤害（含可穿透无敌的伤害类型）、免疫击退、禁止获得负面效果、取消死亡。</p>
 * <p>秒杀：近战攻击直接秒杀目标（不依赖伤害事件，因此免疫某类伤害的目标也会被秒杀），
 * 并可按配置的溅射半径一并秒杀周围敌人。</p>
 */
@Mod.EventBusSubscriber
public class Admin extends SpecialEffect {

    private static final String ADMIN_TAG = "SlashExpansion_AdminMode";
    private static final String ADMIN_ENABLED_TAG = "AdminEnabled";

    private static final int MAX_FOOD_LEVEL = 20;

    private static final ConcurrentHashMap<UUID, Integer> messageCooldown = new ConcurrentHashMap<>();

    /** 延迟 1 tick 的移除兜底任务（先让 die() 的自然流程走完）。 */
    private record PendingRemoval(Entity entity, int executeAtTick) {
    }

    private static final List<PendingRemoval> PENDING_REMOVALS = new ArrayList<>();

    public Admin() {
        super(0, true, false);
    }

    private static int requiredPermissionLevel() {
        return SlashExpansionConfig.Se.Admin.permissionLevel();
    }

    private static boolean bladeHasSE(Player player) {
        ItemStack mainHand = player.getMainHandItem();
        if (!(mainHand.getItem() instanceof ItemSlashBlade)) return false;
        return mainHand.getCapability(ItemSlashBlade.BLADESTATE)
                .map(state -> state.hasSpecialEffect(SlashExpansionSpecialEffectsRegistry.ADMIN.getId()))
                .orElse(false);
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

        boolean shouldHaveAdmin = bladeHasSE(player) && player.hasPermissions(requiredPermissionLevel());
        boolean isInAdminMode = isAdminModeEnabled(player);

        if (shouldHaveAdmin && !isInAdminMode) {
            // 进入管理员模式
            setAdminMode(player, true);
            applyAdminEffects(player);
        } else if (!shouldHaveAdmin && isInAdminMode) {
            // 退出管理员模式
            setAdminMode(player, false);
            revokeAdminAbilities(player);
        } else if (shouldHaveAdmin) {
            // 持续应用效果
            applyAdminEffects(player);
        }
    }

    // ================================================================
    // 防御
    // ================================================================

    /** 在伤害判定的最早阶段直接取消，避免命中效果、击退等副作用。 */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingAttack(LivingAttackEvent event) {
        if (!SlashExpansionConfig.Se.Admin.damageImmunity()) return;
        if (!(event.getEntity() instanceof Player player)) return;
        if (isAdminModeEnabled(player)) {
            event.setCanceled(true);
        }
    }

    /** 免疫击退。 */
    @SubscribeEvent
    public static void onLivingKnockBack(LivingKnockBackEvent event) {
        if (!SlashExpansionConfig.Se.Admin.damageImmunity()) return;
        if (!(event.getEntity() instanceof Player player)) return;
        if (isAdminModeEnabled(player)) {
            event.setCanceled(true);
        }
    }

    /** 禁止获得任何负面效果。 */
    @SubscribeEvent
    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        if (!SlashExpansionConfig.Se.Admin.preventNegativeEffects()) return;
        if (!(event.getEntity() instanceof Player player)) return;
        if (!isAdminModeEnabled(player)) return;
        if (!event.getEffectInstance().getEffect().isBeneficial()) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();

        if (!(entity instanceof Player player)) {
            return;
        }

        if (!bladeHasSE(player)) {
            return;
        }

        if (!player.hasPermissions(requiredPermissionLevel()) || !isAdminModeEnabled(player)) {
            event.setCanceled(false);
            sendPermissionDeniedMessage(player);
            return;
        }

        if (SlashExpansionConfig.Se.Admin.damageImmunity()) {
            event.setCanceled(true);
        }
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

    // ================================================================
    // 秒杀
    // ================================================================

    /**
     * 近战攻击直接秒杀。
     * 该事件在伤害计算之前触发，因此即使目标免疫某类伤害（例如烈焰人免疫火焰）也能被秒杀。
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onAttackEntity(AttackEntityEvent event) {
        if (!SlashExpansionConfig.Se.Admin.instaKill()) return;

        Player player = event.getEntity();
        if (!isAdminModeEnabled(player)) return;

        if (event.getTarget() instanceof LivingEntity target) {
            adminKill(player, target);
        }
    }

    /** 非近战来源（爆炸、荆棘、弹射物等）同样秒杀。 */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void killEntity(LivingHurtEvent event) {
        if (!SlashExpansionConfig.Se.Admin.instaKill()) return;

        if (!(event.getSource().getEntity() instanceof Player player)) return;
        if (!isAdminModeEnabled(player)) return;

        LivingEntity target = event.getEntity();
        if (target != null) {
            adminKill(player, target);
        }
    }

    private static void adminKill(Player player, LivingEntity target) {
        if (target == player || !target.isAlive()) return;
        if (target instanceof Player other && (!SlashExpansionConfig.Se.Admin.instaKillPlayers()
                || other.isCreative() || other.isSpectator())) {
            return;
        }

        DamageSource source = player.damageSources().playerAttack(player);
        instantKill(target, source);

        double radius = SlashExpansionConfig.Se.Admin.instaKillRadius();
        if (radius <= 0.0D) return;

        AABB box = target.getBoundingBox().inflate(radius);
        List<LivingEntity> nearbyTargets = target.level().getEntitiesOfClass(LivingEntity.class, box,
                e -> e != player && e != target && e.isAlive()
                        && (!(e instanceof Player p) || (SlashExpansionConfig.Se.Admin.instaKillPlayers()
                                && !p.isCreative() && !p.isSpectator())));

        for (LivingEntity nearby : nearbyTargets) {
            instantKill(nearby, source);
        }
    }

    private static void instantKill(LivingEntity target, DamageSource source) {
        target.invulnerableTime = 0;
        target.setHealth(0.0F);
        target.die(source);
        // 兜底：延迟 1 tick 检查，若仍未从世界移除则从 level 侧强制移除
        scheduleRemovalCheck(target);
    }

    private static void scheduleRemovalCheck(Entity entity) {
        // 玩家的死亡/重生流程交给原版处理，避免破坏死亡界面与重生
        if (entity instanceof Player) {
            return;
        }
        if (!(entity.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        PENDING_REMOVALS.add(new PendingRemoval(entity, serverLevel.getServer().getTickCount() + 1));
    }

    /** 延迟 1 tick 的移除兜底：检查目标是否真的从世界消失。 */
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || PENDING_REMOVALS.isEmpty()) {
            return;
        }

        int now = event.getServer().getTickCount();
        for (int i = PENDING_REMOVALS.size() - 1; i >= 0; i--) {
            PendingRemoval pending = PENDING_REMOVALS.get(i);
            if (pending.executeAtTick() > now) {
                continue;
            }
            PENDING_REMOVALS.remove(i);

            Entity entity = pending.entity();
            if (entity == null || entity.isRemoved()) {
                continue;
            }
            forceLevelRemove(entity);
        }
    }

    /**
     * 从 level 侧强制移除实体。
     *
     * <p>刻意不调用 {@link Entity#remove}：多数保护类模组都会拦截它。
     * 这里通过 {@link EntityLevelCallbackAccessor} 拿到实体内部的 level 侧回调，
     * 直接触发 {@code onRemove(KILLED)}——这会真正把实体从区块实体表、tick 列表与追踪表中摘掉；
     * 随后补发 {@link ClientboundRemoveEntitiesPacket} 让客户端立即删除实体。</p>
     */
    private static void forceLevelRemove(Entity entity) {
        // 不误伤处于管理员保护中的玩家
        if (entity instanceof Player player
                && isAdminModeEnabled(player)
                && SlashExpansionConfig.Se.Admin.damageImmunity()) {
            return;
        }

        if (entity instanceof EntityLevelCallbackAccessor accessor) {
            EntityInLevelCallback callback = accessor.slashexpansion$getLevelCallback();
            if (callback != null && callback != EntityInLevelCallback.NULL) {
                callback.onRemove(Entity.RemovalReason.KILLED);
                sendRemovalPacket(entity);
            }
        }

        // 统一标记移除状态；上面已把 levelCallback 置为 NULL，不会重复处理
        entity.setRemoved(Entity.RemovalReason.KILLED);
        entity.invalidateCaps();
    }

    /** 立即通知客户端删除该实体，避免出现残影。 */
    private static void sendRemovalPacket(Entity entity) {
        if (!(entity.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        ClientboundRemoveEntitiesPacket packet = new ClientboundRemoveEntitiesPacket(entity.getId());
        for (ServerPlayer player : serverLevel.players()) {
            player.connection.send(packet);
        }
    }

    // ================================================================
    // 状态与效果
    // ================================================================

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
        if (lastMessageTick != null
                && (currentTick - lastMessageTick) < SlashExpansionConfig.Se.Admin.messageCooldownTicks()) {
            return;
        }

        messageCooldown.put(playerId, currentTick);

        int level = requiredPermissionLevel();
        Component message = Component.literal("")
                .append(Component.literal("你没有权限使用管理员之力！").withStyle(ChatFormatting.RED))
                .append(Component.literal("\n"))
                .append(Component.literal("  → 需要 OP 权限等级 " + level).withStyle(ChatFormatting.GRAY))
                .append(Component.literal("  → 请联系服务器管理员获取权限").withStyle(ChatFormatting.GRAY));

        player.sendSystemMessage(message);

        Component actionBarMessage = Component.literal("")
                .append(Component.literal("⚠ ").withStyle(ChatFormatting.RED))
                .append(Component.literal("权限不足！").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD))
                .append(Component.literal(" 需要 OP 权限等级 " + level).withStyle(ChatFormatting.GRAY));

        player.displayClientMessage(actionBarMessage, true);
    }

    /**
     * 应用管理员效果
     */
    private static void applyAdminEffects(Player player) {
        // 1. 抗性提升效果
        player.addEffect(new MobEffectInstance(
                MobEffects.DAMAGE_RESISTANCE,
                SlashExpansionConfig.Se.Admin.resistanceDuration(),
                SlashExpansionConfig.Se.Admin.resistanceAmplifier(),
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

        // 5. 灭火并清除摔落累计
        if (player.isOnFire()) {
            player.clearFire();
        }
        player.fallDistance = 0.0F;

        // 6. 启用无敌模式
        if (!player.getAbilities().invulnerable) {
            player.getAbilities().invulnerable = true;
            player.onUpdateAbilities();
        }

        // 7. 启用飞行模式
        if (!player.getAbilities().mayfly) {
            player.getAbilities().mayfly = true;
            player.onUpdateAbilities();
        }

        // 8. 移除所有负面效果
        removeNegativeEffects(player);

        // 9. 自动获得经验
        if (SlashExpansionConfig.Se.Admin.autoExperience()) {
            int xpNeeded = player.getXpNeededForNextLevel() - player.totalExperience;
            int xpToGive = Math.max(1, xpNeeded);
            if ((Integer.MAX_VALUE - player.totalExperience) >= xpToGive) {
                player.giveExperiencePoints(xpToGive);
            }
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
}
