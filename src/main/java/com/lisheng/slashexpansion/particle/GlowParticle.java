package com.lisheng.slashexpansion.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;

/**
 * 柔和光点：先张开再收缩淡出。
 *
 * <p>TODO(phase2c)：1.21 的自定义 ParticleRenderType 只保留 {@code begin}，
 * 状态恢复的时机与 1.20.1 不同，加法混合版本需要单独验证后再启用；
 * 目前先用原版半透明粒子类型，视觉略保守但不会污染渲染状态。</p>
 */
public class GlowParticle extends TextureSheetParticle {

    private final float baseSize;
    private final float baseAlpha;

    protected GlowParticle(ClientLevel level, double x, double y, double z,
                           GlowParticleOptions options, SpriteSet sprites) {
        super(level, x, y, z);
        this.pickSprite(sprites);
        this.baseSize = Math.max(0.02F, options.size());
        int alphaByte = (options.color() >>> 24) & 0xFF;
        // 调用方只写 0xRRGGBB 时 alpha 为 0，这里按不透明处理，避免粒子完全透明
        this.baseAlpha = (alphaByte == 0 ? 255 : alphaByte) / 255.0F;
        this.setColor(((options.color() >> 16) & 0xFF) / 255.0F,
                ((options.color() >> 8) & 0xFF) / 255.0F,
                (options.color() & 0xFF) / 255.0F);
        this.setAlpha(this.baseAlpha);
        this.quadSize = this.baseSize;
        this.lifetime = Math.max(2, options.life());
        this.gravity = 0.0F;
        this.friction = 0.90F;
        this.hasPhysics = false;
        this.xd = (this.random.nextDouble() - 0.5) * 0.01;
        this.yd = this.random.nextDouble() * 0.012;
        this.zd = (this.random.nextDouble() - 0.5) * 0.01;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }
        this.x += this.xd;
        this.y += this.yd;
        this.z += this.zd;
        this.xd *= this.friction;
        this.yd *= this.friction;
        this.zd *= this.friction;

        float t = (float) this.age / (float) this.lifetime;
        this.quadSize = this.baseSize * (0.70F + 0.45F * Mth.sin(t * Mth.PI));
        this.setAlpha(this.baseAlpha * (float) Math.pow(1.0F - t, 1.5D));
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<GlowParticleOptions> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(GlowParticleOptions options, ClientLevel level,
                                       double x, double y, double z,
                                       double xd, double yd, double zd) {
            return new GlowParticle(level, x, y, z, options, this.sprites);
        }
    }
}