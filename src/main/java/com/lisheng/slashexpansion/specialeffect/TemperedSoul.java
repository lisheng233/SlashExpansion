package com.lisheng.slashexpansion.specialeffect;

import com.lisheng.slashexpansion.config.SlashExpansionConfig;
import com.lisheng.slashexpansion.registry.SlashExpansionSpecialEffectsRegistry;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 千锤百炼：耐久消耗降低、击杀回复耐久、锻造收益提升（等效额外锻造数）。
 * 数值来自 {@code [se.tempered_soul]}。
 */
@Mod.EventBusSubscriber
public class TemperedSoul extends SpecialEffect {

    private static final String LAST_DAMAGE_KEY = "TemperedLastDamage";
    private static final String REFUND_PENDING_KEY = "TemperedRefundPending";

    public TemperedSoul() {
        super(0, true, false);
    }

    private static boolean bladeHasSE(ItemStack blade) {
        if (!(blade.getItem() instanceof ItemSlashBlade)) return false;
        return blade.getCapability(ItemSlashBlade.BLADESTATE)
                .map(state -> state.hasSpecialEffect(SlashExpansionSpecialEffectsRegistry.TEMPERED_SOUL.getId()))
                .orElse(false);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        if (player.level().isClientSide()) return;

        ItemStack blade = player.getMainHandItem();
        if (!bladeHasSE(blade)) return;

        reduceDurabilityCost(player, blade);
    }

    /**
     * 耐久消耗减免：记录上次耐久值，按比例把损耗「退还」回去。
     * 小数部分累积在 NBT 中，避免每次只掉 1 点时因取整而失效。
     */
    private static void reduceDurabilityCost(Player player, ItemStack blade) {
        double multiplier = SlashExpansionConfig.Se.TemperedSoul.durabilityCostMultiplier();
        if (multiplier >= 1.0) return;

        int current = blade.getDamageValue();
        CompoundTag tag = blade.getOrCreateTag();

        if (!tag.contains(LAST_DAMAGE_KEY)) {
            tag.putInt(LAST_DAMAGE_KEY, current);
            return;
        }

        int last = tag.getInt(LAST_DAMAGE_KEY);
        if (current > last) {
            float pending = tag.getFloat(REFUND_PENDING_KEY)
                    + (current - last) * (float) (1.0 - multiplier);
            int refund = (int) pending;
            tag.putFloat(REFUND_PENDING_KEY, pending - refund);
            if (refund > 0) {
                blade.setDamageValue(Math.max(0, current - refund));
                player.getInventory().setChanged();
            }
        }

        tag.putInt(LAST_DAMAGE_KEY, blade.getDamageValue());
    }

    /** 击杀敌人恢复耐久。 */
    @SubscribeEvent
    public static void onKill(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        ItemStack blade = player.getMainHandItem();
        if (!bladeHasSE(blade)) return;

        int repair = SlashExpansionConfig.Se.TemperedSoul.durabilityPerKill();
        if (repair <= 0) return;

        blade.setDamageValue(Math.max(0, blade.getDamageValue() - repair));
        player.getInventory().setChanged();
    }
}
