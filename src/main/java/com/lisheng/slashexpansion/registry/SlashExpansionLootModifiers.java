package com.lisheng.slashexpansion.registry;

import com.lisheng.slashexpansion.SlashExpansion;
import com.lisheng.slashexpansion.loot.SaScrollLootModifier;
import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/** 全局战利品修饰符的 codec 注册（1.21 用它取代 LootTableLoadEvent）。 */
public class SlashExpansionLootModifiers {

    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> SERIALIZERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, SlashExpansion.MOD_ID);

    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<SaScrollLootModifier>> SA_SCROLL =
            SERIALIZERS.register("sa_scroll", () -> SaScrollLootModifier.CODEC);

    public static void register(IEventBus bus) {
        SERIALIZERS.register(bus);
    }

    private SlashExpansionLootModifiers() {
    }
}