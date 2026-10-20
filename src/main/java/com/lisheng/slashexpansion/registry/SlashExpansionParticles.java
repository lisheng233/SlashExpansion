package com.lisheng.slashexpansion.registry;

import com.lisheng.slashexpansion.SlashExpansion;
import com.lisheng.slashexpansion.particle.GlowParticleOptions;
import com.lisheng.slashexpansion.particle.RuneParticleOptions;
import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 本模组的自定义粒子类型（客户端渲染在 {@code ClientEvents} 里注册）。
 */
public class SlashExpansionParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, SlashExpansion.MOD_ID);

    /** 柔和光点 */
    public static final RegistryObject<ParticleType<GlowParticleOptions>> GLOW =
            PARTICLES.<ParticleType<GlowParticleOptions>>register("glow",
                    () -> simple(GlowParticleOptions.DESERIALIZER, GlowParticleOptions.CODEC));

    /** 地面法阵 */
    public static final RegistryObject<ParticleType<RuneParticleOptions>> RUNE =
            PARTICLES.<ParticleType<RuneParticleOptions>>register("rune",
                    () -> simple(RuneParticleOptions.DESERIALIZER, RuneParticleOptions.CODEC));

    private static <T extends ParticleOptions> ParticleType<T> simple(
            @SuppressWarnings("deprecation") ParticleOptions.Deserializer<T> deserializer, Codec<T> codec) {
        return new ParticleType<>(false, deserializer) {
            @Override
            public Codec<T> codec() {
                return codec;
            }
        };
    }

    public static void register(IEventBus bus) {
        PARTICLES.register(bus);
    }

    private SlashExpansionParticles() {
    }
}
