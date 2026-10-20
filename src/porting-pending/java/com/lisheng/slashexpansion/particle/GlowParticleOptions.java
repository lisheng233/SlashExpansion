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
 * 可着色的柔和光点粒子参数。
 *
 * <p>{@code color} 为 ARGB，高位 alpha 参与淡出；{@code size} 为粒子方块边长（格）；
 * {@code life} 为存活刻数。所有 SA 的特效都由它拼出来。</p>
 */
public class GlowParticleOptions implements ParticleOptions {

    public static final Codec<GlowParticleOptions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("color").forGetter(GlowParticleOptions::color),
            Codec.FLOAT.fieldOf("size").forGetter(GlowParticleOptions::size),
            Codec.INT.fieldOf("life").forGetter(GlowParticleOptions::life)
    ).apply(instance, GlowParticleOptions::new));

    @SuppressWarnings("deprecation")
    public static final ParticleOptions.Deserializer<GlowParticleOptions> DESERIALIZER =
            new ParticleOptions.Deserializer<>() {
                @Override
                public GlowParticleOptions fromCommand(ParticleType<GlowParticleOptions> type, StringReader reader) {
                    int color = 0xFFFFFFFF;
                    float size = 0.5F;
                    int life = 12;
                    try {
                        color = (int) Long.parseLong(reader.readUnquotedString(), 16);
                        reader.expect(' ');
                        size = reader.readFloat();
                        reader.expect(' ');
                        life = reader.readInt();
                    } catch (Exception ignored) {
                        // 命令里格式不对就退回默认值，不影响正常调用
                    }
                    return new GlowParticleOptions(color, size, life);
                }

                @Override
                public GlowParticleOptions fromNetwork(ParticleType<GlowParticleOptions> type, FriendlyByteBuf buf) {
                    return new GlowParticleOptions(buf.readInt(), buf.readFloat(), buf.readInt());
                }
            };

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

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeInt(color);
        buf.writeFloat(size);
        buf.writeInt(life);
    }

    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%08X %.3f %d", color, size, life);
    }
}
