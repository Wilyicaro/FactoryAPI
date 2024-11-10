package wily.factoryapi.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.joml.Matrix4f;
import wily.factoryapi.FactoryAPIClient;

public class FluidRenderUtil {
    public static void renderTiledFluid(GuiGraphics graphics, int posX, int posY, int i, int j, int renderAmount, int fluidWidth, TextureAtlasSprite fluidSprite){
        int drawWidth = Math.min(fluidWidth - i, 16);
        int drawHeight = Math.min(renderAmount - j, 16);

        int drawX = posX + i;
        int drawY = posY + j;

        float minU = fluidSprite.getU0();
        float maxU = fluidSprite.getU1();
        float minV = fluidSprite.getV0();
        float maxV = fluidSprite.getV1();
        float dH = minV + (maxV - minV) * drawHeight / 16F;
        float dW = minU + (maxU - minU) * drawWidth / 16F;
        Matrix4f matrix4f = graphics.pose().last().pose();

        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.vertex(matrix4f, drawX, drawY + drawHeight, 0).uv(minU, dH).endVertex();
        bufferBuilder.vertex(matrix4f,drawX + drawWidth, drawY + drawHeight, 0).uv(dW, dH).endVertex();
        bufferBuilder.vertex(matrix4f,drawX + drawWidth, drawY, 0).uv(dW, minV).endVertex();
        bufferBuilder.vertex(matrix4f,drawX, drawY, 0).uv(minU, minV).endVertex();
        BufferUploader.drawWithShader(bufferBuilder.end());

    }

    public static TextureAtlasSprite fluidSprite(FluidInstance fluid, boolean hasColor){
        TextureAtlasSprite fluidSprite = FactoryAPIClient.getFluidStillTexture(fluid.getFluid());
        if (hasColor){
            int color = FactoryAPIClient.getFluidColor(fluid.getFluid(),null,null);
            float a = ((color & 0xFF000000) >> 24) / 255F;
            a = a <= 0.001F ? 1 : a;
            float r = ((color & 0xFF0000) >> 16) / 255F;
            float g = ((color & 0xFF00) >> 8) / 255F;
            float b = (color & 0xFF) / 255F;
            RenderSystem.setShaderColor(r,g,b,a);
        }
        return fluidSprite;
    }
}
