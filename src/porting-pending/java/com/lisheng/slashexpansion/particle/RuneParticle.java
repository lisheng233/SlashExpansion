package com.lisheng.slashexpansion.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

/**
 * 平铺在地上的法阵/光环粒子：绕 Y 轴自转、快速张开、尾段淡出。
 *
 * <p>与普通粒子不同，它是水平放置的四边形，所以重写了 {@link #render}。</p>
 */
public class RuneParticle extends TextureSheetParticle {

    private final float finalSize;
    private final float birthScale;
    private final float spin;
    private final float baseAlpha;
    private float angle;
    private float oAngle;

    protected RuneParticle(ClientLevel level, double x, double y, double z,
                           RuneParticleOptions options, SpriteSet sprites) {
        super(level, x, y, z);
        this.pickSprite(sprites);
        this.finalSize = Math.max(0.5F, options.size());
        this.birthScale = Mth.clamp(options.birthScale(), 0.05F, 1.0F);
        this.spin = options.spin();
        this.baseAlpha = ((options.color() >>> 24) & 0xFF) / 255.0F;
        this.setColor(((options.color() >> 16) & 0xFF) / 255.0F,
                ((options.color() >> 8) & 0xFF) / 255.0F,
                (options.color() & 0xFF) / 255.0F);
        this.setAlpha(this.baseAlpha);
        this.quadSize = this.finalSize * this.birthScale;
        this.lifetime = Math.max(2, options.life());
        this.gravity = 0.0F;
        this.friction = 0.9F;
        this.hasPhysics = false;
        this.angle = this.random.nextFloat() * Mth.TWO_PI;
        this.oAngle = this.angle;
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
        this.oAngle = this.angle;
        this.angle += this.spin;

        float t = (float) this.age / (float) this.lifetime;
        // 前 1/4 快速张开到最终大小，之后保持
        float open = Math.min(1.0F, t * 4.0F);
        float grow = this.birthScale + (1.0F - this.birthScale)
                * (1.0F - (float) Math.pow(1.0F - open, 3.0D));
        this.quadSize = this.finalSize * grow;

        float fadeIn = Math.min(1.0F, t / 0.08F);
        float fadeOut = t < 0.65F ? 1.0F : 1.0F - (t - 0.65F) / 0.35F;
        this.setAlpha(this.baseAlpha * fadeIn * Math.max(0.0F, fadeOut));
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        Vec3 camPos = camera.getPosition();
        float px = (float) (Mth.lerp(partialTicks, this.xo, this.x) - camPos.x());
        float py = (float) (Mth.lerp(partialTicks, this.yo, this.y) - camPos.y());
        float pz = (float) (Mth.lerp(partialTicks, this.zo, this.z) - camPos.z());

        float half = this.getQuadSize(partialTicks) * 0.5F;
        float ang = Mth.lerp(partialTicks, this.oAngle, this.angle);
        float cos = Mth.cos(ang);
        float sin = Mth.sin(ang);

        int light = this.getLightColor(partialTicks);
        float u0 = this.getU0();
        float u1 = this.getU1();
        float v0 = this.getV0();
        float v1 = this.getV1();

        // 本地 (u, v) 在 XZ 平面上绕 Y 轴旋转
        float x0 = px + (-half * cos + half * sin);
        float z0 = pz + (-half * sin - half * cos);
        float x1 = px + (-half * cos - half * sin);
        float z1 = pz + (-half * sin + half * cos);
        float x2 = px + (half * cos - half * sin);
        float z2 = pz + (half * sin + half * cos);
        float x3 = px + (half * cos + half * sin);
        float z3 = pz + (half * sin - half * cos);

        buffer.vertex(x0, py, z0).uv(u1, v1).color(rCol, gCol, bCol, alpha).uv2(light).endVertex();
        buffer.vertex(x1, py, z1).uv(u1, v0).color(rCol, gCol, bCol, alpha).uv2(light).endVertex();
        buffer.vertex(x2, py, z2).uv(u0, v0).color(rCol, gCol, bCol, alpha).uv2(light).endVertex();
        buffer.vertex(x3, py, z3).uv(u0, v1).color(rCol, gCol, bCol, alpha).uv2(light).endVertex();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return SlashExpansionRenderTypes.ADDITIVE_FX;
    }

    @Override
    public boolean shouldCull() {
        return false;
    }

    public static class Provider implements ParticleProvider<RuneParticleOptions> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(RuneParticleOptions options, ClientLevel level,
                                       double x, double y, double z,
                                       double xd, double yd, double zd) {
            return new RuneParticle(level, x, y, z, options, this.sprites);
        }
    }
}
