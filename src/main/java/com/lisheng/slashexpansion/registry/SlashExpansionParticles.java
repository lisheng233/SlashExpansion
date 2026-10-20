package com.lisheng.slashexpansion.registry;

import com.lisheng.slashexpansion.SlashExpansion;
import com.lisheng.slashexpansion.particle.GlowParticleOptions;
import com.lisheng.slashexpansion.particle.RuneParticleOptions;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** 自定义粒子类型（渲染器在 ClientEvents 注册）。 */
public class SlashExpansionParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(Registries.PARTICLE_TYPE, SlashExpansion.MOD_ID);

    public static final DeferredHolder<ParticleType<?>, ParticleType<GlowParticleOptions>> GLOW =
            PARTICLES.register("glow", () -> new ParticleType<>(false) {
                @Override
                public MapCodec<GlowParticleOptions> codec() {
                    return GlowParticleOptions.CODEC;
                }

                @Override
                public StreamCodec<? super RegistryFriendlyByteBuf, GlowParticleOptions> streamCodec() {
                    return GlowParticleOptions.STREAM_CODEC;
                }
            });

    public static final DeferredHolder<ParticleType<?>, ParticleType<RuneParticleOptions>> RUNE =
            PARTICLES.register("rune", () -> new ParticleType<>(false) {
                @Override
                public MapCodec<RuneParticleOptions> codec() {
                    return RuneParticleOptions.CODEC;
                }

                @Override
                public StreamCodec<? super RegistryFriendlyByteBuf, RuneParticleOptions> streamCodec() {
                    return RuneParticleOptions.STREAM_CODEC;
                }
            });

    public static void register(IEventBus bus) {
        PARTICLES.register(bus);
    }

    private SlashExpansionParticles() {
    }
}