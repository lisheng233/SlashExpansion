package com.lisheng.slashexpansion.particle;

import com.lisheng.slashexpansion.registry.SlashExpansionParticles;
import com.mojang.brigadier.StringReader;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Locale;

/**
 * 平铺在地面的法阵粒子参数。
 *
 * <p>{@code color} 为 ARGB；{@code size} 为最终直径（格）；{@code life} 为存活刻数；
 * {@code spin} 为每刻自转弧度；{@code birthScale} 为出生时占最终大小的比例
 * （1.0 表示不放大，0.2 表示从小张开）。</p>
 */
public class RuneParticleOptions implements ParticleOptions {

    public static final Codec<RuneParticleOptions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("color").forGetter(RuneParticleOptions::color),
            Codec.FLOAT.fieldOf("size").forGetter(RuneParticleOptions::size),
            Codec.INT.fieldOf("life").forGetter(RuneParticleOptions::life),
            Codec.FLOAT.fieldOf("spin").forGetter(RuneParticleOptions::spin),
            Codec.FLOAT.fieldOf("birth_scale").forGetter(RuneParticleOptions::birthScale)
    ).apply(instance, RuneParticleOptions::new));

    @SuppressWarnings("deprecation")
    public static final ParticleOptions.Deserializer<RuneParticleOptions> DESERIALIZER =
            new ParticleOptions.Deserializer<>() {
                @Override
                public RuneParticleOptions fromCommand(ParticleType<RuneParticleOptions> type, StringReader reader) {
                    int color = 0xFFFFFFFF;
                    float size = 6.0F;
                    int life = 40;
                    float spin = 0.05F;
                    float birthScale = 1.0F;
                    try {
                        color = (int) Long.parseLong(reader.readUnquotedString(), 16);
                        reader.expect(' ');
                        size = reader.readFloat();
                        reader.expect(' ');
                        life = reader.readInt();
                        reader.expect(' ');
                        spin = reader.readFloat();
                        reader.expect(' ');
                        birthScale = reader.readFloat();
                    } catch (Exception ignored) {
                        // 同上：命令格式错误时使用默认值
                    }
                    return new RuneParticleOptions(color, size, life, spin, birthScale);
                }

                @Override
                public RuneParticleOptions fromNetwork(ParticleType<RuneParticleOptions> type, FriendlyByteBuf buf) {
                    return new RuneParticleOptions(buf.readInt(), buf.readFloat(), buf.readInt(),
                            buf.readFloat(), buf.readFloat());
                }
            };

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

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeInt(color);
        buf.writeFloat(size);
        buf.writeInt(life);
        buf.writeFloat(spin);
        buf.writeFloat(birthScale);
    }

    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%08X %.3f %d %.4f %.3f", color, size, life, spin, birthScale);
    }
}
