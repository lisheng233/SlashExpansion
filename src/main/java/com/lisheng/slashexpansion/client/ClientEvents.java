package com.lisheng.slashexpansion.client;

import com.lisheng.slashexpansion.SlashExpansion;
import com.lisheng.slashexpansion.entity.GiantSlashEntity;
import com.lisheng.slashexpansion.entity.QiShaJianJieDomainEntity;
import com.lisheng.slashexpansion.registry.SlashExpansionEntities;
import com.mojang.blaze3d.vertex.PoseStack;

import mods.flammpfeil.slashblade.client.renderer.entity.SummonedSwordRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
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
        event.registerEntityRenderer(
            SlashExpansionEntities.QI_SHA_JIAN_JIE_DOMAIN.get(),
            (ctx) -> new net.minecraft.client.renderer.entity.EntityRenderer<QiShaJianJieDomainEntity>(ctx) {
                @Override
                public void render(QiShaJianJieDomainEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
                    // 不渲染
                }
                @Override
                public ResourceLocation getTextureLocation(QiShaJianJieDomainEntity entity) {
                    return null;
                }
            }
        );
        event.registerEntityRenderer(
            SlashExpansionEntities.GIANT_SLASH.get(),
            (ctx) -> new net.minecraft.client.renderer.entity.EntityRenderer<GiantSlashEntity>(ctx) {
                @Override
                public void render(GiantSlashEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
                    // 不渲染实体，只用粒子
                }
                @Override
                public ResourceLocation getTextureLocation(GiantSlashEntity entity) {
                    return null;
                }
            }
        );
    }
}