package com.lisheng.slashexpansion.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;

/**
 * 柔和光点：先张开再收缩淡出，静止悬浮，用于剑光、火花、光柱、冲击波环。
 */
public class GlowParticle extends TextureSheetParticle {

    private final float baseSize;
    private final float baseAlpha;

    protected GlowParticle(ClientLevel level, double x, double y, double z,
                           GlowParticleOptions options, SpriteSet sprites) {
        super(level, x, y, z);
        this.pickSprite(sprites);
        this.baseSize = Math.max(0.02F, options.size());
        this.baseAlpha = ((options.color() >>> 24) & 0xFF) / 255.0F;
        this.setColor(((options.color() >> 16) & 0xFF) / 255.0F,
                ((options.color() >> 8) & 0xFF) / 255.0F,
                (options.color() & 0xFF) / 255.0F);
        this.setAlpha(this.baseAlpha);
        this.quadSize = this.baseSize;
        this.lifetime = Math.max(2, options.life());
        this.gravity = 0.0F;
        this.friction = 0.90F;
        this.hasPhysics = false;
        // 随机初速带来的轻微漂移，让整片光点不至于完全静止
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
        // 先胀后缩：0.7 → 1.15 → 0.75，制造“闪一下”的呼吸感
        this.quadSize = this.baseSize * (0.70F + 0.45F * Mth.sin(t * Mth.PI));
        // 尾段快速消失，避免拖泥带水
        this.setAlpha(this.baseAlpha * (float) Math.pow(1.0F - t, 1.5D));
    }

    @Override
    public ParticleRenderType getRenderType() {
        return SlashExpansionRenderTypes.ADDITIVE_FX;
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
