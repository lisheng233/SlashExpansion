package com.lisheng.slashexpansion;

import com.lisheng.slashexpansion.registry.SlashExpansionComboStateRegistry;
import com.lisheng.slashexpansion.registry.SlashExpansionEntities;
import com.lisheng.slashexpansion.registry.SlashExpansionSlashArtsRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SlashExpansion.MOD_ID)
public class SlashExpansion {
    public static final String MOD_ID = "slashexpansion";

    public SlashExpansion() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        SlashExpansionEntities.register(modBus);
        SlashExpansionComboStateRegistry.COMBO_STATES.register(modBus);
        SlashExpansionSlashArtsRegistry.SLASH_ARTS.register(modBus);

        MinecraftForge.EVENT_BUS.register(this);
    }

    // 工具方法：生成 ResourceLocation
    public static ResourceLocation prefix(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}