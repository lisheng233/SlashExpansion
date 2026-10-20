package com.lisheng.slashexpansion.specialeffect;

import com.lisheng.slashexpansion.config.SlashExpansionConfig;
import com.lisheng.slashexpansion.registry.SlashExpansionSpecialEffectsRegistry;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.bus.api.SubscribeEvent;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * 虚空行者：站在方块边缘时悬浮、末地移速加成、对末影人伤害加成。
 * 数值来自 {@code [se.void_walker]}。
 */
@net.neoforged.fml.common.EventBusSubscriber(modid = com.lisheng.slashexpansion.SlashExpansion.MOD_ID)
public class VoidWalker extends SpecialEffect {

    private static final UUID SPEED_MOD_ID = UUID.nameUUIDFromBytes(
            "slashexpansion:void_walker_speed".getBytes(StandardCharsets.UTF_8));
    private static final String SPEED_MOD_NAME = "void_walker_end_speed";

    public VoidWalker() {
        super(0, true, false);
    }

    private static boolean bladeHasSE(ItemStack blade) {
        if (!(blade.getItem() instanceof ItemSlashBlade)) return false;
        return BladeStateAccess.of(blade)
                .map(state -> state.hasSpecialEffect(SlashExpansionSpecialEffectsRegistry.VOID_WALKER.getKey().location()))
                .orElse(false);
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        if (player.level().isClientSide()) return;

        boolean hasSE = bladeHasSE(player.getMainHandItem());
        if (!hasSE) {
            removeSpeed(player);
            return;
        }

        applyEndSpeed(player);
        hover(player);
    }

    private static void applyEndSpeed(Player player) {
        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) return;

        boolean inEnd = player.level().dimension() == Level.END;
        if (!inEnd) {
            if (speed.getModifier(SPEED_MOD_ID) != null) {
                speed.removeModifier(SPEED_MOD_ID);
            }
            return;
        }

        double amount = SlashExpansionConfig.Se.VoidWalker.endSpeedBonus();
        AttributeModifier existing = speed.getModifier(SPEED_MOD_ID);
        if (existing == null) {
            speed.addTransientModifier(new AttributeModifier(
                    SPEED_MOD_ID, SPEED_MOD_NAME, amount, AttributeModifier.Operation.MULTIPLY_BASE));
        } else if (Double.compare(existing.getAmount(), amount) != 0) {
            speed.removeModifier(SPEED_MOD_ID);
            speed.addTransientModifier(new AttributeModifier(
                    SPEED_MOD_ID, SPEED_MOD_NAME, amount, AttributeModifier.Operation.MULTIPLY_BASE));
        }
    }

    private static void removeSpeed(Player player) {
        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null && speed.getModifier(SPEED_MOD_ID) != null) {
            speed.removeModifier(SPEED_MOD_ID);
        }
    }

    /** 方块边缘悬浮：脚下没有支撑方块时抵消下落速度。 */
    private static void hover(Player player) {
        if (!SlashExpansionConfig.Se.VoidWalker.hoverEnabled()) return;

        int interval = Math.max(1, SlashExpansionConfig.Se.VoidWalker.hoverCheckInterval());
        if (player.tickCount % interval != 0) return;

        if (player.onGround() || player.isPassenger() || player.isFallFlying()
                || player.isInWater() || player.isInLava() || player.onClimbable()
                || player.getAbilities().flying || player.isSpectator() || player.isCreative()
                || player.isCrouching()) {
            return;
        }

        Vec3 motion = player.getDeltaMovement();
        if (motion.y >= 0) return;

        int depth = (int) Math.ceil(SlashExpansionConfig.Se.VoidWalker.hoverSearchDepth());
        BlockPos base = player.blockPosition();
        for (int i = 1; i <= depth; i++) {
            if (!player.level().getBlockState(base.below(i)).isAir()) {
                return;
            }
        }

        player.setDeltaMovement(motion.x, 0.0, motion.z);
        player.fallDistance = 0.0F;
        player.hurtMarked = true;
    }

    /** 攻击末影人时伤害加成。 */
    @SubscribeEvent
    public static void onLivingHurt(LivingIncomingDamageEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        if (!(event.getEntity() instanceof EnderMan)) return;
        if (!bladeHasSE(player.getMainHandItem())) return;

        event.setAmount(event.getAmount() * SlashExpansionConfig.Se.VoidWalker.endermanDamageMultiplier());
    }
}
