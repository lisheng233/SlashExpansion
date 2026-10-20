package com.lisheng.slashexpansion.particle;

import com.lisheng.slashexpansion.registry.SlashExpansionParticles;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * 可着色柔光粒子参数（ARGB；life 为存活刻数）。
 *
 * <p>1.21 的 {@link ParticleOptions} 不再有 {@code writeToNetwork}/{@code Deserializer}，
 * 改为由 {@link ParticleType} 提供 {@link MapCodec} 与 {@link StreamCodec}。</p>
 */
public class GlowParticleOptions implements ParticleOptions {

    public static final MapCodec<GlowParticleOptions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("color").forGetter(GlowParticleOptions::color),
            Codec.FLOAT.fieldOf("size").forGetter(GlowParticleOptions::size),
            Codec.INT.fieldOf("life").forGetter(GlowParticleOptions::life)
    ).apply(instance, GlowParticleOptions::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, GlowParticleOptions> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, GlowParticleOptions::color,
                    ByteBufCodecs.FLOAT, GlowParticleOptions::size,
                    ByteBufCodecs.VAR_INT, GlowParticleOptions::life,
                    GlowParticleOptions::new);

    private final int color;
    private final float size;
    private final int life;

    public GlowParticleOptions(int color, float size, int life) {
        this.color = color;
        this.size = size;
        this.life = life;
    }

    public int color() {
        return color;
    }

    public float size() {
        return size;
    }

    public int life() {
        return life;
    }

    @Override
    public ParticleType<GlowParticleOptions> getType() {
        return SlashExpansionParticles.GLOW.get();
    }
}