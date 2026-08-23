package com.lisheng.slashexpansion.specialeffect;

import com.lisheng.slashexpansion.registry.SlashExpansionSpecialEffectsRegistry;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber
public class AbyssWhisper extends SpecialEffect {

    public AbyssWhisper() {
        super(0, true, false);
    }

    // ★ 每 tick 检查，持续施加自身效果
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        if (player.level().isClientSide()) return;
        if (new Random().nextInt(100) >= 2) return; // 2% 概率触发

        var blade = player.getMainHandItem();
        if (!(blade.getItem() instanceof ItemSlashBlade)) return;

        boolean hasSE = blade.getCapability(ItemSlashBlade.BLADESTATE)
                .map(state -> state.hasSpecialEffect(SlashExpansionSpecialEffectsRegistry.ABYSS_WHISPER.getId()))
                .orElse(false);

        if (!hasSE) return;

        int xpLevel = player.experienceLevel;

        if (xpLevel <= 99) {
            // ★ 低等级：自身获得负面效果（持续）
            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 600, 0, false, false,false)); // 失明 (30s)
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0, false, false,false));
            player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 100, 0, false, false,false));
            player.addEffect(new MobEffectInstance(MobEffects.UNLUCK, 60, 4, false, false,false));
            player.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, 1, false, false,false));
            // 攻击时额外施加的负面效果在 onHit 中处理
        } else {
            // ★ 高等级：自身获得较轻负面效果
            player.addEffect(new MobEffectInstance(MobEffects.UNLUCK, 100, 2, false, false,false));
            player.addEffect(new MobEffectInstance(MobEffects.WITHER, 40, 0, false, false,false));
        }
    }

    // ★ 攻击时给目标施加效果
    @SubscribeEvent
    public static void onHitEntity(SlashBladeEvent.HitEvent event) {
        LivingEntity target = event.getTarget();

        boolean hasSE = event.getSlashBladeState()
                .hasSpecialEffect(SlashExpansionSpecialEffectsRegistry.ABYSS_WHISPER.getId());

        if (!hasSE) return;

        target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 600, 0, false, false,false)); // 失明 (30s)
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 240, 4, false, false,false)); // 缓慢 III (12s)
        target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 200, 2, false, false,false)); // 挖掘疲劳 III (10s)
        target.addEffect(new MobEffectInstance(MobEffects.WITHER, 200, 9, true, false)); // 凋零 X (10s)
        target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 360, 4, true, false)); // 虚弱 V (18s)
    }
}