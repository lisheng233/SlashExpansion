package com.lisheng.slashexpansion.specialeffect;

import com.lisheng.slashexpansion.SlashExpansion;
import com.lisheng.slashexpansion.config.SlashExpansionConfig;
import com.lisheng.slashexpansion.registry.SlashExpansionSpecialEffectsRegistry;
import mods.flammpfeil.slashblade.capability.slashblade.BladeStateAccess;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * 烈焰之心：攻击点燃敌人、免疫火焰伤害、在下界时移速加成。
 * 数值来自 {@code [se.flame_heart]}。
 */
@EventBusSubscriber(modid = SlashExpansion.MOD_ID)
public class FlameHeart extends SpecialEffect {

    // 1.21 的 AttributeModifier 改用 ResourceLocation 作为 id
    private static final ResourceLocation SPEED_MOD_ID = SlashExpansion.prefix("flame_heart_speed");

    public FlameHeart() {
        super(0, true, false);
    }

    private static boolean hasSE(ItemStack blade) {
        if (!(blade.getItem() instanceof ItemSlashBlade)) return false;
        return BladeStateAccess.of(blade)
                .map(state -> state.hasSpecialEffect(SlashExpansionSpecialEffectsRegistry.FLAME_HEART.getKey().location()))
                .orElse(false);
    }

    /** 攻击点燃目标。 */
    @SubscribeEvent
    public static void onHitEntity(SlashBladeEvent.HitEvent event) {
        boolean hasSE = event.getSlashBladeState()
                .hasSpecialEffect(SlashExpansionSpecialEffectsRegistry.FLAME_HEART.getKey().location());
        if (!hasSE) return;

        int seconds = SlashExpansionConfig.Se.FlameHeart.igniteSeconds();
        if (seconds > 0) {
            event.getTarget().igniteForSeconds(seconds);
        }
    }

    /** 免疫火焰伤害。 */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        if (!SlashExpansionConfig.Se.FlameHeart.immuneToFire()) return;
        if (!(event.getEntity() instanceof Player player)) return;
        if (!hasSE(player.getMainHandItem())) return;

        if (event.getSource().is(DamageTypes.IN_FIRE)
                || event.getSource().is(DamageTypes.ON_FIRE)
                || event.getSource().is(DamageTypes.LAVA)
                || event.getSource().is(DamageTypes.HOT_FLOOR)
                || event.getSource().is(DamageTypes.FIREBALL)
                || event.getSource().is(DamageTypes.UNATTRIBUTED_FIREBALL)) {
            event.setCanceled(true);
            player.clearFire();
        }
    }

    /** 下界移速加成。 */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
        if (player.tickCount % 20 != 0) return;

        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) return;

        boolean active = hasSE(player.getMainHandItem())
                && player.level().dimension() == Level.NETHER;

        if (active) {
            double amount = SlashExpansionConfig.Se.FlameHeart.netherSpeedBonus();
            AttributeModifier existing = speed.getModifier(SPEED_MOD_ID);
            if (existing == null || Double.compare(existing.amount(), amount) != 0) {
                speed.removeModifier(SPEED_MOD_ID);
                speed.addTransientModifier(new AttributeModifier(
                        SPEED_MOD_ID, amount, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            }
        } else if (speed.getModifier(SPEED_MOD_ID) != null) {
            speed.removeModifier(SPEED_MOD_ID);
        }
    }
}