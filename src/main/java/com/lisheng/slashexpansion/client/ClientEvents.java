package com.lisheng.slashexpansion.client;

import com.lisheng.slashexpansion.SlashExpansion;
import com.lisheng.slashexpansion.registry.SlashExpansionEntities;
import mods.flammpfeil.slashblade.client.renderer.entity.SummonedSwordRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SlashExpansion.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(
                SlashExpansionEntities.WAN_JIAN_SWORD.get(),
                SummonedSwordRenderer::new
        );
        event.registerEntityRenderer(
                SlashExpansionEntities.SENBONZAKURA_SWORD.get(),
                SummonedSwordRenderer::new
        );
        // 裂空斩使用 EntityDrive，已由重锋渲染，无需注册
        // 冰霜领域和业火焚城不使用自定义实体，无需注册
    }
}