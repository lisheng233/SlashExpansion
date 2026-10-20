package com.lisheng.slashexpansion.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 自定义粒子渲染类型：加法混合 + 不写深度。
 *
 * <p>原版 {@code PARTICLE_SHEET_TRANSLUCENT} 会写深度，大量重叠的光点会互相裁切出硬边；
 * 改用加法混合后重叠部分自然叠亮，剑光、法阵、火花才有一层层叠上去的光晕感。</p>
 */
@OnlyIn(Dist.CLIENT)
public final class SlashExpansionRenderTypes {

    public static final ParticleRenderType ADDITIVE_FX = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder builder, TextureManager textureManager) {
            RenderSystem.depthMask(false);
            RenderSystem.disableCull();
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            RenderSystem.setShader(GameRenderer::getParticleShader);
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public void end(Tesselator tesselator) {
            tesselator.end();
            RenderSystem.enableCull();
            RenderSystem.defaultBlendFunc();
            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();
        }

        @Override
        public String toString() {
            return "slashexpansion_additive_fx";
        }
    };

    private SlashExpansionRenderTypes() {
    }
}
