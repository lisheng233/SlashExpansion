package com.lisheng.slashexpansion.client;

import com.lisheng.slashexpansion.SlashExpansion;
import com.lisheng.slashexpansion.config.SlashExpansionConfig;
import com.lisheng.slashexpansion.net.SfxMessage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import org.joml.Matrix4f;

/** 屏幕特效：全屏闪光 + 四边暗角 + 镜头震动（1.21 顶点/缓冲 API）。 */
@EventBusSubscriber(modid = SlashExpansion.MOD_ID, value = Dist.CLIENT)
public final class ClientSfx {

    private static int flashColor = 0xFFFFFF;
    private static float flashIntensity;
    private static int flashTicks;
    private static int flashTotal = 1;

    private static float shakeAmp;
    private static int shakeTicks;
    private static int shakeTotal = 1;

    public static void handle(SfxMessage message) {
        if (flashTicks <= 0 || message.intensity() >= flashIntensity) {
            flashColor = message.color();
            flashIntensity = message.intensity();
            flashTicks = message.ticks();
            flashTotal = Math.max(1, message.ticks());
        }
        if (message.shake() > 0.0F) {
            float remaining = shakeTicks > 0 ? shakeAmp * (shakeTicks / (float) shakeTotal) : 0.0F;
            shakeAmp = Math.max(remaining, message.shake());
            shakeTicks = Math.max(shakeTicks, message.ticks());
            shakeTotal = Math.max(1, shakeTicks);
        }
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (flashTicks > 0) {
            flashTicks--;
            if (flashTicks == 0) flashIntensity = 0.0F;
        }
        if (shakeTicks > 0) {
            shakeTicks--;
            if (shakeTicks == 0) shakeAmp = 0.0F;
        }
    }

    @SubscribeEvent
    public static void onCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        if (shakeTicks <= 0 || shakeAmp <= 0.0F) return;
        if (!SlashExpansionConfig.Vfx.screenShake()) return;

        float progress = shakeTicks / (float) shakeTotal;
        float amp = shakeAmp * progress * progress;
        Minecraft minecraft = Minecraft.getInstance();
        double time = (minecraft.level != null ? minecraft.level.getGameTime() : 0L) + event.getPartialTick();

        event.setYaw(event.getYaw() + (float) (Math.sin(time * 7.31D) * amp));
        event.setPitch(event.getPitch() + (float) (Math.cos(time * 9.17D) * amp * 0.8D));
        event.setRoll(event.getRoll() + (float) (Math.sin(time * 11.73D) * amp * 0.7D));
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        if (flashTicks <= 0 && flashIntensity <= 0.0F) return;

        GuiGraphics graphics = event.getGuiGraphics();
        int width = graphics.guiWidth();
        int height = graphics.guiHeight();
        if (width <= 0 || height <= 0) return;

        float progress = flashTicks / (float) flashTotal;
        float alpha = flashIntensity * (float) Math.pow(progress, 1.6D);
        if (alpha <= 0.004F) return;

        float r = ((flashColor >> 16) & 0xFF) / 255.0F;
        float g = ((flashColor >> 8) & 0xFF) / 255.0F;
        float b = (flashColor & 0xFF) / 255.0F;

        Matrix4f matrix = graphics.pose().last().pose();

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        // 1) 全屏加法闪光
        RenderSystem.blendFunc(com.mojang.blaze3d.platform.GlStateManager.SourceFactor.SRC_ALPHA,
                com.mojang.blaze3d.platform.GlStateManager.DestFactor.ONE);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buffer.addVertex(matrix, 0.0F, height, 0.0F).setColor(r, g, b, alpha);
        buffer.addVertex(matrix, width, height, 0.0F).setColor(r, g, b, alpha);
        buffer.addVertex(matrix, width, 0.0F, 0.0F).setColor(r, g, b, alpha);
        buffer.addVertex(matrix, 0.0F, 0.0F, 0.0F).setColor(r, g, b, alpha);
        BufferUploader.drawWithShader(buffer.buildOrThrow());

        // 2) 四边暗角
        RenderSystem.defaultBlendFunc();
        float edge = alpha * 0.85F;
        float bandW = width * 0.26F;
        float bandH = height * 0.30F;
        float dr = r * 0.35F;
        float dg = g * 0.35F;
        float db = b * 0.35F;

        BufferBuilder vignette = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        // 上
        vignette.addVertex(matrix, 0.0F, 0.0F, 0.0F).setColor(dr, dg, db, edge);
        vignette.addVertex(matrix, width, 0.0F, 0.0F).setColor(dr, dg, db, edge);
        vignette.addVertex(matrix, width, bandH, 0.0F).setColor(dr, dg, db, 0.0F);
        vignette.addVertex(matrix, 0.0F, bandH, 0.0F).setColor(dr, dg, db, 0.0F);
        // 下
        vignette.addVertex(matrix, 0.0F, height - bandH, 0.0F).setColor(dr, dg, db, 0.0F);
        vignette.addVertex(matrix, width, height - bandH, 0.0F).setColor(dr, dg, db, 0.0F);
        vignette.addVertex(matrix, width, height, 0.0F).setColor(dr, dg, db, edge);
        vignette.addVertex(matrix, 0.0F, height, 0.0F).setColor(dr, dg, db, edge);
        // 左
        vignette.addVertex(matrix, 0.0F, 0.0F, 0.0F).setColor(dr, dg, db, edge);
        vignette.addVertex(matrix, bandW, 0.0F, 0.0F).setColor(dr, dg, db, 0.0F);
        vignette.addVertex(matrix, bandW, height, 0.0F).setColor(dr, dg, db, 0.0F);
        vignette.addVertex(matrix, 0.0F, height, 0.0F).setColor(dr, dg, db, edge);
        // 右
        vignette.addVertex(matrix, width - bandW, 0.0F, 0.0F).setColor(dr, dg, db, 0.0F);
        vignette.addVertex(matrix, width, 0.0F, 0.0F).setColor(dr, dg, db, edge);
        vignette.addVertex(matrix, width, height, 0.0F).setColor(dr, dg, db, edge);
        vignette.addVertex(matrix, width - bandW, height, 0.0F).setColor(dr, dg, db, 0.0F);
        BufferUploader.drawWithShader(vignette.buildOrThrow());

        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }

    private ClientSfx() {
    }
}