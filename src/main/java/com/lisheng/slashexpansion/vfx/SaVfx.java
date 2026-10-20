package com.lisheng.slashexpansion.vfx;

import com.lisheng.slashexpansion.net.SfxNet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * 各个 SA 的动画编排。
 *
 * <p>每个方法都是一条“时间轴”：蓄势 → 主体 → 冲击 → 余韵，通过 {@link Vfx#play(Level, int, Vfx.Effect)}
 * 的延迟参数把多个原语叠在一起。伤害逻辑仍然留在 {@code specialattack} 包里，这里只负责好看。</p>
 */
public final class SaVfx {

    private static final double SCREEN_RADIUS = 40.0D;

    private SaVfx() {
    }

    // ==================================================================
    // 万剑归宗
    // ==================================================================

    public static void wanJianGuiZong(LivingEntity caster, List<LivingEntity> targets) {
        Level level = caster.level();
        Vec3 base = caster.position();

        // 脚下的召唤法阵 + 扩散光环
        Vfx.play(level, Vfx.rune(base, 7.5, Vfx.GOLD, 30, 0.045F, 0.2F));
        Vfx.play(level, Vfx.ring(base, 0.3D, 6.4D, Vfx.GOLD, 26, 12, 0.45F, 12));
        Vfx.play(level, Vfx.burst(base.add(0.0, 0.4, 0.0), Vfx.GOLD, 40, 1.8, 0.42F, 14));
        // 头顶聚气：剑气从天而降汇聚
        Vfx.play(level, 2, Vfx.implode(base, 7.5D, 0.6D, 9.0D, Vfx.WHITE, 22, 18, 0.32F, 12, 1.8D));
        Vfx.play(level, 6, Vfx.shell(base.add(0.0, 5.0, 0.0), Vfx.GOLD, 44, 4.2, 0.36F, 14));
        Vfx.play(level, 8, Vfx.orb(base.add(0.0, 7.0, 0.0), 1.4, Vfx.GOLD, 10, 14, 0.5F, 8, 0.5D));

        for (LivingEntity target : targets) {
            Vec3 tp = target.position().add(0.0, target.getBbHeight() * 0.5, 0.0);
            Vfx.play(level, 6, Vfx.pillar(tp.add(0.0, 8.0, 0.0), 8.0, 0.7, Vfx.GOLD, 9, 8, 0.3F, 8));
            Vfx.play(level, 10, Vfx.ring(tp, 0.3D, 2.6D, Vfx.GOLD, 14, 7, 0.36F, 9));
            Vfx.play(level, 12, Vfx.burst(tp, Vfx.WHITE, 18, 1.5, 0.34F, 10));
        }

        // 一把剑落下时的小特效
        sfx(level, base, SoundEvents.ENCHANTMENT_TABLE_USE, 0.9F, 0.7F);
        sfx(level, base, SoundEvents.PLAYER_ATTACK_SWEEP, 1.2F, 0.6F);
        SfxNet.screenEffect(level, base, SCREEN_RADIUS, Vfx.GOLD, 0.16F, 10, 0.9F);
    }

    // ==================================================================
    // 千本樱
    // ==================================================================

    public static void senbonzakura(LivingEntity caster, List<LivingEntity> targets) {
        Level level = caster.level();
        Vec3 base = caster.position();

        Vfx.play(level, Vfx.rune(base, 6.5, Vfx.PINK, 32, 0.06F, 0.25F));
        Vfx.play(level, Vfx.ring(base, 0.4D, 5.5D, Vfx.SAKURA, 24, 12, 0.4F, 14));
        Vfx.play(level, 2, Vfx.spiral(base.add(0.0, 0.5, 0.0), 1.0D, 5.0D, 0.0D, 7.0D,
                Vfx.SAKURA, 20, 26, 0.34F, 16, 2.2D));

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.CHERRY_LEAVES,
                    base.x, base.y + 2.5, base.z, Math.max(1, Vfx.count(40)), 4.0, 2.5, 4.0, 0.02);
        }

        for (LivingEntity target : targets) {
            Vec3 tp = target.position().add(0.0, target.getBbHeight() * 0.5, 0.0);
            Vfx.play(level, 4, Vfx.spiral(tp, 4.5D, 0.6D, 6.0D, 0.0D, Vfx.SAKURA, 16, 18, 0.32F, 14, 2.6D));
            Vfx.play(level, 10, Vfx.burst(tp, Vfx.PINK, 26, 1.7, 0.36F, 16));
            Vfx.play(level, 12, Vfx.halo(tp, new Vec3(0.0, 1.0, 0.0), 0.3D, 2.4D, Vfx.SAKURA, 12, 7, 0.32F, 10));
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.CHERRY_LEAVES,
                        tp.x, tp.y + 2.0, tp.z, Math.max(1, Vfx.count(22)), 1.6, 1.6, 1.6, 0.05);
            }
        }

        sfx(level, base, SoundEvents.AMETHYST_BLOCK_CHIME, 1.4F, 1.6F);
        sfx(level, base, SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 1.8F);
        SfxNet.screenEffect(level, base, SCREEN_RADIUS, Vfx.PINK, 0.12F, 12, 0.6F);
    }

    // ==================================================================
    // 裂空斩
    // ==================================================================

    public static void riftSlash(LivingEntity caster) {
        Level level = caster.level();
        Vec3 eye = caster.getEyePosition();
        Vec3 flat = flatLook(caster);
        Vec3 front = eye.add(flat.scale(2.6D));

        Vfx.play(level, Vfx.trail(caster, Vfx.CYAN, 12, 0.34F, 10, 0.22D));
        // 撕裂的空间裂缝：外层暗紫，内层白芯
        Vfx.play(level, Vfx.beam(front.add(0.0, 1.7, 0.0), front.add(0.0, -1.7, 0.0),
                Vfx.SHADOW, 10, 8, 0.55F, 10, 0.1D));
        Vfx.play(level, Vfx.beam(front.add(0.0, 1.5, 0.0), front.add(0.0, -1.5, 0.0),
                Vfx.WHITE, 8, 8, 0.22F, 8, 0.06D));
        Vfx.play(level, 1, Vfx.halo(front, flat, 0.2D, 3.6D, Vfx.CYAN, 20, 8, 0.38F, 10));
        Vfx.play(level, 2, Vfx.burst(front, Vfx.CYAN, 30, 1.6, 0.34F, 12));
        Vfx.play(level, 4, Vfx.spiral(eye, 1.2D, 0.2D, 1.2D, 0.0D, Vfx.WHITE, 12, 10, 0.3F, 9, 2.0D));

        sfx(level, eye, SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.4F);
        sfx(level, eye, SoundEvents.PLAYER_ATTACK_SWEEP, 1.3F, 1.5F);
        SfxNet.screenEffect(level, eye, SCREEN_RADIUS, Vfx.CYAN, 0.24F, 8, 2.2F);
    }

    // ==================================================================
    // 冰霜领域
    // ==================================================================

    public static void frostDomain(LivingEntity caster, List<LivingEntity> targets, int radius) {
        Level level = caster.level();
        Vec3 base = caster.position();
        double r = Math.max(2.0D, radius);

        Vfx.play(level, Vfx.rune(base, r * 2.0D, Vfx.ICE, 44, 0.008F, 0.1F));
        Vfx.play(level, Vfx.ring(base, 0.4D, r, Vfx.ICE, 30, 12, 0.42F, 14));
        Vfx.play(level, 2, Vfx.spiral(base, r * 0.8D, r, 0.0D, 6.0D, Vfx.ICE, 18, 22, 0.34F, 15, 1.6D));

        // 一圈拔地而起的冰棱
        for (int i = 0; i < 12; i++) {
            double angle = i * Math.PI / 6.0D;
            Vec3 p = base.add(Math.cos(angle) * r * 0.92D, 0.0, Math.sin(angle) * r * 0.92D);
            Vfx.play(level, 3 + i % 4, Vfx.pillar(p, 2.8, 0.22, Vfx.ICE, 7, 8, 0.3F, 10));
        }

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SNOWFLAKE,
                    base.x, base.y + 1.5, base.z, Math.max(1, Vfx.count(70)), r * 0.9, 2.0, r * 0.9, 0.0);
        }

        for (LivingEntity target : targets) {
            Vec3 tp = target.position().add(0.0, target.getBbHeight() * 0.5, 0.0);
            Vfx.play(level, 3, Vfx.halo(tp, new Vec3(0.0, 1.0, 0.0), 0.3D, 1.6D, Vfx.ICE, 12, 6, 0.32F, 9));
            Vfx.play(level, 4, Vfx.burst(tp, Vfx.ICE, 20, 1.2, 0.34F, 12));
        }

        sfx(level, base, SoundEvents.GLASS_BREAK, 1.0F, 0.6F);
        sfx(level, base, SoundEvents.AMETHYST_BLOCK_CHIME, 1.2F, 0.8F);
        SfxNet.screenEffect(level, base, SCREEN_RADIUS, Vfx.ICE, 0.14F, 12, 0.8F);
    }

    // ==================================================================
    // 业火焚城
    // ==================================================================

    public static void inferno(LivingEntity caster, List<LivingEntity> targets, int radius) {
        Level level = caster.level();
        Vec3 base = caster.position();
        double r = Math.max(2.0D, radius);

        Vfx.play(level, Vfx.ring(base, 0.5D, r, Vfx.ORANGE, 32, 12, 0.45F, 13));
        Vfx.play(level, Vfx.rune(base, r * 1.7D, Vfx.ORANGE, 26, -0.02F, 0.3F));
        Vfx.play(level, 2, Vfx.spiral(base, r * 0.7D, r * 0.95D, 0.0D, 7.0D, Vfx.EMBER, 22, 24, 0.36F, 14, 2.4D));
        Vfx.play(level, 4, Vfx.pillar(base, 7.0, 1.1, Vfx.ORANGE, 14, 10, 0.4F, 12));

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.FLAME, base.x, base.y + 1.0, base.z,
                    Math.max(1, Vfx.count(120)), r * 0.9, 1.2, r * 0.9, 0.06);
            serverLevel.sendParticles(ParticleTypes.LAVA, base.x, base.y + 0.4, base.z,
                    Math.max(1, Vfx.count(30)), r * 0.7, 0.4, r * 0.7, 0.0);
        }

        for (LivingEntity target : targets) {
            Vec3 tp = target.position().add(0.0, target.getBbHeight() * 0.5, 0.0);
            Vfx.play(level, 3, Vfx.spiral(tp, 1.6D, 0.2D, 2.5D, 0.0D, Vfx.ORANGE, 12, 10, 0.34F, 10, 2.0D));
            Vfx.play(level, 4, Vfx.burst(tp, Vfx.ORANGE, 30, 1.8, 0.4F, 14));
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, tp.x, tp.y, tp.z,
                        Math.max(1, Vfx.count(18)), 0.8, 0.8, 0.8, 0.04);
            }
        }

        sfx(level, base, SoundEvents.BLAZE_SHOOT, 1.3F, 0.7F);
        sfx(level, base, SoundEvents.FIRECHARGE_USE, 1.0F, 0.6F);
        SfxNet.screenEffect(level, base, SCREEN_RADIUS, Vfx.ORANGE, 0.18F, 12, 1.6F);
    }

    // ==================================================================
    // 斩天拔剑术
    // ==================================================================

    public static void zhanTianBaJianShu(LivingEntity caster) {
        Level level = caster.level();
        Vec3 base = caster.position();
        Vec3 eye = caster.getEyePosition();
        Vec3 flat = flatLook(caster);
        Vec3 right = new Vec3(-flat.z, 0.0, flat.x);

        // 天空蓄势：从极高处垂下一道光
        Vfx.play(level, Vfx.beam(base.add(0.0, 34.0, 0.0), base.add(0.0, 1.0, 0.0),
                Vfx.WHITE, 16, 8, 0.5F, 9, 0.25D));
        // 天空被劈开的一道裂口
        Vfx.play(level, 1, Vfx.beam(base.add(0.0, 30.0, 0.0).subtract(flat.scale(14.0)),
                base.add(0.0, 30.0, 0.0).add(flat.scale(14.0)), Vfx.GOLD, 22, 22, 0.6F, 12, 0.35D));
        // 竖劈刀光
        Vfx.play(level, 2, Vfx.arc(eye, flat, right, 4.5D, 1.15D, Vfx.WHITE, 14, 7, 0.55F, 10));
        Vfx.play(level, 3, Vfx.arc(eye, flat, right, 5.2D, 1.15D, Vfx.GOLD, 12, 8, 0.45F, 12));
        // 地面冲击
        Vfx.play(level, 4, Vfx.halo(eye.add(flat.scale(3.0D)), flat, 0.4D, 6.5D, Vfx.GOLD, 26, 10, 0.45F, 12));
        Vfx.play(level, 4, Vfx.ring(base, 0.5D, 7.0D, Vfx.GOLD, 26, 12, 0.45F, 12));
        Vfx.play(level, 5, Vfx.burst(base.add(0.0, 0.6, 0.0), Vfx.WHITE, 40, 2.4, 0.45F, 14));

        sfx(level, base, SoundEvents.PLAYER_ATTACK_CRIT, 1.4F, 0.6F);
        sfx(level, base, SoundEvents.TRIDENT_THUNDER, 0.7F, 0.8F);
        SfxNet.screenEffect(level, base, SCREEN_RADIUS, Vfx.WHITE, 0.32F, 10, 3.2F);
    }

    // ==================================================================
    // 剑荡八荒
    // ==================================================================

    public static void jianDangBaHuang(LivingEntity caster) {
        Level level = caster.level();
        Vec3 base = caster.position();
        Vec3 eye = caster.getEyePosition();
        Vec3 flat = flatLook(caster);

        Vfx.play(level, Vfx.arc(eye, flat, new Vec3(0.0, 1.0, 0.0), 5.0D, 1.5D, Vfx.GOLD, 18, 9, 0.5F, 12));
        Vfx.play(level, Vfx.arc(eye, flat, new Vec3(0.0, 1.0, 0.0), 6.2D, 1.5D, Vfx.GOLD_DEEP, 14, 11, 0.42F, 13));
        Vfx.play(level, Vfx.ring(base, 0.5D, 6.5D, Vfx.GOLD, 24, 12, 0.42F, 12));
        Vfx.play(level, 2, Vfx.halo(eye.add(flat.scale(2.0D)), flat, 0.3D, 4.0D, Vfx.GOLD, 18, 8, 0.4F, 10));
        Vfx.play(level, 3, Vfx.burst(base.add(0.0, 1.0, 0.0), Vfx.GOLD, 30, 2.0, 0.4F, 12));

        sfx(level, base, SoundEvents.PLAYER_ATTACK_SWEEP, 1.5F, 0.7F);
        sfx(level, base, SoundEvents.BELL_RESONATE, 0.6F, 1.2F);
        SfxNet.screenEffect(level, base, SCREEN_RADIUS, Vfx.GOLD, 0.2F, 10, 1.8F);
    }

    // ==================================================================
    // 庚金剑诀
    // ==================================================================

    public static void gengJinJianJue(LivingEntity caster, List<LivingEntity> targets) {
        Level level = caster.level();
        Vec3 base = caster.position().add(0.0, 1.0, 0.0);

        Vfx.play(level, Vfx.runePulse(base, 4.5D, Vfx.GOLD, 24, 0.09F, 0.2F, 24, 6));
        Vfx.play(level, Vfx.rune(base.subtract(0.0, 1.0, 0.0), 8.5D, Vfx.GOLD_DEEP, 30, -0.03F, 0.15F));
        Vfx.play(level, 2, Vfx.implode(base, 6.0D, 0.8D, 3.0D, Vfx.GOLD, 20, 16, 0.32F, 12, 2.4D));
        Vfx.play(level, 10, Vfx.burst(base, Vfx.GOLD, 34, 2.2, 0.38F, 12));

        for (LivingEntity target : targets) {
            Vec3 tp = target.position().add(0.0, target.getBbHeight() * 0.5, 0.0);
            Vfx.play(level, 5, Vfx.beam(tp.add(0.0, 12.0, 0.0), tp, Vfx.GOLD, 12, 5, 0.28F, 8, 0.12D));
        }

        sfx(level, base, SoundEvents.ANVIL_USE, 1.0F, 1.4F);
        sfx(level, base, SoundEvents.ENCHANTMENT_TABLE_USE, 1.2F, 0.9F);
        SfxNet.screenEffect(level, base, SCREEN_RADIUS, Vfx.GOLD, 0.16F, 10, 1.0F);
    }

    // ==================================================================
    // 七杀剑界
    // ==================================================================

    public static void qiShaJianJie(LivingEntity caster, Vec3 center, int radius) {
        Level level = caster.level();
        double r = Math.max(2.0D, radius);

        Vfx.play(level, Vfx.rune(center, r * 2.0D, Vfx.VIOLET, 60, -0.02F, 0.05F));
        Vfx.play(level, Vfx.halo(center.add(0.0, 0.4, 0.0), new Vec3(0.0, 1.0, 0.0),
                0.5D, r, Vfx.VIOLET, 26, 12, 0.42F, 14));
        Vfx.play(level, 2, Vfx.orb(center.add(0.0, 1.0, 0.0), r * 0.5D, Vfx.DEEP_VIOLET, 10, 40, 0.4F, 14, 0.25D));

        // 七个方位的杀剑光柱
        for (int i = 0; i < 7; i++) {
            double angle = i * Math.PI * 2.0D / 7.0D;
            Vec3 p = center.add(Math.cos(angle) * r * 0.88D, 0.0, Math.sin(angle) * r * 0.88D);
            Vfx.play(level, 1 + i % 3, Vfx.pillar(p, 3.2, 0.24, Vfx.VIOLET, 7, 10, 0.3F, 12));
        }

        sfx(level, center, SoundEvents.WARDEN_SONIC_BOOM, 0.5F, 1.4F);
        sfx(level, center, SoundEvents.EVOKER_CAST_SPELL, 1.2F, 0.8F);
        SfxNet.screenEffect(level, center, SCREEN_RADIUS, Vfx.VIOLET, 0.15F, 12, 1.2F);
    }

    // ==================================================================
    // 血月斩
    // ==================================================================

    public static void bloodMoonSlash(LivingEntity caster, List<LivingEntity> targets, int range) {
        Level level = caster.level();
        Vec3 base = caster.position();
        Vec3 eye = caster.getEyePosition();
        Vec3 flat = flatLook(caster);
        Vec3 moon = base.add(0.0, 4.2, 0.0);

        // 血月升起 → 碎裂
        Vfx.play(level, Vfx.orb(moon, 1.5D, Vfx.CRIMSON, 12, 16, 0.55F, 12, 0.35D));
        Vfx.play(level, Vfx.rune(base, 7.0D, Vfx.CRIMSON, 30, 0.035F, 0.2F));
        Vfx.play(level, 2, Vfx.burst(moon, Vfx.BLOOD, 46, 1.8, 0.45F, 16));
        Vfx.play(level, 2, Vfx.halo(moon, new Vec3(0.0, 1.0, 0.0), 0.3D, 2.6D, Vfx.CRIMSON, 16, 8, 0.4F, 12));

        // 血月刀光横扫
        Vfx.play(level, 1, Vfx.arc(eye, flat, new Vec3(0.0, 1.0, 0.0), 5.0D, 1.35D, Vfx.CRIMSON, 18, 9, 0.55F, 12));
        Vfx.play(level, 1, Vfx.arc(eye, flat, new Vec3(0.0, 1.0, 0.0), 6.0D, 1.35D, Vfx.BLOOD, 12, 11, 0.45F, 14));
        Vfx.play(level, 2, Vfx.ring(base, 0.5D, range, Vfx.BLOOD, 22, 12, 0.42F, 13));

        for (LivingEntity target : targets) {
            Vec3 tp = target.position().add(0.0, target.getBbHeight() * 0.5, 0.0);
            Vfx.play(level, 2, Vfx.burst(tp, Vfx.BLOOD, 26, 1.4, 0.4F, 14));
            Vfx.play(level, 2, Vfx.halo(tp, flat, 0.2D, 1.8D, Vfx.CRIMSON, 10, 6, 0.34F, 10));
        }

        sfx(level, base, SoundEvents.WITHER_HURT, 0.8F, 0.5F);
        sfx(level, base, SoundEvents.PLAYER_ATTACK_CRIT, 1.3F, 0.5F);
        SfxNet.screenEffect(level, base, SCREEN_RADIUS, Vfx.CRIMSON, 0.3F, 14, 2.4F);
    }

    // ==================================================================
    // 雷神之怒
    // ==================================================================

    public static void thorsWrath(LivingEntity caster, List<LivingEntity> targets) {
        Level level = caster.level();
        Vec3 base = caster.position();
        Vec3 eye = caster.getEyePosition();

        // 头顶压下来的雷云
        Vfx.play(level, Vfx.orb(base.add(0.0, 8.0, 0.0), 4.0D, Vfx.SHADOW, 12, 30, 1.1F, 20, 0.3D));
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                    base.x, base.y + 9.0, base.z, Math.max(1, Vfx.count(60)), 4.0, 0.8, 4.0, 0.02);
        }

        // 落雷前的预兆
        Vfx.play(level, 1, Vfx.lightning(base.add(0.0, 26.0, 0.0), eye.add(flatLook(caster).scale(2.0D)),
                Vfx.LIGHTNING, 4, 0.35F));

        for (int i = 0; i < targets.size(); i++) {
            LivingEntity target = targets.get(i);
            Vec3 tp = target.position().add(0.0, target.getBbHeight() * 0.5, 0.0);
            int delay = 1 + i;
            Vfx.play(level, delay, Vfx.lightning(tp.add(0.0, 26.0, 0.0), tp, Vfx.LIGHTNING, 6, 0.42F));
            Vfx.play(level, delay + 1, Vfx.halo(tp, new Vec3(0.0, 1.0, 0.0), 0.4D, 3.2D,
                    Vfx.LIGHTNING, 20, 8, 0.42F, 10));
            Vfx.play(level, delay + 1, Vfx.burst(tp, Vfx.LIGHTNING, 24, 1.8, 0.4F, 12));
        }

        // 目标之间跳跃的电弧
        for (int i = 0; i + 1 < targets.size(); i++) {
            Vec3 a = targets.get(i).position().add(0.0, 1.0, 0.0);
            Vec3 b = targets.get(i + 1).position().add(0.0, 1.0, 0.0);
            Vfx.play(level, 8 + i, Vfx.lightning(a, b, Vfx.CYAN, 4, 0.28F));
        }

        sfx(level, base, SoundEvents.LIGHTNING_BOLT_THUNDER, 1.4F, 0.9F);
        sfx(level, base, SoundEvents.TRIDENT_THUNDER, 1.0F, 1.1F);
        SfxNet.screenEffect(level, base, SCREEN_RADIUS, Vfx.LIGHTNING, 0.38F, 12, 3.0F);
    }

    // ==================================================================
    // 不动明王
    // ==================================================================

    public static void immovableKingCast(LivingEntity caster, int durationTicks) {
        Level level = caster.level();
        Vec3 base = caster.position();

        // 脚下金色曼陀罗
        Vfx.play(level, Vfx.rune(base, 9.0D, Vfx.GOLD, durationTicks + 20, 0.03F, 0.08F));
        Vfx.play(level, Vfx.ring(base, 0.4D, 5.0D, Vfx.GOLD, 28, 12, 0.46F, 14));
        // 八根金色光柱
        for (int i = 0; i < 8; i++) {
            double angle = i * Math.PI / 4.0D;
            Vec3 p = base.add(Math.cos(angle) * 3.6D, 0.0, Math.sin(angle) * 3.6D);
            Vfx.play(level, 2 + i % 3, Vfx.pillar(p, 4.2, 0.26, Vfx.GOLD, 8, 12, 0.32F, 14));
        }
        // 持续护体金光
        Vfx.play(level, 2, Vfx.orb(base.add(0.0, 1.1, 0.0), 1.5D, Vfx.GOLD, 10, durationTicks + 4, 0.42F, 12, 0.25D));
        Vfx.play(level, 6, Vfx.runePulse(base, 6.2D, Vfx.GOLD, 26, -0.06F, 0.35F, durationTicks, 20));

        sfx(level, base, SoundEvents.BEACON_ACTIVATE, 1.2F, 0.9F);
        sfx(level, base, SoundEvents.BELL_RESONATE, 1.0F, 0.7F);
        SfxNet.screenEffect(level, base, SCREEN_RADIUS, Vfx.GOLD, 0.2F, 12, 1.0F);
    }

    public static void immovableKingRelease(LivingEntity caster, int radius) {
        Level level = caster.level();
        Vec3 base = caster.position();
        double r = Math.max(2.0D, radius);

        Vfx.play(level, Vfx.rune(base, r * 2.2D, Vfx.WHITE, 18, 0.02F, 1.0F));
        Vfx.play(level, Vfx.halo(base.add(0.0, 0.6, 0.0), new Vec3(0.0, 1.0, 0.0),
                0.5D, r * 1.15D, Vfx.GOLD, 32, 10, 0.5F, 14));
        Vfx.play(level, Vfx.ring(base, 0.5D, r * 1.05D, Vfx.GOLD, 30, 10, 0.48F, 13));
        Vfx.play(level, Vfx.burst(base.add(0.0, 0.8, 0.0), Vfx.WHITE, 56, r * 0.5D, 0.5F, 16));
        Vfx.play(level, Vfx.shell(base.add(0.0, 1.2, 0.0), Vfx.GOLD, 40, r * 0.8D, 0.4F, 14));

        sfx(level, base, SoundEvents.GENERIC_EXPLODE, 1.2F, 0.6F);
        sfx(level, base, SoundEvents.WARDEN_SONIC_BOOM, 0.7F, 1.2F);
        SfxNet.screenEffect(level, base, SCREEN_RADIUS, Vfx.GOLD, 0.4F, 14, 3.4F);
    }

    // ==================================================================
    // 通用
    // ==================================================================

    private static Vec3 flatLook(LivingEntity entity) {
        Vec3 look = entity.getLookAngle();
        Vec3 flat = new Vec3(look.x, 0.0, look.z);
        if (flat.lengthSqr() < 1.0E-4D) {
            double yaw = Math.toRadians(entity.getYRot());
            flat = new Vec3(-Math.sin(yaw), 0.0, Math.cos(yaw));
        }
        return flat.normalize();
    }

    /** SoundEvents 在 1.21 里有的字段是 Holder<SoundEvent>，有的是裸 SoundEvent，这里都接住。 */
    private static void sfx(Level level, Vec3 pos, net.minecraft.core.Holder<SoundEvent> sound, float volume, float pitch) {
        play(level, pos, sound.value(), volume, pitch);
    }

    private static void sfx(Level level, Vec3 pos, SoundEvent sound, float volume, float pitch) {
        play(level, pos, sound, volume, pitch);
    }

    private static void play(Level level, Vec3 pos, SoundEvent sound, float volume, float pitch) {
        level.playSound(null, pos.x, pos.y, pos.z, sound, SoundSource.PLAYERS, volume, pitch);
    }
}
