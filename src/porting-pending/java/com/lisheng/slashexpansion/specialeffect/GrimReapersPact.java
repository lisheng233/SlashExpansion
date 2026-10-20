package com.lisheng.slashexpansion.specialeffect;

import com.lisheng.slashexpansion.config.SlashExpansionConfig;
import com.lisheng.slashexpansion.registry.SlashExpansionSpecialEffectsRegistry;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 死神之约：生命值低于阈值时攻击力提升，但持续流失生命值（不会致死）。
 * 数值来自 {@code [se.grim_reapers_pact]}。
 */
@Mod.EventBusSubscriber
public class GrimReapersPact extends SpecialEffect {

    public GrimReapersPact() {
        super(0, true, false);
    }

    private static boolean bladeHasSE(ItemStack blade) {
        if (!(blade.getItem() instanceof ItemSlashBlade)) return false;
        return blade.getCapability(ItemSlashBlade.BLADESTATE)
                .map(state -> state.hasSpecialEffect(SlashExpansionSpecialEffectsRegistry.GRIM_REAPERS_PACT.getId()))
                .orElse(false);
    }

    private static boolean isBelowThreshold(Player player) {
        return player.getHealth() <= player.getMaxHealth()
                * (float) SlashExpansionConfig.Se.GrimReapersPact.healthThreshold();
    }

    /** 每秒流失生命值（不会致死）。 */
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        if (player.level().isClientSide()) return;

        int interval = Math.max(1, SlashExpansionConfig.Se.GrimReapersPact.drainInterval());
        if (player.tickCount % interval != 0) return;

        if (!bladeHasSE(player.getMainHandItem())) return;
        if (!isBelowThreshold(player)) return;

        float drain = SlashExpansionConfig.Se.GrimReapersPact.healthDrainPerSecond();
        if (drain <= 0.0F) return;

        player.setHealth(Math.max(1.0F, player.getHealth() - drain));
    }

    /** 低血量时攻击力加成。 */
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        if (!bladeHasSE(player.getMainHandItem())) return;
        if (!isBelowThreshold(player)) return;

        double bonus = SlashExpansionConfig.Se.GrimReapersPact.attackBonus();
        event.setAmount(event.getAmount() * (float) (1.0 + bonus));
    }
}
