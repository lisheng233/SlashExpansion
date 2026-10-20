package com.lisheng.slashexpansion.specialeffect;

import com.lisheng.slashexpansion.config.SlashExpansionConfig;
import com.lisheng.slashexpansion.registry.SlashExpansionSpecialEffectsRegistry;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 千锤百炼：耐久消耗降低、击杀回复耐久、以「杀敌数 + 耀魂」换取额外锻造。
 *
 * <p>锻造规则（均可在 {@code [se.tempered_soul]} 中调整）：每累计 10 点杀敌数，
 * 消耗 100 耀魂提升 1 级锻造，最多提升 100 次。</p>
 */
@Mod.EventBusSubscriber
public class TemperedSoul extends SpecialEffect {

    private static final String LAST_DAMAGE_KEY = "TemperedLastDamage";
    private static final String REFUND_PENDING_KEY = "TemperedRefundPending";
    private static final String REFINE_BASELINE_KEY = "TemperedRefineKillBaseline";
    private static final String REFINE_UPGRADES_KEY = "TemperedRefineUpgrades";

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
        tryRefine(player, blade);
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

    /**
     * 杀敌数换锻造：每 {@code killsPerRefine} 点杀敌数消耗 {@code proudSoulCost} 耀魂提升 1 级锻造。
     * 第一次生效时以当前杀敌数为基准，避免旧存档瞬间白嫖大量锻造。
     */
    private static void tryRefine(Player player, ItemStack blade) {
        ISlashBladeState state = blade.getCapability(ItemSlashBlade.BLADESTATE).orElse(null);
        if (state == null) return;

        int killsPerRefine = Math.max(1, SlashExpansionConfig.Se.TemperedSoul.killsPerRefine());
        int proudSoulCost = Math.max(0, SlashExpansionConfig.Se.TemperedSoul.proudSoulCost());
        int maxRefines = Math.max(0, SlashExpansionConfig.Se.TemperedSoul.maxRefines());
        if (maxRefines <= 0) return;

        CompoundTag tag = blade.getOrCreateTag();
        if (!tag.contains(REFINE_BASELINE_KEY)) {
            tag.putInt(REFINE_BASELINE_KEY, state.getKillCount());
            tag.putInt(REFINE_UPGRADES_KEY, 0);
            return;
        }

        int baseline = tag.getInt(REFINE_BASELINE_KEY);
        int upgrades = tag.getInt(REFINE_UPGRADES_KEY);
        if (upgrades >= maxRefines) return;

        int killCount = state.getKillCount();
        boolean upgraded = false;

        while (upgrades < maxRefines && killCount >= baseline + killsPerRefine) {
            if (state.getProudSoulCount() < proudSoulCost) {
                break;
            }
            state.setProudSoulCount(state.getProudSoulCount() - proudSoulCost);
            state.setRefine(state.getRefine() + 1);
            upgrades++;
            baseline += killsPerRefine;
            upgraded = true;
        }

        tag.putInt(REFINE_BASELINE_KEY, baseline);
        tag.putInt(REFINE_UPGRADES_KEY, upgrades);

        if (upgraded) {
            player.getInventory().setChanged();
            if (player.level() instanceof ServerLevel serverLevel) {
                serverLevel.playSound(null, player.blockPosition(), SoundEvents.ANVIL_USE,
                        SoundSource.PLAYERS, 0.8F, 1.4F);
                serverLevel.sendParticles(ParticleTypes.CRIT,
                        player.getX(), player.getY() + 1.0, player.getZ(),
                        16, 0.4, 0.4, 0.4, 0.1);
            }
        }
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
