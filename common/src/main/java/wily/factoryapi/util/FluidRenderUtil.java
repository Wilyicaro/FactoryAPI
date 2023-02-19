package wily.factoryapi.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.architectury.fluid.FluidStack;
import dev.architectury.hooks.fluid.FluidStackHooks;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class FluidRenderUtil {
    public static void renderTiledFluid( int posX, int posY,int i, int j, int renderAmount, int sizeX, int sizeY, FluidStack fluid, boolean hasColor){

        TextureAtlasSprite fluidSprite = fluidSprite(fluid, hasColor);
        RenderSystem.setShaderTexture(0, fluidSprite.atlas().location());
        int drawWidth = Math.min(sizeX - i, 16);
        int drawHeight = Math.min(renderAmount - j, 16);

        int drawX = posX + i;
        int drawY = posY + j;

        float minU = fluidSprite.getU0();
        float maxU = fluidSprite.getU1();
        float minV = fluidSprite.getV0();
        float maxV = fluidSprite.getV1();
        float dH = minV + (maxV - minV) * drawHeight / 16F;
        float dW = minU + (maxU - minU) * drawWidth / 16F;

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder tes = tessellator.getBuilder();
        tes.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        tes.vertex(drawX, drawY + drawHeight, 0).uv(minU, dH).endVertex();
        tes.vertex(drawX + drawWidth, drawY + drawHeight, 0).uv(dW, dH).endVertex();
        tes.vertex(drawX + drawWidth, drawY, 0).uv(dW, minV).endVertex();
        tes.vertex(drawX, drawY, 0).uv(minU, minV).endVertex();
        tessellator.end();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static TextureAtlasSprite fluidSprite(FluidStack fluid, boolean hasColor){
        TextureAtlasSprite fluidSprite = FluidStackHooks.getStillTexture(fluid);
        if (hasColor){
            int color = FluidStackHooks.getColor(fluid);
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
