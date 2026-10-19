package com.lisheng.slashexpansion;

import com.lisheng.slashexpansion.config.SlashExpansionConfig;
import com.lisheng.slashexpansion.registry.SlashExpansionComboStateRegistry;
import com.lisheng.slashexpansion.registry.SlashExpansionEntities;
import com.lisheng.slashexpansion.registry.SlashExpansionSlashArtsRegistry;
import com.lisheng.slashexpansion.registry.SlashExpansionSpecialEffectsRegistry;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SlashExpansion.MOD_ID)
public class SlashExpansion {
    public static final String MOD_ID = "slashexpansion";

    @SuppressWarnings("removal")
    public SlashExpansion() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 注册配置文件：config/slashexpansion-common.toml
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SlashExpansionConfig.SPEC);

        SlashExpansionEntities.register(modBus);
        SlashExpansionComboStateRegistry.COMBO_STATES.register(modBus);
        SlashExpansionSlashArtsRegistry.SLASH_ARTS.register(modBus);
        SlashExpansionSpecialEffectsRegistry.SPECIAL_EFFECTS.register(modBus);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SuppressWarnings("removal")
    public static ResourceLocation prefix(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
