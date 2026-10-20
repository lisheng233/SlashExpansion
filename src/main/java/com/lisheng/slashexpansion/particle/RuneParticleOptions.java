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
public class RuneParticleOptions implements ParticleOptions {

    public static final MapCodec<RuneParticleOptions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("color").forGetter(RuneParticleOptions::color),
            Codec.FLOAT.fieldOf("size").forGetter(RuneParticleOptions::size),
            Codec.INT.fieldOf("life").forGetter(RuneParticleOptions::life),
            Codec.FLOAT.fieldOf("spin").forGetter(RuneParticleOptions::spin),
            Codec.FLOAT.fieldOf("birth_scale").forGetter(RuneParticleOptions::birthScale)
    ).apply(instance, RuneParticleOptions::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, RuneParticleOptions> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, RuneParticleOptions::color,
                    ByteBufCodecs.FLOAT, RuneParticleOptions::size,
                    ByteBufCodecs.VAR_INT, RuneParticleOptions::life,
                    ByteBufCodecs.FLOAT, RuneParticleOptions::spin,
                    ByteBufCodecs.FLOAT, RuneParticleOptions::birthScale,
                    RuneParticleOptions::new);

    private final int color;
    private final float size;
    private final int life;
    private final float spin;
    private final float birthScale;

    public RuneParticleOptions(int color, float size, int life, float spin, float birthScale) {
        this.color = color;
        this.size = size;
        this.life = life;
        this.spin = spin;
        this.birthScale = birthScale;
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

    public float spin() {
        return spin;
    }

    public float birthScale() {
        return birthScale;
    }

    @Override
    public ParticleType<RuneParticleOptions> getType() {
        return SlashExpansionParticles.RUNE.get();
    }
}