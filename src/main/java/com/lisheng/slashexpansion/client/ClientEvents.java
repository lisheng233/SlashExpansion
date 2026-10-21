package com.lisheng.slashexpansion.client;

import com.lisheng.slashexpansion.SlashExpansion;
import com.lisheng.slashexpansion.entity.GiantSlashEntity;
import com.lisheng.slashexpansion.entity.QiShaJianJieDomainEntity;
import com.lisheng.slashexpansion.entity.SenbonzakuraSwordEntity;
import com.lisheng.slashexpansion.entity.WanJianGuiZongSwordEntity;
import com.lisheng.slashexpansion.particle.GlowParticle;
import com.lisheng.slashexpansion.particle.RuneParticle;
import com.lisheng.slashexpansion.registry.SlashExpansionEntities;
import com.lisheng.slashexpansion.registry.SlashExpansionParticles;
import com.mojang.blaze3d.vertex.PoseStack;
import mods.flammpfeil.slashblade.client.renderer.entity.SummonedSwordRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

/**
 * 客户端注册：实体渲染器与粒子 provider。
 *
 * <p>本模组的实体本体全部由 {@code Vfx} 自绘特效表现，本身不需要模型，
 * 所以统一挂一个空渲染器（但必须注册，否则客户端在实体生成时会报缺少渲染器）。</p>
 */
@EventBusSubscriber(modid = SlashExpansion.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {

    private static final ResourceLocation EMPTY_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SlashExpansion.MOD_ID, "textures/entity/empty.png");

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // 幻影剑直接用 SlashBlade 的召唤剑渲染器，才能显示刀身模型
        event.registerEntityRenderer(SlashExpansionEntities.WAN_JIAN_SWORD.get(), SummonedSwordRenderer::new);
        event.registerEntityRenderer(SlashExpansionEntities.SENBONZAKURA_SWORD.get(), SummonedSwordRenderer::new);
        event.registerEntityRenderer(SlashExpansionEntities.QI_SHA_JIAN_JIE_DOMAIN.get(), EmptyRenderer::new);
        event.registerEntityRenderer(SlashExpansionEntities.GIANT_SLASH.get(), EmptyRenderer::new);
    }

    @SubscribeEvent
    public static void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(SlashExpansionParticles.GLOW.get(), GlowParticle.Provider::new);
        event.registerSpriteSet(SlashExpansionParticles.RUNE.get(), RuneParticle.Provider::new);
    }

    /** 只提供占位贴图、不画任何东西的渲染器。 */
    private static final class EmptyRenderer<T extends Entity> extends EntityRenderer<T> {

        private EmptyRenderer(EntityRendererProvider.Context context) {
            super(context);
        }

        @Override
        public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack,
                           MultiBufferSource buffer, int packedLight) {
            // 视觉全部由粒子系统承担
        }

        @Override
        public ResourceLocation getTextureLocation(T entity) {
            return EMPTY_TEXTURE;
        }
    }

    private ClientEvents() {
    }
}